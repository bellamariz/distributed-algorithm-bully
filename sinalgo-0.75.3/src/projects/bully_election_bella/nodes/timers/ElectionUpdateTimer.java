package projects.bully_election_bella.nodes.timers;

import projects.bully_election_bella.models.connectivityModels.AntennaConnection;
import projects.bully_election_bella.models.connectivityModels.DirectConnection;
import projects.bully_election_bella.nodes.nodeImplementations.Antenna;
import projects.bully_election_bella.nodes.nodeImplementations.ElectionNode;
import projects.bully_election_bella.states.ElectionNodeState;
import projects.bully_election_bella.states.ElectionNodeStateDown;
import sinalgo.configuration.Configuration;
import sinalgo.configuration.CorruptConfigurationEntryException;
import sinalgo.nodes.Node;
import sinalgo.nodes.Position;
import sinalgo.nodes.timers.Timer;
import sinalgo.tools.Tools;

/**
 *  Implements timer to update Node status after every round.
 */
public class ElectionUpdateTimer extends Timer {
	private int radius;
	
	public ElectionUpdateTimer() throws CorruptConfigurationEntryException {
		radius = Configuration.getIntegerParameter("GeometricNodeCollection/rMax") / 2;
		radius *= radius;
	}
	
	@Override
	public void fire() {
		ElectionNode node = (ElectionNode) this.getTargetNode();
		
		if(node.getConnectivityModel() instanceof DirectConnection) {
			fireWithDirectConnection(node);
		}else if(node.getConnectivityModel() instanceof AntennaConnection) {
			fireWithAntennaModel(node);
		}
	}
	
	public void fireWithDirectConnection(ElectionNode node) {
		node.checkAllNeighbours();
		
		Tools.reevaluateConnections();
		node.state.handleUpdate();
		this.startRelative(1, this.getTargetNode());
	}
	
	public void fireWithAntennaModel(ElectionNode mn) {
		Position pos = mn.getPosition();

		Antenna connectedAntenna = null;
		for (Node n: Tools.getNodeList()) {
			if (n instanceof Antenna) {
				Antenna a = (Antenna) n;
				if (a.getPosition().squareDistanceTo(pos) < radius) {
					connectedAntenna = a;
					break;
				}
			}
		}

		if (connectedAntenna == null && !(mn.state instanceof ElectionNodeStateDown)) {
			mn.setState(ElectionNodeState.States.Down);
		}

		mn.setCurrentAntenna(connectedAntenna);
		
		Tools.reevaluateConnections();
		mn.state.handleUpdate();
		this.startRelative(1, this.getTargetNode());
	}

}
