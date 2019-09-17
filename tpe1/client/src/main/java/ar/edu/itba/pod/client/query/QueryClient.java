package ar.edu.itba.pod.client.query;

import ar.edu.itba.pod.client.Client;
import ar.edu.itba.pod.interfaces.State;
import ar.edu.itba.pod.interfaces.exceptions.IllegalElectionStateException;
import ar.edu.itba.pod.interfaces.exceptions.PollingPlaceNotFoundException;
import ar.edu.itba.pod.interfaces.models.QueryResult;
import ar.edu.itba.pod.interfaces.services.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.SortedSet;

public class QueryClient extends Client {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryClient.class);
    private static QueryService service;
    private static State state;
    private static Integer pollingPlaceNumber;
    private static String filename;
    private static boolean parsedPollingPlaceNumber = false;
    private static boolean parsedState = false;
    private static SortedSet<QueryResult> votes;

    private static final String INVALID_STATE_ERROR = "The election is not active. Could not query votes.";
    private static final String POLLING_PLACE_ERROR = "Polling place number does not exist.";
    private static final String FILE_COLUMN_MAPPING = "Porcentaje;Partido\n";

    public static void main(String[] args) throws Exception {
        LOGGER.info("tpe1 QueryClient Starting ...");
        if(!parseArguments())
            System.exit(1);
        System.out.println("servAdd: " + serverAddress + "; state: " + state +
                "; id: " + pollingPlaceNumber + "; filename: " + filename);

        service = (QueryService) getRemoteService("query-service");

        queryElection();
    }

    private static boolean parseArguments() {
        if(!parseServerAddress(LOGGER) || !parseFilename()) {
            return false;
        }
        boolean parseStateSuccess = parseState();
        boolean parsePollingNumberSuccess = parsePollingNumber();
        if (parsedState && parsedPollingPlaceNumber) {
            LOGGER.error("Either the state or the pollingPlaceNumber (id) must be passed.");
            return false;
        } else if(!parsedState && !parsedPollingPlaceNumber) {
            return true;
        } else if((parsedState && !parseStateSuccess)
                || (parsedPollingPlaceNumber && !parsePollingNumberSuccess)) {
            return false;
        }
        return true;
    }

    private static boolean parseState() {
        String stateName = System.getProperty("state");
        try {
            state = State.valueOf(stateName.toUpperCase());
        } catch(NullPointerException e) {
            return false;
        } catch(IllegalArgumentException e) {
            LOGGER.error("State is invalid. Must be in {} (was {}).", State.values(), stateName);
            parsedState = true;
            return false;
        }
        parsedState = true;
        return true;
    }

    private static boolean parsePollingNumber() {
        String number = System.getProperty("id");
        if(number != null)
            parsedPollingPlaceNumber = true;
        pollingPlaceNumber = stringToInt(number);
        if(pollingPlaceNumber == null) {
            LOGGER.error("Polling number (id) must be an integer (was {}).", number);
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

    private static boolean isNationalQuery() {
        return !parsedState && !parsedPollingPlaceNumber;
    }

    private static boolean isStateQuery() {
        return parsedState && !parsedPollingPlaceNumber;
    }

    private static void queryElection() throws RemoteException {
        try {
            if(isNationalQuery()) {
                LOGGER.debug("Running national query...");
                votes = service.queryNationResults();
            } else if(isStateQuery()) {
                LOGGER.debug("Running state query...");
                votes = service.queryStateResults(state);
            } else {
                LOGGER.debug("Running table query...");
                votes = service.queryTableResults(pollingPlaceNumber);
            }

            writeVotesToCsv();
        } catch(IllegalElectionStateException e) {
            LOGGER.error("{}", INVALID_STATE_ERROR);
        } catch(PollingPlaceNotFoundException e) {
            LOGGER.error("{}", POLLING_PLACE_ERROR);
        }
    }

    private static void writeVotesToCsv() {
        File file = new File(filename);
        try(FileWriter fw = new FileWriter(file)) {
            file.createNewFile();
            fw.write(FILE_COLUMN_MAPPING);
            for(QueryResult result : votes) {
                writeResultToFile(fw, result);
            }
        } catch(IOException e) {
            LOGGER.error("Could not write to file.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void writeResultToFile(final FileWriter fw, final QueryResult result)
        throws IOException {
        fw.write(String.format("%g", result.getPercentage())
                + "%;" + result.getPoliticalParty() + "\n");
    }
}
