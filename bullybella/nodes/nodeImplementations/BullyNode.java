package projects.bullybella.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import projects.bullybella.LogL;
import projects.bullybella.nodes.messages.ApplicationMessage;
import projects.bullybella.nodes.messages.BullyMessage;
import projects.bullybella.states.NodeState;
import projects.bullybella.states.NodeStateDown;
import projects.bullybella.states.NodeStateElection;
import projects.bullybella.states.NodeStateNormal;
import projects.bullybella.states.NodeStateReorg;
import sinalgo.configuration.Configuration;
import sinalgo.configuration.CorruptConfigurationEntryException;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.exception.SinalgoFatalException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;
import sinalgo.tools.Tuple;
import sinalgo.tools.logging.Logging;

public class BullyNode extends Node{
	
	public Logging logger = Logging.getLogger();

	// node variables
	public int ID;
	public int currCoordID;
	public int halterID;
	public int totalAnswersReceived;
	public NodeState state;
	public List<Tuple<Long,Boolean>> up = new ArrayList<Tuple<Long, Boolean>>();
	public boolean isCandidate;
	
	public BullyNode() {
		this.ID = new java.util.Random().nextInt(500);
		this.currCoordID = 0;
		this.halterID = 0;
		this.totalAnswersReceived = 0;
	}
	
	// getters and setters
	public boolean isCandidate() {
		return isCandidate;
	}

	public void setCandidate(boolean isCandidate) {
		this.isCandidate = isCandidate;
	}
	
	public long getID() {
		return ID;
	}
	
	public long getCurrCoordID() {
		return currCoordID;
	}

	public void setCurrCoordID(int currCoordID) {
		this.currCoordID = currCoordID;
	}

	public long getHalterID() {
		return halterID;
	}

	public void setHalterID(int halterID) {
		this.halterID = halterID;
	}

	 public void readStates(NodeState.States states) {
        switch (states) {
        case Normal:
            this.state = new NodeStateNormal(this);
            break;
		case Down:
			this.state = new NodeStateDown(this);
			break;
		case Election:
			this.state = new NodeStateElection(this);
			break;
		case Reorganizing:
			this.state = new NodeStateReorg(this);
			break;
		default:
			break;
        }
	}
	
	
	
	// auxiliary methods
	private boolean isCoordenator() {
		return this.getCurrCoordID() == this.getID();
	}
	
	private void incrementAnswersReceived() {
		this.totalAnswersReceived++;
	}
	
	
	// inherited methods from Node class

	@Override
	public void handleMessages(Inbox inbox) {
        while (inbox.hasNext()) {
        	Message msg = inbox.next();
        	
        	if (msg instanceof BullyMessage) {
        		BullyMessage bMsg = (BullyMessage) msg;
        		this.state.handleMessage(bMsg);
        	}
        	
            if (msg instanceof ApplicationMessage) {
            	// TODO: Normal application messages
            }
            
        }
		
	}

	@Override
	public void preStep() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init() {
		System.out.println("Running BullyNode.init()...");
		
		try {
			this.state = new NodeStateDown(this);
			System.out.println("New Node <" + this.ID + ">");
		} catch (SinalgoFatalException e) {
			System.out.println("Failed to initialize Sinalgo");
		}
	}

	@Override
	public void neighborhoodChange() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postStep() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkRequirements() throws WrongConfigurationException {
		// TODO Auto-generated method stub
		
	}
	
    @Override
    public void draw(Graphics g, PositionTransformation pt, boolean highlight) {
        Color nodeColor;
        long nodeID = this.getID();
        NodeState nodeState = this.state;

        if (nodeState instanceof NodeStateNormal) {
            nodeColor = Color.BLUE;
        } else if (nodeState instanceof NodeStateElection) {
            nodeColor = Color.GREEN;
        } else if (nodeState instanceof NodeStateReorg) {
            nodeColor = Color.YELLOW;
        } else if (nodeState instanceof NodeStateDown) {
            nodeColor = Color.RED;
        } else {
            nodeColor = Color.PINK;
        }
        
        if (this.isCoordenator()){
        	nodeColor = Color.ORANGE;
        }

        // set the color of this node
        this.setColor(nodeColor);

        // set the text of this node
        String text = "<" + nodeID + ">";

        // draw the node as a circle with the text inside
        super.drawNodeAsDiskWithText(g, pt, highlight, text, 50, Color.WHITE);
    }
	
}
