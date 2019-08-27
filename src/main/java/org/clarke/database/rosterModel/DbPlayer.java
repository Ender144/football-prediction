package org.clarke.database.rosterModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@SuppressWarnings({"WeakerAccess", "unused"})
@Table(schema = "roster", name = "Player", uniqueConstraints = {@UniqueConstraint(columnNames = "player_id")})
public class DbPlayer {
	@Transient
	public static final String ID = "player_id";
	@Transient
	public static final String NAME_FULL = "name_full";
	@Transient
	public static final String NAME_FIRST = "name_first";
	@Transient
	public static final String NAME_LAST = "name_last";
	@Transient
	public static final String NAME_ABBR = "name_abbr";
	@Transient
	public static final String BIRTH_PLACE = "birthplace";
	@Transient
	public static final String HEIGHT = "height";
	@Transient
	public static final String WEIGHT = "weight";
	@Transient
	public static final String POSITION = "position";
	@Transient
	public static final String JERSEY_NUMBER = "jersey_number";
	@Transient
	public static final String STATUS = "status";
	@Transient
	public static final String EXPERIENCE = "experience";

	@Id
	@Column(name = ID)
	private String id;

	@Column(name = NAME_FULL)
	private String name_full;

	@Column(name = NAME_FIRST)
	private String name_first;

	@Column(name = NAME_LAST)
	private String name_last;

	@Column(name = NAME_ABBR)
	private String name_abbr;

	@Column(name = BIRTH_PLACE)
	private String birth_place;

	@Column(name = HEIGHT)
	private int height;

	@Column(name = WEIGHT)
	private int weight;

	@Column(name = POSITION)
	private String position;

	@Column(name = JERSEY_NUMBER)
	private int jersey_number;

	@Column(name = STATUS)
	private String status;

	@Column(name = EXPERIENCE)
	private String experience;

	@ManyToOne
	@JoinColumn(name = DbTeam.ID)
	private DbTeam dbTeam;

	public DbPlayer() {}

	public String getBirth_place() {
		return birth_place;
	}

	public void setBirth_place(String birth_place) {
		this.birth_place = birth_place;
	}

	public DbTeam getDbTeam() {
		return dbTeam;
	}

	public void setDbTeam(DbTeam dbTeam) {
		this.dbTeam = dbTeam;
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
