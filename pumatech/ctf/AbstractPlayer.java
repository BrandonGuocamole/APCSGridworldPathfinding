package org.pumatech.ctf;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;

public abstract class AbstractPlayer extends Actor {

	private Team team;
	private boolean hasFlag;
	private Location startLocation;
	private int tagCoolDown;
	private int steps;
	
	public AbstractPlayer(Location startLocation) {
		this.startLocation = startLocation;
	}
	
	public final void act() {
		try {
			team.announceScores(steps);
			steps++;
			if (team.hasWon() || team.getOpposingTeam().hasWon())  {
				if (team.hasWon()) {
					if (hasFlag)
						setColor(Color.MAGENTA);
					else
						setColor(Color.YELLOW);
				}
				return;
			}
			
			if (steps >= Team.MAX_GAME_LENGTH) {
				if (team.getScore() > team.getOpposingTeam().getScore()) {
					team.setHasWon(true);
				} else if (team.getScore() == team.getOpposingTeam().getScore()) {
					team.setHasWon(true);
				}
			} else if (tagCoolDown > 0) {
				setColor(Color.BLACK);
				tagCoolDown--;
			} else {
				if (hasFlag) {
					setColor(Color.YELLOW);
					if (team.onSide(getLocation())) {
						team.setHasWon(true);
					}
				} else {
					setColor(team.getColor());
				}
				processNeighbors();
				final Location loc = new Location(-1, -1);
				Thread getMoveLocationThread = new Thread() {
					@Override
					public void run() {
						Location l = getMoveLocation();
						loc.setCol(l.getCol());
						loc.setRow(l.getRow());
					}
				};
				getMoveLocationThread.start();
				long timeLimit = 100 / team.getPlayers().size();
				long startTime = System.currentTimeMillis();
				while (!this.getGrid().isValid(loc) && System.currentTimeMillis() - startTime < timeLimit) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						
					}
				}
				if (!getMoveLocationThread.isInterrupted()) {
					getMoveLocationThread.interrupt();
				}
				makeMove(!this.getGrid().isValid(loc) ? this.getLocation() : loc); // fixed behavior
			}
		} catch (Exception e) {
			System.err.println("Player " + this + " has generated a runtime exception: " + e);
		}
	}
	
	private void processNeighbors() {
		List<Location> neighborLocations = getGrid().getOccupiedAdjacentLocations(getLocation());
		for (int i = neighborLocations.size() - 1; i >= 0; i--) {
			Actor neighbor = getGrid().get(neighborLocations.get(i));
			if (!(neighbor instanceof AbstractPlayer) || ((AbstractPlayer) neighbor).team.equals(team)) {
				neighborLocations.remove(i);
				if (neighbor instanceof Flag && !((Flag) neighbor).getTeam().equals(team)) {
					hasFlag = true;
					team.getOpposingTeam().getFlag().pickUp(this);
					team.scorePlay(ScoringPlay.CARRY);
				}
			}
		}
		if (team.onSide(getLocation())) {
			Collections.shuffle(neighborLocations);
			for (Location neighborLocation : neighborLocations) {
				Actor neighbor = getGrid().get(neighborLocation);
				if (((AbstractPlayer) neighbor).hasFlag() || Math.random() < (1d / neighborLocations.size())) {
					((AbstractPlayer) neighbor).tag();
					team.scorePlay(ScoringPlay.TAG);
				}
			}
		}
	}
	
	private void makeMove(Location loc) {
		if (loc == null) loc=getLocation();
		if (team.onSide(getLocation()) && getGrid().get(team.getFlag().getLocation()) instanceof Flag && team.nearFlag(getLocation())) {
			int dir = getLocation().getDirectionToward(team.getFlag().getLocation()) + Location.HALF_CIRCLE;
			loc = getLocation().getAdjacentLocation(dir);
			while (getGrid().get(loc) != null) {
				System.out.println(loc + "is occupied");
				loc = loc.getAdjacentLocation(dir);
			}
		}
		else {
			loc = getLocation().getAdjacentLocation(getLocation().getDirectionToward(loc));
		}
		if (getGrid().isValid(loc) && getGrid().get(loc) == null) {
			moveTo(loc);
			if (team.onSide(getLocation()))
				team.scorePlay(ScoringPlay.MOVE);
			else
				team.scorePlay(ScoringPlay.MOVE_ON_OPPONENT_SIDE);
		}
	}
	
	public abstract Location getMoveLocation();
	
	private void tag() {
		Location oldLoc = getLocation();
		Location nextLoc;
		do {
			nextLoc = team.adjustForSide(new Location((int) (Math.random() * getGrid().getNumRows()), 0), getGrid());
		} while (getGrid().get(nextLoc) != null);
		moveTo(nextLoc);
		tagCoolDown = 10;
		
		if (hasFlag) {
			team.getOpposingTeam().getFlag().putSelfInGrid(getGrid(), oldLoc);
			hasFlag = false;
		}
	}
	
	protected final void putSelfInGridProtected(Grid<Actor> grid, Location loc) {
		if (getGrid() != null)
			super.removeSelfFromGrid();
		hasFlag = false;
		steps = 0;
		tagCoolDown = 0;
		setColor(team.getColor());
		super.putSelfInGrid(grid, loc);
	}
	
	public final void removeSelfFromGrid() {
		System.err.println("Someone has cheated and tried to remove a player from the grid");
	}
	
	protected final void setTeam(Team team) {
		this.team = team;
		setColor(team.getColor());
	}
	
	protected final void setStartLocation(Location startLocation) {
		this.startLocation = startLocation;
	}
	
	public final boolean hasFlag() {
		return hasFlag;
	}
	
	protected final Location getStartLocation() {
		return startLocation;
	}
	
	public final Team getTeam() {
		return team;
	}

	public final int getSteps() {
		return steps;
	}	
}
