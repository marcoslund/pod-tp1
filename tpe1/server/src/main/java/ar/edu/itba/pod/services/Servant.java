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
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class Servant
        implements AdministrationService, MonitoringService, QueryService, VotingService {

    private static final AtomicBoolean electionStarted = new AtomicBoolean(false);
    private static final AtomicBoolean electionFinished = new AtomicBoolean(false);

    public Servant() throws RemoteException {

    }

    @Override
    public void startElection() throws RemoteException, IllegalElectionStateException {
        if(!electionStarted.compareAndSet(false, true)) {
            throw new IllegalElectionStateException("Election already active.");
        }
    }

    @Override
    public synchronized void endElection() throws RemoteException, IllegalElectionStateException {
        if(electionStarted.get() && !electionFinished.get())
            electionFinished.set(true);
        else {
            throw new IllegalElectionStateException("Election not active.");
        }
    }

    @Override
    public ElectionState queryElection() throws RemoteException {
        if(!electionStarted.get())
            return ElectionState.NOT_STARTED;
        else if(electionFinished.get()) {
            return ElectionState.FINISHED;
        } else {
            return ElectionState.IN_PROGRESS;
        }
    }

    @Override
    public void registerFiscal(final PoliticalParty politicalParty, final long tableNumber)
            throws RemoteException, IllegalElectionStateException {

    }

    @Override
    public SortedSet<QueryResult> queryNationResults()
            throws RemoteException, IllegalElectionStateException {
        return null;
    }

    @Override
    public SortedSet<QueryResult> queryStateResults(final State state)
            throws RemoteException, IllegalElectionStateException {
        return null;
    }

    @Override
    public SortedSet<QueryResult> queryTableResults(final long tableNumber)
            throws RemoteException, IllegalElectionStateException {
        return null;
    }

    @Override
    public void vote(final List<Vote> votes)
            throws RemoteException, IllegalElectionStateException {

    }

}
