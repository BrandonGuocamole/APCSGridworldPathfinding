package org.pumatech.teams.daddies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.pumatech.ctf.AbstractPlayer;

import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;

public class Bear extends AbstractPlayer {
	private Location goal;
	private Location OGFlag;

	public Bear(Location startLocation) {
		super(startLocation);
	}

	public int getSector(Location loc) {
		// 50 tall, 100 wide
		// 5 sectors tall
		// 10 sectors wide
		if (loc.getRow() < 10) {
			return 1;
		}
		if (loc.getRow() < 20) {
			return 2;
		}
		if (loc.getRow() < 30) {
			return 3;
		}
		if (loc.getRow() < 40) {
			return 4;
		}
		return 5;
	}

	public int getConcentration() {
		int one = 0;
		int two = 0;
		int three = 0;
		int four = 0;
		int five = 0;
		List<AbstractPlayer> players = getTeam().getOpposingTeam().getPlayers();
		for (int i = 0; i < players.size(); i++) {
			int sect = getSector(players.get(i).getLocation());
			if (sect == 1) {
				one++;
			}
			if (sect == 2) {
				two++;
			}
			if (sect == 3) {
				three++;
			}
			if (sect == 4) {
				four++;
			}
			if (sect == 5) {
				five++;
			}
		}
		int[] slicc = { one, two, three, four, five };
		Integer j = slicc[0];
		int index = 0;
		for (int i = 0; i < 5; i++) {
			if (slicc[i] > j) {
				j = slicc[i];
				index = i;
			}
		}
		return index + 1;
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
				if ((item == null
						|| (item instanceof AbstractPlayer && !(item instanceof StarDaddy || item instanceof Bear)))
						&& hScore(loc, OGFlag) > 3) {
					locs.add(loc);
				}
			}
		}
		return locs;
	}

	public boolean teamFlag() {
		List<AbstractPlayer> players = getTeam().getOpposingTeam().getPlayers();
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).hasFlag()) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<Location> onSide() {
		List<AbstractPlayer> players = getTeam().getOpposingTeam().getPlayers();
		ArrayList<Location> locs = new ArrayList<Location>();
		if (OGFlag.getCol() < 49) {
			for (int i = 0; i < players.size(); i++) {
				if (players.get(i).getLocation().getCol() < 49) {
					locs.add(players.get(i).getLocation());
				}
			}
		} else {
			for (int i = 0; i < players.size(); i++) {
				if (players.get(i).getLocation().getCol() > 50) {
					locs.add(players.get(i).getLocation());
				}
			}
		}
		if (locs.size() == 0) {
			return locs;
		}
		ArrayList<Location> loc = new ArrayList<Location>();
		Integer j = null;
		int index = 0;
		for (int i = 0; i < locs.size(); i++) {
			if (j == null || hScore(getLocation(), locs.get(i)) < j) {
				j = hScore(getLocation(), locs.get(i));
				index = i;
			}
		}
		loc.addAll(locs);
		loc.remove(locs.get(index));
		loc.add(0, locs.get(index));
		return loc;
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
		return total;
	}

	public Location getMoveLocation() {
		Location teamFlag = getTeam().getFlag().getLocation();
		Location opponentFlag = getTeam().getOpposingTeam().getFlag().getLocation();
		Location location = getLocation();
		if (OGFlag == null) {
			OGFlag = teamFlag;
		}
		if (teamFlag()) {
			goal = teamFlag;
		} else {
			// if (goal == null) {
			// if (OGFlag.getCol() < 50) {
			// goal = new Location(25, 40);
			// } else {
			// goal = new Location(25, 60);
			// }
			// } else if (hScore(location, goal) < 4) {
			// if (OGFlag.getCol() < 50) {
			// if (getStartLocation().getRow() > 25) {
			// if (goal.equals(new Location(45, 40))) {
			// goal = new Location(25, 40);
			// } else {
			// goal = new Location(45, 40);
			// }
			// } else {
			// if (goal.equals(new Location(5, 40))) {
			// goal = new Location(25, 40);
			// } else {
			// goal = new Location(5, 40);
			// }
			// }
			// } else {
			// if (getStartLocation().getRow() > 25) {
			// if (goal.equals(new Location(45, 60))) {
			// goal = new Location(25, 60);
			// } else {
			// goal = new Location(45, 60);
			// }
			// } else {
			// if (goal.equals(new Location(5, 60))) {
			// goal = new Location(25, 60);
			// } else {
			// goal = new Location(5, 60);
			// }
			// }
			// }
			// }
			if (goal == null) {
				if (OGFlag.getCol() < 50) {
					goal = new Location(25, 40);
				} else {
					goal = new Location(25, 60);
				}
			} else if (hScore(location, goal) < 4) {
				if (OGFlag.getCol() < 50) {
					if (getStartLocation().getRow() == 5) {
						if (goal.equals(new Location(0, 20))) {
							goal = new Location(15, 35);
						} else {
							goal = new Location(0, 20);
						}
					} else if (getStartLocation().getRow() == 20) {
						if (goal.equals(new Location(15, 35))) {
							goal = new Location(24, 40);
						} else {
							goal = new Location(15, 35);
						}
					} else if (getStartLocation().getRow() == 30) {
						if (goal.equals(new Location(35, 35))) {
							goal = new Location(25, 40);
						} else {
							goal = new Location(35, 35);
						}
					} else {
						if (goal.equals(new Location(49, 20))) {
							goal = new Location(35, 35);
						} else {
							goal = new Location(49, 20);
						}
					}
				} else {
					if (getStartLocation().getRow() == 5) {
						if (goal.equals(new Location(0, 80))) {
							goal = new Location(15, 65);
						} else {
							goal = new Location(0, 80);
						}
					} else if (getStartLocation().getRow() == 20) {
						if (goal.equals(new Location(15, 65))) {
							goal = new Location(24, 60);
						} else {
							goal = new Location(15, 65);
						}
					} else if (getStartLocation().getRow() == 30) {
						if (goal.equals(new Location(35, 65))) {
							goal = new Location(25, 60);
						} else {
							goal = new Location(35, 65);
						}
					} else {
						if (goal.equals(new Location(49, 80))) {
							goal = new Location(35, 65);
						} else {
							goal = new Location(49, 80);
						}
					}
				}
			}
		}
		ArrayList<Location> locs = onSide();
		if (locs.size() != 0) {
			goal = locs.get(0);
		}
		HashMap<Location, Location> cameFrom = aStar(getLocation(), goal);
		ArrayList<Location> path = this.reconstructPath(cameFrom, goal);
		return path.get(path.size() - 2);
	}
}
