package ar.edu.itba.pod.client.voting;

import ar.edu.itba.pod.client.Client;
import ar.edu.itba.pod.interfaces.exceptions.IllegalElectionStateException;
import ar.edu.itba.pod.interfaces.models.Vote;
import ar.edu.itba.pod.interfaces.services.VotingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

public class VotingClient extends Client {
    private static final Logger LOGGER = LoggerFactory.getLogger(VotingClient.class);
    private static VotingService service;
    private static String filename;
    private static List<Vote> votes;

    private static final String INVALID_STATE_ERROR = "The election is not active. Could not vote.";

    public static void main(String[] args) throws Exception {
        LOGGER.info("tpe1 VotingClient Starting ...");
        if(!parseArguments())
            System.exit(1);

        System.out.println("serverAddr: " + serverAddress + "; votesPath: " + filename);

        service = (VotingService) getRemoteService("voting-service");
        parseCsv();
        vote();
    }

    private static boolean parseArguments() {
        boolean success;
        success = parseServerAddress(LOGGER);
        success &= parseFilename();
        return success;
    }

    private static boolean parseFilename() {
        filename = System.getProperty("votesPath");
        if(filename == null) {
            LOGGER.error("votes path must be present");
            return false;
        }
        return true;
    }

    private static void parseCsv() {
        try {
            votes = CsvParser.parseVoteCsv(filename);
        } catch(IOException e) {
            LOGGER.error("Error reading {} file.", filename);
            e.printStackTrace();
            System.exit(1);
        } catch(InvalidCsvException e) {
            LOGGER.error("Error reading {} file at line {} (got {})", filename, e.lineNumber, e.line);
            System.exit(1);
        }
    }

    private static void vote() throws RemoteException {
        try {
            service.vote(votes);
            LOGGER.info("{} votes registered.", votes.size());
        } catch(IllegalElectionStateException e) {
            LOGGER.error(INVALID_STATE_ERROR);
        }
    }
}
