package ar.edu.itba.pod.interfaces.services;

import ar.edu.itba.pod.interfaces.PoliticalParty;
import ar.edu.itba.pod.interfaces.exceptions.ConflictException;
import ar.edu.itba.pod.interfaces.exceptions.IllegalElectionStateException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MonitoringService extends Remote {

    void registerFiscal(RemoteMonitoringClient monitoringClient, Integer pollingPlaceNumber, PoliticalParty politicalParty)
            throws RemoteException, IllegalElectionStateException, ConflictException;
}
