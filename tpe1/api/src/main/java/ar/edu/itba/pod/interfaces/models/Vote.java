package ar.edu.itba.pod.interfaces.models;

import ar.edu.itba.pod.interfaces.PoliticalParty;

import java.util.Objects;
import java.util.Optional;

public class Vote {

    private final PoliticalParty mainChoice;
    private final Optional<PoliticalParty> secondChoice;
    private final Optional<PoliticalParty> thirdChoice;

    public Vote(final PoliticalParty mainChoice) {
        this.mainChoice = mainChoice;
        this.secondChoice = Optional.empty();
        this.thirdChoice = Optional.empty();
    }

    public Vote(final PoliticalParty mainChoice, final PoliticalParty secondChoice) {
        this.mainChoice = mainChoice;
        this.secondChoice = Optional.of(secondChoice);
        this.thirdChoice = Optional.empty();
    }

    public Vote(final PoliticalParty mainChoice, final PoliticalParty secondChoice,
                final PoliticalParty thirdChoice) {
        this.mainChoice = mainChoice;
        this.secondChoice = Optional.of(secondChoice);
        this.thirdChoice = Optional.of(thirdChoice);
    }

}
