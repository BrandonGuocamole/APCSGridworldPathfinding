package org.pumatech.teams.daddies;

import org.pumatech.ctf.AbstractPlayer;

import info.gridworld.grid.Location;

public class Bear extends AbstractPlayer {
	public Bear(Location startLocation) {
		super(startLocation);
	}

	public Location getMoveLocation() {
		if (hasFlag())
			return getTeam().getFlag().getLocation();
		return getTeam().getOpposingTeam().getFlag().getLocation();
	}
}
