package projects.bullybella.nodes.messages;

import sinalgo.nodes.messages.Message;

public class BullyMessage extends Message {
    public enum MessageType {
        AYUp, AYNormal, EnterElection, SetCoordinator, SetState,
        AYUp_Answer, AYNormal_Answer, EnterElection_Answer, SetCoordinator_Answer
    }
    
    public long senderID;
    public long coordID;
    public MessageType msgType;
    public boolean msgAck;
    //TODO: Message timer 
    
    public BullyMessage(long senderID, long coordID, MessageType msgType, boolean msgAck) {
        this.senderID = senderID;
        this.coordID = coordID;
        this.msgType = msgType;
        this.msgAck = msgAck;
    }

	@Override
	public Message clone() {
		return new BullyMessage(this.senderID, this.coordID, this.msgType, this.msgAck);
	}

}
