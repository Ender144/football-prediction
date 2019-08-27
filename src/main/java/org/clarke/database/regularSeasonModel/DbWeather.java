package org.clarke.database.regularSeasonModel;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@SuppressWarnings({"WeakerAccess", "unused"})
@Table(schema = "regular_season", name = "Weather", uniqueConstraints = {@UniqueConstraint(columnNames = "weather_id")})
public class DbWeather {
	@Transient
	public static final String ID = "weather_id";
	@Transient
	public static final String TEMPERATURE = "temperature";
	@Transient
	public static final String CONDITION = "condition";
	@Transient
	public static final String HUMIDITY = "humidity";

	@Id
	@GeneratedValue
	@Column(name = ID)
	private int id;

	@Column(name = TEMPERATURE)
	private String temperature;

	@Column(name = CONDITION)
	private String condition;

	@Column(name = HUMIDITY)
	private String humidity;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = DbWind.ID)
	private DbWind dbWind;

	@OneToOne(mappedBy = "dbWeather", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private DbGame dbGame;

	public DbWeather() {}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public DbWind getDbWind() {
		return dbWind;
	}

	public void setDbWind(DbWind dbWind) {
		this.dbWind = dbWind;
	}

	public String getHumidity() {
		return humidity;
	}

	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}
}
