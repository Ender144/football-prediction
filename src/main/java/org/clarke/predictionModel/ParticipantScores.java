package org.clarke.predictionModel;

import org.apache.commons.lang3.tuple.Pair;
import org.clarke.MainVerticle;
import org.clarke.boxscoreModel.Boxscore;
import org.clarke.regularSeasonModel.Game;
import org.clarke.regularSeasonModel.RegularSeason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
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
        int ourScore = game.getOurScore();
        int theirScore = game.getTheirScore();

        if (game.getDate().isEqual(LocalDate.now()))
        {
            Boxscore gameScore = MainVerticle.getBoxscore(game);
            logger.info(gameScore.toString());
            if (gameScore.getAwayTeam().getId().equalsIgnoreCase("mich"))
            {
                ourScore = gameScore.getAwayTeam().getPoints();
                theirScore = gameScore.getHomeTeam().getPoints();
            } else
            {
                ourScore = gameScore.getHomeTeam().getPoints();
                theirScore = gameScore.getAwayTeam().getPoints();
            }
            logger.info("boxscore points: us={}, them={}", ourScore, theirScore);
        }

        return getScoreForGame(game, participant, ourScore, theirScore);
    }

    @SuppressWarnings("WeakerAccess")
    public int getScoreForGame(Game game, String participant, int ourScore, int theirScore)
    {
        int score = 0;
        SeasonPrediction participantPrediction = getParticipantPrediction(participant);
        logger.info("Game scores for {}, participant={}, ourScore={}, theirScore={}", game.getThem(), participant, ourScore, theirScore);

        if (theirScore != -1 && ourScore != -1 && participantPrediction != null)
        {
            score += predictedCorrectOutcome(participantPrediction, game) ?
                MetricWeights.SAME_OUTCOME.getWeight() :
                MetricWeights.DIFFERENT_OUTCOMES.getWeight();

            if (participantIsClosestToUs(participantPrediction, game, ourScore, theirScore))
            {
                score += MetricWeights.CLOSEST_OUR_SCORE.getWeight();
            }

            if (participantIsClosestToThem(participantPrediction, game, ourScore, theirScore))
            {
                score += MetricWeights.CLOSEST_THEIR_SCORE.getWeight();
            }
        }

        return score;
    }

    @SuppressWarnings("WeakerAccess")
    public boolean participantIsClosestToThem(SeasonPrediction participantPrediction, Game game, int ourScore, int theirScore)
    {
        Pair<Integer, Integer> minimumPointsDifferences = getMinimumPointDifferences(game, ourScore, theirScore);
        logger.info("closest to them? min differences: us-difference={}, them-difference={}", minimumPointsDifferences.getLeft(), minimumPointsDifferences.getRight());
        int minimumTheirPointsDifference = minimumPointsDifferences.getRight();

        logger.info("prediction and actual difference: {}, {}", minimumTheirPointsDifference, participantPrediction.getGamePrediction(game).getTheirScore() - theirScore);
        return minimumTheirPointsDifference == Math.abs(participantPrediction.getGamePrediction(game).getTheirScore() - theirScore);
    }

    @SuppressWarnings("WeakerAccess")
    public boolean participantIsClosestToUs(SeasonPrediction participantPrediction, Game game, int ourScore, int theirScore)
    {
        Pair<Integer, Integer> minimumPointsDifferences = getMinimumPointDifferences(game, ourScore, theirScore);
        logger.info("closest to us? min differences: us-difference={}, them-difference={}", minimumPointsDifferences.getLeft(), minimumPointsDifferences.getRight());
        int minimumOurPointsDifference = minimumPointsDifferences.getLeft();

        logger.info("prediction and actual difference: {}, {}", minimumOurPointsDifference, participantPrediction.getGamePrediction(game).getOurScore() - ourScore);
        return minimumOurPointsDifference == Math.abs(participantPrediction.getGamePrediction(game).getOurScore() - ourScore);
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

    private Pair<Integer, Integer> getMinimumPointDifferences(Game game, int ourScore, int theirScore)
    {
        int minimumOurScoreDifference = Integer.MAX_VALUE;
        int minimumTheirScoreDifference = Integer.MAX_VALUE;

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
