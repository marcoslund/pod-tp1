package ar.edu.itba.pod.interfaces.services;

import ar.edu.itba.pod.interfaces.exceptions.IllegalElectionStateException;
import ar.edu.itba.pod.interfaces.models.Vote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface VotingService extends Remote {

    void vote(List<Vote> votes) throws RemoteException, IllegalElectionStateException;

}
