package projects.bullybella.states;

import projects.bullybella.nodes.messages.BullyMessage;
import projects.bullybella.nodes.nodeImplementations.BullyNode;

public class NodeStateNormal extends NodeState{

	public NodeStateNormal(BullyNode bn) {
		super(bn);
	}

	@Override
	public void handleAck(BullyMessage msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleAYNormal(BullyMessage msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleAYUp(BullyMessage msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleEnterElection(BullyMessage msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleSetCoordinator(BullyMessage msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleSetState(BullyMessage msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleAYNormalAnswer(BullyMessage msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleAYUpAnswer(BullyMessage msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleEnterElectionAnswer(BullyMessage msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleSetCoordinatorAnswer(BullyMessage msg) {
		// TODO Auto-generated method stub
		
	}
}
