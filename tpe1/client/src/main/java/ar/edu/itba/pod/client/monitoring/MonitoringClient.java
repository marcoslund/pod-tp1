package ar.edu.itba.pod.client.monitoring;

import ar.edu.itba.pod.client.Client;
import ar.edu.itba.pod.interfaces.PoliticalParty;
import ar.edu.itba.pod.interfaces.models.Vote;
import ar.edu.itba.pod.interfaces.services.MonitoringService;
import ar.edu.itba.pod.interfaces.services.RemoteMonitoringClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MonitoringClient extends Client implements Serializable, RemoteMonitoringClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringClient.class);
    private static MonitoringService service;
    private static Integer pollingPlaceNumber;

    private static PoliticalParty politicalParty;

    public MonitoringClient(PoliticalParty politicalParty) {
        this.setPoliticalParty(politicalParty);

        try {
            UnicastRemoteObject.exportObject(this, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public PoliticalParty getPoliticalParty() {
        return politicalParty;
    }

    public static void setPoliticalParty(PoliticalParty politicalParty) {
        MonitoringClient.politicalParty = politicalParty;
    }

    private static MonitoringClient getInstance(){
        return new MonitoringClient(politicalParty);
    }

    public static void main(String[] args) throws Exception {
        LOGGER.info("tpe1 MonitoringClient Starting ...");
        if(!parseArguments())
            System.exit(1);
        System.out.println("serverAddr: " + serverAddress + "; pollingPlace: " + pollingPlaceNumber
            + "; party: " + politicalParty);

        service = (MonitoringService) getRemoteService("monitor-service");

        service.registerFiscal(getInstance(), pollingPlaceNumber);

        while(true);
    }

    private static boolean parseArguments() {
        boolean success;
        success = parseServerAddress(LOGGER);
        success &= parsePollingPlaceNumber();
        success &= parsePartyName();
        return success;
    }

    private static boolean parsePollingPlaceNumber() {
        String number = System.getProperty("id");
        pollingPlaceNumber = stringToInt(number);
        if(pollingPlaceNumber == null) {
            LOGGER.error("pollingPlaceNumber must be an Integer (is {})", number);
            return false;
        }
        return true;
    }

    private static boolean parsePartyName() {
        String partyName = System.getProperty("party");
        try {
            politicalParty = PoliticalParty.valueOf(partyName.toUpperCase());
        } catch (NullPointerException | IllegalArgumentException e) {
            LOGGER.error("Invalid political party name passed as argument. " +
                    "Possible values: {} (was {})", PoliticalParty.values(), partyName);
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null || o.getClass() != this.getClass()) return false;
        MonitoringClient other = (MonitoringClient) o;
        return other.getPoliticalParty().equals(politicalParty);
    }

    @Override
    public int hashCode() {
        return politicalParty.hashCode();
    }

    @Override
    public void notifyVote(Vote vote) {
        System.out.println("Vote notified");
    }
}
