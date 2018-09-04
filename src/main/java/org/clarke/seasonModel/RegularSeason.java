package org.clarke.seasonModel;

import java.util.ArrayList;
import java.util.List;

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
}
