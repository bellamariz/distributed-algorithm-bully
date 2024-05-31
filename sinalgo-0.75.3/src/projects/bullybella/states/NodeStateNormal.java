package projects.bullybella.states;

import java.util.ArrayList;

import projects.bullybella.nodes.messages.BullyMessage;
import projects.bullybella.nodes.nodeImplementations.BullyNode;
import sinalgo.tools.Tools;
import sinalgo.tools.Tuple;

public class NodeStateNormal extends NodeState{
	
	private final ArrayList<Integer> membersReplied = new ArrayList<>();
	private boolean coordReplied = false;

	public NodeStateNormal(BullyNode bn) {
		super(bn);
		
		if (node.isCoordenator()) {
			Tools.appendToOutput("Node COORD " + node.getID() + " state NORMAL\n");
			checkMembers();
		}else {
			Tools.appendToOutput("Node " + node.getID() + " state NORMAL\n");
			checkCoordinator();
		}
	}
	
	// if node is COORD, ping other members
	public void checkMembers() {
		if (node.isCoordenator()){
			membersReplied.clear();
			
			// TODO: Set up message timer timeout
			
			// create new bully message
			BullyMessage bMsg_AYNormal = new BullyMessage(
					node.getID(), node.getCurrCoordID(), BullyMessage.MessageType.AYNormal, false
			);
			
			// send message to nodes in UP list
			for(int member: node.up) {
				BullyNode bn = (BullyNode) Tools.getNodeByID(member);
				bn.send(bMsg_AYNormal.clone(), bn);
			}
			
			Tools.appendToOutput("Node COORD " + node.getID() + " checking if members are alive\n");
		}
	}
	
	
	// if node is member, ping COORD
	public void checkCoordinator() {
		if (!node.isCoordenator()){
			coordReplied = false;
	
			// TODO: Set up message timer timeout
			
			// create new bully message
			BullyMessage bMsg_AYUp = new BullyMessage(
					node.getID(), node.getCurrCoordID(), BullyMessage.MessageType.AYUp, false
			);
			
			// send message to node COORD
			node.send(bMsg_AYUp, Tools.getNodeByID(node.getCurrCoordID()));
			
			Tools.appendToOutput("Node member " + node.getID() + " checking if COORD is alive\n");
		}
	}

	// members respond to AYNormal sent by COORD
	@Override
	public void handleAYNormal(BullyMessage msg) {
		if (!node.isCoordenator()) {
			sendAnswer(msg);
		}
	}

	// COORD responds to AYUp sent by members
	@Override
	public void handleAYUp(BullyMessage msg) {
		if (node.isCoordenator()) {
	        if (!node.up.contains(msg.senderID)) {
	            Tools.appendToOutput("Node COORD " + node.getID() + " detected new member " + msg.senderID + " \n");
	            node.up.add(msg.senderID);
	            checkMembers();
	        }
	        
			sendAnswer(msg);
		}
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
	public void handleAnswer(BullyMessage msg) {
		if (node.isCoordenator()) {
	        if (msg.msgType == BullyMessage.MessageType.AYNormal && node.up.contains(msg.senderID)) {
	            membersReplied.add(msg.senderID);
	            Tools.appendToOutput("Node member" + msg.senderID + " responded to COORD " + node.getID() + "\n");
	        }
		}else {
	        if (msg.msgType == BullyMessage.MessageType.AYUp && msg.senderID == node.getCurrCoordID()) {
	            coordReplied = true;
	            Tools.appendToOutput("Node COORD" + msg.senderID + " responded to member " + node.getID() + "\n");
	        }
		}
	}

	@Override
	public void handleTimeout() {
		
	}
}
