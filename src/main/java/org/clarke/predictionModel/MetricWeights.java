package org.clarke.predictionModel;

public enum MetricWeights
{
    SAME_OUTCOME(10),
    DIFFERENT_OUTCOMES(0),
    CLOSEST_OUR_SCORE(5),
    CLOSEST_THEIR_SCORE(5),;

    private int weight;

    MetricWeights(int weight)
    {
        this.weight = weight;
    }

    public int getWeight()
    {
        return weight;
    }
}
