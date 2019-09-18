package ar.edu.itba.pod.model;

import ar.edu.itba.pod.interfaces.PoliticalParty;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class VoteProportion {

    public static final int MAX_PARTIES = 3;
    private final List<Optional<PoliticalParty>> parties;
    private double chunkPercentage;

    public VoteProportion(final List<Optional<PoliticalParty>> parties, final double chunkPercentage) {
        this.parties = parties;
        this.chunkPercentage = chunkPercentage;
    }

    public List<Optional<PoliticalParty>> getParties() {
        return parties;
    }

    public double getChunkPercentage() {
        return chunkPercentage;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(!(o instanceof VoteProportion))
            return false;
        VoteProportion other = (VoteProportion) o;
        return parties.equals(other.getParties()) &&
                Double.compare(chunkPercentage, other.getChunkPercentage()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(parties, chunkPercentage);
    }
}
