/*
BSD 3-Clause License

Copyright (c) 2007-2013, Distributed Computing Group (DCG)
                         ETH Zurich
                         Switzerland
                         dcg.ethz.ch
              2017-2018, André Brait

All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

* Neither the name of the copyright holder nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package projects.bully_election_bella.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import projects.bully_election_bella.CustomGlobal;
import projects.bully_election_bella.nodes.messages.ApplicationMessage;
import projects.bully_election_bella.nodes.messages.BullyMessage;
import projects.bully_election_bella.nodes.messages.StatusMessage;
import projects.bully_election_bella.nodes.timers.ApplicationMessageTimer;
import projects.bully_election_bella.nodes.timers.ElectionTimeoutTimer;
import projects.bully_election_bella.nodes.timers.ElectionUpdateTimer;
import projects.bully_election_bella.nodes.timers.NodeDownTimer;
import projects.bully_election_bella.states.ElectionNodeState;
import projects.bully_election_bella.states.ElectionNodeStateDown;
import projects.bully_election_bella.states.ElectionNodeStateElectionCandidate;
import projects.bully_election_bella.states.ElectionNodeStateElectionParticipant;
import projects.bully_election_bella.states.ElectionNodeStateNormal;
import projects.bully_election_bella.states.ElectionNodeStateNormalCoordinator;
import sinalgo.configuration.Configuration;
import sinalgo.configuration.CorruptConfigurationEntryException;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.exception.SinalgoFatalException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;
import sinalgo.runtime.nodeCollection.NodeCollectionInterface;
import sinalgo.tools.Tools;
import sinalgo.tools.logging.Logging;

/**
 *  Implements node type ElectionNode (network nodes which will go through ELECTION).
 */
public class ElectionNode extends Node {
	
	CustomGlobal global = (CustomGlobal) Tools.getCustomGlobal();

    public ElectionNodeState state;
    public long c;
    public int coordinatorId;
    public int reliability;
    
    // when in AntennaConnection model
    private Antenna currentAntenna = null;
    public Antenna getCurrentAntenna() { return currentAntenna; }
    public void setCurrentAntenna(Antenna a) { currentAntenna = a; }
    
    // up: nodes not DOWN, application: node's latest application status, neighbours: when in DirectConnection model
    public ArrayList <Integer> up = new ArrayList<>();
    public Map <Integer,Date> application = new HashMap<>();
    public Map <Integer,Boolean> neighbours = new HashMap<>();

    // node timers
    public ElectionTimeoutTimer activeTimeout = new ElectionTimeoutTimer(BullyMessage.MessageType.AYUp);
    public ApplicationMessageTimer appMessageTimer = null;
    public NodeDownTimer nodeDownTimer = null;

    Logging logger = Logging.getLogger("election_log");

    public void setState(ElectionNodeState.States state) {
        switch (state) {
            case Normal:
                this.state = new ElectionNodeStateNormal(this);
                break;
            case NormalCoordinator:
                this.state = new ElectionNodeStateNormalCoordinator(this);
                break;
            case ElectionCandidate:
                this.state = new ElectionNodeStateElectionCandidate(this);
                break;
            case ElectionParticipant:
                this.state = new ElectionNodeStateElectionParticipant(this);
                break;
            case Down:
                this.state = new ElectionNodeStateDown(this);
                break;
        }
    }
    
    public void startApplication() {
    	Tools.appendToOutput("\n[Application] Node " + this.ID + " starting application broadcast\n");
        this.appMessageTimer = new ApplicationMessageTimer(this.ID);
        this.appMessageTimer.shouldFire = true;
    }
    
    public void stopApplication() {
    	if (this.appMessageTimer != null) {
    		Tools.appendToOutput("[Application] Node " + this.ID + " stopping application broadcast\n");
        	this.appMessageTimer.shouldFire = false;
        	this.appMessageTimer = null;
        }
    }
    
    public void checkAllNeighbours() {
		for (Node neighbour: Tools.getNodeList()) {
			if (neighbour instanceof ElectionNode) {
				StatusMessage statusMsg = new StatusMessage(this.ID, neighbour.ID);
				this.sendDirect(statusMsg, neighbour);
			}
		}
    }

    @Override
    public void handleMessages(Inbox inbox) {
        while (inbox.hasNext()) {
        	Message msg = inbox.next();
        	
        	if (msg instanceof BullyMessage) {
        		BullyMessage bullyMsg = (BullyMessage) msg;
        		global.electionMessages++;
        		this.state.handleMessage(bullyMsg);
        	}else if (msg instanceof ApplicationMessage) {
        		ApplicationMessage appMsg = (ApplicationMessage) msg;
        		global.applicationMessages++;
        		this.state.handleApplication(appMsg);
        	}else if (msg instanceof StatusMessage) {
        		StatusMessage statusMsg = (StatusMessage) msg;
        		global.statusP2PMessages++;
        		int senderID = (int) statusMsg.senderID;
        		this.neighbours.put((int) senderID, true);
        	}
        }
    }

    @Override
    public void preStep() {
    }

    @Override
    public void init() {
        this.c = 0;
        this.reliability = 50000;
        this.state = new ElectionNodeStateDown(this);

        // initialize the node
        try {
            this.setDefaultDrawingSizeInPixels(Configuration.getIntegerParameter("MobileNode/Size"));
        } catch (CorruptConfigurationEntryException e) {
            throw new SinalgoFatalException(e.getMessage());
        } catch (IllegalArgumentException e) {}

		
		// call update timer
        try {
            new ElectionUpdateTimer().startRelative(1, this);
        } catch (CorruptConfigurationEntryException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void neighborhoodChange() {
    }

    @Override
    public void draw(Graphics g, PositionTransformation pt, boolean highlight) {
        Color nodeColor;

        if (state instanceof ElectionNodeStateDown) {
            nodeColor = Color.RED;
        } else if (state instanceof ElectionNodeStateNormalCoordinator) {
            nodeColor = Color.BLUE;
        } else if (state instanceof  ElectionNodeStateElectionParticipant) {
            nodeColor = Color.YELLOW;
        } else if (state instanceof ElectionNodeStateElectionCandidate) {
            nodeColor = Color.ORANGE;
        } else {
            nodeColor = Color.GREEN;
        }

        // set the color of this node
        this.setColor(nodeColor);

        // set the text of this node
        String text = "<" + this.ID + ">";

        // draw the node as a circle with the text inside
        super.drawNodeAsDiskWithText(g, pt, highlight, text, 14, Color.WHITE);
    }

    @Override
    public void postStep() {
    }

    @Override
    public void checkRequirements() throws WrongConfigurationException {
    }
}
