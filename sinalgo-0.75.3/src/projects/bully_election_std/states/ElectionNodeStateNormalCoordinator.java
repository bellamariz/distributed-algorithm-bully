package projects.bully_election_std.states;

import java.util.ArrayList;
import java.util.Date;

import projects.bully_election_std.CustomGlobal;
import projects.bully_election_std.nodes.messages.ApplicationMessage;
import projects.bully_election_std.nodes.messages.BullyMessage;
import projects.bully_election_std.nodes.nodeImplementations.ElectionNode;
import projects.bully_election_std.nodes.timers.ApplicationMessageTimer;
import projects.bully_election_std.nodes.timers.ElectionTimeoutTimer;
import sinalgo.tools.Tools;

public class ElectionNodeStateNormalCoordinator extends ElectionNodeState {

    private final ArrayList<Long> responded = new ArrayList<>();

    CustomGlobal global;

    public ElectionNodeStateNormalCoordinator(ElectionNode ctx) {
        super(ctx);

        global = (CustomGlobal) Tools.getCustomGlobal();
        
        Tools.appendToOutput("Node " + ctx.ID + " became the coordinator\n");
        
        ctx.startApplication();

        pingChildren();
    }

    public void pingChildren() {
        responded.clear();

        if (ctx.activeTimeout != null) {
            ctx.activeTimeout.shouldFire = false;
        }
        BullyMessage msg = new BullyMessage(ctx.ID, ctx.c, ctx.coordinatorId, BullyMessage.MessageType.AYUp, false);
        for (long id: ctx.up) {
            ElectionNode n = (ElectionNode) Tools.getNodeByID((int)id);
            ctx.send(msg.clone(), n);
            global.messagesSent++;
        }

        ctx.activeTimeout = new ElectionTimeoutTimer(BullyMessage.MessageType.AYOk);
        ctx.activeTimeout.startRelative(3, ctx);
        Tools.appendToOutput("Node " + ctx.ID + " checking if children are alive\n");
    }

    @Override
    public void handleAYUp(BullyMessage msg) {
        reply(msg);
    }

    @Override
    public void handleAYOk(BullyMessage msg) {
        if (!ctx.up.contains(msg.senderId)) {
            Tools.appendToOutput("Node " + ctx.ID + " new child " + msg.senderId + " found\n");
            ctx.up.add(msg.senderId);
            pingChildren();
        }
        reply(msg);
    }

    @Override
    public void handleEnterElection(BullyMessage msg) {
        Tools.appendToOutput("Node " + ctx.ID + " invited to take part in election\n");
        //reply(msg);

        if(ctx.ID < msg.senderId){
            Tools.appendToOutput("Node " + ctx.ID + " giving up on CADIDATE state\n");
            reply(msg);
            ctx.setState(States.ElectionParticipant);
        }
        else{
        ctx.setState(States.ElectionCandidate);
        }
    }

    @Override
    public void handleSetCoordinator(BullyMessage msg) {

    }

    @Override
    public void handleSetState(BullyMessage msg) {

    }

    @Override
    public void handleAck(BullyMessage msg) {
        if (msg.type == BullyMessage.MessageType.AYUp && ctx.up.contains(msg.senderId)) {
            responded.add((long) msg.senderId);
            Tools.appendToOutput("Node " + ctx.ID + " child " + msg.senderId + " responded\n");
        }
    }

    @Override
    public void handleTimeout() {
        ctx.reliability++;

        pingChildren();
        if (responded.containsAll(ctx.up)) {
            //pingChildren();
            //ctx.reliability++;
        } else {
            Tools.appendToOutput("Node " + ctx.ID + " not all children responded\n");
            //ctx.setState(States.ElectionCandidate);
        }
    }

    @Override
    public void handleUpdate() {
        global.workDone++;      
    }

	@Override
	public void handleApplication(ApplicationMessage msg) {
		Tools.appendToOutput("\nNode " + ctx.ID + " is updating application status from " + msg.senderID + "\n");
		if (ctx.appActive.putIfAbsent((int)msg.senderID, msg.lastUpdate) != null) {
			ctx.appActive.replace((int)msg.senderID, msg.lastUpdate);
		}
		Tools.appendToOutput("Node " + ctx.ID + " appActive: " + ctx.appActive + "\n\n");
	}
}
