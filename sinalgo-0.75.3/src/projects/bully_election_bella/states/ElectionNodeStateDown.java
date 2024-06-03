package projects.bully_election_bella.states;

import projects.bully_election_bella.nodes.messages.ApplicationMessage;
import projects.bully_election_bella.nodes.messages.BullyMessage;
import projects.bully_election_bella.nodes.nodeImplementations.ElectionNode;
import projects.bully_election_bella.nodes.timers.ElectionTimeoutTimer;
import sinalgo.tools.Tools;

public class ElectionNodeStateDown extends ElectionNodeState {
    public ElectionNodeStateDown(ElectionNode electionNode) {
        super(electionNode);
        electionNode.up.clear();
        electionNode.coordinatorId = -1;
        electionNode.stopApplication();
    }

    @Override
    public void handleAYUp(BullyMessage msg) {

    }

    @Override
    public void handleAYOk(BullyMessage msg) {

    }

    @Override
    public void handleEnterElection(BullyMessage msg) {

    }

    @Override
    public void handleSetCoordinator(BullyMessage msg) {
    }

    @Override
    public void handleSetState(BullyMessage msg) {

    }

    @Override
    public void handleAck(BullyMessage msg) {
    }

    @Override
    public void handleTimeout() {
        electionNode.c--;
        electionNode.setState(States.ElectionCandidate);
    }

    @Override
    public void handleUpdate() {
    	handleUpdateWithLastActive();
    }
    
    public void handleUpdateWithLastActive() {
        electionNode.checkAllNeighbours();
        
    	if (!electionNode.neighbours.isEmpty()) {
    		Tools.appendToOutput("Node " + electionNode.ID + " found connection\n");
            if (electionNode.activeTimeout == null) {
                electionNode.activeTimeout = new ElectionTimeoutTimer(BullyMessage.MessageType.AYOk);
                electionNode.activeTimeout.startRelative(1, electionNode);
            }
    	}
    }
    
    public void handleUpdateWithAntenna() {

        electionNode.reliability--;

        if(electionNode.reliability <= 0){
            electionNode.reliability = 0;
        }

        if (electionNode.getCurrentAntenna() != null) {
            Tools.appendToOutput("Node " + electionNode.ID + " found connection\n");
            if (electionNode.activeTimeout == null) {
                electionNode.activeTimeout = new ElectionTimeoutTimer(BullyMessage.MessageType.AYOk);
                electionNode.activeTimeout.startRelative(1, electionNode);
            }
        }
    }

	@Override
	public void handleApplication(ApplicationMessage msg) {
		
	}
}
