package ar.edu.itba.pod.client.monitoring;

import ar.edu.itba.pod.interfaces.PoliticalParty;
import ar.edu.itba.pod.interfaces.models.Vote;
import ar.edu.itba.pod.interfaces.services.RemoteMonitoringClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteMonitoringClientImpl implements RemoteMonitoringClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteMonitoringClientImpl.class);
    PoliticalParty politicalParty;
    Integer pollingPlaceNumber;

    public RemoteMonitoringClientImpl(PoliticalParty politicalParty, Integer pollingPlaceNumber) throws RemoteException {
        this.politicalParty = politicalParty;
        this.pollingPlaceNumber = pollingPlaceNumber;
    }

    @Override
    public void notifyVote(Vote vote) throws RemoteException {
        LOGGER.info("New vote for {} on polling place {}", politicalParty, pollingPlaceNumber);
    }
}
