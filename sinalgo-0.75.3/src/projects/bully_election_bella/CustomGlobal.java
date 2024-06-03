
package projects.bully_election_bella;


import java.io.File;
import java.io.FileOutputStream;

import projects.bully_election_bella.nodes.nodeImplementations.ElectionNode;
import projects.bully_election_bella.nodes.timers.NodeDownTimer;
import projects.bully_election_bella.states.ElectionNodeState;
import projects.bully_election_bella.states.ElectionNodeStateNormal;
import projects.bully_election_bella.states.ElectionNodeStateNormalCoordinator;
import sinalgo.configuration.CorruptConfigurationEntryException;
import sinalgo.exception.SinalgoFatalException;
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

	public long workDone = 0;
	public long messagesSent = 0;
	public int totalNodes = 0;
	public String maliciousStr;

	public String baseFilepath = "output\\";

	public String algoName = "STd_4";
	
	
	public static double TIME_MSG_STATUS_DELIVERY = 2.0;
	public static double TIME_MSG_STATUS_REPLY = 3.0;
	public static double TIME_MSG_STATUS_LIMIT = (2*TIME_MSG_STATUS_DELIVERY)+TIME_MSG_STATUS_REPLY;
	public boolean nodeDownExists = false;
	
	
	public void preRound() {
		// at the beginning of every round, get a random node
		Node node = Tools.getNodeList().getRandomNode();
		
		// if node is ElectionNode + there are no nodes programmed with a NodeDownTimer
		if (node instanceof ElectionNode && !nodeDownExists) {
			ElectionNode electionNode = (ElectionNode) node;
			
			// if node is in NORMAL state, create a random timer to take it down
			if (electionNode.state instanceof ElectionNodeStateNormal || 
					electionNode.state instanceof ElectionNodeStateNormalCoordinator) {
				
				if (electionNode.nodeDownTimer == null) {
					electionNode.nodeDownTimer = new NodeDownTimer(electionNode);
					electionNode.nodeDownTimer.enable();
					
					// NodeDownTimer will disable itself after firing
					electionNode.nodeDownTimer.startRelative(20, electionNode);
					
					// flag tracking that there is a node with a NodeDownTimer
					nodeDownExists = true;
					
					Tools.appendToOutput("Node " + electionNode.ID + " is set up to fail in 20 round(s)\n");
				}
			}
		}
	}
	
	
	public void postRound() {
		NodeCollectionInterface nodes = Tools.getNodeList();
		
		for(Node node: nodes) {
			if (node instanceof ElectionNode) {
				ElectionNode electionNode = (ElectionNode) node;
				
				Tools.appendToOutput("Node " + electionNode.ID + " appActive: " + electionNode.appActive + "\n\n");
				
				if (electionNode.appMessageTimer != null && electionNode.appMessageTimer.shouldFire){
					electionNode.appMessageTimer.startRelative(1, electionNode);
				}
				
				// if node was programmed with a NodeDownTimer and it already fired
				if (nodeDownExists) {
					if (electionNode.nodeDownTimer != null && !electionNode.nodeDownTimer.active) {	
						electionNode.nodeDownTimer = null;
						nodeDownExists = false;
						
						Tools.appendToOutput("Resetting node down timer for node " + electionNode.ID + "\n");
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
			appendToFile();
		}catch (CorruptConfigurationEntryException e) {
			logger.logln("Favor criar NumberOfNodes em Config.xml");
            throw new SinalgoFatalException(e.getMessage());
		}catch (Exception e) {
			logger.logln("Falhou ao criar csv");
			e.printStackTrace();
		}

		logger.logln("Number of nodes: " + totalNodes);
		logger.logln("Total work done: " + workDone);
		logger.logln("Total messages sent: " + messagesSent);
		logger.logln("Simulation steps: " + Tools.getGlobalTime());
		logger.logln("Total messages " + (workDone+messagesSent));
	}

	public void appendToFile() throws Exception {
		/*
	FileOutputStream fos = new FileOutputStream(baseFilepath + algoName  + "_" + totalNodes + "_nodes.csv", true);
		fos.write((workDone + ", " + messagesSent + ", " + Tools.getGlobalTime() + "\r\n").getBytes());
		fos.close();
	}
	*/
	File file = new File(baseFilepath + algoName  + "_" + totalNodes + "_nodes.csv");
    boolean fileExists = file.exists();

    try (FileOutputStream fos = new FileOutputStream(file, true)) {
        if (!fileExists) {
            // Write headers if the file does not exist
            fos.write("workDone, messagesSent, simulationTime,TotalMenssages\r\n".getBytes());
        }
        fos.write((workDone + ", " + messagesSent + ", " + Tools.getGlobalTime() + ", " + (messagesSent+workDone)  + "\r\n").getBytes());
        fos.close();
    }
	}
}

