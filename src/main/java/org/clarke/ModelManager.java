package org.clarke;

import com.google.gson.Gson;
import org.clarke.boxscoreModel.Boxscore;
import org.clarke.configuration.Configuration;
import org.clarke.configuration.SR_API_Configuration;
import org.clarke.database.DBConnection;
import org.clarke.json.Response;
import org.clarke.json.RestMessenger;
import org.clarke.predictionModel.ParticipantScores;
import org.clarke.predictionModel.PredictedScore;
import org.clarke.predictionModel.SeasonPrediction;
import org.clarke.regularSeasonModel.Game;
import org.clarke.regularSeasonModel.RegularSeason;
import org.clarke.rosterModel.Opponent;
import org.clarke.rosterModel.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ModelManager {
	public static final String UNINITIALIZED_BOXSCORE = "uninitialized";
	public static final String PRE_GAME_BOXSCORE = "created";
	//    public static final String POST_GAME_BOXSCORE = "closed";
	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ModelManager.class);
	private static final String DEFAULT_SEASON = "2018";
	private static final String SEASON_KEY = "season";
	private static final List<String> participants = new ArrayList<>();
	private static final String PREDICTIONS_CONFIG = "predictions-" + DEFAULT_SEASON + ".properties";
	private static final String DATABASE_CONFIG = "database.properties";
	private static final SR_API_Configuration SR_API_CONFIGURATION = SR_API_Configuration.getInstance();

	private static DBConnection dbConnection = DBConnection.getInstance();
	private static boolean overwriteSeason;
	private static boolean overwriteTeams;
	private static List<SeasonPrediction> seasonPredictions;
	private static ParticipantScores scores;
	private static List<Opponent> opponents;
	private static RegularSeason season;
	private static Boxscore todaysBoxscore;
	private static Configuration predictionsConfiguration;

	static {
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

		predictionsConfiguration = new Configuration(PREDICTIONS_CONFIG);

		initializeModels(false);
	}

	private ModelManager() {}

	public static List<Opponent> getOpponents() {
		return opponents;
	}

	public static List<Opponent> getOpponentsForSeason(RegularSeason season) {
		return initializeOpponents(season);
	}

	public static List<String> getParticipants() {
		return participants;
	}

	public static List<SeasonPrediction> getPredictionsForSeason(RegularSeason season) {
		return initializePredictionModel(season);
	}

	public static ParticipantScores getScores() {
		return scores;
	}

	public static RegularSeason getSeason() {
		return season;
	}

	public static Boxscore getTodaysBoxscore() {
		return todaysBoxscore;
	}

	static void checkBoxscore() {
		for (Game game : season.getMichiganGamesThisSeason()) {
			if (game.getDate().isEqual(LocalDate.now())) {
				System.out.println("Getting today's boxscore...");
				initializeBoxscore(game);
				logger.info("Today's boxscore retrieved at {}", LocalDateTime.now());
				logger.info("Boxscore: {}", todaysBoxscore.toString());
			}
		}
	}

	static List<SeasonPrediction> getSeasonPredictions() {
		return seasonPredictions;
	}

	static void rebuildSeason() {
		initializeModels(true);
	}

	private static void initializeBoxscore(Game game) {
		todaysBoxscore = new Boxscore();
		todaysBoxscore.setStatus(UNINITIALIZED_BOXSCORE);

		if (game != null) {
			logger.info("Loading boxscore from API...");
			Response response = null;
			try {
				System.out.println(SR_API_CONFIGURATION.getBoxScore(season, game));
				response = RestMessenger.get(SR_API_CONFIGURATION.getBoxScore(season, game));
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (response != null) {
				todaysBoxscore = new Gson().fromJson(response.getResponseJSON(), Boxscore.class);
			}
		}
	}

	private static void initializeModels(boolean rebuildSeason) {
		if (season == null || rebuildSeason) {
			System.out.println("Building season model...");
			initializeSeasonModel();
			boolean initializedBoxscore = false;

			for (Game game : season.getMichiganGamesThisSeason()) {
				if (game.getDate().isEqual(LocalDate.now())) {
					initializedBoxscore = true;
					System.out.println("Getting today's boxscore...");
					initializeBoxscore(game);
					logger.info("Today's boxscore retrieved at {}", LocalDateTime.now());
					logger.info("Boxscore: {}", todaysBoxscore.toString());
				}
			}

			if (!initializedBoxscore) {
				initializeBoxscore(null);
			}
		}

		if (seasonPredictions == null) {
			System.out.println("Building prediction model...");
			initializePredictionModel(season);
		}

		if (scores == null || rebuildSeason) {
			System.out.println("Calculating participant scores...");
			scores = new ParticipantScores(seasonPredictions, season);
		}

		if (opponents == null) {
			initializeOpponents(season);
		}
	}

	private static List<Opponent> initializeOpponents(RegularSeason season) {
		System.out.println("Initializing opponents list...");
		opponents = new ArrayList<>();
		season.getMichiganGamesThisSeason().forEach(game -> opponents.add(new Opponent(game.them(), initializeTeam(game.them()))));

		return opponents;
	}

	private static List<SeasonPrediction> initializePredictionModel(RegularSeason regularSeason) {
		seasonPredictions = new ArrayList<>();

		for (String participant : participants) {
			Map<Game, PredictedScore> predictedScores = new TreeMap<>();
			List<String> configuredPredictedScores = new ArrayList<>(predictionsConfiguration.getCSVListValue(participant, new ArrayList<>()));
			List<Game> michiganGames = regularSeason.getMichiganGamesThisSeason();
			Collections.sort(michiganGames);

			int predictedGameCount = configuredPredictedScores.size();
			int predictionGap = michiganGames.size() - predictedGameCount;
			if (predictionGap > 0) {
				logger.error("{} has not predicted scores for all games! Adding 0-0 predictions for {} games " +
					"at the end of season...", participant, predictionGap);

				for (int i = 0; i < predictionGap; i++) {
					try {
						configuredPredictedScores.add("0-0");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if (predictionGap < 0) {
				logger.error("{} predicted too many games! Removing {} game predictions from end of season...",
					participant, Math.abs(predictionGap));

				for (int i = 0; i < Math.abs(predictionGap); i++) {
					configuredPredictedScores.remove(predictedGameCount - i);
				}
			}

			int i = 0;
			for (String configuredPredictedScore : configuredPredictedScores) {
				String[] scores = configuredPredictedScore.split("-");
				if (scores.length != 2) {
					logger.error(participant + " has not predicted both teams' scores for a game (missing dash?)");
				} else {
					try {
						Game predictedGame = michiganGames.get(i);
						int ourScore = Integer.parseInt(scores[0]);
						int theirScore = Integer.parseInt(scores[1]);

						predictedScores.put(predictedGame, new PredictedScore(ourScore, theirScore));
					} catch (NumberFormatException nfe) {
						logger.error("One of " + participant + "\'s predicted scores was not a number!");
						nfe.printStackTrace();
					}
				}

				i++;
			}

			seasonPredictions.add(new SeasonPrediction(participant, predictedScores, predictionsConfiguration.getBooleanValue("printUnplayedGames", false)));
		}

		return seasonPredictions;
	}

	private static void initializeSeasonModel() {
		if (overwriteSeason) {
			logger.info("Overwriting DB Season via configuration...");
			loadSeasonFromAPI();
		} else if (!dbConnection.connect().isEmpty()) {
			logger.error("Database could not connect... ");
			loadSeasonFromAPI();
		} else {
			season = dbConnection.getRegularSeason(predictionsConfiguration.getStringValue(SEASON_KEY, DEFAULT_SEASON));
			if (season == null) {
				logger.info("Season not initialized in DB...");
				loadSeasonFromAPI();
			}
		}
	}

	private static Team initializeTeam(String teamAbbreviation) {
		Team team;
		String connectionOutcome = dbConnection.connect();

		if (overwriteTeams) {
			logger.info("Overwriting DB Season via configuration...");
			team = loadTeamFromAPI(teamAbbreviation);
		} else if (!connectionOutcome.isEmpty()) {
			logger.error("Database could not connect... " + connectionOutcome);
			team = loadTeamFromAPI(teamAbbreviation);
		} else {
			team = dbConnection.getTeam(teamAbbreviation);
			if (team == null) {
				logger.info("Team not initialized in DB...");
				team = loadTeamFromAPI(teamAbbreviation);
			}
		}

		return team;
	}

	private static void loadSeasonFromAPI() {
		logger.info("Loading season from API...");
		Response response = null;
		try {
			response = RestMessenger.get(SR_API_CONFIGURATION.getRegularSeasonScheduleUrl());
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (response != null) {
			season = new Gson().fromJson(response.getResponseJSON(), RegularSeason.class);
		}

		dbConnection.addRegularSeason(season);
	}

	private static Team loadTeamFromAPI(String teamAbbreviation) {
		logger.info("Loading Team from API...");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Team team = null;

		Response response = null;
		try {
			response = RestMessenger.get(SR_API_CONFIGURATION.getTeamRoster(teamAbbreviation));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (response != null) {
			team = new Gson().fromJson(response.getResponseJSON(), Team.class);
		}

		dbConnection.addTeam(team);

		return team;
	}
}
