package org.clarke.predictionModel;

import org.clarke.ExcelSeasonOutput;
import org.clarke.Main;
import org.clarke.regularSeasonModel.MockRegularSeason;
import org.clarke.regularSeasonModel.RegularSeason;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

class ParticipantScoresTests
{
    @Test
    void test()
    {
        RegularSeason mockRegularSeason = new MockRegularSeason();
        List<SeasonPrediction> predictions = Main.initializePredictionModel(mockRegularSeason);
        ParticipantScores scores = new ParticipantScores(predictions, mockRegularSeason);

        for (String participant : Main.participants)
        {
            System.out.println(participant + " current score: " + scores.getCurrentParticipantScore(participant));
        }

        try
        {
            ExcelSeasonOutput.printExcelSheet(mockRegularSeason, predictions);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
