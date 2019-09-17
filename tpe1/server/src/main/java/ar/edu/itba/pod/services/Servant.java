package ar.edu.itba.pod.services;

import ar.edu.itba.pod.client.monitoring.MonitoringClient;
import ar.edu.itba.pod.interfaces.ElectionState;
import ar.edu.itba.pod.interfaces.PoliticalParty;
import ar.edu.itba.pod.interfaces.State;
import ar.edu.itba.pod.interfaces.exceptions.ConflictException;
import ar.edu.itba.pod.interfaces.exceptions.IllegalElectionStateException;
import ar.edu.itba.pod.interfaces.exceptions.PollingPlaceNotFoundException;
import ar.edu.itba.pod.interfaces.models.QueryResult;
import ar.edu.itba.pod.interfaces.models.Vote;
import ar.edu.itba.pod.interfaces.services.AdministrationService;
import ar.edu.itba.pod.interfaces.services.MonitoringService;
import ar.edu.itba.pod.interfaces.services.QueryService;
import ar.edu.itba.pod.interfaces.services.VotingService;
import ar.edu.itba.pod.services.helpers.NationalQueryHelper;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Servant
        implements AdministrationService, MonitoringService, QueryService, VotingService {

    private static final AtomicBoolean electionStarted = new AtomicBoolean(false);
    private static final AtomicBoolean electionFinished = new AtomicBoolean(false);
    private static final Map<State, Map<Long, List<Vote>>> stateVotes = new HashMap<>();
    private static final Map<Long, List<MonitoringClient>> fiscalsMap = new HashMap<>();
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
        if(electionActive())
            electionFinished.set(true);
        else {
            throw new IllegalElectionStateException("Election not active.");
        }
    }

    private boolean electionActive() {
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
    public void registerFiscal(final MonitoringClient monitoringClient, final long tableNumber)
            throws RemoteException, ConflictException, IllegalElectionStateException {

        if(electionStarted.get() || electionFinished.get()) {
            throw new IllegalElectionStateException("Election started or finished");
        }

        Optional<List<MonitoringClient>> fiscals = Optional.ofNullable(fiscalsMap.get(tableNumber));
        if(!fiscals.isPresent()) {
            fiscalsMap.put(tableNumber, new LinkedList<>());
        }

        if(fiscals.get().contains(monitoringClient))
            throw new ConflictException("There is already a fiscal of the same political party in the table");

        fiscals.get().add(monitoringClient);
    }

    @Override
    public SortedSet<QueryResult> queryNationResults()
            throws RemoteException, IllegalElectionStateException {

        if(!electionActive()) {
            throw new IllegalElectionStateException("Election not active.");
        }

        SortedSet<QueryResult> results = new TreeSet<>();
        final SortedSet<QueryResult> auxResults = results;
        final int voteQty;
        final Map<PoliticalParty, List<Vote>> votes;
        final Map<Vote, Integer> currentVotesRank = new HashMap<>();

        // Retrieve and group votes by main choice
        synchronized (stateVotes) {
             votes = stateVotes.values().stream()
                    .map(Map::values)
                    .flatMap(Collection::parallelStream)
                    .flatMap(List::parallelStream)
                    .collect(Collectors.groupingBy(Vote::getMainChoice));
            voteQty = voteCount;
        }

        // Initialize query results
        votes.forEach((k, votesList) -> {
            auxResults.add(
                    new QueryResult(k, votesList.size() * 100 / (double) voteQty));
            votesList.forEach(vote -> currentVotesRank.put(vote, 1));
        });

        while(!NationalQueryHelper.foundWinner(results)) {

            // Reprocess all the least popular candidate's votes
            votes.get(NationalQueryHelper.getLeastPopularCandidate(results)).forEach(
                v -> {
                    while(!NationalQueryHelper.reprocessVote(v, votes, currentVotesRank));
                }
            );
            votes.remove(NationalQueryHelper.getLeastPopularCandidate(results));
            results.remove(results.last());

            // Update percentages based on new vote distribution
            results = NationalQueryHelper.updateResults(results, votes,
                    NationalQueryHelper.getRemainingVoteQty(currentVotesRank));
        }
        return results;
    }

    @Override
    public SortedSet<QueryResult> queryStateResults(final State state)
            throws RemoteException, IllegalElectionStateException {

        if(!electionActive()) {
            throw new IllegalElectionStateException("Election not active.");
        }

        SortedSet<QueryResult> results = new TreeSet<>();
        final int voteQty;
        final Map<PoliticalParty, List<Vote>> votes;
        final Map<Vote, Integer> currentVotesRank = new HashMap<>();

        // Retrieve and group votes by main choice
        synchronized (stateVotes) {
            votes = stateVotes.get(state).values().stream()
                    .flatMap(List::parallelStream)
                    .collect(Collectors.groupingBy(Vote::getMainChoice));
            voteQty = voteCount;
        }

        /*while() {
            while() {
                // Repartir
            }
            // Eliminar al peor
        }*/
        return null;
    }

    @Override
    public SortedSet<QueryResult> queryTableResults(final long tableNumber)
            throws RemoteException, IllegalElectionStateException,
                PollingPlaceNotFoundException {

        if(!electionActive()) {
            throw new IllegalElectionStateException("Election not active.");
        }

        SortedSet<QueryResult> results = new TreeSet<>();

        // Find the table among all state tables
        List<Vote> tableVotes = stateVotes.values().parallelStream()
                .map(x -> x.get(tableNumber))
                .filter(Objects::nonNull)
                .findFirst().orElseThrow(PollingPlaceNotFoundException::new);

        Map<PoliticalParty, Long> voteQtyByParty;
        int voteCount;
        // Calculate amount of votes by parties
        synchronized (stateVotes) {
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
        if(!electionActive()) {
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

                for(MonitoringClient fiscal : fiscalsMap.get(vote.getPollingPlaceNumber()))
                {
                    if(vote.getPoliticalParties().contains(fiscal.getPoliticalParty()))
                    {
                        fiscal.notifyVote(vote);
                    }
                }
            }
        }
    }

}
