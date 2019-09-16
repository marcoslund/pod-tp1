package ar.edu.itba.pod.services;

import ar.edu.itba.pod.interfaces.ElectionState;
import ar.edu.itba.pod.interfaces.PoliticalParty;
import ar.edu.itba.pod.interfaces.State;
import ar.edu.itba.pod.interfaces.exceptions.IllegalElectionStateException;
import ar.edu.itba.pod.interfaces.exceptions.PollingPlaceNotFoundException;
import ar.edu.itba.pod.interfaces.models.QueryResult;
import ar.edu.itba.pod.interfaces.models.Vote;
import ar.edu.itba.pod.interfaces.services.AdministrationService;
import ar.edu.itba.pod.interfaces.services.MonitoringService;
import ar.edu.itba.pod.interfaces.services.QueryService;
import ar.edu.itba.pod.interfaces.services.VotingService;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Servant
        implements AdministrationService, MonitoringService, QueryService, VotingService {

    private static final AtomicBoolean electionStarted = new AtomicBoolean(false);
    private static final AtomicBoolean electionFinished = new AtomicBoolean(false);
    private static final Map<State, Map<Long, List<Vote>>> stateVotes = new HashMap<>();
    private static int voteCount = 0;

    public Servant() throws RemoteException {
        for(State state : State.values()) {
            stateVotes.put(state, new HashMap<>());
        }
    }

    @Override
    public void startElection() throws RemoteException, IllegalElectionStateException {
        if(!electionStarted.compareAndSet(false, true)) {
            throw new IllegalElectionStateException("Election already active.");
        }
    }

    @Override
    public synchronized void endElection() throws RemoteException, IllegalElectionStateException {
        if(electionStartedAndNotFinished())
            electionFinished.set(true);
        else {
            throw new IllegalElectionStateException("Election not active.");
        }
    }

    private boolean electionStartedAndNotFinished() {
        return electionStarted.get() && !electionFinished.get();
    }

    @Override
    public ElectionState queryElection() throws RemoteException {
        if(!electionStarted.get())
            return ElectionState.NOT_STARTED;
        else if(electionFinished.get()) {
            return ElectionState.FINISHED;
        } else {
            return ElectionState.IN_PROGRESS;
        }
    }

    @Override
    public void registerFiscal(final PoliticalParty politicalParty, final long tableNumber)
            throws RemoteException, IllegalElectionStateException {

    }

    @Override
    public SortedSet<QueryResult> queryNationResults()
            throws RemoteException, IllegalElectionStateException {
        SortedSet<QueryResult> results = new TreeSet<>();

        int voteQty;
        Map<PoliticalParty, List<Vote>> votes;
        synchronized (stateVotes) {
             votes = stateVotes.values().parallelStream()
                    .map(Map::values)
                    .flatMap(Collection::parallelStream)
                    .flatMap(List::parallelStream)
                    .collect(Collectors.groupingBy(Vote::getMainChoice));
            voteQty = voteCount;
        }

        votes.forEach((k, v) -> results.add(
                new QueryResult(k, v.size() * 100 / (double) voteQty)));

        boolean foundWinner = Double.compare(results.first().getPercentage(), 50) >= 0;
        int n = 2;
        while(n <= Vote.PARTY_COUNT && !foundWinner) {
            System.out.println("CYCLE " + n + ": " + results);
            final int aux = n;
            for(int i = 0; i < votes.get(results.last().getPoliticalParty()).size(); i++) {
                Vote vote = votes.get(results.last().getPoliticalParty()).get(i);
                System.out.println("Vote: " + vote);
                vote.getChoice(aux).ifPresent(x -> {
                    if(!votes.containsKey(x))
                        votes.put(x, new ArrayList<>());
                    votes.get(x).add(vote);
                });
            }
            votes.remove(results.last().getPoliticalParty());
            results.remove(results.last());

            results.forEach(r -> r.setPercentage(
                    votes.get(r.getPoliticalParty()).size() * 100 / (double) voteQty
            ));
            foundWinner = Double.compare(results.first().getPercentage(), 50) >= 0;
            n++;
        }
        return results;
    }

    @Override
    public SortedSet<QueryResult> queryStateResults(final State state)
            throws RemoteException, IllegalElectionStateException {
        return null;
    }

    @Override
    public SortedSet<QueryResult> queryTableResults(final long tableNumber)
            throws RemoteException, IllegalElectionStateException,
                PollingPlaceNotFoundException {
        SortedSet<QueryResult> results = new TreeSet<>();

        // Find the table among all state tables
        List<Vote> tableVotes = stateVotes.values().parallelStream()
                .map(x -> x.get(tableNumber))
                .filter(Objects::nonNull)
                .findFirst().orElseThrow(PollingPlaceNotFoundException::new);

        Map<PoliticalParty, Long> voteQtyByParty;
        int voteCount;
        synchronized (stateVotes) {
            // Calculate amount of votes by parties
            voteQtyByParty = tableVotes.parallelStream()
                    .collect(Collectors.groupingBy(
                            Vote::getMainChoice, Collectors.counting()));
             voteCount = tableVotes.size();
        }

        // Calculate percentage of every party's votes
        Map<PoliticalParty, Double> percentages = voteQtyByParty
                .entrySet().parallelStream().collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> (e.getValue().doubleValue() / voteCount)
        ));

        // Add QueryResults to sorted set
        percentages.entrySet().parallelStream()
                .map(x -> new QueryResult(x.getKey(), x.getValue() * 100))
                .forEach(results::add);

        return results;
    }

    @Override
    public void vote(final List<Vote> votes)
            throws RemoteException, IllegalElectionStateException {
        Objects.requireNonNull(votes);
        if(!electionStartedAndNotFinished()) {
            throw new IllegalElectionStateException("Election not active.");
        }

        synchronized (stateVotes) {
            for (Vote vote : votes) {
                stateVotes.get(vote.getState())
                        .putIfAbsent(vote.getPollingPlaceNumber(), new ArrayList<>());
                stateVotes.get(vote.getState())
                        .get(vote.getPollingPlaceNumber())
                        .add(vote);
                voteCount++;
            }
        }

    }

}
