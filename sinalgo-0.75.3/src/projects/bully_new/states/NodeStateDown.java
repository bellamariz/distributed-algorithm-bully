package projects.bully_new.states;

import projects.bully_new.nodes.messages.BullyMessage;
import projects.bully_new.nodes.nodeImplementations.BullyNode;

public class NodeStateDown extends NodeState{

	// node is down, it does not know the current COORD nor the UP processes
	public NodeStateDown(BullyNode bn) {
		super(bn);
		bn.currCoordID = -1;
		bn.up.clear();
	}

	@Override
	public void handleAYNormal(BullyMessage msg) {
		
		
	}

	@Override
	public void handleAYUp(BullyMessage msg) {
		
		
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
		
		
	}

	@Override
	public void handleTimeout() {
		
	}
	
}

