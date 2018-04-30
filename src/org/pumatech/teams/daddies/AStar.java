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
		for (int i = 180; i < 540; i = i + 45) {
			Location loc = location.getAdjacentLocation(i);
			if (getGrid().isValid(loc)) {
				if (getGrid().get(loc) == null) {
					locs.add(loc);
				}
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

	public static int hScore(Location a, Location b) {
		double row = Math.abs(a.getRow() - b.getRow());
		double col = Math.abs(a.getCol() - b.getCol());
		double squared = Math.pow(row, 2) + Math.pow(col, 2);
		return (int) (Math.pow(squared, 0.5));
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

	public HashMap<Location, Location> aStar(Location start, Location goal) {
		ArrayList<Location> open = new ArrayList<Location>();
		ArrayList<Location> closed = new ArrayList<Location>();
		HashMap<Location, Location> cameFrom = new HashMap<Location, Location>();
		HashMap<Location, Integer> gscore = new HashMap<Location, Integer>();
		HashMap<Location, Integer> fscore = new HashMap<Location, Integer>();
		open.add(start);
		gscore.put(start, 0);
		fscore.put(start, hScore(start, goal));
		while (open.size() != 0) {
			Location current = fscore.entrySet().stream()
					.min((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
			if (current.equals(goal)) {
				System.out.println(cameFrom);
				return cameFrom;
			}
			open.remove(current);
			closed.add(current);
			ArrayList<Location> adjacent = getAllEmptyAdjacent(current);
			// ^^ you were getting the same adjacent values. ur dumb
			// System.out.println("current: "+current);
			for (int i = 0; i < adjacent.size(); i++) {
				if (closed.contains(adjacent.get(i))) {
					continue;
				}
				if (!open.contains(adjacent.get(i))) {
					open.add(adjacent.get(i));
				}
				int tempGScore = gscore.get(current) + 1;
				cameFrom.put(adjacent.get(i), current);
				gscore.put(adjacent.get(i), tempGScore);
				fscore.put(adjacent.get(i), tempGScore + hScore(adjacent.get(i), goal));
				if (tempGScore >= gscore.get(adjacent.get(i))) {
					continue;
				}
			}
		}
		System.out.println(
				"u failed to find a single path? r u that dumb? how can one person named brandon be so imcompetent at coding?");
		return cameFrom;
	}

	public ArrayList<Location> reconstructPath(HashMap<Location, Location> cameFrom, Location current) {
		ArrayList<Location> total = new ArrayList<Location>();
		total.add(current);
		while (cameFrom.containsKey(current)) {
			current = cameFrom.get(current);
			total.add(current);
		}
		System.out.println(total);
		return total;
	}

	public Location getMoveLocation() {
		Location teamFlag = getTeam().getFlag().getLocation();
		Location opponentFlag = getTeam().getOpposingTeam().getFlag().getLocation();
		System.out.println("Opponent's Flag: " + opponentFlag);
		HashMap<Location, Location> cameFrom = this.aStar(this.getLocation(), opponentFlag);
		ArrayList<Location> path = this.reconstructPath(cameFrom, this.getLocation());
		return path.get(0);
	}
}