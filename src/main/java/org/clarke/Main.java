package org.clarke;

import com.google.gson.Gson;
import org.clarke.api.JsonResponse;
import org.clarke.api.JsonRestMessenger;
import org.clarke.configuration.Configuration;
import org.clarke.configuration.SR_API_Configuration;
import org.clarke.predictionModel.ParticipantScores;
import org.clarke.predictionModel.PredictedScore;
import org.clarke.predictionModel.SeasonPrediction;
import org.clarke.regularSeasonModel.Game;
import org.clarke.regularSeasonModel.RegularSeason;
import org.clarke.rosterModel.Team;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Main
{
    public static final List<String> participants = new ArrayList<>();
    private static final String PREDICTIONS_CONFIG = "predictions.properties";

    static
    {
        participants.add("Ashley");
        participants.add("Kailey");
        participants.add("Dad");
        participants.add("Mom");
        participants.add("Brad");
        participants.add("Britt");
        participants.add("Tyler");
        participants.add("Heather");
    }

    private static final SR_API_Configuration SR_API_CONFIGURATION = SR_API_Configuration.getInstance();

    public static List<SeasonPrediction> initializePredictionModel(RegularSeason regularSeason)
    {
        List<SeasonPrediction> seasonPredictions = new ArrayList<>();
        Configuration predictionsConfiguration = new Configuration(PREDICTIONS_CONFIG);

        for (String participant : participants)
        {
            Map<Game, PredictedScore> predictedScores = new TreeMap<>();
            List<String> configuredPredictedScores = predictionsConfiguration.getCSVListValue(participant, new ArrayList<>());
            List<Game> michiganGames = regularSeason.getMichiganGamesThisSeason();
            Collections.sort(michiganGames);

            if (configuredPredictedScores.size() != michiganGames.size())
            {
                System.err.println(participant + " has not predicted scores for all games!");
                break;
            } else
            {
                int i = 0;
                for (String configuredPredictedScore : configuredPredictedScores)
                {
                    String[] scores = configuredPredictedScore.split("-");
                    if (scores.length != 2)
                    {
                        System.err.println(participant + " has not predicted both teams' scores for a game (missing dash?)");
                    } else
                    {
                        try
                        {
                            Game predictedGame = michiganGames.get(i);
                            int ourScore = Integer.parseInt(scores[0]);
                            int theirScore = Integer.parseInt(scores[1]);

                            predictedScores.put(predictedGame, new PredictedScore(ourScore, theirScore));
                        } catch (NumberFormatException nfe)
                        {
                            System.err.println("One of " + participant + "\'s predicted scores was not a number!");
                            nfe.printStackTrace();
                        }
                    }

                    i++;
                }
            }

            seasonPredictions.add(new SeasonPrediction(participant, predictedScores, predictionsConfiguration.getBooleanValue("printUnplayedGames", false)));
        }

        return seasonPredictions;
    }

    public static void main(String[] args)
    {
        RegularSeason season2018 = initializeSeasonModel();
        Map<String, Team> opponents = new HashMap<>();
        season2018.getMichiganGamesThisSeason().forEach(game -> opponents.put(game.them(), initializeTeam(game.them())));

        List<SeasonPrediction> seasonPredictions = initializePredictionModel(season2018);

        ParticipantScores scores = new ParticipantScores(seasonPredictions, season2018);

        System.out.println();
        seasonPredictions.forEach(System.out::println);

        for (String participant : participants)
        {
            System.out.println(participant + " current score: " + scores.getCurrentParticipantScore(participant));
        }

        System.out.println("Printing excel sheet...");
        try
        {
            ExcelSeasonOutput.printExcelSheet(season2018, seasonPredictions, opponents);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static RegularSeason initializeSeasonModel()
    {
        RegularSeason season2018 = new RegularSeason();

        JsonResponse response = null;
        try
        {
            response = JsonRestMessenger.get(SR_API_CONFIGURATION.getRegularSeasonScheduleUrl());
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        if (response != null)
        {
            season2018 = new Gson().fromJson(response.getResponseJSON(), RegularSeason.class);
        }

        return season2018;
    }

    private static Team initializeTeam(String teamAbbreviation)
    {
        try
        {
            Thread.sleep(1000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        Team team = null;

        JsonResponse response = null;
        try
        {
            response = JsonRestMessenger.get(SR_API_CONFIGURATION.getTeamRoster(teamAbbreviation));
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        if (response != null)
        {
            team = new Gson().fromJson(response.getResponseJSON(), Team.class);
        }

        return team;
    }
}
