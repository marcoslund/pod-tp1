package ar.edu.itba.pod.interfaces.services;

import ar.edu.itba.pod.interfaces.State;
import ar.edu.itba.pod.interfaces.exceptions.IllegalElectionStateException;
import ar.edu.itba.pod.interfaces.models.QueryResult;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.SortedSet;

public interface QueryService extends Remote {

    SortedSet<QueryResult> queryNationResults()
            throws RemoteException, IllegalElectionStateException;

    SortedSet<QueryResult> queryStateResults(State state)
            throws RemoteException, IllegalElectionStateException;

    SortedSet<QueryResult> queryTableResults(long tableNumber)
            throws RemoteException, IllegalElectionStateException;

}
