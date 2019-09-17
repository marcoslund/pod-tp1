package ar.edu.itba.pod.interfaces.models;

import ar.edu.itba.pod.interfaces.PoliticalParty;

import java.io.Serializable;
import java.util.Objects;

public class QueryResult implements Comparable<QueryResult>, Serializable {

    private final PoliticalParty politicalParty;
    private double percentage;

    public QueryResult(final PoliticalParty politicalParty, final double percentage) {
        this.politicalParty = politicalParty;
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        return "Party: " + politicalParty + "; "
                + String.format("%g", percentage) + "%";
    }

    @Override
    public boolean equals(Object o) {
        if(o == this)
            return true;
        if(!(o instanceof QueryResult))
            return false;
        QueryResult qr = (QueryResult) o;
        return this.politicalParty.equals(qr.getPoliticalParty())
                && Double.compare(this.percentage, qr.getPercentage()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(politicalParty, percentage);
    }

    @Override
    public int compareTo(QueryResult o) {
        int cmp = Double.compare(o.percentage, this.percentage);
        if(cmp != 0)
            return cmp;
        return this.politicalParty.toString().compareTo(
                o.getPoliticalParty().toString());
    }

    public PoliticalParty getPoliticalParty() {
        return politicalParty;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
