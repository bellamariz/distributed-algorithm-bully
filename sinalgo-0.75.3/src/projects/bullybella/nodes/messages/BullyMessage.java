package projects.bullybella.nodes.messages;

import sinalgo.nodes.messages.Message;

public class BullyMessage extends Message {
    public enum MessageType {
        AYUp, AYNormal, EnterElection, SetCoordinator, SetState,
        AYUp_Answer, AYNormal_Answer, EnterElection_Answer, SetCoordinator_Answer
    }
    
    public int senderID;
    public int coordID;
    public MessageType msgType;
    public boolean handleAnswer;
    //TODO: Message timer 
    
    public BullyMessage(int senderID, int coordID, MessageType msgType, boolean handleAnswer) {
        this.senderID = senderID;
        this.coordID = coordID;
        this.msgType = msgType;
        this.handleAnswer = handleAnswer;
    }

	@Override
	public Message clone() {
		return new BullyMessage(this.senderID, this.coordID, this.msgType, this.handleAnswer);
	}

}
