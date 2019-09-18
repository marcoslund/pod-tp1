package ar.edu.itba.pod.model;

import ar.edu.itba.pod.interfaces.PoliticalParty;

import java.util.Set;

public class PercentageChunk {

    private final PoliticalParty currentParty;
    private int currentRank;
    private double percentage;
    private final Set<VoteProportion> proportions;

    public PercentageChunk(final PoliticalParty party, final int rank, final double percentage,
                           Set<VoteProportion> proportions) {
        this.currentParty = party;
        this.currentRank = rank;
        this.percentage = percentage;
        this.proportions = proportions;
    }

    public int getCurrentRank() {
        return currentRank;
    }

    public double getPercentage() {
        return percentage;
    }

    public Set<VoteProportion> getProportions() {
        return proportions;
    }

    public PoliticalParty getCurrentParty() {
        return currentParty;
    }
}
