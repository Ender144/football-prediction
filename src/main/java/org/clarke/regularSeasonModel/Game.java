package org.clarke.regularSeasonModel;

import org.clarke.predictionModel.Outcome;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Game implements Comparable<Game>
{
    protected String scheduled;
    protected String away;
    protected String away_points;
    protected String home;
    protected String home_points;
    private String id;
    private String coverage;
    private String home_rotation;
    private String away_rotation;
    private String status;
    private String neutral_site;
    private Venue venue;
    private Broadcast broadcast;
    private Weather weather;

    @Override
    public int compareTo(Game otherGame)
    {
        return getDate().compareTo(otherGame.getDate());
    }

    public String them()
    {
        return away.equalsIgnoreCase("mich") ? home : away;
    }

    public String us()
    {
        return away.equalsIgnoreCase("mich") ? away : home;
    }

    public Outcome getActualOutcome()
    {
        if ((home_points == null && away_points == null) || (home_points != null && home_points.equals("-1") && away_points != null && away_points.equals("-1")))
        {
            return Outcome.UNPLAYED;
        } else
        {
            return getOurScore() > getTheirScore() ? Outcome.WIN : Outcome.LOSE;
        }
    }

    public String getAwayTeam()
    {
        return away;
    }

    public LocalDate getDate()
    {
        String pattern = "yyyy-MM-dd";
        String substringScheduled;

        substringScheduled = scheduled.substring(0, scheduled.indexOf("T"));

        DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);

        return LocalDate.parse(substringScheduled, simpleDateFormat);
    }

    public String getHomeTeam()
    {
        return home;
    }

    public int getOurScore()
    {
        boolean michiganIsAway = away.equals("mich");
        if (away_points == null || home_points == null)
        {
            return -1;
        }

        return michiganIsAway ? Integer.parseInt(home_points) : Integer.parseInt(away_points);
    }

    public int getTheirScore()
    {
        boolean michiganIsAway = away.equals("mich");
        if (away_points == null || home_points == null)
        {
            return -1;
        }

        return michiganIsAway ? Integer.parseInt(away_points) : Integer.parseInt(home_points);
    }
}
