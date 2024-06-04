package projects.bully_election_bella.states;

import java.util.ArrayList;

import projects.bully_election_bella.CustomGlobal;
import projects.bully_election_bella.nodes.messages.ApplicationMessage;
import projects.bully_election_bella.nodes.messages.BullyMessage;
import projects.bully_election_bella.nodes.nodeImplementations.ElectionNode;
import projects.bully_election_bella.nodes.timers.ElectionTimeoutTimer;
import sinalgo.tools.Tools;

public class ElectionNodeStateNormalCoordinator extends ElectionNodeState {

    private final ArrayList<Integer> responded = new ArrayList<>();

    CustomGlobal global;

    public ElectionNodeStateNormalCoordinator(ElectionNode electionNode) {
        super(electionNode);

        global = (CustomGlobal) Tools.getCustomGlobal();
   
        electionNode.startApplication();
        checkMembers();
    }

    public void checkMembers() {
        responded.clear();

        if (electionNode.activeTimeout != null) {
            electionNode.activeTimeout.shouldFire = false;
        }
        
        BullyMessage msg = new BullyMessage(electionNode.ID, electionNode.c, electionNode.coordinatorId, BullyMessage.MessageType.AYUp, false);
        for (long id: electionNode.up) {
            ElectionNode n = (ElectionNode) Tools.getNodeByID((int)id);
            electionNode.send(msg.clone(), n);
        }

        electionNode.activeTimeout = new ElectionTimeoutTimer(BullyMessage.MessageType.AYUp);
        electionNode.activeTimeout.startRelative(3, electionNode);
        Tools.appendToOutput("Node " + electionNode.ID + " checking if children are alive\n");
    }

    @Override
    public void handleAYUp(BullyMessage msg) {
        reply(msg);
    }

    @Override
    public void handleAYOk(BullyMessage msg) {
        if (!electionNode.up.contains(msg.senderId)) {
            Tools.appendToOutput("Node " + electionNode.ID + " new child " + msg.senderId + " found\n");
            electionNode.up.add(msg.senderId);
            checkMembers();
        }
        reply(msg);
    }

    @Override
    public void handleEnterElection(BullyMessage msg) {
        Tools.appendToOutput("Node " + electionNode.ID + " invited to take part in election\n");
        //reply(msg);

        if(electionNode.ID < msg.senderId){
            Tools.appendToOutput("Node " + electionNode.ID + " giving up on CADIDATE state\n");
            reply(msg);
            electionNode.setState(States.ElectionParticipant);
        }
        else{
        electionNode.setState(States.ElectionCandidate);
        }
    }

    @Override
    public void handleSetCoordinator(BullyMessage msg) {

    }

    @Override
    public void handleSetState(BullyMessage msg) {

    }

    @Override
    public void handleAck(BullyMessage msg) {
        if (msg.type == BullyMessage.MessageType.AYUp && electionNode.up.contains(msg.senderId)) {
            responded.add(msg.senderId);
            Tools.appendToOutput("Node " + electionNode.ID + " child " + msg.senderId + " responded\n");
        }
    }

    @Override
    public void handleTimeout() {
        electionNode.reliability++;

        checkMembers();
        if (responded.containsAll(electionNode.up)) {
            //pingChildren();
            //electionNode.reliability++;
        } else {
            Tools.appendToOutput("Node " + electionNode.ID + " not all children responded\n");
            //electionNode.setState(States.ElectionCandidate);
        }
    }

    @Override
    public void handleUpdate() {     
    }

	@Override
	public void handleApplication(ApplicationMessage msg) {
//		Tools.appendToOutput("\nNode " + electionNode.ID + " is updating application status from " + msg.senderID + "\n");
		if (electionNode.application.putIfAbsent((int)msg.senderID, msg.lastUpdate) != null) {
			electionNode.application.replace((int)msg.senderID, msg.lastUpdate);
		}
	}
}
