package org.clarke.database.regularSeasonModel;

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
@Table(schema = "regular_season", name = "RegularSeason", uniqueConstraints = {@UniqueConstraint(columnNames = "season")})
public class DbRegularSeason
{
    @Transient
    public static final String SEASON = "season";

    @Transient
    public static final String TYPE = "type";

    @Id
    @Column(name = SEASON)
    private String season;

    @Column(name = TYPE)
    private String type;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dbSeason")
    private List<DbWeek> dbWeeks;

    public DbRegularSeason() {}

    public List<DbWeek> getDbWeeks()
    {
        return dbWeeks;
    }

    public void setDbWeeks(List<DbWeek> dbWeeks)
    {
        this.dbWeeks = dbWeeks;
    }

    public String getSeason()
    {
        return season;
    }

    public void setSeason(String season)
    {
        this.season = season;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
