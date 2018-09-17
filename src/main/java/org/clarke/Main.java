package org.clarke;

import com.google.gson.Gson;
import org.clarke.api.JsonResponse;
import org.clarke.api.JsonRestMessenger;
import org.clarke.configuration.Configuration;
import org.clarke.configuration.SR_API_Configuration;
import org.clarke.database.DBConnection;
import org.clarke.predictionModel.ParticipantScores;
import org.clarke.predictionModel.PredictedScore;
import org.clarke.predictionModel.SeasonPrediction;
import org.clarke.regularSeasonModel.Game;
import org.clarke.regularSeasonModel.RegularSeason;
import org.clarke.rosterModel.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String PREDICTIONS_CONFIG = "predictions.properties";
    private static final String DATABASE_CONFIG = "database.properties";
    private static final SR_API_Configuration SR_API_CONFIGURATION = SR_API_Configuration.getInstance();
    private static DBConnection dbConnection = DBConnection.getInstance();

    private static boolean overwriteSeason;
    private static boolean overwriteTeams;

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

        Configuration dbConfig = new Configuration(DATABASE_CONFIG);
        overwriteSeason = dbConfig.getBooleanValue("overwriteSeason", false);
        overwriteTeams = dbConfig.getBooleanValue("overwriteTeams", false);
    }

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
                logger.error(participant + " has not predicted scores for all games!");
                break;
            } else
            {
                int i = 0;
                for (String configuredPredictedScore : configuredPredictedScores)
                {
                    String[] scores = configuredPredictedScore.split("-");
                    if (scores.length != 2)
                    {
                        logger.error(participant + " has not predicted both teams' scores for a game (missing dash?)");
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
                            logger.error("One of " + participant + "\'s predicted scores was not a number!");
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

    @SuppressWarnings("WeakerAccess")
    public static RegularSeason initializeSeasonModel()
    {
        RegularSeason season2018;
        String connectionOutcome = dbConnection.connect();

        if (overwriteSeason)
        {
            logger.info("Overwriting DB Season via configuration...");
            season2018 = loadSeasonFromAPI();
        } else if (!connectionOutcome.isEmpty())
        {
            logger.error("Database could not connect... " + connectionOutcome);
            season2018 = loadSeasonFromAPI();
        } else
        {
            season2018 = dbConnection.getRegularSeason("2018");
            if (season2018 == null)
            {
                logger.info("Season not initialized in DB...");
                season2018 = loadSeasonFromAPI();
            }
        }

        return season2018;
    }

    public static Team initializeTeam(String teamAbbreviation)
    {
        Team team;
        String connectionOutcome = dbConnection.connect();

        if (overwriteTeams)
        {
            logger.info("Overwriting DB Season via configuration...");
            team = loadTeamFromAPI(teamAbbreviation);
        } else if (!connectionOutcome.isEmpty())
        {
            logger.error("Database could not connect... " + connectionOutcome);
            team = loadTeamFromAPI(teamAbbreviation);
        } else
        {
            team = dbConnection.getTeam(teamAbbreviation);
            if (team == null)
            {
                logger.info("Team not initialized in DB...");
                team = loadTeamFromAPI(teamAbbreviation);
            }
        }

        return team;
    }

    public static void main(String[] args)
    {
        RegularSeason season2018 = initializeSeasonModel();
        Map<String, Team> opponents = new HashMap<>();
        season2018.getMichiganGamesThisSeason().forEach(game -> opponents.put(game.them(), initializeTeam(game.them())));

        List<SeasonPrediction> seasonPredictions = initializePredictionModel(season2018);

        ParticipantScores scores = new ParticipantScores(seasonPredictions, season2018);

        seasonPredictions.forEach(System.out::println);

        for (String participant : participants)
        {
            System.out.println(participant + " current score: " + scores.getCurrentParticipantScore(participant));
            logger.info(participant + " current score: " + scores.getCurrentParticipantScore(participant));
        }

        logger.info("Printing excel sheet...");
        try
        {
            ExcelSeasonOutput.printExcelSheet(season2018, seasonPredictions, opponents);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        logger.info("Finished writing excel sheet...");
        System.out.println("Finished writing excel sheet...");
        System.exit(0);

        //        logger.info("Grabbing google sheet...");
        //        try
        //        {
        //            new GoogleSheetsConfiguration().retrieveSheet();
        //        } catch (IOException | GeneralSecurityException e)
        //        {
        //            e.printStackTrace();
        //        }
    }

    private static RegularSeason loadSeasonFromAPI()
    {
        logger.info("Loading season from API...");
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

        dbConnection.addRegularSeason(season2018);

        return season2018;
    }

    private static Team loadTeamFromAPI(String teamAbbreviation)
    {
        logger.info("Loading Team from API...");
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

        dbConnection.addTeam(team);

        return team;
    }
}
