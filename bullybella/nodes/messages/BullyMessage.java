package projects.bullybella.nodes.messages;

import sinalgo.nodes.messages.Message;

public class BullyMessage extends Message {
    public enum MessageType {
        AYUp, AYNormal, EnterElection, SetCoordinator, SetState,
        AYUp_Answer, AYNormal_Answer, EnterElection_Answer, SetCoordinator_Answer
    }

	@Override
	public Message clone() {
		return null;
	}

}
