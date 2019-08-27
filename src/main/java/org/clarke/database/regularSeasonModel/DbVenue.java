package org.clarke.database.regularSeasonModel;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@SuppressWarnings({"WeakerAccess", "unused"})
@Table(schema = "regular_season", name = "Venue", uniqueConstraints = {@UniqueConstraint(columnNames = "venue_id")})
public class DbVenue {
	@Transient
	public static final String ID = "venue_id";
	@Transient
	public static final String COUNTRY = "country";
	@Transient
	public static final String NAME = "name";
	@Transient
	public static final String CITY = "city";
	@Transient
	public static final String STATE = "state";
	@Transient
	public static final String CAPACITY = "capacity";
	@Transient
	public static final String TYPE = "type";
	@Transient
	public static final String ZIP = "zip";
	@Transient
	public static final String ADDRESS = "address";

	@Id
	@Column(name = ID)
	private String id;

	@Column(name = COUNTRY)
	private String country;

	@Column(name = NAME)
	private String name;

	@Column(name = CITY)
	private String city;

	@Column(name = STATE)
	private String state;

	@Column(name = CAPACITY)
	private String capacity;

	@Column(name = TYPE)
	private String type;

	@Column(name = ZIP)
	private String zip;

	@Column(name = ADDRESS)
	private String address;

	@OneToOne(mappedBy = "dbVenue", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private DbGame dbGame;

	public DbVenue() {}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCapacity() {
		return capacity;
	}

	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}
}
