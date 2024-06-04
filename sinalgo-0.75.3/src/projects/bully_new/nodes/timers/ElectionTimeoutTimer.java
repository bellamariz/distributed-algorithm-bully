package projects.bully_new.nodes.timers;

import projects.bully_new.nodes.messages.BullyMessage;
import projects.bully_new.nodes.nodeImplementations.BullyNode;
import sinalgo.nodes.timers.Timer;

public class ElectionTimeoutTimer extends Timer{

	public final BullyMessage.MessageType msgType;
	public boolean shouldFire = true;

	public ElectionTimeoutTimer(BullyMessage.MessageType msgType) {
		this.msgType = msgType;
	}
	
	@Override
	public void fire() {
		if(shouldFire) {
			BullyNode bn = (BullyNode) this.getTargetNode();
			bn.getState().handleTimeout();
		}
	}

}
