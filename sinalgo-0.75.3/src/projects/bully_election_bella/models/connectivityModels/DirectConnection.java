package projects.bully_election_bella.models.connectivityModels;

import java.util.Map;

import projects.bully_election_bella.nodes.nodeImplementations.ElectionNode;
import sinalgo.models.ConnectivityModelHelper;
import sinalgo.nodes.Node;

/**
 *  Implements connectivity model where nodes are always connected due to global P2P messaging.
 */
public class DirectConnection extends ConnectivityModelHelper{
	
	@Override
	protected boolean isConnected(Node from, Node to) {
		if(from instanceof ElectionNode && to instanceof ElectionNode) {
			Map <Integer,Boolean> to_neighbours = ((ElectionNode) to).neighbours;
			Map <Integer,Boolean> from_neighbours = ((ElectionNode) from).neighbours;
			
			if (to_neighbours.isEmpty() || from_neighbours.isEmpty()) {
				return false;
			}
			
			return to_neighbours.containsKey((int) from.ID) && from_neighbours.containsKey((int) to.ID);
		}
		
		return false;
	}

}
