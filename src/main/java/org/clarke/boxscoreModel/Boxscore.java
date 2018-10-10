
package org.clarke.boxscoreModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

@SuppressWarnings("unused")
public class Boxscore
{
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("scheduled")
    @Expose
    private String scheduled;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("coverage")
    @Expose
    private String coverage;
    @SerializedName("attendance")
    @Expose
    private int attendance;
    @SerializedName("completed")
    @Expose
    private String completed;
    @SerializedName("quarter")
    @Expose
    private int quarter;
    @SerializedName("clock")
    @Expose
    private String clock;
    @SerializedName("home_team")
    @Expose
    private HomeTeam homeTeam;
    @SerializedName("away_team")
    @Expose
    private AwayTeam awayTeam;
    @SerializedName("scoring_drives")
    @Expose
    private List<ScoringDrive> scoringDrives = null;

    public int getAttendance()
    {
        return attendance;
    }

    public void setAttendance(int attendance)
    {
        this.attendance = attendance;
    }

    public AwayTeam getAwayTeam()
    {
        return awayTeam;
    }

    public void setAwayTeam(AwayTeam awayTeam)
    {
        this.awayTeam = awayTeam;
    }

    public String getClock()
    {
        return clock;
    }

    public void setClock(String clock)
    {
        this.clock = clock;
    }

    public String getCompleted()
    {
        return completed;
    }

    public void setCompleted(String completed)
    {
        this.completed = completed;
    }

    public String getCoverage()
    {
        return coverage;
    }

    public void setCoverage(String coverage)
    {
        this.coverage = coverage;
    }

    public HomeTeam getHomeTeam()
    {
        return homeTeam;
    }

    public void setHomeTeam(HomeTeam homeTeam)
    {
        this.homeTeam = homeTeam;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public int getQuarter()
    {
        return quarter;
    }

    public void setQuarter(int quarter)
    {
        this.quarter = quarter;
    }

    public String getScheduled()
    {
        return scheduled;
    }

    public void setScheduled(String scheduled)
    {
        this.scheduled = scheduled;
    }

    public List<ScoringDrive> getScoringDrives()
    {
        return scoringDrives;
    }

    public void setScoringDrives(List<ScoringDrive> scoringDrives)
    {
        this.scoringDrives = scoringDrives;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this).append("id", id).append("scheduled", scheduled).append("status", status).append("coverage", coverage).append("attendance", attendance).append("completed", completed).append("quarter", quarter).append("clock", clock).append("homeTeam", homeTeam).append("awayTeam", awayTeam).append("scoringDrives", scoringDrives).toString();
    }
}
