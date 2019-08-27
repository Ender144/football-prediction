package org.clarke.predictionModel;

import org.apache.commons.lang3.tuple.Pair;
import org.clarke.ModelManager;
import org.clarke.boxscoreModel.Boxscore;
import org.clarke.regularSeasonModel.Game;
import org.clarke.regularSeasonModel.RegularSeason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ParticipantScores {
	private static final Logger logger = LoggerFactory.getLogger(ParticipantScores.class);

	private List<SeasonPrediction> allSeasonPredictions = new ArrayList<>();
	private RegularSeason regularSeason;

	public ParticipantScores(List<SeasonPrediction> allSeasonPredictions, RegularSeason regularSeason) {
		this.allSeasonPredictions.clear();
		this.allSeasonPredictions.addAll(allSeasonPredictions);
		this.regularSeason = regularSeason;
	}

	public int getCurrentParticipantScore(String participant) {
		int participantScore = regularSeason.getMichiganGamesThisSeason().stream().mapToInt(game -> getScoreForGame(game, participant)).sum();
		logger.info("Getting current participant score: {} = {}", participant, participantScore);

		return participantScore;
	}

	public int getScoreForGame(Game game, String participant) {
		int score = 0;
		SeasonPrediction participantPrediction = getParticipantPrediction(participant);

		if (getCurrentGamePoints(game).getLeft() != -1 && getCurrentGamePoints(game).getRight() != -1 && participantPrediction != null) {
			score += predictedCorrectOutcome(participantPrediction, game) ?
				MetricWeights.SAME_OUTCOME.getWeight() :
				MetricWeights.DIFFERENT_OUTCOMES.getWeight();

			if (participantIsClosestToUs(participantPrediction, game)) {
				score += MetricWeights.CLOSEST_OUR_SCORE.getWeight();
			}

			if (participantIsClosestToThem(participantPrediction, game)) {
				score += MetricWeights.CLOSEST_THEIR_SCORE.getWeight();
			}
		}

		return score;
	}

	@SuppressWarnings("WeakerAccess")
	public boolean participantIsClosestToThem(SeasonPrediction participantPrediction, Game game) {
		Pair<Integer, Integer> minimumPointsDifferences = getMinimumPointDifferences(game);
		int minimumTheirPointsDifference = minimumPointsDifferences.getRight();

		return minimumTheirPointsDifference == Math.abs(participantPrediction.getGamePrediction(game).getTheirScore() - getCurrentGamePoints(game).getRight());
	}

	@SuppressWarnings("WeakerAccess")
	public boolean participantIsClosestToUs(SeasonPrediction participantPrediction, Game game) {
		Pair<Integer, Integer> minimumPointsDifferences = getMinimumPointDifferences(game);
		int minimumOurPointsDifference = minimumPointsDifferences.getLeft();

		return minimumOurPointsDifference == Math.abs(participantPrediction.getGamePrediction(game).getOurScore() - getCurrentGamePoints(game).getLeft());
	}

	@SuppressWarnings("WeakerAccess")
	public boolean predictedCorrectOutcome(SeasonPrediction participantPrediction, Game game) {
		if (participantPrediction != null) {
			Outcome predictedOutcome = participantPrediction.getGamePrediction(game).getPredictedOutcome();
			Outcome actualOutcome = game.getActualOutcome();

			return predictedOutcome == actualOutcome;
		}

		return false;
	}

	private Pair<Integer, Integer> getCurrentGamePoints(Game currentGame) {
		int ourScore = currentGame.getOurScore();
		int theirScore = currentGame.getTheirScore();

		Boxscore todaysBoxscore = ModelManager.getTodaysBoxscore();
		logger.info("checking if game is in progress to calculate scores; game status = {}, game completed = {}", todaysBoxscore.getStatus(), todaysBoxscore.getCompleted());
		if (!todaysBoxscore.getStatus().equals(ModelManager.UNINITIALIZED_BOXSCORE) &&
			!todaysBoxscore.getStatus().equals(ModelManager.PRE_GAME_BOXSCORE) &&
			todaysBoxscore.getAwayTeam().getId().equals(currentGame.getAway()) &&
			todaysBoxscore.getHomeTeam().getId().equals(currentGame.getHome())) {
			if (todaysBoxscore.getAwayTeam().getId().equalsIgnoreCase("mich")) {
				ourScore = todaysBoxscore.getAwayTeam().getPoints();
				theirScore = todaysBoxscore.getHomeTeam().getPoints();
			} else {
				ourScore = todaysBoxscore.getHomeTeam().getPoints();
				theirScore = todaysBoxscore.getAwayTeam().getPoints();
			}
		}

		return Pair.of(ourScore, theirScore);
	}

	private Pair<Integer, Integer> getMinimumPointDifferences(Game game) {
		int minimumOurScoreDifference = Integer.MAX_VALUE;
		int minimumTheirScoreDifference = Integer.MAX_VALUE;

		Pair<Integer, Integer> currentGamePoints = getCurrentGamePoints(game);
		int ourScore = currentGamePoints.getLeft();
		int theirScore = currentGamePoints.getRight();

		if (theirScore != -1 && ourScore != -1) {
			for (SeasonPrediction prediction : allSeasonPredictions) {
				minimumOurScoreDifference = Integer.min(minimumOurScoreDifference, Math.abs(prediction.getGamePrediction(game).getOurScore() - ourScore));
				minimumTheirScoreDifference = Integer.min(minimumTheirScoreDifference, Math.abs(prediction.getGamePrediction(game).getTheirScore() - theirScore));
			}
		}
		return Pair.of(minimumOurScoreDifference, minimumTheirScoreDifference);
	}

	private SeasonPrediction getParticipantPrediction(String participant) {
		return allSeasonPredictions.stream().filter(prediction -> prediction.getParticipant().equals(participant)).findFirst().orElse(null);
	}
}
