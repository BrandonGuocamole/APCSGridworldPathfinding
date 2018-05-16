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
	private Location goal;
	private Location OGFlag;
	private boolean hasflag;
	public static boolean alarm;

	public AStar(Location startLocation) {
		super(startLocation);
		hasflag = false;
	}

	public boolean hasflog() {
		if (!hasFlag()) {
			return false;
		}
		if (!hasflag && hasFlag()) {
			hasflag = true;
			return false;
		} else {
			return true;
		}
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
				if (item == null && hScore(loc, OGFlag) > 3) {
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
		return (int) (Math.max(row, col));
	}

	public double danger(Location start, Location goal) {
		double row = Math.abs(start.getRow() - goal.getRow());
		double col = Math.abs(start.getCol() - goal.getCol());
		double frighten = Math.max(row, col);
		List<AbstractPlayer> danger = getTeam().getOpposingTeam().getPlayers();
		for (int i = 0; i < danger.size(); i++) {
			AbstractPlayer oppo = danger.get(i);
			row = Math.abs(start.getRow() - oppo.getLocation().getRow());
			col = Math.abs(start.getCol() - oppo.getLocation().getCol());
			if (Math.abs(oppo.getLocation().getCol() - this.getLocation().getCol()) <= 1
					&& Math.abs(oppo.getLocation().getRow() - this.getLocation().getRow()) <= 1) {
				frighten += 999;
			} else {
				frighten += 10 / (Math.max(row, col));
			}
		}
		return frighten;
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
					cost += 10;
				}
			}
		}
		if (empty < 6) {
			cost += 1;
		}
		if (empty < 4) {
			cost += 2;
		}
		if (empty < 2) {
			cost += 5;
		}
		return cost;
	}

	public int gCalculator(Location a) {
		return 1;
	}

	public HashMap<Location, Location> aStar(Location start, Location goal) {
		ArrayList<Location> open = new ArrayList<Location>();
		ArrayList<Location> closed = new ArrayList<Location>();
		HashMap<Location, Location> cameFrom = new HashMap<Location, Location>();
		HashMap<Location, Double> gscore = new HashMap<Location, Double>();
		HashMap<Location, Double> fscore = new HashMap<Location, Double>();
		open.add(start);
		gscore.put(start, (double) 0);
		fscore.put(start, this.danger(start, goal));
		while (open.size() != 0) {
			// System.out.println(open.size());
			Location current = open.get(0);
			for (int i = 1; i < open.size(); i++) {
				if (fscore.get(open.get(i)).compareTo(fscore.get(current)) < 0) {
					current = open.get(i);
				}
			}
			if (getAllAdjacent(current).contains(goal)) {
				// System.out.println(cameFrom);
				cameFrom.put(goal, current);
				return cameFrom;
			}
			open.remove(current);
			closed.add(current);
			ArrayList<Location> adjacent = getGrid().getEmptyAdjacentLocations(current);
			for (int i = 0; i < adjacent.size(); i++) {
				if (closed.contains(adjacent.get(i))) {
					continue;
				}
				// System.out.println(open.contains(adjacent.get(i)));
				//
				// if (open.containsAll(adjacent)==false) {
				// open.add(adjacent.get(i));
				// }
				if (!open.contains(adjacent.get(i))) {
					open.add(adjacent.get(i));
				}
				double tempGScore = gscore.get(current) + gCalculator(adjacent.get(i));
				cameFrom.put(adjacent.get(i), current);
				gscore.put(adjacent.get(i), tempGScore);
				fscore.put(adjacent.get(i), tempGScore + this.danger(adjacent.get(i), goal));
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
		return total;
	}

	public boolean opponentHasFlag() {
		List<AbstractPlayer> players = getTeam().getOpposingTeam().getPlayers();
		Location teamFlag = getTeam().getFlag().getLocation();
		int asdf = -1;
		if(teamFlag.getCol()-50<0) {
			asdf = 1;
		}
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).hasFlag()) {
				for (int r = 0; r <= 3; r++) {
					for (int c = 0; r <= 3; c++) {
						Location search = new Location(players.get(i).getLocation().getRow() + asdf*r,
								players.get(i).getLocation().getCol() + asdf*c);
						if (this.getGrid().get(search) instanceof Bear) {
							return false;
						}
					}
				}
				return true;
			}
		}
		return false;
	}

	public Location getMoveLocation() {
		alarm = opponentHasFlag();
		Location flag;
		Location teamFlag = getTeam().getFlag().getLocation();
		Location opponentFlag = getTeam().getOpposingTeam().getFlag().getLocation();
		Location location = getLocation();

		if (OGFlag == null) {
			OGFlag = teamFlag;
		}
		if (teamFlag()) {
			if (!hasFlag()) {
				goal = opponentFlag;
			} else {
				goal = teamFlag;
			}
		} else {
			goal = opponentFlag;
		}
		if (hScore(location, opponentFlag) < 3 && !hasflog()) {
			return goal;
		}
		if(alarm) {
			List<AbstractPlayer> players = getTeam().getOpposingTeam().getPlayers();
			for(int i = 0; i<players.size();i++) {
				if(players.get(i).hasFlag()) {
					goal = players.get(i).getLocation();
				}
			}
		}
		HashMap<Location, Location> cameFrom = aStar(getLocation(), goal);
		ArrayList<Location> path = this.reconstructPath(cameFrom, goal);
		return path.get(path.size() - 2);
	}
}