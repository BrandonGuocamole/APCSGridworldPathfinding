package org.pumatech.teams.daddies;

import java.util.ArrayList;
import java.util.List;

import org.pumatech.ctf.AbstractPlayer;

import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;

public class ShovethInneth extends AbstractPlayer {
	private ArrayList<Location> recent;
	private Location goal;
	private Location OGFlag;

	public ShovethInneth(Location startLocation) {
		super(startLocation);
		recent = new ArrayList<Location>();
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

	public ArrayList<Location> getAdjacent(Location loc) {
		ArrayList<Location> locs = getAllAdjacent(loc);
		for (int i = 0; i < locs.size(); i++) {
			if (getGrid().get(loc) == null && loc != getLocation() && getScore(loc, OGFlag) > 4) {
				locs.add(loc.getAdjacentLocation(i));
			}
		}
		return locs;
	}

	public int getScore(Location a, Location b) {
		return Math.abs(a.getCol() - b.getCol()) + Math.abs(a.getRow() - b.getRow());
	}
	
	public boolean teamFlag() {
		List<AbstractPlayer> players = getTeam().getPlayers();
		for(int i = 0; i < players.size(); i++) {
			if(players.get(i).hasFlag()) {
				return true;
			}
		}
		return false;
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
		flag = goal;
		ArrayList<Location> adjacent = new ArrayList<Location>();
		for (int i = 0; i < 360; i = i + 45) {
			Location loc = getLocation().getAdjacentLocation(i);
			if (getGrid().isValid(loc)) {
				if (getGrid().get(loc) == null) {
					adjacent.add(loc);
				}
			}
		}
		ArrayList<Integer> scores = new ArrayList<Integer>();
		for (int i = 0; i < adjacent.size(); i++) {
			int k = getScore(adjacent.get(i), flag);
			ArrayList<Location> locs = getAdjacent(location);
			if (locs.size() < 6) {
				k += 2;
			}
			if (locs.size() < 4) {
				k += 4;
			}
			if (locs.size() < 3) {
				k += 6;
			}
			if (locs.size() < 2) {
				k += 10;
			}
			if (locs.size() == 0) {
				k += 100;
			} else {
				ArrayList<Location> locations = getAllAdjacent(location);
				for (int j = 0; j < locations.size(); j++) {
					Actor compare = getGrid().get(locations.get(j));
					if (!(compare instanceof ShovethInneth || compare instanceof Bear)
							&& compare instanceof AbstractPlayer) {
						k += 50;
					}
				}
				for (int j = 0; j < locs.size(); j++) {
					if (getScore(locs.get(i), flag) > getScore(location, flag)) {
						k += 3;
					} else {
						k--;
					}
				}
			}

			scores.add(k);
		}
		int winner = scores.get(0);
		int index = 0;
		for (int i = 0; i < adjacent.size(); i++) {
			if (winner > scores.get(i)) {
				winner = scores.get(i);
				index = i;
			}
		}
		for (int i = 0; i < recent.size(); i++) {
			if (recent.get(i) == location) {
				int col = location.getCol();
				int row = location.getRow();
				Location pot = new Location(row + 3, col);
				if (grid.get(pot) == null && grid.isValid(pot)) {
					goal = pot;
				} else {
					pot = new Location(row - 3, col);
					if (grid.get(pot) == null && grid.isValid(pot)) {
						goal = pot;
					} else {
						pot = new Location(row, col - 3);
						if (grid.get(pot) == null && grid.isValid(pot)) {
							goal = pot;
						} else {
							pot = new Location(row, col + 3);
							if (grid.get(pot) == null && grid.isValid(pot)) {
								goal = pot;
							}
						}
					}
				}

			}
		}
		if (recent.size() > 3) {
			recent.remove(0);
		}
		recent.add(location);
		return adjacent.get(index);
	}
}
