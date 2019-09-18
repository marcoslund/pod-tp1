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
        return Double.compare(result.getPercentage(), getWinningPercentage()) >= 0;
    }

    private static double getWinningPercentage() {
        return 100 / (double) REPS_PER_STATE;
    }

//    private static long getWinningVoteQty(final int voteQty) {
//        return Math.round(Math.floor(voteQty / (REPS_PER_STATE + 1)) + 1);
//    }

    public static SortedSet<QueryResult> redistributeSurplusVotes(
            final Map<PoliticalParty, List<PercentageChunk>> partyChunks,
            final QueryResult surplusResult) {

        final SortedSet<QueryResult> tmp = new TreeSet<>();
        final List<PercentageChunk> chunkList = partyChunks.get(surplusResult.getPoliticalParty());

        while(candidateHasSurplus(surplusResult)) {
            final PercentageChunk chunkToDistribute = chunkList.get(chunkList.size() - 1);
            double surplusPercentage = surplusResult.getPercentage() - getWinningPercentage();
            final double currentSurplusPercentage = surplusPercentage;
            Map<Optional<PoliticalParty>, List<VoteProportion>> splitFragments =
                    chunkToDistribute.getProportions().stream()
                    .collect(Collectors.groupingBy(
                            VoteProportion::getNextChoice
                    ));

            splitFragments.forEach((partyOpt, props) -> {
                partyOpt.ifPresent(party -> {
                    partyChunks.get(party).add(generateChunk(props, currentSurplusPercentage,
                            party, chunkToDistribute);
                });
            });

            if(Double.compare(surplusPercentage, chunkToDistribute.getPercentage()) < 0) {
                chunkToDistribute.setPercentage(chunkToDistribute.getPercentage() - surplusPercentage);
            }

            //

            surplusPercentage -= chunkToDistribute.getPercentage();
        }


//        Map<PoliticalParty, Integer> nextChoiceVotes = new HashMap<>();
//        for(Vote vote : votes.get(results.first())) {
//            int currentRank = currentVotesRank.get(vote);
//            if((currentRank != 3) && vote.getChoice(currentRank + 1).isPresent()) {
//                PoliticalParty nextChoice = vote.getChoice(currentRank + 1).get();
//                currentVotesRank.put(vote, currentRank + 1);
//                if(votes.containsKey(nextChoice)) {
//                    if (!nextChoiceVotes.containsKey(nextChoice)) {
//                        nextChoiceVotes.put(nextChoice, 1);
//                    } else {
//                        nextChoiceVotes.put(nextChoice, nextChoiceVotes.get(nextChoice) + 1);
//                    }
//                }
//            }
//        }
        // Buscar distribucion de votos
        // Pasar votos
        // Armar nuevos results


//        results.forEach(result -> tmp.add(new QueryResult(
//                result.getPoliticalParty(),
//                votes.get(result.getPoliticalParty()).size() * 100
//                        / (double) voteCount)
//        ));
        return tmp;
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
            int toIndex = props.size();
            if(toIndex > fromIndex)
                nextChoices = vp.getParties().subList(fromIndex, toIndex);
            else
                nextChoices = Collections.emptyList();
            double newVotePropPercentage = vp.getChunkPercentage() * 100 / proportionSum;
            newProps.add(new VoteProportion(nextChoices, newVotePropPercentage));
        }

        double newChunkPercentage = currentSurplusPercentage * proportionSum;

        return new PercentageChunk(party, chunkToDistribute.getCurrentRank() + 1,
                newChunkPercentage, newProps);
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
}
