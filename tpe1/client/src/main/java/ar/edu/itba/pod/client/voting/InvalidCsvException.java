package ar.edu.itba.pod.client.voting;

class InvalidCsvException extends Exception {
    protected int lineNumber;
    protected String line;

    public InvalidCsvException(String msg, int lineNumber, String line) {
        super(msg);
        this.lineNumber = lineNumber;
        this.line = line;
    }
}
