package org.pumatech.teams.sample;

import java.awt.Color;

import org.pumatech.ctf.Team;
import org.pumatech.teams.daddies.ShovethInneth;

import info.gridworld.grid.Location;

public class SampleTeam extends Team {

	public SampleTeam() {
		this(Color.DARK_GRAY);
	}
	
	public SampleTeam(Color color) {
		this("Sample Team", color);
	}
	
	public SampleTeam(String name, Color color) {
		super(name, color);
		addPlayer(new ShovethInneth(new Location(5, 30)));
		addPlayer(new ShovethInneth(new Location(10, 30)));
		addPlayer(new ShovethInneth(new Location(15, 30)));
		addPlayer(new ShovethInneth(new Location(20, 30)));
		addPlayer(new ShovethInneth(new Location(30, 30)));
		addPlayer(new ShovethInneth(new Location(35, 30)));
		addPlayer(new ShovethInneth(new Location(40, 30)));
		addPlayer(new ShovethInneth(new Location(45, 30)));
	}

}
