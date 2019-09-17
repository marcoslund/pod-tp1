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

    private static final boolean HAS_BEEN_REPROCESSED = true;

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

        if(!electionStartedAndNotFinished()) {
            throw new IllegalElectionStateException("Election not active.");
        }

        SortedSet<QueryResult> results = new TreeSet<>();
        final SortedSet<QueryResult> auxResults = results;
        final int voteQty;
        final Map<PoliticalParty, List<Vote>> votes;
        final Map<Vote, Integer> currentVotesRank = new HashMap<>();

        synchronized (stateVotes) {
             votes = stateVotes.values().stream()
                    .map(Map::values)
                    .flatMap(Collection::parallelStream)
                    .flatMap(List::parallelStream)
                    .collect(Collectors.groupingBy(Vote::getMainChoice));
            voteQty = voteCount;
        }

        votes.forEach((k, votesList) -> {
            auxResults.add(
                    new QueryResult(k, votesList.size() * 100 / (double) voteQty));
            votesList.forEach(vote -> currentVotesRank.put(vote, 1));
        });

        while(!foundWinner(results)) {

            // Reprocess all the least popular candidate's votes
            votes.get(getLeastPopularCandidate(results)).forEach(
                v -> {
                    while(!reprocessVote(v, votes, currentVotesRank));
                }
            );
            votes.remove(getLeastPopularCandidate(results));
            results.remove(results.last());

            // Update percentages based on new vote distribution
            results = updateResults(results, votes, getRemainingVoteQty(currentVotesRank));
        }
        return results;
    }

    private boolean foundWinner(final SortedSet<QueryResult> results) {
        return Double.compare(results.first().getPercentage(), 50) >= 0;
    }

    private int getRemainingVoteQty(Map<Vote, Integer> currentVotesRank) {
        return currentVotesRank.size();
    }

    private SortedSet<QueryResult> updateResults(final SortedSet<QueryResult> results,
                                                 final Map<PoliticalParty, List<Vote>> votes,
                                                 final int voteCount) {
        SortedSet<QueryResult> tmp = new TreeSet<>();
        results.forEach(result -> tmp.add(new QueryResult(
                result.getPoliticalParty(),
                votes.get(result.getPoliticalParty()).size() * 100
                        / (double) voteCount)
        ));
        return tmp;
    }

    private PoliticalParty getLeastPopularCandidate(SortedSet<QueryResult> results) {
        return results.last().getPoliticalParty();
    }

    private boolean reprocessVote(final Vote v,
                                  final Map<PoliticalParty, List<Vote>> votes,
                                  final Map<Vote, Integer> currentVotesRank) {
        currentVotesRank.put(v, currentVotesRank.get(v) + 1);
        if(currentVotesRank.get(v) > Vote.PARTY_COUNT) {
            currentVotesRank.remove(v);
            return HAS_BEEN_REPROCESSED; // All choices have been used
        } else {
            Optional<PoliticalParty> nextChoice = v.getChoice(currentVotesRank.get(v));
            if(nextChoice.isPresent()) {
                if(votes.containsKey(nextChoice.get())) {
                    votes.get(nextChoice.get()).add(v);
                    return HAS_BEEN_REPROCESSED; // Valid next choice has been used
                } else
                    return !HAS_BEEN_REPROCESSED; // Next candidate has been eliminated
            } else {
                currentVotesRank.remove(v);
                return HAS_BEEN_REPROCESSED; // Empty next choice (no more choices left)
            }
        }
    }

    @Override
    public SortedSet<QueryResult> queryStateResults(final State state)
            throws RemoteException, IllegalElectionStateException {

        if(!electionStartedAndNotFinished()) {
            throw new IllegalElectionStateException("Election not active.");
        }
        return null;
    }

    @Override
    public SortedSet<QueryResult> queryTableResults(final long tableNumber)
            throws RemoteException, IllegalElectionStateException,
                PollingPlaceNotFoundException {

        if(!electionStartedAndNotFinished()) {
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
