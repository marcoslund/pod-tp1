package ar.edu.itba.pod.services.helpers;

import ar.edu.itba.pod.interfaces.PoliticalParty;
import ar.edu.itba.pod.interfaces.models.QueryResult;
import ar.edu.itba.pod.interfaces.models.Vote;

import java.util.*;

public class NationalQueryHelper {

    public static boolean foundWinner(final SortedSet<QueryResult> results) {
        return results.size() == 1 || Double.compare(results.first().getPercentage(), 50) >= 0;
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

}
