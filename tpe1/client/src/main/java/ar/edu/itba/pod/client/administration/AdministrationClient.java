package ar.edu.itba.pod.client.administration;

import ar.edu.itba.pod.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdministrationClient extends Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdministrationClient.class);
    private static AdministratorAction action;

    public static void main(String[] args) {
        LOGGER.info("tpe1 AdministrationClient Starting ...");
        if(!parseArguments())
            System.exit(1);
        System.out.println("serverAddr: " + serverAddress + "; action: " + action);
    }

    private static boolean parseArguments() {
        boolean success;
        success = parseServerAddress(LOGGER);
        success &= parseAction();
        return success;
    }

    private static boolean parseAction() {
        String actionString = System.getProperty("action");
        try {
            action = AdministratorAction.valueOf(actionString.toUpperCase());
        } catch(NullPointerException | IllegalArgumentException e) {
            LOGGER.error("Invalid action name passed as argument. " +
                    "Possible values: {} (was {})", AdministratorAction.values(), actionString);
            System.exit(1);
            return false;
        }
        return true;
    }
}
