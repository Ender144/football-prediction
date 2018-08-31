package org.clarke.predictionModel;

import org.clarke.seasonModel.Game;

import java.util.Map;

public class SeasonPrediction
{
    private String predictorName;
    private Map<Game, PredictedScore> gamePredictions;

    public SeasonPrediction(String predictorName, Map<Game, PredictedScore> gamePredictions)
    {
        this.predictorName = predictorName;
        this.gamePredictions = gamePredictions;
    }

    public Map<Game, PredictedScore> getGamePredictions()
    {
        return gamePredictions;
    }

    public String getPredictorName()
    {
        return predictorName;
    }
}
