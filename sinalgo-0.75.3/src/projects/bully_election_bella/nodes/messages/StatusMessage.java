package projects.bully_election_bella.nodes.messages;

import sinalgo.nodes.messages.Message;

/**
 *  Implements P2P messaging (for DirectConnection model).
 */
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
