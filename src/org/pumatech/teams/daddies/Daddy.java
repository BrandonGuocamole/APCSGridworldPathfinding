package org.pumatech.teams.daddies;

import info.gridworld.grid.Location;

public class Daddy {
	public Location loc;
	public Location parent;
	public int rank;
	
	public Daddy(Location location, Location daddy, int val) {
		loc = location;
		parent = daddy;
		rank = val;
	}
	
	public Location getLoc() {
		return loc;
	}

	public void setLoc(Location loc) {
		this.loc = loc;
	}

	public Location getParent() {
		return parent;
	}

	public void setParent(Location parent) {
		this.parent = parent;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}
	
	

}
