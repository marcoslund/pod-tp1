package ar.edu.itba.pod.client.monitoring;

import ar.edu.itba.pod.interfaces.PoliticalParty;
import ar.edu.itba.pod.interfaces.models.Vote;
import ar.edu.itba.pod.interfaces.services.RemoteMonitoringClient;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteMonitoringClientImpl implements RemoteMonitoringClient {

    PoliticalParty politicalParty;
    Integer pollingPlaceNumber;

    public RemoteMonitoringClientImpl(PoliticalParty politicalParty, Integer pollingPlaceNumber) throws RemoteException {
        this.politicalParty = politicalParty;
        this.pollingPlaceNumber = pollingPlaceNumber;
    }

    @Override
    public void notifyVote(Vote vote) throws RemoteException {
        System.out.println("Vote notified");
    }
}
