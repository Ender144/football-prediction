package org.clarke.predictionModel;

import org.clarke.ExcelSeasonOutput;
import org.clarke.ModelManager;
import org.clarke.regularSeasonModel.MockRegularSeason;
import org.clarke.regularSeasonModel.RegularSeason;
import org.clarke.rosterModel.Opponent;
import org.clarke.rosterModel.Team;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class ParticipantScoresTests
{
    @Test
    void test()
    {
        RegularSeason mockRegularSeason = new MockRegularSeason();
        List<SeasonPrediction> predictions = ModelManager.getPredictionsForSeason(mockRegularSeason);
        ParticipantScores scores = new ParticipantScores(predictions, mockRegularSeason);
        Map<String, Team> opponents = ModelManager.getOpponentsForSeason(mockRegularSeason).stream().collect(Collectors.toMap(Opponent::getName, Opponent::getTeam));

        for (String participant : ModelManager.getParticipants())
        {
            System.out.println(participant + " current score: " + scores.getCurrentParticipantScore(participant));
        }

        try
        {
            ExcelSeasonOutput.printExcelSheet(mockRegularSeason, predictions, opponents);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
