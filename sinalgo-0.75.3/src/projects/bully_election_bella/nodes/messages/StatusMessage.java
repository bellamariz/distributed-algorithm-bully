package projects.bully_election_bella.nodes.messages;

import sinalgo.nodes.messages.Message;

public class StatusMessage extends Message{
	public long senderID;
	public long receiverID;
	
	public StatusMessage(long senderID, long receiverID) {
		this.senderID = senderID;
		this.receiverID = receiverID;
	}

	@Override
	public Message clone() {
		return new StatusMessage(this.senderID, this.receiverID);
	}

}
