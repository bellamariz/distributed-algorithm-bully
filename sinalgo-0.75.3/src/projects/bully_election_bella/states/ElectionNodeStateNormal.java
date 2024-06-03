package projects.bully_election_bella.states;

import projects.bully_election_bella.CustomGlobal;
import projects.bully_election_bella.nodes.messages.ApplicationMessage;
import projects.bully_election_bella.nodes.messages.BullyMessage;
import projects.bully_election_bella.nodes.nodeImplementations.ElectionNode;
import projects.bully_election_bella.nodes.timers.ElectionTimeoutTimer;
import sinalgo.tools.Tools;

public class ElectionNodeStateNormal extends ElectionNodeState {
    boolean responded = true;

    CustomGlobal global; 

    public ElectionNodeStateNormal(ElectionNode electionNode) {
        super(electionNode);

        global = (CustomGlobal) Tools.getCustomGlobal();
        
        electionNode.startApplication();
        
        electionNode.c = 0;

        Tools.appendToOutput("Node " + electionNode.ID + " going back to normal\n");
        electionNode.activeTimeout = new ElectionTimeoutTimer(BullyMessage.MessageType.AYOk);
        electionNode.activeTimeout.startRelative(6, electionNode); 
    }

    @Override
    public void handleAYUp(BullyMessage msg) {
        reply(msg);
        electionNode.reliability++;

        electionNode.activeTimeout.shouldFire = false;
        electionNode.activeTimeout = new ElectionTimeoutTimer(BullyMessage.MessageType.AYOk);
        electionNode.activeTimeout.startRelative(6, electionNode);
    }

    @Override
    public void handleAYOk(BullyMessage msg) {

    }

    @Override
    public void handleEnterElection(BullyMessage msg) {
        Tools.appendToOutput("Node " + electionNode.ID + " invited to take part in election\n");
        if(electionNode.ID < msg.senderId){
            Tools.appendToOutput("Node " + electionNode.ID + " giving up on CADIDATE state\n");
            reply(msg);
            electionNode.setState(States.ElectionParticipant);
        }else{
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
        if (msg.type == BullyMessage.MessageType.AYOk && msg.senderId == electionNode.coordinatorId) {
            Tools.appendToOutput("Node " + electionNode.ID + " coordinator responded to ping\n");
            responded = true;
        }
    }

    @Override
    public void handleTimeout() {
        if (!responded) {
            Tools.appendToOutput("Node " + electionNode.ID + " coordinator failed to respond to ping - calling elections\n");
            electionNode.setState(States.ElectionCandidate);
        } else {
            Tools.appendToOutput("Node " + electionNode.ID + " pinging coordinator\n");
            
            responded = false;
            BullyMessage msg = new BullyMessage(electionNode.ID, electionNode.c, electionNode.coordinatorId, BullyMessage.MessageType.AYOk, false);
            

            electionNode.send(msg, Tools.getNodeByID((int) electionNode.coordinatorId));
            
            global.messagesSent++;
            electionNode.activeTimeout = new ElectionTimeoutTimer(BullyMessage.MessageType.AYOk);
            electionNode.activeTimeout.startRelative(3, electionNode);
        }
    }

    @Override
    public void handleUpdate() {
        global.workDone++;

        if (electionNode.ID > electionNode.coordinatorId){
            electionNode.setState(States.ElectionCandidate);
            //electionNode.coordinatorId = -1;
            electionNode.c = -1;
        }
    }

	@Override
	public void handleApplication(ApplicationMessage msg) {
		Tools.appendToOutput("\nNode " + electionNode.ID + " is updating application status from " + msg.senderID + "\n");
		if (electionNode.appActive.putIfAbsent((int)msg.senderID, msg.lastUpdate) != null) {
			electionNode.appActive.replace((int)msg.senderID, msg.lastUpdate);
		}
	}
}
