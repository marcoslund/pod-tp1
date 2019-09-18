package ar.edu.itba.pod.server;

import ar.edu.itba.pod.services.Servant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        LOGGER.info("tpe1 Server Starting ...");
        System.setProperty("java.rmi.server.hostname", "localhost");

        try {
            final Servant servant = new Servant();
            final Remote remote = UnicastRemoteObject.exportObject(servant, 0);
            final Registry registry = LocateRegistry.getRegistry();
            registry.rebind("admin-service", remote);
            registry.rebind("monitor-service", remote);
            registry.rebind("query-service", remote);
            registry.rebind("voting-service", remote);
            System.out.println("Service bound");
        } catch(RemoteException e) {
            System.err.println("Error instantiating servant.");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
