package org.clarke.database.regularSeasonModel;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import java.util.List;

@Entity
@SuppressWarnings("WeakerAccess, unused")
@Table(schema = "regular_season", name = "Week", uniqueConstraints = {@UniqueConstraint(columnNames = "week_id")})
public class DbWeek
{
    @Transient
    public static final String ID = "week_id";

    @Transient
    public static final String NUMBER = "number";

    @Id
    @Column(name = ID)
    private String id;

    @Column(name = NUMBER)
    private String number;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dbWeek")
    private List<DbGame> dbGames;

    @ManyToOne
    @JoinColumn(name = DbRegularSeason.SEASON)
    private DbRegularSeason dbSeason;

    public DbWeek() {}

    public List<DbGame> getDbGames()
    {
        return dbGames;
    }

    public void setDbGames(List<DbGame> dbGames)
    {
        this.dbGames = dbGames;
    }

    public DbRegularSeason getDbSeason()
    {
        return dbSeason;
    }

    public void setDbSeason(DbRegularSeason dbSeason)
    {
        this.dbSeason = dbSeason;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getNumber()
    {
        return number;
    }

    public void setNumber(String number)
    {
        this.number = number;
    }
}
