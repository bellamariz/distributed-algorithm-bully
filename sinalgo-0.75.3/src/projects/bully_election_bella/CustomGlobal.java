
package projects.bully_election_bella;


import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import projects.bully_election_bella.models.connectivityModels.AntennaConnection;
import projects.bully_election_bella.models.connectivityModels.DirectConnection;
import projects.bully_election_bella.nodes.nodeImplementations.ElectionNode;
import projects.bully_election_bella.nodes.timers.NodeDownTimer;
import projects.bully_election_bella.states.ElectionNodeStateNormal;
import projects.bully_election_bella.states.ElectionNodeStateNormalCoordinator;
import sinalgo.configuration.CorruptConfigurationEntryException;
import sinalgo.exception.SinalgoFatalException;
import sinalgo.models.ConnectivityModel;
import sinalgo.nodes.Node;
import sinalgo.runtime.AbstractCustomGlobal;
import sinalgo.runtime.nodeCollection.NodeCollectionInterface;
import sinalgo.tools.Tools;
import sinalgo.tools.logging.Logging;

/**
 * This class holds customized global state and methods for the framework. 
 * The only mandatory method to overwrite is 
 * <code>hasTerminated</code>
 * <br>
 * Optional methods to override are
 * <ul>
 * <li><code>customPaint</code></li>
 * <li><code>handleEmptyEventQueue</code></li>
 * <li><code>onExit</code></li>
 * <li><code>preRun</code></li>
 * <li><code>preRound</code></li>
 * <li><code>postRound</code></li>
 * <li><code>checkProjectRequirements</code></li>
 * </ul>
 * @see AbstractCustomGlobal for more details.
 * <br>
 * In addition, this class also provides the possibility to extend the framework with
 * custom methods that can be called either through the menu or via a button that is
 * added to the GUI. 
 */
public class CustomGlobal extends AbstractCustomGlobal{
	private Random rand = new Random();
	private String baseFilepath = "output\\";
	private String algoName = "bully_bella";
	private String connModel;
	private double steps;
	private static final int MIN_RANDOM = 10;
	private static final int MAX_RANDOM = 20;
	
	public int statusP2PMessages = 0;
	public int electionMessages = 0;
	public int applicationMessages = 0;
	public int sentMessages = 0;
	public int totalMessages = 0;
	public int totalNodes = 0;
	public static final double TIME_MSG_STATUS_DELIVERY = 2.0;
	
	// flag tracking that there is a node with an active NodeDownTimer
	// only one node will be taken down per timer
	public boolean nodeDownExists = false;
	
	
	// used in DirectConnection connectivity model to set a random node as DOWN (one per time)
	private void useRandomNodeDown(Node randomNode) {
		if (randomNode instanceof ElectionNode && !nodeDownExists) {
			ElectionNode electionNode = (ElectionNode) randomNode;
			
			// if node is in NORMAL state, create a random timer to take it DOWN
			if (electionNode.state instanceof ElectionNodeStateNormal || 
					electionNode.state instanceof ElectionNodeStateNormalCoordinator) {
				
				if (electionNode.nodeDownTimer == null) {
					int random = rand.nextInt(MAX_RANDOM - MIN_RANDOM + 1) + MIN_RANDOM;
					electionNode.nodeDownTimer = new NodeDownTimer(electionNode);
					
					// NodeDownTimer will disable itself after firing
					electionNode.nodeDownTimer.enable();
					electionNode.nodeDownTimer.startRelative(random, electionNode);
					
					nodeDownExists = true;
					
					Tools.appendToOutput("\n[Node Down Timer] Node " + electionNode.ID + " in " + random + " rounds\n\n");
				}
			}
		}
	}
	
    public static boolean isDirectConnectionModel(Node node) {
    	return (node.getConnectivityModel() instanceof DirectConnection);
    }
    
    public static boolean isAtennaConnectionModel(Node node) {
    	return (node.getConnectivityModel() instanceof AntennaConnection);
    }
	
	@Override
	public void preRound() {
		// run only after nodes have been initialized
		if(Tools.getNodeList().size() > 0) {
			Node randomNode = Tools.getNodeList().getRandomNode();
			
			if (isDirectConnectionModel(randomNode)) {
				useRandomNodeDown(randomNode);
			}
		}
	}
	
	@Override
	public void postRound() {
		NodeCollectionInterface nodes = Tools.getNodeList();
		
		for(Node node: nodes) {
			if (node instanceof ElectionNode) {
				ElectionNode electionNode = (ElectionNode) node;
				
				// reload AppMessageTimer
				if (electionNode.appMessageTimer != null && electionNode.appMessageTimer.shouldFire){
					electionNode.appMessageTimer.startRelative(1, electionNode);
					
					Tools.appendToOutput("\n[Application] Node " + electionNode.ID + " - Last Active: " + electionNode.application + "\n\n");
				}
				
				// reload NodeDownTimer - if it was programmed and already fired
				if (nodeDownExists) {
					if (electionNode.nodeDownTimer != null && !electionNode.nodeDownTimer.active) {	
						electionNode.nodeDownTimer = null;
						nodeDownExists = false;
					}
				}
			}
		}
	}
	
	@Override
	public boolean hasTerminated() {
		return false;
	}

	@Override
	public void onExit() {
		Logging logger = Logging.getLogger();

        try{
			totalNodes = Tools.getNodeList().size();
			
			if(totalNodes > 0) {
				connModel = Tools.getRandomNode().getConnectivityModel().toString();
			}
			
	        steps = Tools.getGlobalTime();
	        sentMessages = Tools.getNumberOfSentMessages();
	        totalMessages = electionMessages+applicationMessages+statusP2PMessages;
	        
	        appendToFile();
		}catch (CorruptConfigurationEntryException e) {
			logger.logln("Favor criar NumberOfNodes em Config.xml");
            throw new SinalgoFatalException(e.getMessage());
		}catch (Exception e) {
			logger.logln("Falhou ao criar csv");
			e.printStackTrace();
		}
        
		logger.logln("Number of nodes: " + totalNodes);
		logger.logln("Connectivity model: " + connModel);
		logger.logln("Simulation steps: " + steps);
		logger.logln("Total messages sent: " + sentMessages);
		logger.logln("- Election messages: " + electionMessages);
		logger.logln("- Application messages: " + applicationMessages);
		logger.logln("- Status P2P messages: " + statusP2PMessages);
		logger.logln("Total messages " + totalMessages);
	}
	
	// write output to file
	private void appendToFile() throws Exception {
		File file = new File(baseFilepath + algoName  + "_" + totalNodes + "_nodes.csv");
	    boolean fileExists = file.exists();
	
	    try (FileOutputStream fos = new FileOutputStream(file, true)) {
	        if (!fileExists) {
	            fos.write("totalNodes, connectivityModel, simulationTime, sentMessages, election, application, statusP2P, total\r\n".getBytes());
	        }
	        
	        fos.write((totalNodes + ", " + connModel + ", " + steps + ", " + sentMessages + ", " + 
	        		electionMessages + ", " + applicationMessages + ", " + statusP2PMessages + ", " + totalMessages + "\r\n").getBytes());
	        fos.close();
	    }
	}
}

