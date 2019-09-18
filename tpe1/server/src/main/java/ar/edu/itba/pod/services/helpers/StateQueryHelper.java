package ar.edu.itba.pod.services.helpers;

import ar.edu.itba.pod.interfaces.PoliticalParty;
import ar.edu.itba.pod.interfaces.models.QueryResult;
import ar.edu.itba.pod.interfaces.models.Vote;
import ar.edu.itba.pod.model.PercentageChunk;
import ar.edu.itba.pod.model.VoteProportion;

import java.util.*;
import java.util.stream.Collectors;

public class StateQueryHelper {

    public static final int REPS_PER_STATE = 5;

    public static boolean foundWinners(final SortedSet<QueryResult> results) {
        return results.size() == REPS_PER_STATE;
    }

    public static boolean candidateHasSurplus(final QueryResult result) {
        return Double.compare(result.getPercentage(), getWinningPercentage()) > 0;
    }

    private static boolean candidateHasSurplus(
            final Map<PoliticalParty, List<PercentageChunk>> partyChunks,
            final PoliticalParty party) {
        return Double.compare(
                partyChunks.get(party).stream().mapToDouble(PercentageChunk::getPercentage).sum(),
                getWinningPercentage()) > 0;
    }

    private static double getWinningPercentage() {
        return 100 / (double) REPS_PER_STATE;
    }

    public static SortedSet<QueryResult> getResults(
            final Map<PoliticalParty, List<PercentageChunk>> partyChunks) {
        final SortedSet<QueryResult> results = new TreeSet<>();
        partyChunks.forEach((party, chunks) ->
                results.add(
                        new QueryResult(party, chunks.stream()
                                .mapToDouble(PercentageChunk::getPercentage).sum())
                )
        );
        return results;
    }

    public static Map<PoliticalParty, List<PercentageChunk>> initializePartyChunks(
            final Map<PoliticalParty, List<Vote>> votes) {

        final int stateVoteQty = votes.values().stream().mapToInt(List::size).sum();
        final Map<PoliticalParty, List<PercentageChunk>> partyChunks = new HashMap<>();

        votes.forEach((mainChoice, votesList) -> {
            final int totalVotes = votesList.size();
            final Set<VoteProportion> voteProportions = new HashSet<>();
            Map<Optional<PoliticalParty>, Map<Optional<PoliticalParty>, List<Vote>>> secondThirdCombination =
                votesList.stream().collect(
                    Collectors.groupingBy(Vote::getSecondChoice, Collectors.groupingBy(
                        Vote::getThirdChoice
                        )
                    )
                );

            secondThirdCombination.forEach((secondChoice, thirdChoiceMap) -> {
                thirdChoiceMap.forEach((thirdChoice, groupedVotesList) -> {
                    final List<Optional<PoliticalParty>> choiceComb = new ArrayList<>();
                    choiceComb.add(secondChoice);
                    choiceComb.add(thirdChoice);
                    final double percentage = 100 * groupedVotesList.size() / (double) totalVotes;
                    voteProportions.add(new VoteProportion(choiceComb, percentage));
                });
            });

            final List<PercentageChunk> chunks = new ArrayList<>();
            final PercentageChunk firstChunk = new PercentageChunk(mainChoice, 1,
                    votesList.size() * 100 / (double) stateVoteQty, voteProportions);
            chunks.add(firstChunk);
            partyChunks.put(mainChoice, chunks);
        });

        return partyChunks;
    }

    public static SortedSet<QueryResult> redistributeSurpluses(
            final Map<PoliticalParty, List<PercentageChunk>> partyChunks,
            SortedSet<QueryResult> results) {
        while(StateQueryHelper.candidateHasSurplus(results.first())) {
            results = StateQueryHelper.redistributeSurplusVotes(partyChunks, results.first());
        }
        return results;
    }

    private static SortedSet<QueryResult> redistributeSurplusVotes(
            final Map<PoliticalParty, List<PercentageChunk>> partyChunks,
            final QueryResult surplusResult) {

        final List<PercentageChunk> chunkList = partyChunks.get(surplusResult.getPoliticalParty());
        double surplusPercentage = surplusResult.getPercentage() - getWinningPercentage();

        while(candidateHasSurplus(partyChunks, surplusResult.getPoliticalParty())) {
            final PercentageChunk chunkToDistribute = chunkList.get(chunkList.size() - 1);

            final double currentSurplusPercentage;
            if(Double.compare(surplusPercentage, chunkToDistribute.getPercentage()) < 0) {
                chunkToDistribute.setPercentage(chunkToDistribute.getPercentage() - surplusPercentage);
                currentSurplusPercentage = surplusPercentage;
            } else {
                currentSurplusPercentage = chunkToDistribute.getPercentage();
            }

            distributeChunk(chunkToDistribute, partyChunks, currentSurplusPercentage);

            surplusPercentage -= chunkToDistribute.getPercentage();
        }

        return getResults(partyChunks);
    }

    public static SortedSet<QueryResult> distributeLeastPopularChunks(
            final Map<PoliticalParty, List<PercentageChunk>> partyChunks,
            final SortedSet<QueryResult> results) {
        partyChunks.get(QueryHelper.getLeastPopularCandidate(results)).forEach(chunk -> {
            distributeChunk(chunk, partyChunks, chunk.getPercentage());
        });
        partyChunks.remove(QueryHelper.getLeastPopularCandidate(results));
        return getResults(partyChunks);
    }

    private static void distributeChunk(final PercentageChunk chunk,
                                        final Map<PoliticalParty, List<PercentageChunk>> partyChunks,
                                        final double percentageToDistribute) {
        Map<Optional<PoliticalParty>, List<VoteProportion>> splitFragments =
                chunk.getProportions().stream()
                        .collect(Collectors.groupingBy(
                                VoteProportion::getNextChoice
                        ));

        splitFragments.forEach((partyOpt, props) -> {
            partyOpt.ifPresent(party -> {
                if(partyChunks.containsKey(party))
                    partyChunks.get(party).add(generateChunk(props, percentageToDistribute,
                            party, chunk));
            });
        });
    }

    private static PercentageChunk generateChunk(
            final List<VoteProportion> props,
            final double currentSurplusPercentage,
            final PoliticalParty party,
            final PercentageChunk chunkToDistribute) {

        Set<VoteProportion> newProps = new HashSet<>();
        double proportionSum =
                props.stream().mapToDouble(VoteProportion::getChunkPercentage).sum();

        for(VoteProportion vp : props) {
            List<Optional<PoliticalParty>> nextChoices;
            int fromIndex = 1;
            int toIndex = vp.getParties().size();
            if(toIndex > fromIndex)
                nextChoices = vp.getParties().subList(fromIndex, toIndex);
            else
                nextChoices = Collections.emptyList();
            double newVotePropPercentage = vp.getChunkPercentage() * 100 / proportionSum;
            newProps.add(new VoteProportion(nextChoices, newVotePropPercentage));
        }

        double newChunkPercentage = currentSurplusPercentage * proportionSum / 100;

        return new PercentageChunk(party, chunkToDistribute.getCurrentRank() + 1,
                newChunkPercentage, newProps);
    }
}
