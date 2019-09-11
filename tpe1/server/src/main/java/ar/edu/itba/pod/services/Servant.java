package ar.edu.itba.pod.services;

import ar.edu.itba.pod.interfaces.ElectionState;
import ar.edu.itba.pod.interfaces.PoliticalParty;
import ar.edu.itba.pod.interfaces.State;
import ar.edu.itba.pod.interfaces.exceptions.IllegalElectionStateException;
import ar.edu.itba.pod.interfaces.models.QueryResult;
import ar.edu.itba.pod.interfaces.models.Vote;
import ar.edu.itba.pod.interfaces.services.AdministrationService;
import ar.edu.itba.pod.interfaces.services.MonitoringService;
import ar.edu.itba.pod.interfaces.services.QueryService;
import ar.edu.itba.pod.interfaces.services.VotingService;

import java.rmi.RemoteException;
import java.util.List;

public class Servant
        implements AdministrationService, MonitoringService, QueryService, VotingService {

    public Servant() throws RemoteException {

    }

    @Override
    public void startElection() throws RemoteException, IllegalElectionStateException {

    }

    @Override
    public void endElection() throws RemoteException, IllegalElectionStateException {

    }

    @Override
    public ElectionState queryElection() throws RemoteException {
        return null;
    }

    @Override
    public void registerFiscal(final PoliticalParty politicalParty, final long tableNumber)
            throws RemoteException, IllegalElectionStateException {

    }

    @Override
    public List<QueryResult> queryNationResults()
            throws RemoteException, IllegalElectionStateException {
        return null;
    }

    @Override
    public List<QueryResult> queryStateResults(final State state)
            throws RemoteException, IllegalElectionStateException {
        return null;
    }

    @Override
    public List<QueryResult> queryTableResults(final long tableNumber)
            throws RemoteException, IllegalElectionStateException {
        return null;
    }

    @Override
    public void vote(final List<Vote> votes)
            throws RemoteException, IllegalElectionStateException {

    }

}
