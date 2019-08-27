package org.clarke.rosterModel;

import java.util.List;

@SuppressWarnings("unused")
public class Team {
	private String id;
	private String name;
	private String market;
	private List<Player> players;

	public String getFullTeamName() {
		return market;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}
}
