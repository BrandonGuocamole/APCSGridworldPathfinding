package org.pumatech.teams.daddies;

import java.util.ArrayList;
import java.util.List;

import org.pumatech.ctf.AbstractPlayer;

import info.gridworld.grid.Location;

public class StarDaddy extends AbstractPlayer {
	private ArrayList<Location> open;
	private ArrayList<Location> closed;
	
	public StarDaddy(Location startLocation) {
		super(startLocation);
		open = new ArrayList<Location>();
		closed = new ArrayList<Location>();
	}
	
	public boolean teamFlag() {
		List<AbstractPlayer> players = getTeam().getPlayers();
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).hasFlag()) {
				return true;
			}
		}
		return false;
	}

	public int getPythag(Location a, Location b) {
		return (int) (Math.pow(
				Math.pow(Math.abs(a.getCol() - b.getCol()), 2 + Math.pow(Math.abs(a.getRow() - b.getRow()), 2)), 0.5));
	}

	public ArrayList<Location> aStar(Location a, Location b) {
		Location loc = a;
		if(open.size() == 0) {
			open.add(loc);
		}
		Location location = a;
		while(location != b) {
			// http://theory.stanford.edu/~amitp/GameProgramming/ImplementationNotes.html

		}
		return null;
		// Heuristic: f(n) = g(n) + h(n)
		// Need g(n) and h(n) to be equally weighted
	}

	public Location getMoveLocation() {
		return null;

	}
}
