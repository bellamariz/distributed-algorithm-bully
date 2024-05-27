package projects.bullybella.states;

import projects.bullybella.nodes.messages.BullyMessage;
import projects.bullybella.nodes.nodeImplementations.BullyNode;

public abstract class NodeState {
	final BullyNode node;

	public NodeState(BullyNode bn) {
		this.node = bn;
	}
	
	public enum States {
		Normal, Reorganizing, Down, Election
	}
	
	public void handleMessage(BullyMessage msg) {
		if (msg.msgAck) {
            handleAck(msg);
        } else {
            switch (msg.msgType) {
            case AYUp:
                handleAYUp(msg);
                break;
            case AYNormal:
                handleAYNormal(msg);
                break;
            case EnterElection:
                handleEnterElection(msg);
                break;
            case SetCoordinator:
                handleSetCoordinator(msg);
                break;
            case SetState:
                handleSetState(msg);
                break;
            case AYUp_Answer:
            	handleAYUpAnswer(msg);
                break;
            case AYNormal_Answer:
            	handleAYNormalAnswer(msg);
                break;
            case EnterElection_Answer:
            	handleEnterElectionAnswer(msg);
                break;
            case SetCoordinator_Answer:
            	handleSetCoordinatorAnswer(msg);
                break;
			default:
				break;    
            }
        }
	}
	
	// reply message
	public abstract void handleAck(BullyMessage msg);
	
	// when in normal/reorganizing
	public abstract void handleAYNormal(BullyMessage msg);
	public abstract void handleAYUp (BullyMessage msg);
	public abstract void handleAYNormalAnswer(BullyMessage msg);
	public abstract void handleAYUpAnswer(BullyMessage msg);

	// when in election
    public abstract void handleEnterElection(BullyMessage msg);
    public abstract void handleSetCoordinator(BullyMessage msg);
    public abstract void handleSetState(BullyMessage msg);
	public abstract void handleEnterElectionAnswer(BullyMessage msg);
    public abstract void handleSetCoordinatorAnswer(BullyMessage msg);
	
}
