package projects.bully_election_bella.nodes.timers;

import projects.bully_election_bella.nodes.nodeImplementations.ElectionNode;
import projects.bully_election_bella.states.ElectionNodeState.States;
import sinalgo.nodes.timers.Timer;

/**
 *  Implements timer that will take DOWN a NORMAL ElectionNode after a random interval (for DirectConnection model)
 *  - see CustomGlobal method useRandomNodeDown.
 */
public class NodeDownTimer extends Timer{
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
			this.node.neighbours.clear();
			this.node.setState(States.Down);
			disable();
		}
		
	}

}
