package ar.edu.itba.pod.interfaces.services;

import ar.edu.itba.pod.interfaces.PoliticalParty;
import ar.edu.itba.pod.interfaces.models.Vote;

import java.rmi.Remote;

public interface RemoteMonitoringClient extends Remote {

    public void notifyVote(Vote vote);

    public PoliticalParty getPoliticalParty();
}
