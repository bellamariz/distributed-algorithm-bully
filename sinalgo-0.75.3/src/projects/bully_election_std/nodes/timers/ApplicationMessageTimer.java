package projects.bully_election_std.nodes.timers;

import java.util.Date;

import projects.bully_election_std.nodes.messages.ApplicationMessage;
import projects.bully_election_std.nodes.messages.BullyMessage;
import projects.bully_election_std.nodes.nodeImplementations.ElectionNode;
import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Message;
import sinalgo.nodes.timers.Timer;
import sinalgo.tools.Tools;

public class ApplicationMessageTimer extends Timer{
	public boolean shouldFire = false;
	public final long senderID;

	public ApplicationMessageTimer(long nodeID) {
		this.senderID = nodeID;
	}

	@Override
	public void fire() {
		if (shouldFire) {
			ElectionNode mn = (ElectionNode) Tools.getNodeByID((int) this.senderID);
			ApplicationMessage appMsg = new ApplicationMessage(this.senderID, new Date());
			mn.broadcast(appMsg);
		}
	}

}
