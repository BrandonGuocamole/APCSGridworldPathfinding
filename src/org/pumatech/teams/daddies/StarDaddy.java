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
		int row = Math.abs(a.getRow() - b.getRow());
		int col = Math.abs(a.getCol() - b.getCol());
		int squared = (int) (Math.pow(row, 2) + Math.pow(col, 2));
		return (int) (Math.pow(squared, 0.5));
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
			if (incumb == null) {
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
		while (open.size() != 0) {
			Daddy current = open.get(0);
			open.remove(0);
			closed.add(current);
			ArrayList<Location> locs = getAllEmptyAdjacent(current.getLoc());
			for (int i = 0; i < locs.size(); i++) {
				int cost = getPythag(current.getLoc(), b);
			}
		}
		Location val = closed.get(0).getLoc();
		closed.clear();
		open.clear();
		return val;
		/*
		 * // A* Search Algorithm 1. Initialize the open list 2. Initialize the closed
		 * list put the starting node on the open list (you can leave its f at zero)
		 * 
		 * 3. while the open list is not empty a) find the node with the least f on the
		 * open list, call it "q"
		 * 
		 * b) pop q off the open list
		 * 
		 * c) generate q's 8 successors and set their parents to q
		 * 
		 * d) for each successor i) if successor is the goal, stop search successor.g =
		 * q.g + distance between successor and q successor.h = distance from goal to
		 * successor (This can be done using many ways, we will discuss three
		 * heuristics- Manhattan, Diagonal and Euclidean Heuristics)
		 * 
		 * successor.f = successor.g + successor.h
		 * 
		 * ii) if a node with the same position as successor is in the OPEN list which
		 * has a lower f than successor, skip this successor
		 * 
		 * iii) if a node with the same position as successor is in the CLOSED list
		 * which has a lower f than successor, skip this successor otherwise, add the
		 * node to the open list end (for loop)
		 * 
		 * e) push q on the closed list end (while loop)
		 */
	}

	public Location getMoveLocation() {
		Location teamFlag = getTeam().getFlag().getLocation();
		Location opponentFlag = getTeam().getOpposingTeam().getFlag().getLocation();
		if (OGFlag == null) {
			OGFlag = teamFlag;
		}
		if (goal == null || teamFlag()) {
			if (!hasFlag()) {
				goal = opponentFlag;
			} else {
				goal = teamFlag;
			}
		}
		return aStar(getLocation(), goal);
	}
}
