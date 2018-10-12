package org.clarke.json;

import com.google.gson.Gson;
import org.clarke.configuration.SR_API_Configuration;
import org.clarke.regularSeasonModel.Game;
import org.clarke.regularSeasonModel.RegularSeason;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

class SRDataTests
{
    private SR_API_Configuration srApiConfiguration = SR_API_Configuration.getInstance();

    @Test
    void testCFB()
    {
        try
        {
            Response response = RestMessenger.get(srApiConfiguration.getRegularSeasonScheduleUrl());
            RegularSeason season2018 = new Gson().fromJson(response.getResponseJSON(), RegularSeason.class);
            List<Game> michiganGames = season2018.getMichiganGamesThisSeason();
            for (Game game : michiganGames)
            {
                int michiganPoints = game.getOurScore();
                System.out.println("DbGame: Home:" + game.getHomeTeam() + ", Away: " + game.getAwayTeam() + ", Mich points: " + michiganPoints);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
