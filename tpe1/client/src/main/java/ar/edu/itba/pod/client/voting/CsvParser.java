package ar.edu.itba.pod.client.voting;

import ar.edu.itba.pod.interfaces.PoliticalParty;
import ar.edu.itba.pod.interfaces.State;
import ar.edu.itba.pod.interfaces.models.Vote;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class CsvParser {

    private static final String SEPARATOR = ";";
    private static final String PARTIES_SEPARATOR = ",";
    private static final int POLLING_PLACE_LINE = 0;
    private static final int STATE_LINE = 1;
    private static final int PARTIES_LINE = 2;

    static List<Vote> parseVoteCsv(final String path)
            throws IOException, InvalidCsvException {
        List<Vote> votes = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            for(int i = 0; (line = br.readLine()) != null; i++) {
                String[] voteStr = line.split(SEPARATOR);
                printLine(voteStr);
                try {
                    int pollingPlaceNumber = Integer.parseInt(voteStr[POLLING_PLACE_LINE]);
                    System.out.println("pollingPlaceNumber: " + pollingPlaceNumber);
                    State state = State.valueOf(voteStr[STATE_LINE]);
                    System.out.println("State: " + state);
                    Vote vote = new Vote(
                            pollingPlaceNumber,
                            state,
                            findParties(voteStr[PARTIES_LINE].split(PARTIES_SEPARATOR))
                    );
                    System.out.println("vote: " + vote);
                    votes.add(vote);
                } catch(IllegalArgumentException e) {
                    throw new InvalidCsvException(
                            "Invalid CSV file. Error parsing line " + i, i, line);
                }
            }
        }
        return votes;
    }

    private static List<PoliticalParty> findParties(final String[] parties) {
        return Arrays.asList(parties)
                .stream()
                .map(CsvParser::partyFromString)
                .collect(Collectors.toList());
    }

    private static PoliticalParty partyFromString(final String str) {
        return PoliticalParty.valueOf(str.toUpperCase());
    }

    private static void printLine(final String[] line) {
        System.out.println("Line:");
        for(String str : line)
            System.out.print(str + " ");
        System.out.print('\n');
    }

}
