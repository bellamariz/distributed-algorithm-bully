package projects.bully_election_bella.nodes.timers;

import projects.bully_election_bella.nodes.nodeImplementations.ElectionNode;

import projects.bully_election_bella.states.ElectionNodeState;
import projects.bully_election_bella.states.ElectionNodeState.States;
import projects.bully_election_bella.states.ElectionNodeStateNormal;
import projects.bully_election_bella.states.ElectionNodeStateNormalCoordinator;
import sinalgo.nodes.Node;
import sinalgo.nodes.timers.Timer;
import sinalgo.tools.Tools;

public class NodeDownTimer extends Timer{
	// this timer will take DOWN a NORMAL node after a random interval
	
	public boolean active = false;
	public ElectionNode node;
	
	public NodeDownTimer(ElectionNode node) {
		this.node = node;
	}
	
	public void disable() {
		this.active = false;
	}
	
	public void enable() {
		this.active = true;
	}

	@Override
	public void fire() {
		if (active) {
			Tools.appendToOutput("NodeDownTimer firing for Node " + this.node.ID + "\n");
			this.node.neighbours.clear();
			this.node.setState(States.Down);
			disable();
		}
		
	}

}
