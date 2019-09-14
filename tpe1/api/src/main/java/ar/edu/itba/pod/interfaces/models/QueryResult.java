package ar.edu.itba.pod.interfaces.models;

import ar.edu.itba.pod.interfaces.PoliticalParty;

public class QueryResult implements Comparable<QueryResult> {

    private final PoliticalParty politicalParty;
    private final double percentage;

    public QueryResult(final PoliticalParty politicalParty, final double percentage) {
        this.politicalParty = politicalParty;
        this.percentage = percentage;
    }

    @Override
    public int compareTo(QueryResult o) {
        return Double.compare(o.percentage, this.percentage);
    }

    public PoliticalParty getPoliticalParty() {
        return politicalParty;
    }

    public double getPercentage() {
        return percentage;
    }
}
