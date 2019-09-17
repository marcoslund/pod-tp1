package ar.edu.itba.pod.services.helpers;

import ar.edu.itba.pod.interfaces.PoliticalParty;
import ar.edu.itba.pod.interfaces.models.QueryResult;
import ar.edu.itba.pod.interfaces.models.Vote;

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

    private static long getWinningVoteQty(final int voteQty) {
        return Math.round(Math.floor(voteQty / (REPS_PER_STATE + 1)) + 1);
    }

    public static SortedSet<QueryResult> redistributeSurplusVotes(
            final Map<PoliticalParty, List<Vote>> votes,
            final SortedSet<QueryResult> results,
            final Map<Vote, Integer> currentVotesRank) {

        SortedSet<QueryResult> tmp = new TreeSet<>();

        Map<PoliticalParty, Integer> nextChoiceVotes = new HashMap<>();
        for(Vote vote : votes.get(results.first())) {
            int currentRank = currentVotesRank.get(vote);
            if((currentRank != 3) && vote.getChoice(currentRank + 1).isPresent()) {
                PoliticalParty nextChoice = vote.getChoice(currentRank + 1).get();
                currentVotesRank.put(vote, currentRank + 1);
                if(votes.containsKey(nextChoice)) {
                    if (!nextChoiceVotes.containsKey(nextChoice)) {
                        nextChoiceVotes.put(nextChoice, 1);
                    } else {
                        nextChoiceVotes.put(nextChoice, nextChoiceVotes.get(nextChoice) + 1);
                    }
                }
            }
        }
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

}
