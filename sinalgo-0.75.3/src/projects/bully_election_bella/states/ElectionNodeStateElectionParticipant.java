package projects.bully_election_bella.states;

import projects.bully_election_bella.nodes.messages.ApplicationMessage;
import projects.bully_election_bella.nodes.messages.BullyMessage;
import projects.bully_election_bella.nodes.nodeImplementations.ElectionNode;
import projects.bully_election_bella.nodes.timers.ElectionTimeoutTimer;
import sinalgo.tools.Tools;

public class ElectionNodeStateElectionParticipant extends ElectionNodeState {
    public ElectionNodeStateElectionParticipant(ElectionNode electionNode) {
        super(electionNode);
        electionNode.stopApplication();
        electionNode.activeTimeout = new ElectionTimeoutTimer(BullyMessage.MessageType.SetCoordinator);
        electionNode.activeTimeout.startRelative(8, electionNode);
        Tools.appendToOutput("Node " + electionNode.ID + " joining election\n");
    }

    @Override
    public void handleAYUp(BullyMessage msg) {
        reply(msg);
    }

    @Override
    public void handleAYOk(BullyMessage msg) {

    }

    @Override
    public void handleEnterElection(BullyMessage msg) {
        int senderId = msg.senderId;
        Tools.appendToOutput("Node " + electionNode.ID + " entering election from node " + senderId + "\n");

        reply(msg);

        electionNode.activeTimeout.shouldFire = false;
        electionNode.activeTimeout = new ElectionTimeoutTimer(BullyMessage.MessageType.SetCoordinator);
        electionNode.activeTimeout.startRelative(8, electionNode);
    }

    @Override
    public void handleSetCoordinator(BullyMessage msg) {
        int senderId = msg.senderId;
        Tools.appendToOutput("Node " + electionNode.ID + " accepting new coordinator " + senderId + "\n");

        reply(msg);

        electionNode.activeTimeout.shouldFire = false;
        electionNode.activeTimeout = new ElectionTimeoutTimer(BullyMessage.MessageType.SetState);
        electionNode.activeTimeout.startRelative(8, electionNode);
    }

    @Override
    public void handleSetState(BullyMessage msg) {
        int senderId = msg.senderId;
        Tools.appendToOutput("Node " + electionNode.ID + " accepting election result\n");

        electionNode.coordinatorId = msg.coordinatorId;

        reply(msg);

        electionNode.setState(States.Normal);
    }

    @Override
    public void handleAck(BullyMessage msg) {

    }

    @Override
    public void handleTimeout() {
        Tools.appendToOutput("Node " + electionNode.ID + " election went silent, trying to call new one\n");
        electionNode.setState(States.ElectionCandidate);
    }

    @Override
    public void handleUpdate() {

    }

	@Override
	public void handleApplication(ApplicationMessage msg) {
		
	}
}
