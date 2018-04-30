package org.pumatech.teams.daddies;

import java.awt.Color;

import org.pumatech.ctf.Team;
import org.pumatech.teams.sample.BeelinePlayer;
import org.pumatech.teams.sample.RandomPlayer;

import info.gridworld.grid.Location;

public class DaddyTeam extends Team {
	public DaddyTeam() {
		this(Color.RED);
	}

	public DaddyTeam(Color color) {
		this("Daddy Team", color);
	}

	public DaddyTeam(String name, Color color) {
		super(name, color);
		addPlayer(new AStar(new Location(5, 30)));
//		addPlayer(new Bear(new Location(10, 30)));
//		addPlayer(new Bear(new Location(15, 30)));
//		addPlayer(new Bear(new Location(20, 30)));
//		addPlayer(new Bear(new Location(30, 30)));
//		addPlayer(new StarDaddy(new Location(35, 30)));
//		addPlayer(new StarDaddy(new Location(40, 30)));
//		addPlayer(new StarDaddy(new Location(45, 30)));
	}
}
