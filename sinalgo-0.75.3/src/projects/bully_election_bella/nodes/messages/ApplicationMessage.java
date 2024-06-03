package projects.bully_election_bella.nodes.messages;

import java.util.Date;

import sinalgo.nodes.messages.Message;

public class ApplicationMessage extends Message{
	public long senderID;
	public Date lastUpdate;
	
	public ApplicationMessage(long senderID, Date lastUpdate) {
		this.senderID = senderID;
		this.lastUpdate = lastUpdate;
	}

	@Override
	public Message clone() {
		return new ApplicationMessage(this.senderID, this.lastUpdate);
	}

}
