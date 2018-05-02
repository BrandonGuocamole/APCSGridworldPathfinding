package org.pumatech.teams.daddies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.pumatech.ctf.AbstractPlayer;
import org.pumatech.ctf.Flag;

import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;

public class StarDaddy extends AbstractPlayer {
	private Location goal;
	private Location OGFlag;

	public StarDaddy(Location startLocation) {
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
				Actor item = getGrid().get(loc);
				if (item == null || item instanceof Flag) {
					locs.add(loc);
				}
			}
		}
		return locs;
	}
	
	public ArrayList<Location> getAllValidEmpty(Location location, ArrayList<Location> open, ArrayList<Location> closed) {
		ArrayList<Location> locs = getAllEmptyAdjacent(location);
		
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
		return (int) (Math.max(row, col));
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

	public static int getPos(ArrayList<Daddy> papa, Location loc) {
		for (int i = 0; i < papa.size(); i++) {
			if (papa.get(i).getLoc().equals(loc)) {
				return i;
			}
		}
		return -1;
	}

	public static int getPos(ArrayList<Daddy> papa, int rank) {
		for (int i = 0; i < papa.size(); i++) {
			if (papa.get(i).getRank() == rank) {
				return i;
			}
		}
		return -1;
	}

	public static int getLowest(ArrayList<Daddy> papa) {
		int j = -1;
		for (int i = 0; i < papa.size(); i++) {
			if (papa.get(i).getRank() < j || j == -1) {
				j = papa.get(i).getRank();
			}
		}
		return j;
	}

	public static ArrayList<Integer> getAllPos(ArrayList<Daddy> papa, Location loc) {
		ArrayList<Integer> poss = new ArrayList<Integer>();
		for (int i = 0; i < papa.size(); i++) {
			if (papa.get(i).getLoc().equals(loc)) {
				poss.add(i);
			}
		}
		return poss;
	}

	public ArrayList<Daddy> aStar(Location start, Location goal) {
		ArrayList<Daddy> open = new ArrayList<Daddy>();
		ArrayList<Daddy> closed = new ArrayList<Daddy>();
		open.add(new Daddy(start, start, hScore(start, goal)));
		while (open.size() != 0) {
			System.out.println(getLowest(open));
			Location current = open.get(getPos(open, getLowest(open))).getLoc();
			Daddy currentDaddy = open.get(getPos(open, current));
			open.remove(currentDaddy);
			closed.add(currentDaddy);
			if (current.equals(goal)) {
				return closed;
			}
			ArrayList<Location> adjacent = getAllEmptyAdjacent(current);
			for (int i = 0; i < adjacent.size(); i++) {
				Location loc = adjacent.get(i);
				if (getPos(closed, loc) != -1) {
					continue;
				}
				if (getPos(open, loc) == -1) {
					int val = currentDaddy.getRank() + 1;
					val += hScore(loc, goal);
					val += getCost(loc);
					open.add(new Daddy(loc, current, val));
				}
				ArrayList<Integer> poss = getAllPos(open, loc);
				if (poss.size() > 1) {
					int j = -1;
					for (int k = 0; i < poss.size(); k++) {
						if (open.get(poss.get(k)).getRank() < j || j == -1) {
							j = open.get(poss.get(k)).getRank();
						}
					}
					Daddy papi = new Daddy(loc, current, j);
					open.set(getPos(open, loc), papi);
				}
			}
		}
		System.out.println(
				"u failed to find a single path? r u that dumb? how can one person named brandon be so imcompetent at coding?");
		return closed;
	}

	public Location getMoveLocation() {
		Location flag;
		Location teamFlag = getTeam().getFlag().getLocation();
		Location opponentFlag = getTeam().getOpposingTeam().getFlag().getLocation();
		Location location = getLocation();
		Grid<Actor> grid = getGrid();
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
		ArrayList<Daddy> path = aStar(location, goal);
		System.out.println(path);
		return path.get(0).getLoc();
	}
}
