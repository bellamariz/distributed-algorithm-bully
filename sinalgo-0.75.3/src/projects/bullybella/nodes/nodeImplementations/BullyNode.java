package projects.bullybella.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import projects.bullybella.nodes.messages.ApplicationMessage;
import projects.bullybella.nodes.messages.BullyMessage;
import projects.bullybella.states.NodeState;
import projects.bullybella.states.NodeStateDown;
import projects.bullybella.states.NodeStateElection;
import projects.bullybella.states.NodeStateNormal;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.exception.SinalgoFatalException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;

public class BullyNode extends Node{
	
	public int ID;
	public int currCoordID;
	public int halterID;
	public int totalAnswersReceived;
	public NodeState state;
	public List<Integer> up;
	public boolean isCandidate;
	
	public BullyNode() {
		this.ID = new java.util.Random().nextInt(500);
		this.currCoordID = 0;
		this.halterID = 0;
		this.totalAnswersReceived = 0;
		this.up = new ArrayList<Integer>();
	}
	
	
	//
	// getters and setters
	//
	
	public boolean isCandidate() {
		return isCandidate;
	}

	public void setCandidate(boolean isCandidate) {
		this.isCandidate = isCandidate;
	}
	
	public int getID() {
		return ID;
	}
	
	public int getCurrCoordID() {
		return currCoordID;
	}

	public void setCurrCoordID(int currCoordID) {
		this.currCoordID = currCoordID;
	}

	public int getHalterID() {
		return halterID;
	}

	public void setHalterID(int halterID) {
		this.halterID = halterID;
	}

	public NodeState getState() {
		return state;
	}

	public void setState(NodeState state) {
		this.state = state;
	}
	
	
	//
	// class methods
	//
	
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
		default:
			break;
        }
	}
	 
	public boolean isCoordenator() {
		return this.getCurrCoordID() == this.getID();
	}
	
	public void incrementAnswersReceived() {
		this.totalAnswersReceived++;
	}
	
	
	//
	// inherited methods from Node class
	//

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
		
		
	}

	@Override
	public void postStep() {
		
		
	}

	@Override
	public void checkRequirements() throws WrongConfigurationException {
		
		
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
