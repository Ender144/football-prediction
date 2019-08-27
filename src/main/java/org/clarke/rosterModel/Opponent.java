package org.clarke.rosterModel;

public class Opponent {
	private String name;
	private Team team;

	public Opponent(String name, Team team) {
		this.name = name;
		this.team = team;
	}

	public Opponent() {
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Opponent && getName() != null && getName().equals(((Opponent) other).getName());
	}

	@Override
	public String toString() {
		return getName() + "; " + getTeam().getFullTeamName();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}
}
