
package org.clarke.boxscoreModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

@SuppressWarnings("unused")
public class Score
{
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("clock")
    @Expose
    private String clock;
    @SerializedName("team")
    @Expose
    private String team;
    @SerializedName("quarter")
    @Expose
    private int quarter;
    @SerializedName("points")
    @Expose
    private int points;
    @SerializedName("summary")
    @Expose
    private String summary;

    public String getClock()
    {
        return clock;
    }

    public void setClock(String clock)
    {
        this.clock = clock;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public int getPoints()
    {
        return points;
    }

    public void setPoints(int points)
    {
        this.points = points;
    }

    public int getQuarter()
    {
        return quarter;
    }

    public void setQuarter(int quarter)
    {
        this.quarter = quarter;
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    public String getTeam()
    {
        return team;
    }

    public void setTeam(String team)
    {
        this.team = team;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this).append("id", id).append("type", type).append("clock", clock).append("team", team).append("quarter", quarter).append("points", points).append("summary", summary).toString();
    }
}
