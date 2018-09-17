package org.clarke.predictionModel;

import org.clarke.ExcelSeasonOutput;
import org.clarke.Main;
import org.clarke.regularSeasonModel.MockRegularSeason;
import org.clarke.regularSeasonModel.RegularSeason;
import org.clarke.rosterModel.Team;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ParticipantScoresTests
{
    @Test
    void test()
    {
        RegularSeason mockRegularSeason = new MockRegularSeason();
        List<SeasonPrediction> predictions = Main.initializePredictionModel(mockRegularSeason);
        ParticipantScores scores = new ParticipantScores(predictions, mockRegularSeason);
        Map<String, Team> opponents = new HashMap<>();
        mockRegularSeason.getMichiganGamesThisSeason().forEach(game -> opponents.put(game.them(), Main.initializeTeam(game.them())));

        for (String participant : Main.participants)
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
