package ar.edu.itba.pod.interfaces.services;

import ar.edu.itba.pod.interfaces.models.Vote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteMonitoringClient extends Remote {

    void notifyVote(Vote vote) throws RemoteException;
}
