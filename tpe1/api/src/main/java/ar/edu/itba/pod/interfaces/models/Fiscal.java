package ar.edu.itba.pod.interfaces.models;

import ar.edu.itba.pod.interfaces.PoliticalParty;

public class Fiscal {
    private PoliticalParty politicalParty;

    public Fiscal(PoliticalParty politicalParty) {
        this.politicalParty = politicalParty;
    }

    public PoliticalParty getPoliticalParty() {
        return politicalParty;
    }

    public void setPoliticalParty(PoliticalParty politicalParty) {
        this.politicalParty = politicalParty;
    }
}
