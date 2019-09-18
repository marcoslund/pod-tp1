package ar.edu.itba.pod.interfaces.models;

import ar.edu.itba.pod.interfaces.PoliticalParty;
import ar.edu.itba.pod.interfaces.State;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Vote implements Serializable {

    public static final int PARTY_COUNT = 3;
    private final long pollingPlaceNumber;
    private final State state;
    private final List<PoliticalParty> parties = new ArrayList<>(PARTY_COUNT);

    public Vote(final long pollingPlaceNumber, final State state, final PoliticalParty mainChoice) {
        this.pollingPlaceNumber = pollingPlaceNumber;
        this.state = state;
        parties.add(mainChoice);
    }

    public Vote(final long pollingPlaceNumber, final State state, final PoliticalParty[] parties) {
        this.pollingPlaceNumber = pollingPlaceNumber;
        this.state = state;
        if(parties.length < 1)
            throw new IllegalArgumentException("Vote must have at least one party.");
        for(int i = 0; i < PARTY_COUNT && i < parties.length; i++) {
            this.parties.add(parties[i]);
        }
    }

    public Vote(final long pollingPlaceNumber, final State state, final List<PoliticalParty> parties) {
        this.pollingPlaceNumber = pollingPlaceNumber;
        this.state = state;
        if(parties.size() < 1)
            throw new IllegalArgumentException("Vote must have at least one party.");
        for(int i = 0; i < PARTY_COUNT && i < parties.size(); i++) {
            this.parties.add(parties.get(i));
        }
    }

    @Override
    public String toString() {
        return "Polling Place No.: " + pollingPlaceNumber +
                "; State: " + state +
                "; Main vote: " + getMainChoice() +
                "; Second vote: " + getSecondChoice().orElse(null) +
                "; Third vote: " + getThirdChoice().orElse(null);
    }

    public long getPollingPlaceNumber() {
        return pollingPlaceNumber;
    }

    public State getState() {
        return state;
    }

    public PoliticalParty getMainChoice() {
        return parties.get(0);
    }

    public Optional<PoliticalParty> getSecondChoice() {
        Optional<PoliticalParty> pp;
        try {
            pp = Optional.of(parties.get(1));
        } catch(IndexOutOfBoundsException e) {
            return Optional.empty();
        }
        return pp;
    }

    public Optional<PoliticalParty> getThirdChoice() {
        Optional<PoliticalParty> pp;
        try {
            pp = Optional.of(parties.get(2));
        } catch(IndexOutOfBoundsException e) {
            return Optional.empty();
        }
        return pp;
    }

    public Optional<PoliticalParty> getChoice(int choiceNumber) {
        if(choiceNumber < 1 || choiceNumber > PARTY_COUNT)
            throw new IllegalArgumentException("Choice number must be between 1 and 3.");
        Optional<PoliticalParty> pp;
        try {
            pp = Optional.of(parties.get(choiceNumber - 1));
        } catch(IndexOutOfBoundsException e) {
            return Optional.empty();
        }
        return pp;
    }

    public List<PoliticalParty> getPoliticalParties()
    {
        return parties;
    }

}
