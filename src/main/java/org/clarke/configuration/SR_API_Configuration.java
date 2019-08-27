package org.clarke.configuration;

import org.clarke.regularSeasonModel.Game;
import org.clarke.regularSeasonModel.RegularSeason;
import org.clarke.regularSeasonModel.Week;

public class SR_API_Configuration {
	private static final String UNKNOWN_API_PROP = "";
	private static final String UNKNOWN_BASE_URL = "";
	private static final String UNKNOWN_BOXSCORE_PREFIX = "";
	private static final String UNKNOWN_BOXSCORE_SUFFIX = "";
	private static final String UNKNOWN_REG_SEASON_SCHEDULE = "";
	private static final String UNKNOWN_ROSTER = "";
	private static final String UNKNOWN_TEAMS = "";

	private static final String API_KEY_KEY = "keyProperty";
	private static final String BASE_URL_KEY = "baseUrl";
	private static final String BOXSCORE_PREFIX_KEY = "boxscorePrefix";
	private static final String BOXSCORE_SUFFIX_KEY = "boxscoreSuffix";
	private static final String DATA_API_CONFIGURATION = "data-api.properties";
	private static final String REGULAR_SEASON_SCHEDULE_KEY = "regularSeasonSchedule";
	private static final String ROSTER_KEY = "roster";
	private static final String TEAMS_KEY = "teams";
	private static final String PROP_SEPARATOR = "?";
	private static SR_API_Configuration instance;
	private String baseURL;
	private String apiKeyProperty;
	private String regularSeasonSchedule;
	private String teams;
	private String roster;
	private String boxScorePrefix;
	private String boxScoreSuffix;

	private SR_API_Configuration() {
		Configuration dataAPIConfiguration = new Configuration(DATA_API_CONFIGURATION);

		// URLs
		baseURL = dataAPIConfiguration.getStringValue(BASE_URL_KEY, UNKNOWN_BASE_URL);
		regularSeasonSchedule = dataAPIConfiguration.getStringValue(REGULAR_SEASON_SCHEDULE_KEY, UNKNOWN_REG_SEASON_SCHEDULE);
		teams = dataAPIConfiguration.getStringValue(TEAMS_KEY, UNKNOWN_TEAMS);
		roster = dataAPIConfiguration.getStringValue(ROSTER_KEY, UNKNOWN_ROSTER);
		boxScorePrefix = dataAPIConfiguration.getStringValue(BOXSCORE_PREFIX_KEY, UNKNOWN_BOXSCORE_PREFIX);
		boxScoreSuffix = dataAPIConfiguration.getStringValue(BOXSCORE_SUFFIX_KEY, UNKNOWN_BOXSCORE_SUFFIX);

		// Properties
		apiKeyProperty = dataAPIConfiguration.getStringValue(API_KEY_KEY, UNKNOWN_API_PROP);
	}

	public static SR_API_Configuration getInstance() {
		if (instance == null) {
			instance = new SR_API_Configuration();
		}

		return instance;
	}

	public String getBoxScore(RegularSeason season, Game game) {
		Week gameWeek = season.getWeeks().stream().filter(week -> week.getGames().contains(game)).findFirst().orElse(null);
		String weekNumber = gameWeek == null ? "0" : gameWeek.getNumber();
		return baseURL + boxScorePrefix + season.getSeason() + "/" + weekNumber + "/" + game.getAway() + "/" + game.getHome() + boxScoreSuffix + PROP_SEPARATOR + apiKeyProperty;
	}

	public String getRegularSeasonScheduleUrl() {
		return baseURL + regularSeasonSchedule + PROP_SEPARATOR + apiKeyProperty;
	}

	public String getTeamRoster(String teamName) {
		return baseURL + teams + teamName + roster + PROP_SEPARATOR + apiKeyProperty;
	}
}
