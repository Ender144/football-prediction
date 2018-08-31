package org.clarke.data;

import com.google.gson.Gson;
import org.clarke.seasonModel.Game;
import org.clarke.seasonModel.RegularSeason;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

public class SRDataTests
{
    private SR_API_Configuration srApiConfiguration = SR_API_Configuration.getInstance();

    @Test
    void testCFB()
    {
        try
        {
            JsonResponse response = JsonRestMessenger.get(srApiConfiguration.getRegularSeasonScheduleUrl());
            RegularSeason season2018 = new Gson().fromJson(response.getResponseJSON(), RegularSeason.class);
            List<Game> michiganGames = season2018.getGamesByTeam("mich");
            for (Game game : michiganGames)
            {
                String michiganPoints = game.home.equals("mich") ? game.home_points : game.away_points;
                System.out.println("Game: Home:" + game.home + ", Away: " + game.away + ", Mich points: " + michiganPoints);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
