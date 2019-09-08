package ar.edu.itba.pod.client.voting;

import ar.edu.itba.pod.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VotingClient extends Client {
    private static final Logger LOGGER = LoggerFactory.getLogger(VotingClient.class);
    private static String filename;

    public static void main(String[] args) {
        LOGGER.info("tpe1 VotingClient Starting ...");
        if(!parseArguments())
            System.exit(1);
        System.out.println("serverAddr: " + serverAddress + "; votesPath: " + filename);
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
}
