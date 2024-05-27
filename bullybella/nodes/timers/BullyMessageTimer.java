package projects.bullybella.nodes.timers;

import projects.bullybella.nodes.messages.BullyMessage;
import projects.bullybella.nodes.messages.BullyMessage.MessageType;
import projects.bullybella.nodes.nodeImplementations.BullyNode;
import sinalgo.nodes.timers.Timer;

public class BullyMessageTimer extends Timer {
	
	// time for a message to be received by a node after it was sent
	public static int TP = 5;
	// time for an active node to acknowledge all received messages
	public static int TM = 10;
	// if node takes longer than TMAX to reply, other processes will assume it has failed
	public static int TMAX = 2*TM + TP;

	private BullyNode sender;
    private long receiverPID;
    private boolean enabled = true;
    private MessageType mType;

    public BullyMessageTimer(BullyNode sender, long receiverID, boolean enabled) {
        super();
        this.sender = sender;
        this.receiverPID = receiverID;
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void disable() {
        this.setEnabled(false);
    }

    @Override
    public void fire() {
    }

}
