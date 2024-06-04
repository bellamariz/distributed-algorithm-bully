package projects.bully_new.states;

import projects.bully_new.nodes.messages.BullyMessage;
import projects.bully_new.nodes.nodeImplementations.BullyNode;
import sinalgo.tools.Tools;

public abstract class NodeState {
	final BullyNode node;

	public NodeState(BullyNode bn) {
		this.node = bn;
	}
	
	public enum States {
		Normal, Reorganizing, Down, Election
	}
	
	public final void handleMessage(BullyMessage msg) {
		if (msg.handleAnswer) {
			handleAnswer(msg);
		}
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
		default:
			break;    
        }
	}
	
    public void sendAnswer(BullyMessage msg) {
        int senderId = msg.senderID;
        BullyMessage answer = (BullyMessage) msg.clone();
        answer.senderID = node.getID();
        answer.coordID = node.currCoordID;
        answer.handleAnswer = true;
        node.send(answer, Tools.getNodeByID(senderId));
    }
    
    // handle answers
    public abstract void handleAnswer(BullyMessage msg);
		
	// when in normal/reorganizing
	public abstract void handleAYNormal(BullyMessage msg);
	public abstract void handleAYUp (BullyMessage msg);

	// when in election
    public abstract void handleEnterElection(BullyMessage msg);
    public abstract void handleSetCoordinator(BullyMessage msg);
    public abstract void handleSetState(BullyMessage msg);

    // handle timeout
	public abstract void handleTimeout();
	
}
