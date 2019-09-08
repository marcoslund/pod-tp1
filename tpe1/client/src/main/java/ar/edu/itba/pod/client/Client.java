package ar.edu.itba.pod.client;

import org.slf4j.Logger;

public abstract class Client {
    protected static String serverAddress;

    protected static boolean parseServerAddress(final Logger logger) {
        serverAddress = System.getProperty("serverAddress");
        if(serverAddress == null) {
            logger.error("serverAddress must be present.");
            return false;
        }
        return true;
    }

    protected static Integer stringToInt(final String str) {
        Integer i = null;
        try {
            i = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
        return i;
    }
}
