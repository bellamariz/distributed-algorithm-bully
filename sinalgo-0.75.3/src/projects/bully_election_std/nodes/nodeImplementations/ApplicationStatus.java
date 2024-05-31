package projects.bully_election_std.nodes.nodeImplementations;

import java.util.Date;

public class ApplicationStatus {
	private long nodeID;
	private Date lastUpdate;
	
	public ApplicationStatus(long nodeID, Date lastUpdate) {
		this.nodeID = nodeID;
		this.lastUpdate = lastUpdate;
	}

	public long getNodeID() {
		return nodeID;
	}
	
	public void setNodeID(long nodeID) {
		this.nodeID = nodeID;
	}
	
	public Date getLastUpdate() {
		return lastUpdate;
	}
	
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	

}
