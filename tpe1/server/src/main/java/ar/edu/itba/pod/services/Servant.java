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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Servant
        implements AdministrationService, MonitoringService, QueryService, VotingService {

    private static final AtomicBoolean electionStarted = new AtomicBoolean(false);
    private static final AtomicBoolean electionFinished = new AtomicBoolean(false);
    private static Map<State, Map<Long, List<Vote>>> stateVotes;

    public Servant() throws RemoteException {
        stateVotes = new HashMap<>();
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

        return null;
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
        List<Vote> tableVotes = stateVotes.values().stream()
                .map(x -> x.get(tableNumber))
                .filter(Objects::nonNull)
                .findFirst().orElseThrow(PollingPlaceNotFoundException::new);
        System.out.println(tableVotes);
        // Calculate percentage of every party's votes
        Map<PoliticalParty, Double> percentages = tableVotes.stream()
                .collect(Collectors.groupingBy(
                            Vote::getMainChoice,
                        Collectors.averagingDouble(x -> 1)));
        System.out.println(percentages);
        // Add QueryResults to sorted set
        percentages.entrySet().stream()
                .map(x -> new QueryResult(x.getKey(), x.getValue() * 100))
                .forEach(results::add);
        System.out.println(results);
        return results;
    }

    @Override
    public void vote(final List<Vote> votes)
            throws RemoteException, IllegalElectionStateException {
        Objects.requireNonNull(votes);
        if(!electionStartedAndNotFinished()) {
            throw new IllegalElectionStateException("Election not active.");
        }

        for(Vote vote : votes) {
            stateVotes.get(vote.getState())
                    .putIfAbsent(vote.getPollingPlaceNumber(), new ArrayList<>());
            stateVotes.get(vote.getState())
                    .get(vote.getPollingPlaceNumber())
                    .add(vote);
        }

    }

}
