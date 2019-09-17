package ar.edu.itba.pod.services.helpers;

import ar.edu.itba.pod.interfaces.PoliticalParty;
import ar.edu.itba.pod.interfaces.models.QueryResult;
import ar.edu.itba.pod.interfaces.models.Vote;

import java.util.*;

public class NationalQueryHelper {

    private static final boolean HAS_BEEN_REPROCESSED = true;

    public static boolean foundWinner(final SortedSet<QueryResult> results) {
        return Double.compare(results.first().getPercentage(), 50) >= 0;
    }

    public static int getRemainingVoteQty(Map<Vote, Integer> currentVotesRank) {
        return currentVotesRank.size();
    }

    public static SortedSet<QueryResult> updateResults(final SortedSet<QueryResult> results,
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

    public static PoliticalParty getLeastPopularCandidate(SortedSet<QueryResult> results) {
        return results.last().getPoliticalParty();
    }

    public static boolean reprocessVote(final Vote v,
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

}
