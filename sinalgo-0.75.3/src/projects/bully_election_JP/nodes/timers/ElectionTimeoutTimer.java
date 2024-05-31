package projects.bully_election_JP.nodes.timers;

import projects.bully_election_JP.nodes.messages.BullyMessage;
import projects.bully_election_JP.nodes.nodeImplementations.ElectionNode;
import sinalgo.nodes.timers.Timer;

public class ElectionTimeoutTimer extends Timer {
	public final BullyMessage.MessageType type;
	public boolean shouldFire = true;

	public ElectionTimeoutTimer(BullyMessage.MessageType type) {
		this.type = type;
	}
	
	@Override
	public void fire() {
		if(shouldFire) {
			ElectionNode mn = (ElectionNode) this.getTargetNode();
			mn.state.handleTimeout();
		}
	}

}
