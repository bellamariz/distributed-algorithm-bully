package projects.bully_election_JP.states;

import projects.bully_election_JP.nodes.messages.BullyMessage;
import projects.bully_election_JP.nodes.nodeImplementations.ElectionNode;
import projects.bully_election_JP.nodes.timers.ElectionTimeoutTimer;
import sinalgo.tools.Tools;

public class ElectionNodeStateDown extends ElectionNodeState {
    public ElectionNodeStateDown(ElectionNode ctx) {
        super(ctx);
        ctx.up.clear();
        ctx.coordinatorId = -1;
    }

    @Override
    public void handleAYUp(BullyMessage msg) {

    }

    @Override
    public void handleAYOk(BullyMessage msg) {

    }

    @Override
    public void handleEnterElection(BullyMessage msg) {

    }

    @Override
    public void handleSetCoordinator(BullyMessage msg) {
    }

    @Override
    public void handleSetState(BullyMessage msg) {

    }

    @Override
    public void handleAck(BullyMessage msg) {
    }

    @Override
    public void handleTimeout() {
        ctx.c--;
        ctx.setState(States.ElectionCandidate);
    }

    @Override
    public void handleUpdate() {

        ctx.reliability -= ctx.PUNISHMENT_WEIGHT;

         if(ctx.reliability < ctx.RELIABILITY_THRESHOLD_MIN){
            ctx.inHysteresis = true;
        }


        if(ctx.reliability <= 0){
            ctx.reliability = 0;
        }

        if (ctx.getCurrentAntenna() != null) {
            Tools.appendToOutput("Node " + ctx.ID + " found connection\n");
            if (ctx.activeTimeout == null) {
                ctx.activeTimeout = new ElectionTimeoutTimer(BullyMessage.MessageType.AYOk);
                ctx.activeTimeout.startRelative(1, ctx);
            }
        }
    }
}
