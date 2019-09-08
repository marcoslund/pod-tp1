package ar.edu.itba.pod.client.voting;

import ar.edu.itba.pod.client.Client;
import ar.edu.itba.pod.interfaces.models.Vote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class VotingClient extends Client {
    private static final Logger LOGGER = LoggerFactory.getLogger(VotingClient.class);
    private static String filename;
    private static List<Vote> votes;

    public static void main(String[] args) {
        LOGGER.info("tpe1 VotingClient Starting ...");
        if(!parseArguments())
            System.exit(1);

        System.out.println("serverAddr: " + serverAddress + "; votesPath: " + filename);

        parseCsv();
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
            LOGGER.error("Error reading {} file at line {}", filename, e.lineNumber);
            System.exit(1);
        }
    }
}
