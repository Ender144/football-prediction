package org.clarke.database.regularSeasonModel;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@SuppressWarnings({"WeakerAccess", "unused"})
@Table(schema = "regular_season", name = "Wind", uniqueConstraints = {@UniqueConstraint(columnNames = "wind_id")})
public class DbWind {
	@Transient
	public static final String ID = "wind_id";
	@Transient
	public static final String SPEED = "speed";
	@Transient
	public static final String DIRECTION = "direction";

	@Id
	@GeneratedValue
	@Column(name = ID)
	private int id;

	@Column(name = SPEED)
	private String speed;

	@Column(name = DIRECTION)
	private String direction;

	@OneToOne(mappedBy = "dbWind", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private DbWeather dbWeather;

	public DbWind() {}

	public DbWeather getDbWeather() {
		return dbWeather;
	}

	public void setDbWeather(DbWeather dbWeather) {
		this.dbWeather = dbWeather;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}
}
