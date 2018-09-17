package org.clarke.database.regularSeasonModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@SuppressWarnings({"WeakerAccess", "unused"})
@Table(schema = "regular_season", name = "Game", uniqueConstraints = {@UniqueConstraint(columnNames = "game_id")})
public class DbGame
{
    @Transient
    public static final String ID = "game_id";
    @Transient
    public static final String SCHEDULED = "scheduled";
    @Transient
    public static final String AWAY = "away";
    @Transient
    public static final String AWAY_POINTS = "away_points";
    @Transient
    public static final String HOME = "home";
    @Transient
    public static final String HOME_POINTS = "home_points";
    @Transient
    public static final String COVERAGE = "coverage";
    @Transient
    public static final String HOME_ROTATION = "home_rotation";
    @Transient
    public static final String AWAY_ROTATION = "away_rotation";
    @Transient
    public static final String STATUS = "status";
    @Transient
    public static final String NEUTRAL_SITE = "neutral_site";
    @Column(name = SCHEDULED)
    protected String scheduled;
    @Column(name = AWAY)
    protected String away;
    @Column(name = AWAY_POINTS)
    protected String away_points;
    @Column(name = HOME)
    protected String home;
    @Column(name = HOME_POINTS)
    protected String home_points;
    /** Primitive Members **/
    @Id
    @Column(name = ID)
    private String id;
    @Column(name = COVERAGE)
    private String coverage;

    @Column(name = HOME_ROTATION)
    private String home_rotation;

    @Column(name = AWAY_ROTATION)
    private String away_rotation;

    @Column(name = STATUS)
    private String status;

    @Column(name = NEUTRAL_SITE)
    private String neutral_site;

    /** DB Members **/
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = DbVenue.ID)
    private DbVenue dbVenue;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = DbBroadcast.ID)
    private DbBroadcast dbBroadcast;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = DbWeather.ID)
    private DbWeather dbWeather;

    @ManyToOne
    @JoinColumn(name = DbWeek.ID)
    private DbWeek dbWeek;

    public DbGame() {}

    public String getAway()
    {
        return away;
    }

    public void setAway(String away)
    {
        this.away = away;
    }

    public String getAway_points()
    {
        return away_points;
    }

    public void setAway_points(String away_points)
    {
        this.away_points = away_points;
    }

    public String getAway_rotation()
    {
        return away_rotation;
    }

    public void setAway_rotation(String away_rotation)
    {
        this.away_rotation = away_rotation;
    }

    public String getCoverage()
    {
        return coverage;
    }

    public void setCoverage(String coverage)
    {
        this.coverage = coverage;
    }

    public DbBroadcast getDbBroadcast()
    {
        return dbBroadcast;
    }

    public void setDbBroadcast(DbBroadcast dbBroadcast)
    {
        this.dbBroadcast = dbBroadcast;
    }

    public DbVenue getDbVenue()
    {
        return dbVenue;
    }

    public void setDbVenue(DbVenue dbVenue)
    {
        this.dbVenue = dbVenue;
    }

    public DbWeather getDbWeather()
    {
        return dbWeather;
    }

    public void setDbWeather(DbWeather dbWeather)
    {
        this.dbWeather = dbWeather;
    }

    public DbWeek getDbWeek()
    {
        return dbWeek;
    }

    public void setDbWeek(DbWeek dbWeek)
    {
        this.dbWeek = dbWeek;
    }

    public String getHome()
    {
        return home;
    }

    public void setHome(String home)
    {
        this.home = home;
    }

    public String getHome_points()
    {
        return home_points;
    }

    public void setHome_points(String home_points)
    {
        this.home_points = home_points;
    }

    public String getHome_rotation()
    {
        return home_rotation;
    }

    public void setHome_rotation(String home_rotation)
    {
        this.home_rotation = home_rotation;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getNeutral_site()
    {
        return neutral_site;
    }

    public void setNeutral_site(String neutral_site)
    {
        this.neutral_site = neutral_site;
    }

    public String getScheduled()
    {
        return scheduled;
    }

    public void setScheduled(String scheduled)
    {
        this.scheduled = scheduled;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
}
