package org.clarke;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.clarke.predictionModel.ParticipantScores;
import org.clarke.predictionModel.PredictedScore;
import org.clarke.predictionModel.SeasonPrediction;
import org.clarke.seasonModel.Game;
import org.clarke.seasonModel.RegularSeason;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ExcelSeasonOutput
{
    private static final String SHEET_SUFFIX = " Michigan Prediction";
    private static final String TITLE_SUFFIX = " Michigan Prediction Results";

    private static final String WEEKLY_RESULT = "Weekly Result";

    public static void printExcelSheet(RegularSeason season, List<SeasonPrediction> predictions) throws IOException
    {
        String sheetName = season.getSeason();
        String fileName = sheetName + SHEET_SUFFIX + ".xlsx";
        String title = sheetName + TITLE_SUFFIX;

        ParticipantScores scores = new ParticipantScores(predictions, season);

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);
        int rowIndex = 0;

        writeTitleRow(sheet, title, rowIndex++);
        writeNamesRow(sheet, predictions, rowIndex++);
        writeHeadersRow(sheet, predictions, rowIndex++);

        for (Game game : season.getMichiganGamesThisSeason())
        {
            writeGameRow(sheet, game, predictions, scores, rowIndex++);
        }

        rowIndex++;
        writeResultsRow(sheet, predictions, scores, rowIndex);

        rowIndex += 2;
        writeCurrentStandings(sheet, predictions, scores, rowIndex);

        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        workbook.write(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    private static void writeCurrentStandings(XSSFSheet sheet, List<SeasonPrediction> predictions, ParticipantScores scores, int rowIndex)
    {
        int cellIndex = 1;

        Map<String, Integer> standingsMap = new TreeMap<>();

        XSSFRow standingsStartRow = sheet.createRow(rowIndex++);

        XSSFCell standings = standingsStartRow.createCell(cellIndex);
        standings.setCellValue("Standings");

        XSSFCell name = standingsStartRow.createCell(cellIndex + 1);
        name.setCellValue("Name");

        XSSFCell score = standingsStartRow.createCell(cellIndex + 2);
        score.setCellValue("Current Score");

        for (SeasonPrediction prediction : predictions)
        {
            standingsMap.put(prediction.getParticipant(), scores.getCurrentParticipantScore(prediction.getParticipant()));
        }

        List<Map.Entry<String, Integer>> list = new ArrayList<>(standingsMap.entrySet());
        list.sort(Map.Entry.comparingByValue());
        Collections.reverse(list);

        Map<String, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list)
        {
            result.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Integer> standing : result.entrySet())
        {
            XSSFRow standingRow = sheet.createRow(rowIndex++);

            XSSFCell participant = standingRow.createCell(cellIndex + 1);
            participant.setCellValue(standing.getKey());

            XSSFCell currentScore = standingRow.createCell(cellIndex + 2);
            currentScore.setCellValue(standing.getValue());
        }
    }

    private static void writeGameRow(XSSFSheet sheet, Game game, List<SeasonPrediction> predictions, ParticipantScores scores, int rowNumber)
    {
        XSSFRow gameRow = sheet.createRow(rowNumber);
        int cellIndex = 0;
        XSSFCell date, opponent, outcome, us, them, spread, overUnder;
        int ourScore = game.getOurScore() == -1 ? 0 : game.getOurScore();
        int theirScore = game.getTheirScore() == -1 ? 0 : game.getTheirScore();
        int spreadNumber = Math.abs(ourScore - theirScore);
        int overUnderNumber = ourScore + theirScore;

        date = gameRow.createCell(cellIndex++);
        date.setCellValue(game.getDate().toString());

        opponent = gameRow.createCell(cellIndex++);
        String opponentAndLocation = game.getAwayTeam().equalsIgnoreCase("mich") ? "AT " + game.getHomeTeam() : "VS " + game.getAwayTeam();
        opponent.setCellValue(opponentAndLocation);

        outcome = gameRow.createCell(cellIndex++);
        outcome.setCellValue(game.getActualOutcome().toString());

        us = gameRow.createCell(cellIndex++);
        us.setCellValue(ourScore);

        them = gameRow.createCell(cellIndex++);
        them.setCellValue(theirScore);

        spread = gameRow.createCell(cellIndex++);
        spread.setCellValue(spreadNumber);

        overUnder = gameRow.createCell(cellIndex++);
        overUnder.setCellValue(overUnderNumber);

        for (SeasonPrediction prediction : predictions)
        {
            XSSFCell weeklyScore, predictedOutcome, predictedUs, predictedThem;
            PredictedScore predictedScore = prediction.getGamePrediction(game);

            weeklyScore = gameRow.createCell(cellIndex++);
            weeklyScore.setCellValue(scores.getScoreForGame(game, prediction.getParticipant()));

            predictedOutcome = gameRow.createCell(cellIndex++);
            predictedOutcome.setCellValue(predictedScore.getPredictedOutcome().toString());

            predictedUs = gameRow.createCell(cellIndex++);
            predictedUs.setCellValue(predictedScore.getOurScore());

            predictedThem = gameRow.createCell(cellIndex++);
            predictedThem.setCellValue(predictedScore.getTheirScore());
        }
    }

    private static void writeHeadersRow(XSSFSheet sheet, List<SeasonPrediction> predictions, int rowIndex)
    {
        // row 3 has Outcome, Us, Them, Spread, Over / Under, then just Outcome, Us, Them
        int cellIndex = 2;
        XSSFRow columnHeadersRow = sheet.createRow(rowIndex);

        XSSFCell actualOutcomeCell = columnHeadersRow.createCell(cellIndex);
        actualOutcomeCell.setCellValue("Outcome");
        cellIndex++;

        XSSFCell actualOurScoreCell = columnHeadersRow.createCell(cellIndex);
        actualOurScoreCell.setCellValue("Us");
        cellIndex++;

        XSSFCell actualTheirScoreCell = columnHeadersRow.createCell(cellIndex);
        actualTheirScoreCell.setCellValue("Them");
        cellIndex++;

        XSSFCell actualSpreadScoreCell = columnHeadersRow.createCell(cellIndex);
        actualSpreadScoreCell.setCellValue("Spread");
        cellIndex++;

        XSSFCell actualOverUnderCell = columnHeadersRow.createCell(cellIndex);
        actualOverUnderCell.setCellValue("Over / Under");

        for (SeasonPrediction ignored : predictions)
        {
            cellIndex += 2;
            XSSFCell predictedOutcomeCell = columnHeadersRow.createCell(cellIndex);
            predictedOutcomeCell.setCellValue("Outcome");
            cellIndex++;

            XSSFCell predictedOurScoreCell = columnHeadersRow.createCell(cellIndex);
            predictedOurScoreCell.setCellValue("Us");
            cellIndex++;

            XSSFCell predictedTheirScoreCell = columnHeadersRow.createCell(cellIndex);
            predictedTheirScoreCell.setCellValue("Them");
        }
    }

    private static void writeNamesRow(XSSFSheet sheet, List<SeasonPrediction> predictions, int rowIndex)
    {
        // row 2 has "Actual", "Weekly Result", and Names
        XSSFRow namesRow = sheet.createRow(rowIndex);
        int cellIndex = 4;
        XSSFCell actualResultsHeaderCell = namesRow.createCell(cellIndex);
        actualResultsHeaderCell.setCellValue("Actual");
        cellIndex += 3;

        for (SeasonPrediction prediction : predictions)
        {
            // Users start in col 8, zero indexed
            XSSFCell weeklyResultCell = namesRow.createCell(cellIndex);
            weeklyResultCell.setCellValue(WEEKLY_RESULT);
            cellIndex += 2;

            XSSFCell nameCell = namesRow.createCell(cellIndex);
            nameCell.setCellValue(prediction.getParticipant());
            cellIndex += 2;
        }
    }

    private static void writeResultsRow(XSSFSheet sheet, List<SeasonPrediction> predictions, ParticipantScores scores, int rowIndex)
    {
        XSSFRow resultsRow = sheet.createRow(rowIndex);
        int cellIndex = 1;

        XSSFCell title = resultsRow.createCell(cellIndex);
        title.setCellValue("Total Score");
        cellIndex += 6;

        for (SeasonPrediction prediction : predictions)
        {
            XSSFCell participantTotalScore = resultsRow.createCell(cellIndex);
            participantTotalScore.setCellValue(scores.getCurrentParticipantScore(prediction.getParticipant()));
            cellIndex += 4;
        }
    }

    private static void writeTitleRow(XSSFSheet sheet, String title, int rowIndex)
    {
        XSSFRow row = sheet.createRow(rowIndex);
        XSSFCell cell = row.createCell(0);
        //        cell.setCellStyle(null);
        cell.setCellValue(title);
    }
}
