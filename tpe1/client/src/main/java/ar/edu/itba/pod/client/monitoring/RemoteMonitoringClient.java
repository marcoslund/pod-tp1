package ar.edu.itba.pod.client.monitoring;

import ar.edu.itba.pod.interfaces.models.Vote;

import java.rmi.Remote;

public interface RemoteMonitoringClient extends Remote {

    public void notifyVote(Vote vote);
}
