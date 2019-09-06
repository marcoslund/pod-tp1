package ar.edu.itba.pod.interfaces.models;

import ar.edu.itba.pod.interfaces.PoliticalParty;

import java.util.Optional;

public class Vote {

    private final PoliticalParty mainChoice;
    private final PoliticalParty secondChoice;
    private final PoliticalParty thirdChoice;

    public Vote(final PoliticalParty mainChoice) {
        this.mainChoice = mainChoice;
        this.secondChoice = this.thirdChoice = null;
    }

    public Vote(final PoliticalParty mainChoice, final PoliticalParty secondChoice) {
        this.mainChoice = mainChoice;
        this.secondChoice = secondChoice;
        this.thirdChoice = null;
    }

    public Vote(final PoliticalParty mainChoice, final PoliticalParty secondChoice,
                final PoliticalParty thirdChoice) {
        this.mainChoice = mainChoice;
        this.secondChoice = secondChoice;
        this.thirdChoice = thirdChoice;
    }

    public PoliticalParty getMainChoice() {
        return mainChoice;
    }

    public Optional<PoliticalParty> getSecondChoice() {
        return Optional.ofNullable(secondChoice);
    }

    public Optional<PoliticalParty> getThirdChoice() {
        return Optional.ofNullable(thirdChoice);
    }
}
