package org.clarke.configuration;

public class SR_API_Configuration
{
    public static final String UNKNOWN_BASE_URL = "";
    public static final String UNKNOWN_API_PROP = "";
    public static final String UNKNOWN_REG_SEASON_SCHEDULE = "";
    public static final String UNKNOWN_TEAMS = "";
    public static final String UNKNOWN_ROSTER = "";
    private static final String DATA_API_CONFIGURATION = "data-api.properties";
    private static final String BASE_URL_KEY = "baseUrl";
    private static final String API_KEY_KEY = "keyProperty";
    private static final String REGULAR_SEASON_SCHEDULE_KEY = "regularSeasonSchedule";
    private static final String TEAMS_KEY = "teams";
    private static final String ROSTER_KEY = "roster";
    private static final String PROP_SEPARATOR = "?";
    private static SR_API_Configuration instance;
    private String baseURL;
    private String apiKeyProperty;
    private String regularSeasonSchedule;
    private String teams;
    private String roster;

    private SR_API_Configuration()
    {
        Configuration dataAPIConfiguration = new Configuration(DATA_API_CONFIGURATION);

        // URLs
        baseURL = dataAPIConfiguration.getStringValue(BASE_URL_KEY, UNKNOWN_BASE_URL);
        regularSeasonSchedule = dataAPIConfiguration.getStringValue(REGULAR_SEASON_SCHEDULE_KEY, UNKNOWN_REG_SEASON_SCHEDULE);
        teams = dataAPIConfiguration.getStringValue(TEAMS_KEY, UNKNOWN_TEAMS);
        roster = dataAPIConfiguration.getStringValue(ROSTER_KEY, UNKNOWN_ROSTER);

        // Properties
        apiKeyProperty = dataAPIConfiguration.getStringValue(API_KEY_KEY, UNKNOWN_API_PROP);
    }

    public static SR_API_Configuration getInstance()
    {
        if (instance == null)
        {
            instance = new SR_API_Configuration();
        }

        return instance;
    }

    public String getRegularSeasonScheduleUrl()
    {
        return baseURL + regularSeasonSchedule + PROP_SEPARATOR + apiKeyProperty;
    }

    public String getTeamRoster(String teamName)
    {
        return baseURL + teams + teamName + roster + PROP_SEPARATOR + apiKeyProperty;
    }
}
