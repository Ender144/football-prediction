package org.clarke.database.rosterModel;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import java.util.List;

@Entity
@SuppressWarnings({"WeakerAccess", "unused"})
@Table(schema = "roster", name = "Team", uniqueConstraints = {@UniqueConstraint(columnNames = "team_id")})
public class DbTeam {
	@Transient
	public static final String ID = "team_id";
	@Transient
	public static final String NAME = "name";
	@Transient
	public static final String MARKET = "market";

	@Id
	@Column(name = ID)
	private String id;

	@Column(name = NAME)
	private String name;

	@Column(name = MARKET)
	private String market;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "dbTeam")
	private List<DbPlayer> dbPlayers;

	public DbTeam() {}

	public List<DbPlayer> getDbPlayers() {
		return dbPlayers;
	}

	public void setDbPlayers(List<DbPlayer> dbPlayers) {
		this.dbPlayers = dbPlayers;
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
}
