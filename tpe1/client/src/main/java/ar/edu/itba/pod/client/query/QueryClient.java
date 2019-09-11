package ar.edu.itba.pod.client.query;

import ar.edu.itba.pod.client.Client;
import ar.edu.itba.pod.interfaces.State;
import ar.edu.itba.pod.interfaces.services.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryClient extends Client {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryClient.class);
    private static QueryService service;
    private static State state;
    private static Integer pollingPlaceNumber;
    private static String filename;
    private static boolean parsedPollingPlaceNumber = false;

    public static void main(String[] args) throws Exception {
        LOGGER.info("tpe1 QueryClient Starting ...");
        if(!parseArguments())
            System.exit(1);
        System.out.println("servAdd: " + serverAddress + "; state: " + state +
                "; id: " + pollingPlaceNumber + "; filename: " + filename);

        service = (QueryService) getRemoteService("query-service");
    }

    private static boolean parseArguments() {
        boolean success;
        success = parseServerAddress(LOGGER);
        success &= (parseState() ^ parsePollingNumber());
        success &= parseFilename();
        if(!success) {
            if (state != null && parsedPollingPlaceNumber) {
                LOGGER.error("Either the state or the pollingPlaceNumber (id) must be passed.");
            }
        }
        return success;
    }

    private static boolean parseState() {
        String stateName = System.getProperty("state");
        try {
            state = State.valueOf(stateName.toUpperCase());
        } catch(NullPointerException | IllegalArgumentException e) {
            LOGGER.error("State is invalid. Must be in {} (was {})", State.values(), stateName);
            return false;
        }
        return true;
    }

    private static boolean parsePollingNumber() {
        String number = System.getProperty("id");
        if(number != null)
            parsedPollingPlaceNumber = true;
        pollingPlaceNumber = stringToInt(number);
        if(pollingPlaceNumber == null) {
            LOGGER.error("Polling number (id) must be an integer (was {})", number);
            return false;
        }
        return true;
    }

    private static boolean parseFilename() {
        filename = System.getProperty("outPath");
        if(filename == null) {
            LOGGER.error("Output filepath must be present.");
            return false;
        }
        return true;
    }
}
