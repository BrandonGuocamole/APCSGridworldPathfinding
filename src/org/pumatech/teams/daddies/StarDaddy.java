package org.pumatech.teams.daddies;

import java.util.ArrayList;
import java.util.List;

import org.pumatech.ctf.AbstractPlayer;

import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;

public class StarDaddy extends AbstractPlayer {
	private ArrayList<Daddy> open;
	private ArrayList<Daddy> closed;
	private Location OGFlag;
	private Location goal;

	public StarDaddy(Location startLocation) {
		super(startLocation);
		open = new ArrayList<Daddy>();
		closed = new ArrayList<Daddy>();
	}

	public ArrayList<Location> getAllAdjacent(Location loc) {
		ArrayList<Location> locs = new ArrayList<Location>();
		for (int i = 0; i < 360; i += 45) {
			if (getGrid().isValid(loc.getAdjacentLocation(i))) {
				locs.add(loc.getAdjacentLocation(i));
			}
		}
		return locs;
	}

	public ArrayList<Location> getAllEmptyAdjacent(Location location) {
		ArrayList<Location> locs = new ArrayList<Location>();
		for (int i = 0; i < 360; i = i + 45) {
			Location loc = location.getAdjacentLocation(i);
			if (getGrid().isValid(loc)) {
				if (getGrid().get(loc) == null) {
					locs.add(loc);
				}
			}
		}
		return locs;
	}

	public ArrayList<Location> getAdjacent(Location loc) {
		ArrayList<Location> locs = getAllAdjacent(loc);
		for (int i = 0; i < locs.size(); i++) {
			if (getGrid().get(loc) == null && loc != getLocation() && getScore(loc, OGFlag) > 4) {
				locs.add(loc.getAdjacentLocation(i));
			}
		}
		return locs;
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

	public Daddy openLowest() {
		Daddy lowest = open.get(0);
		for (int i = 0; i < open.size(); i++) {
			if (lowest.getRank() > open.get(i).getRank()) {
				lowest = open.get(i);
			}
		}
		return lowest;
	}

	public int getPythag(Location a, Location b) {
		return (int) (Math.pow(
				Math.pow(Math.abs(a.getCol() - b.getCol()), 2 + Math.pow(Math.abs(a.getRow() - b.getRow()), 2)), 0.5));
	}

	public int getScore(Location a, Location b) {
		return Math.abs(a.getCol() - b.getCol()) + Math.abs(a.getRow() - b.getRow());
	}

	public int getCost(Location loc) {
		int cost = 0;
		ArrayList<Location> locs = getAllAdjacent(loc);
		int empty = 0;
		for (int i = 0; i < locs.size(); i++) {
			Location val = locs.get(i);
			Actor incumb = getGrid().get(val);
			if (val == null) {
				empty++;
			} else {
				if (!(incumb instanceof StarDaddy || incumb instanceof Bear) && incumb instanceof AbstractPlayer) {
					cost += 100;
				}
			}
		}
		if (empty < 6) {
			cost += 10;
		}
		if (empty < 4) {
			cost += 20;
		}
		if (empty < 2) {
			cost += 50;
		}
		return cost;
	}

	public Location aStar(Location a, Location b) {
		if (open.size() == 0) {
			open.add(new Daddy(a, a, 0));
		}
		Location location = a;
		while (a != b) {
			// http://theory.stanford.edu/~amitp/GameProgramming/ImplementationNotes.html
			Daddy current = openLowest();
			a = current.getLoc();
			open.remove(current);
			closed.add(current);
			ArrayList<Location> locs = getAllEmptyAdjacent(location);
			for (int i = 0; i < locs.size(); i++) {
				Location loc = locs.get(i);
				int cost = getPythag(location, b) + getCost(a);
				System.out.println(getPythag(location, b));
				System.out.println(getCost(a));
				int cc = current.getRank();
				if (open.contains(loc) && cost < cc) {
					open.remove(current);
				}
				if (closed.contains(loc) && cost < cc) {
					closed.remove(current);
				}
				if (!(open.contains(loc) || closed.contains(loc))) {
					open.add(new Daddy(loc, a, cost));
					a = loc;
				}
			}
		}
		Location val = closed.get(0).getLoc();
		closed.clear();
		open.clear();
		return val;
	}

	public Location getMoveLocation() {
		Location teamFlag = getTeam().getFlag().getLocation();
		Location opponentFlag = getTeam().getOpposingTeam().getFlag().getLocation();
		if (OGFlag == null) {
			OGFlag = teamFlag;
		}
		if (goal == null) {
			goal = getLocation();
		}
		if (goal == getLocation() || teamFlag()) {
			if (!hasFlag()) {
				goal = opponentFlag;
			} else {
				goal = teamFlag;
			}
		}
		return aStar(getLocation(), goal);
	}
}
