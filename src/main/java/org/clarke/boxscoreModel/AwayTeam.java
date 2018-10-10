
package org.clarke.boxscoreModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

@SuppressWarnings("unused")
public class AwayTeam
{
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("market")
    @Expose
    private String market;
    @SerializedName("remaining_challenges")
    @Expose
    private int remainingChallenges;
    @SerializedName("remaining_timeouts")
    @Expose
    private int remainingTimeouts;
    @SerializedName("points")
    @Expose
    private int points;
    @SerializedName("scoring")
    @Expose
    private List<Scoring> scoring = null;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getMarket()
    {
        return market;
    }

    public void setMarket(String market)
    {
        this.market = market;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getPoints()
    {
        return points;
    }

    public void setPoints(int points)
    {
        this.points = points;
    }

    public int getRemainingChallenges()
    {
        return remainingChallenges;
    }

    public void setRemainingChallenges(int remainingChallenges)
    {
        this.remainingChallenges = remainingChallenges;
    }

    public int getRemainingTimeouts()
    {
        return remainingTimeouts;
    }

    public void setRemainingTimeouts(int remainingTimeouts)
    {
        this.remainingTimeouts = remainingTimeouts;
    }

    public List<Scoring> getScoring()
    {
        return scoring;
    }

    public void setScoring(List<Scoring> scoring)
    {
        this.scoring = scoring;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this).append("id", id).append("name", name).append("market", market).append("remainingChallenges", remainingChallenges).append("remainingTimeouts", remainingTimeouts).append("points", points).append("scoring", scoring).toString();
    }
}
