package ar.edu.itba.pod.interfaces.models;

import ar.edu.itba.pod.interfaces.PoliticalParty;

public class QueryResult {

    private final PoliticalParty politicalParty;
    private final double percentage;

    public QueryResult(final PoliticalParty politicalParty, final double percentage) {
        this.politicalParty = politicalParty;
        this.percentage = percentage;
    }
}
