package org.clarke.regularSeasonModel;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class RegularSeason
{
    private String season;
    private String type;
    private List<Week> weeks;

    public List<Game> getGamesByTeam(String teamName)
    {
        List<Game> games = new ArrayList<>();

        for (Week week : weeks)
        {
            for (Game game : week.getGames())
            {
                if (teamName.equalsIgnoreCase(game.getAwayTeam()) || teamName.equalsIgnoreCase(game.getHomeTeam()))
                {
                    games.add(game);
                }
            }
        }

        return games;
    }

    public List<Game> getMichiganGamesThisSeason()
    {
        return getGamesByTeam("mich");
    }

    public String getSeason()
    {
        return season;
    }

    public List<Week> getWeeks()
    {
        return weeks;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public void setSeason(String season)
    {
        this.season = season;
    }

    public void setWeeks(List<Week> weeks)
    {
        this.weeks = weeks;
    }
}
