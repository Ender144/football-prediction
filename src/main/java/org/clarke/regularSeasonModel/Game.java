package org.clarke.regularSeasonModel;

import org.clarke.ModelManager;
import org.clarke.boxscoreModel.Boxscore;
import org.clarke.predictionModel.Outcome;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Game implements Comparable<Game> {
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
	public int compareTo(Game otherGame) {
		return getDate().compareTo(otherGame.getDate());
	}

	public Outcome getActualOutcome() {
		int ourScore = getOurScore();
		int theirScore = getTheirScore();

		if ((home_points == null && away_points == null) || (home_points != null && home_points.equals("-1") && away_points != null && away_points.equals("-1"))) {
			Boxscore todaysBoxscore = ModelManager.getTodaysBoxscore();
			if (!todaysBoxscore.getStatus().equals(ModelManager.UNINITIALIZED_BOXSCORE) && !todaysBoxscore.getStatus().equals(ModelManager.PRE_GAME_BOXSCORE)) {
				if (todaysBoxscore.getCompleted() == null) {
					return Outcome.IN_PROGRESS;
				} else {
					if (todaysBoxscore.getAwayTeam().getId().equalsIgnoreCase("mich")) {
						ourScore = todaysBoxscore.getAwayTeam().getPoints();
						theirScore = todaysBoxscore.getHomeTeam().getPoints();
					} else {
						ourScore = todaysBoxscore.getHomeTeam().getPoints();
						theirScore = todaysBoxscore.getAwayTeam().getPoints();
					}
				}
			} else {
				return Outcome.UNPLAYED;
			}
		}

		return ourScore > theirScore ? Outcome.WIN : Outcome.LOSE;
	}

	public String getAway() {
		return away;
	}

	public void setAway(String away) {
		this.away = away;
	}

	public String getAwayTeam() {
		return away;
	}

	public String getAway_points() {
		return away_points;
	}

	public void setAway_points(String away_points) {
		this.away_points = away_points;
	}

	public String getAway_rotation() {
		return away_rotation;
	}

	public void setAway_rotation(String away_rotation) {
		this.away_rotation = away_rotation;
	}

	public Broadcast getBroadcast() {
		return broadcast;
	}

	public void setBroadcast(Broadcast broadcast) {
		this.broadcast = broadcast;
	}

	public String getCoverage() {
		return coverage;
	}

	public void setCoverage(String coverage) {
		this.coverage = coverage;
	}

	public LocalDate getDate() {
		String pattern = "yyyy-MM-dd";
		String substringScheduled;

		substringScheduled = scheduled.substring(0, scheduled.indexOf("T"));

		DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);

		return LocalDate.parse(substringScheduled, simpleDateFormat);
	}

	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}

	public String getHomeTeam() {
		return home;
	}

	public String getHome_points() {
		return home_points;
	}

	public void setHome_points(String home_points) {
		this.home_points = home_points;
	}

	public String getHome_rotation() {
		return home_rotation;
	}

	public void setHome_rotation(String home_rotation) {
		this.home_rotation = home_rotation;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNeutral_site() {
		return neutral_site;
	}

	public void setNeutral_site(String neutral_site) {
		this.neutral_site = neutral_site;
	}

	public int getOurScore() {
		boolean michiganIsAway = away.equalsIgnoreCase("mich");
		if (away_points == null || home_points == null) {
			return -1;
		}

		return michiganIsAway ? Integer.parseInt(away_points) : Integer.parseInt(home_points);
	}

	public String getScheduled() {
		return scheduled;
	}

	public void setScheduled(String scheduled) {
		this.scheduled = scheduled;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getTheirScore() {
		boolean michiganIsAway = away.equalsIgnoreCase("mich");
		if (away_points == null || home_points == null) {
			return -1;
		}

		return michiganIsAway ? Integer.parseInt(home_points) : Integer.parseInt(away_points);
	}

	public String getThem() {
		return them();
	}

	public String getUs() {
		return us();
	}

	public Venue getVenue() {
		return venue;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}

	public Weather getWeather() {
		return weather;
	}

	public void setWeather(Weather weather) {
		this.weather = weather;
	}

	public String them() {
		return away.equalsIgnoreCase("mich") ? home : away;
	}

	public String us() {
		return away.equalsIgnoreCase("mich") ? away : home;
	}
}
