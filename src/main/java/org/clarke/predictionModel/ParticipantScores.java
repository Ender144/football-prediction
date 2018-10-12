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

public class ParticipantScores
{
    private static final Logger logger = LoggerFactory.getLogger(ParticipantScores.class);

    private List<SeasonPrediction> allSeasonPredictions = new ArrayList<>();
    private RegularSeason regularSeason;

    public ParticipantScores(List<SeasonPrediction> allSeasonPredictions, RegularSeason regularSeason)
    {
        this.allSeasonPredictions.clear();
        this.allSeasonPredictions.addAll(allSeasonPredictions);
        this.regularSeason = regularSeason;
    }

    public int getCurrentParticipantScore(String participant)
    {
        return regularSeason.getMichiganGamesThisSeason().stream().mapToInt(game -> getScoreForGame(game, participant)).sum();
    }

    public int getScoreForGame(Game game, String participant)
    {
        int score = 0;
        SeasonPrediction participantPrediction = getParticipantPrediction(participant);

        if (getCurrentGamePoints(game).getLeft() != -1 && getCurrentGamePoints(game).getRight() != -1 && participantPrediction != null)
        {
            score += predictedCorrectOutcome(participantPrediction, game) ?
                MetricWeights.SAME_OUTCOME.getWeight() :
                MetricWeights.DIFFERENT_OUTCOMES.getWeight();

            if (participantIsClosestToUs(participantPrediction, game))
            {
                score += MetricWeights.CLOSEST_OUR_SCORE.getWeight();
            }

            if (participantIsClosestToThem(participantPrediction, game))
            {
                score += MetricWeights.CLOSEST_THEIR_SCORE.getWeight();
            }
        }

        return score;
    }

    @SuppressWarnings("WeakerAccess")
    public boolean participantIsClosestToThem(SeasonPrediction participantPrediction, Game game)
    {
        Pair<Integer, Integer> minimumPointsDifferences = getMinimumPointDifferences(game);
        logger.info("closest to them? min differences: us-difference={}, them-difference={}", minimumPointsDifferences.getLeft(), minimumPointsDifferences.getRight());
        int minimumTheirPointsDifference = minimumPointsDifferences.getRight();

        logger.info("prediction and actual difference: {}, {}", minimumTheirPointsDifference, participantPrediction.getGamePrediction(game).getTheirScore() - getCurrentGamePoints(game).getRight());
        return minimumTheirPointsDifference == Math.abs(participantPrediction.getGamePrediction(game).getTheirScore() - getCurrentGamePoints(game).getRight());
    }

    @SuppressWarnings("WeakerAccess")
    public boolean participantIsClosestToUs(SeasonPrediction participantPrediction, Game game)
    {
        Pair<Integer, Integer> minimumPointsDifferences = getMinimumPointDifferences(game);
        logger.info("closest to us? min differences: us-difference={}, them-difference={}", minimumPointsDifferences.getLeft(), minimumPointsDifferences.getRight());
        int minimumOurPointsDifference = minimumPointsDifferences.getLeft();

        logger.info("prediction and actual difference: {}, {}", minimumOurPointsDifference, participantPrediction.getGamePrediction(game).getOurScore() - getCurrentGamePoints(game).getLeft());
        return minimumOurPointsDifference == Math.abs(participantPrediction.getGamePrediction(game).getOurScore() - getCurrentGamePoints(game).getLeft());
    }

    private Pair<Integer, Integer> getCurrentGamePoints(Game currentGame)
    {
        int ourScore = currentGame.getOurScore();
        int theirScore = currentGame.getTheirScore();

        Boxscore todaysBoxscore = ModelManager.getTodaysBoxscore();
        if (!todaysBoxscore.getStatus().equals(ModelManager.UNINITIALIZED_BOXSCORE))
        {
            logger.info(todaysBoxscore.toString());
            if (todaysBoxscore.getAwayTeam().getId().equalsIgnoreCase("mich"))
            {
                ourScore = todaysBoxscore.getAwayTeam().getPoints();
                theirScore = todaysBoxscore.getHomeTeam().getPoints();
            } else
            {
                ourScore = todaysBoxscore.getHomeTeam().getPoints();
                theirScore = todaysBoxscore.getAwayTeam().getPoints();
            }
            logger.info("boxscore points: us={}, them={}", ourScore, theirScore);
        }

        return Pair.of(ourScore, theirScore);
    }

    @SuppressWarnings("WeakerAccess")
    public boolean predictedCorrectOutcome(SeasonPrediction participantPrediction, Game game)
    {
        if (participantPrediction != null)
        {
            Outcome predictedOutcome = participantPrediction.getGamePrediction(game).getPredictedOutcome();
            Outcome actualOutcome = game.getActualOutcome();

            return predictedOutcome == actualOutcome;
        }

        return false;
    }

    private Pair<Integer, Integer> getMinimumPointDifferences(Game game)
    {
        int minimumOurScoreDifference = Integer.MAX_VALUE;
        int minimumTheirScoreDifference = Integer.MAX_VALUE;

        Pair<Integer, Integer> currentGamePoints = getCurrentGamePoints(game);
        int ourScore = currentGamePoints.getLeft();
        int theirScore = currentGamePoints.getRight();

        if (theirScore != -1 && ourScore != -1)
        {
            for (SeasonPrediction prediction : allSeasonPredictions)
            {
                minimumOurScoreDifference = Integer.min(minimumOurScoreDifference, Math.abs(prediction.getGamePrediction(game).getOurScore() - ourScore));
                minimumTheirScoreDifference = Integer.min(minimumTheirScoreDifference, Math.abs(prediction.getGamePrediction(game).getTheirScore() - theirScore));
            }
        }
        return Pair.of(minimumOurScoreDifference, minimumTheirScoreDifference);
    }

    private SeasonPrediction getParticipantPrediction(String participant)
    {
        return allSeasonPredictions.stream().filter(prediction -> prediction.getParticipant().equals(participant)).findFirst().orElse(null);
    }
}
