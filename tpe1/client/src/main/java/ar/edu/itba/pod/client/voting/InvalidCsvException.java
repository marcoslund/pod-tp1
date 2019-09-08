package ar.edu.itba.pod.client.voting;

public class InvalidCsvException extends Exception {
    public int lineNumber;

    public InvalidCsvException(String msg, int lineNumber) {
        super(msg);
        this.lineNumber = lineNumber;
    }
}
