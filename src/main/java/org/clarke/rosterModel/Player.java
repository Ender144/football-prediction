package org.clarke.rosterModel;

@SuppressWarnings("unused")
public class Player {
	private String id;
	private String name_full;
	private String name_first;
	private String name_last;
	private String name_abbr;
	private String birth_place;
	private int height;
	private int weight;
	private String position;
	private int jersey_number;
	private String status;
	private String experience;

	public String getBirth_place() {
		return birth_place;
	}

	public void setBirth_place(String birth_place) {
		this.birth_place = birth_place;
	}

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getJersey_number() {
		return jersey_number;
	}

	public void setJersey_number(int jersey_number) {
		this.jersey_number = jersey_number;
	}

	public String getName_abbr() {
		return name_abbr;
	}

	public void setName_abbr(String name_abbr) {
		this.name_abbr = name_abbr;
	}

	public String getName_first() {
		return name_first;
	}

	public void setName_first(String name_first) {
		this.name_first = name_first;
	}

	public String getName_full() {
		return name_full;
	}

	public void setName_full(String name_full) {
		this.name_full = name_full;
	}

	public String getName_last() {
		return name_last;
	}

	public void setName_last(String name_last) {
		this.name_last = name_last;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
}
