package org.clarke.predictionModel;

public class PredictedScore
{
    private int ourScore, theirScore;

    public PredictedScore(int ourScore, int theirScore)
    {
        this.ourScore = ourScore;
        this.theirScore = theirScore;
    }

    public int getOurScore()
    {
        return ourScore;
    }

    public int getTheirScore()
    {
        return theirScore;
    }
}
