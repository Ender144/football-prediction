package org.clarke.seasonModel;

import java.util.ArrayList;
import java.util.List;

public class RegularSeason
{
    public String season;
    public String type;
    public List<Week> weeks;

    public List<Game> getGamesByTeam(String teamName)
    {
        List<Game> games = new ArrayList<>();

        for (Week week : weeks)
        {
            for (Game game : week.games)
            {
                if (teamName.equalsIgnoreCase(game.away) || teamName.equalsIgnoreCase(game.home))
                {
                    games.add(game);
                }
            }
        }

        return games;
    }
}
