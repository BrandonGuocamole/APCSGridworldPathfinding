package org.pumatech.teams.daddies;

import java.util.ArrayList;

import org.pumatech.ctf.AbstractPlayer;

import info.gridworld.grid.Location;

public class ShovethInneth extends AbstractPlayer {
	private ArrayList<Location> recent;

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
			if (getGrid().get(loc) == null && loc != getLocation()) {
				locs.add(loc.getAdjacentLocation(i));
			}
		}
		return locs;
	}

	public int getScore(Location a, Location b) {
		return Math.abs(a.getCol() - b.getCol()) + Math.abs(a.getRow() - b.getRow());
	}

	public Location getMoveLocation() {
		// implement better not sticking
		Location flag;
		Location teamFlag = getTeam().getFlag().getLocation();
		Location opponentFlag = getTeam().getOpposingTeam().getFlag().getLocation();
		Location location = getLocation();
		if (!hasFlag()) {
			flag = opponentFlag;
		} else {
			flag = teamFlag;
		}
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
					if (getGrid().get(locations.get(j)) instanceof AbstractPlayer) {
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
		if (recent.size() > 2) {
			recent.remove(0);
		}
		recent.add(location);
		return adjacent.get(index);
	}
}
