package ar.edu.itba.pod.interfaces.services;

import ar.edu.itba.pod.interfaces.ElectionState;
import ar.edu.itba.pod.interfaces.exceptions.IllegalElectionStateException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AdministrationService extends Remote {

    void startElection() throws RemoteException, IllegalElectionStateException;

    void endElection() throws RemoteException, IllegalElectionStateException;

    ElectionState queryElection() throws RemoteException;

}
