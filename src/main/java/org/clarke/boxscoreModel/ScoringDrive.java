
package org.clarke.boxscoreModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

@SuppressWarnings("unused")
public class ScoringDrive
{
    @SerializedName("sequence")
    @Expose
    private int sequence;
    @SerializedName("clock")
    @Expose
    private String clock;
    @SerializedName("quarter")
    @Expose
    private int quarter;
    @SerializedName("team")
    @Expose
    private String team;
    @SerializedName("scores")
    @Expose
    private List<Score> scores = null;

    public String getClock()
    {
        return clock;
    }

    public void setClock(String clock)
    {
        this.clock = clock;
    }

    public int getQuarter()
    {
        return quarter;
    }

    public void setQuarter(int quarter)
    {
        this.quarter = quarter;
    }

    public List<Score> getScores()
    {
        return scores;
    }

    public void setScores(List<Score> scores)
    {
        this.scores = scores;
    }

    public int getSequence()
    {
        return sequence;
    }

    public void setSequence(int sequence)
    {
        this.sequence = sequence;
    }

    public String getTeam()
    {
        return team;
    }

    public void setTeam(String team)
    {
        this.team = team;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this).append("sequence", sequence).append("clock", clock).append("quarter", quarter).append("team", team).append("scores", scores).toString();
    }
}
