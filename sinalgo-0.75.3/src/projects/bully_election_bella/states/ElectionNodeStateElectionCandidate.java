package projects.bully_election_bella.states;

import java.util.ArrayList;

import projects.bully_election_bella.nodes.messages.ApplicationMessage;
import projects.bully_election_bella.nodes.messages.BullyMessage;
import projects.bully_election_bella.nodes.nodeImplementations.ElectionNode;
import projects.bully_election_bella.nodes.timers.ElectionTimeoutTimer;
import sinalgo.nodes.Node;
import sinalgo.tools.Tools;

public class ElectionNodeStateElectionCandidate extends ElectionNodeState {
    private final ArrayList<Integer> responded = new ArrayList<>();

    public ElectionNodeStateElectionCandidate(ElectionNode electionNode) {
        super(electionNode);
        electionNode.up.clear();
        electionNode.coordinatorId = -1;
        electionNode.stopApplication();
        
//        BullyMessage msg = new BullyMessage(electionNode.ID, electionNode.c, electionNode.coordinatorId, BullyMessage.MessageType.AYUp, false);
//
//        for (Node n: Tools.getNodeList()) {
//            if (n instanceof ElectionNode) {
//                ElectionNode aux = (ElectionNode) n;
//                electionNode.send(msg.clone(), aux);
//            }
//        }

        electionNode.activeTimeout = new ElectionTimeoutTimer(BullyMessage.MessageType.AYUp);
        electionNode.activeTimeout.startRelative(3, electionNode);

        Tools.appendToOutput("Node " + electionNode.ID + " entering CANDIDATE state\n");
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
        if(electionNode.ID < msg.senderId){
            Tools.appendToOutput("Node " + electionNode.ID + " giving up on CADIDATE state\n");
            reply(msg);
            electionNode.setState(States.ElectionParticipant);
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
        Tools.appendToOutput("Node " + electionNode.ID + " received ack from " + msg.senderId + "\n");

        if (msg.type == BullyMessage.MessageType.AYUp) {
            Tools.appendToOutput("Node " + electionNode.ID + " received UP from <" + msg.c + "," + msg.senderId + ">\n");
            
            
            if (msg.senderId > electionNode.ID) {
                Tools.appendToOutput("Node " + electionNode.ID + " giving up on CADIDATE state\n");
             
                if (msg.coordinatorId != -1) {
                    electionNode.coordinatorId = msg.coordinatorId;
                    electionNode.setState(States.Normal);
                } else {
                    
                    electionNode.setState(States.ElectionParticipant);
                }
            }
            
        } 
        else if (msg.type == BullyMessage.MessageType.EnterElection) {

            if(electionNode.ID < msg.senderId){
            	Tools.appendToOutput("Node " + electionNode.ID + " giving up on CADIDATE state\n");
            	electionNode.setState(States.ElectionParticipant);
            }else{
                Tools.appendToOutput("Node " + electionNode.ID + " adding " + msg.senderId + " to up list.\n");
                electionNode.up.add(msg.senderId);
            }
        } 
        else if (msg.type == BullyMessage.MessageType.SetCoordinator) {
            Tools.appendToOutput("Node " + electionNode.ID + " adding " + msg.senderId + " to SetCoordinator responded list.\n");
            responded.add(msg.senderId);
            if (responded.containsAll(electionNode.up)) {
                electionNode.activeTimeout.shouldFire = false;
                handleTimeout();
            }
        } 
        else if (msg.type == BullyMessage.MessageType.SetState) {
            Tools.appendToOutput("Node " + electionNode.ID + " adding " + msg.senderId + " to SetState responded list.\n");
            responded.add( msg.senderId);
            if (responded.containsAll(electionNode.up)) {
                electionNode.activeTimeout.shouldFire = false;
                handleTimeout();
            }
        }
    }

    @Override
    public void handleTimeout() {
        if (electionNode.activeTimeout.type == BullyMessage.MessageType.AYUp) {
            Tools.appendToOutput("Node " + electionNode.ID + " calling for elections.\n");
            BullyMessage msg = new BullyMessage(electionNode.ID, electionNode.c, electionNode.coordinatorId, BullyMessage.MessageType.EnterElection, false);

            for (Node n: Tools.getNodeList()) {
                if (n instanceof ElectionNode) {
                    ElectionNode aux = (ElectionNode) n;
                    electionNode.send(msg.clone(), aux);
                }
            }

            electionNode.up.clear();
            electionNode.activeTimeout = new ElectionTimeoutTimer(BullyMessage.MessageType.EnterElection);
            electionNode.activeTimeout.startRelative(4, electionNode);
        } 
        else if (electionNode.activeTimeout.type == BullyMessage.MessageType.EnterElection) {
            Tools.appendToOutput("Node " + electionNode.ID + " electing himself\n");
            BullyMessage msg = new BullyMessage(electionNode.ID, electionNode.c, electionNode.coordinatorId, BullyMessage.MessageType.SetCoordinator, false);

            Tools.appendToOutput("Node " + electionNode.ID + " up: " + electionNode.up + "\n");
            for (int id: electionNode.up) {
                ElectionNode n = (ElectionNode) Tools.getNodeByID(id);
                electionNode.send(msg.clone(), n);
            }
            
            responded.clear();
            electionNode.activeTimeout = new ElectionTimeoutTimer(BullyMessage.MessageType.SetCoordinator);
            electionNode.activeTimeout.startRelative(3, electionNode);
        } else if (electionNode.activeTimeout.type == BullyMessage.MessageType.SetCoordinator) {
            if (!responded.containsAll(electionNode.up)) {
                Tools.appendToOutput("Node " + electionNode.ID + " trying again - not all nodes responded to SetCoordinator\n");
                electionNode.setState(States.ElectionCandidate);
            } else {
                electionNode.coordinatorId = electionNode.ID;
                Tools.appendToOutput("Node " + electionNode.ID + " setting normal\n");
                BullyMessage msg = new BullyMessage(electionNode.ID, electionNode.c, electionNode.coordinatorId, BullyMessage.MessageType.SetState, false);

                for (int id: electionNode.up) {
                    ElectionNode n = (ElectionNode) Tools.getNodeByID(id);
                    electionNode.send(msg.clone(), n);
                }

                responded.clear();
                electionNode.activeTimeout = new ElectionTimeoutTimer(BullyMessage.MessageType.SetState);
                electionNode.activeTimeout.startRelative(3, electionNode);
            }
        } else if (electionNode.activeTimeout.type == BullyMessage.MessageType.SetState) {
            if (!responded.containsAll(electionNode.up)) {
                Tools.appendToOutput("Node " + electionNode.ID + " trying again - not all nodes responded to SetState\n");
                electionNode.setState(States.ElectionCandidate);
            } else {
                Tools.appendToOutput("Node " + electionNode.ID + " becoming coordinator\n");
                electionNode.setState(States.NormalCoordinator);
            }
        }
    }

    @Override
    public void handleUpdate() {

    }

	@Override
	public void handleApplication(ApplicationMessage msg) {
		
	}
}
