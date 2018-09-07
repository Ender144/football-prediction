package org.clarke.predictionModel;

import org.apache.commons.lang3.tuple.Pair;
import org.clarke.regularSeasonModel.Game;
import org.clarke.regularSeasonModel.RegularSeason;

import java.util.ArrayList;
import java.util.List;

public class ParticipantScores
{
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

        if (game.getTheirScore() != -1 && game.getOurScore() != -1 && participantPrediction != null)
        {
            Pair<Integer, Integer> minimumPointsDifferences = getMinimumPointDifferences(game);
            int minimumOurPointsDifference = minimumPointsDifferences.getLeft();
            int minimumTheirPointsDifference = minimumPointsDifferences.getRight();

            score += getParticipantOutcomeScore(game, participant);

            if (minimumOurPointsDifference == Math.abs(participantPrediction.getGamePrediction(game).getOurScore() - game.getOurScore()))
            {
                score += MetricWeights.CLOSEST_OUR_SCORE.getWeight();
            }

            if (minimumTheirPointsDifference == Math.abs(participantPrediction.getGamePrediction(game).getTheirScore() - game.getTheirScore()))
            {
                score += MetricWeights.CLOSEST_THEIR_SCORE.getWeight();
            }
        }

        return score;
    }

    private Pair<Integer, Integer> getMinimumPointDifferences(Game game)
    {
        int minimumOurScoreDifference = Integer.MAX_VALUE;
        int minimumTheirScoreDifference = Integer.MAX_VALUE;

        if (game.getTheirScore() != -1 && game.getOurScore() != -1)
        {
            for (SeasonPrediction prediction : allSeasonPredictions)
            {
                minimumOurScoreDifference = Integer.min(minimumOurScoreDifference, Math.abs(prediction.getGamePrediction(game).getOurScore() - game.getOurScore()));
                minimumTheirScoreDifference = Integer.min(minimumTheirScoreDifference, Math.abs(prediction.getGamePrediction(game).getTheirScore() - game.getTheirScore()));
            }
        }
        return Pair.of(minimumOurScoreDifference, minimumTheirScoreDifference);
    }

    private int getParticipantOutcomeScore(Game game, String participant)
    {
        SeasonPrediction participantPrediction = getParticipantPrediction(participant);
        if (participantPrediction != null)
        {
            Outcome predictedOutcome = getParticipantPrediction(participant).getGamePrediction(game).getPredictedOutcome();
            Outcome actualOutcome = game.getActualOutcome();

            return predictedOutcome == actualOutcome ?
                MetricWeights.SAME_OUTCOME.getWeight() :
                MetricWeights.DIFFERENT_OUTCOMES.getWeight();
        }

        return MetricWeights.DIFFERENT_OUTCOMES.getWeight();
    }

    private SeasonPrediction getParticipantPrediction(String participant)
    {
        return allSeasonPredictions.stream().filter(prediction -> prediction.getParticipant().equals(participant)).findFirst().orElse(null);
    }
}
