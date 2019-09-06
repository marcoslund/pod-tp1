package ar.edu.itba.pod.interfaces.services;

import ar.edu.itba.pod.interfaces.QueryMode;
import ar.edu.itba.pod.interfaces.State;
import ar.edu.itba.pod.interfaces.exceptions.IllegalElectionStateException;
import ar.edu.itba.pod.interfaces.models.QueryResult;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface QueryService extends Remote {

    List<QueryResult> queryNationResults()
            throws RemoteException, IllegalElectionStateException;

    List<QueryResult> queryStateResults(State state)
            throws RemoteException, IllegalElectionStateException;

    List<QueryResult> queryTableResults(long tableNumber)
            throws RemoteException, IllegalElectionStateException;

}
