package ar.edu.itba.pod.client.voting;

import ar.edu.itba.pod.interfaces.PoliticalParty;
import ar.edu.itba.pod.interfaces.State;
import ar.edu.itba.pod.interfaces.models.Vote;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CsvParser {

    private static final String SEPARATOR = ",";

    public static List<Vote> parseVoteCsv(final String path)
            throws IOException, InvalidCsvException {
        List<Vote> votes = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;

            for(int i = 0; (line = br.readLine()) != null; i++) {
                String[] voteStr = line.split(SEPARATOR);
                try {
                    int pollingPlaceNumber = Integer.parseInt(voteStr[0]);
                    State state = State.valueOf(voteStr[1]);
                    Vote vote = new Vote(
                            pollingPlaceNumber,
                            state,
                            findParties(voteStr)
                    );
                    votes.add(vote);
                } catch(IllegalArgumentException e) {
                    throw new InvalidCsvException(
                            "Invalid CSV file. Error parsing line " + i, i);
                }
            }
        }
        return votes;
    }

    private static List<PoliticalParty> findParties(final String[] line) {
        return Arrays.asList(line).subList(2, line.length)
                .stream()
                .map(CsvParser::partyFromString)
                .collect(Collectors.toList());
    }

    private static PoliticalParty partyFromString(String str) {
        return PoliticalParty.valueOf(str);
    }

}
