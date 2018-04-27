package org.pumatech.teams.daddies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.pumatech.ctf.AbstractPlayer;

import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;

public class AStar extends AbstractPlayer {

	public AStar(Location startLocation) {
		super(startLocation);
		open = new ArrayList<Location>();
		closed = new ArrayList<Location>();
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
			if (getGrid().get(loc) == null && loc != getLocation()
					&& getScore(loc, getTeam().getFlag().getLocation()) > 4) {
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

	public static double hScore(Location a, Location b) {
		double dCol = Math.abs(a.getCol() - b.getCol());
		double dRow = Math.abs(a.getRow() - b.getRow());
		return Math.max(dCol, dRow);
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

	public Location aStar(Location start, Location goal) {
		ArrayList<Location> open = new ArrayList();
		ArrayList<Location> closed = new ArrayList();
		HashMap<Location, Integer> gscore = new HashMap<Location, Integer>();
		HashMap<Location, Integer> fscore = new HashMap<Location, Integer>();

		open.add(start);
		gscore.put(start, 0);
		fscore.put(start, (int) hScore(start, goal));

		while (open.size() != 0) {
			Location current = Collections.min(fscore.entrySet(), Comparator.comparing(Entry::getValue));
			
		}
		return new Location(0, 0);
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
