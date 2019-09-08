package ar.edu.itba.pod.client.monitoring;

import ar.edu.itba.pod.client.Client;
import ar.edu.itba.pod.interfaces.PoliticalParty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitoringClient extends Client {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringClient.class);
    private static Integer pollingPlaceNumber;
    private static PoliticalParty politicalParty;

    public static void main(String[] args) {
        LOGGER.info("tpe1 MonitoringClient Starting ...");
        boolean success;
        success = parseServerAddress(LOGGER);
        success &= parsePollingPlaceNumber();
        success &= parsePartyName();
        if(!success)
            System.exit(1);
        System.out.println("serverAddr: " + serverAddress + "; pollingPlace: " + pollingPlaceNumber
            + "; party: " + politicalParty);
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
}
