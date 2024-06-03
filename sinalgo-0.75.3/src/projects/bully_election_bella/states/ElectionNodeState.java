package projects.bully_election_bella.states;

import projects.bully_election_bella.CustomGlobal;
import projects.bully_election_bella.nodes.messages.ApplicationMessage;
import projects.bully_election_bella.nodes.messages.BullyMessage;
import projects.bully_election_bella.nodes.nodeImplementations.ElectionNode;
import sinalgo.tools.Tools;

public abstract class ElectionNodeState {

    public enum States {
        Normal, NormalCoordinator, ElectionCandidate, ElectionParticipant, Down
    }

    final ElectionNode electionNode;
    final CustomGlobal global;

    public ElectionNodeState(ElectionNode electionNode) {
        this.electionNode = electionNode;
        this.global = (CustomGlobal) Tools.getCustomGlobal();
        if (this.electionNode.activeTimeout != null) {
            this.electionNode.activeTimeout.shouldFire = false;
            this.electionNode.activeTimeout = null;
        }
    }

    public final void handleMessage (BullyMessage msg) {
        if (msg.ack) {
            handleAck(msg);
        } else {
            switch (msg.type) {
                case AYUp:
                    handleAYUp(msg);
                    break;
                case AYOk:
                    handleAYOk(msg);
                    break;
                case EnterElection:
                    handleEnterElection(msg);
                    break;
                case SetCoordinator:
                    handleSetCoordinator(msg);
                    break;
                case SetState:
                    handleSetState(msg);
                    break;
            }
        }
    }

    public void reply(BullyMessage msg) {
        int senderId = msg.senderId;
        BullyMessage reply = (BullyMessage) msg.clone();
        reply.senderId = electionNode.ID;
        reply.c = electionNode.c;
        reply.coordinatorId = electionNode.coordinatorId;
        reply.ack = true;
        electionNode.send(reply, Tools.getNodeByID(senderId));
    }

    public abstract void handleAYUp (BullyMessage msg);
    public abstract void handleAYOk(BullyMessage msg);
    public abstract void handleEnterElection(BullyMessage msg);
    public abstract void handleSetCoordinator(BullyMessage msg);
    public abstract void handleSetState(BullyMessage msg);
    public abstract void handleAck(BullyMessage msg);
    public abstract void handleTimeout();
    public abstract void handleUpdate();
    public abstract void handleApplication(ApplicationMessage msg);
}
