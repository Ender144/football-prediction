package org.clarke;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.clarke.parsers.TeamColorsManager;
import org.clarke.predictionModel.ParticipantScores;
import org.clarke.predictionModel.PredictedScore;
import org.clarke.predictionModel.SeasonPrediction;
import org.clarke.regularSeasonModel.Game;
import org.clarke.regularSeasonModel.RegularSeason;
import org.clarke.rosterModel.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ExcelSeasonOutput
{
    private static final Logger logger = LoggerFactory.getLogger(ExcelSeasonOutput.class);
    private static final String SHEET_SUFFIX = " Michigan Prediction";
    private static final String TITLE_SUFFIX = " Michigan Prediction Results";

    private static final String WEEKLY_RESULT = "Weekly Result";

    private static final XSSFWorkbook workbook = new XSSFWorkbook();
    private static final TeamColorsManager colors = TeamColorsManager.getInstance();
    private static final Pair<Color, Color> michiganColors = colors.getTeamColors("michigan");
    private static int rowNumber = 0;

    public static void printExcelSheet(RegularSeason season, List<SeasonPrediction> predictions, Map<String, Team> opponents) throws IOException
    {
        String sheetName = season.getSeason();
        String fileName = sheetName + SHEET_SUFFIX + ".xlsx";
        String title = sheetName + TITLE_SUFFIX;
        logger.info("Preparing to write " + fileName);

        ParticipantScores scores = new ParticipantScores(predictions, season);

        XSSFSheet sheet = workbook.createSheet(sheetName);

        logger.info("Writing title row...");
        writeTitleRow(sheet, title, rowNumber++);
        logger.info("Writing names row...");
        writeNamesRow(sheet, predictions, rowNumber++);
        logger.info("Writing headers row...");
        writeHeadersRow(sheet, predictions, rowNumber++);

        LocalDate lastGameDay = LocalDate.MIN, nextGameDay;
        for (Game game : season.getMichiganGamesThisSeason())
        {
            if (opponents.get(game.them()) != null)
            {
                logger.info("Writing game row for " + opponents.get(game.them()).getFullTeamName());
            }

            nextGameDay = game.getDate();

            if (!lastGameDay.isEqual(LocalDate.MIN) && !lastGameDay.plusDays(7).isEqual(nextGameDay))
            {
                writeByeWeekRow(sheet, lastGameDay.plusDays(7), rowNumber++, 5 + 4 * predictions.size());
            }
            writeGameRow(sheet, game, opponents.get(game.them()), predictions, scores, rowNumber++);
            lastGameDay = nextGameDay;
        }

        rowNumber++;
        logger.info("Writing results row...");
        writeResultsRow(sheet, predictions, scores, rowNumber);

        rowNumber += 2;
        logger.info("Writing current standings...");
        writeCurrentStandings(sheet, predictions, scores, rowNumber);

        for (int i = 0; i < predictions.size() * 4 + 7; i++)
        {
            sheet.autoSizeColumn(i);
        }

        logger.info("Saving file...");
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        workbook.write(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        logger.info("File saved...");
    }

    private static void styleStripe(XSSFCell cell)
    {
        Color back, front;
        if (rowNumber % 2 == 0)
        {
            back = michiganColors.getLeft();
            front = michiganColors.getRight();
        } else
        {
            back = michiganColors.getRight();
            front = michiganColors.getLeft();
        }

        XSSFCellStyle style = workbook.createCellStyle();
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillForegroundColor(new XSSFColor(front));

        XSSFFont font = workbook.createFont();
        style.setFillBackgroundColor(new XSSFColor(back));
        font.setColor(new XSSFColor(back));
        style.setFont(font);

        style.setAlignment(HorizontalAlignment.CENTER);
        cell.setCellStyle(style);
    }

    private static void styleTeamColors(String teamName, XSSFCell cell)
    {
        XSSFCellStyle style = workbook.createCellStyle();
        Pair<Color, Color> teamColors = colors.getTeamColors(teamName);

        if (teamColors != null)
        {
            if (teamColors.getLeft() != null)
            {
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                style.setFillForegroundColor(new XSSFColor(teamColors.getLeft()));
            }

            XSSFFont font = workbook.createFont();
            if (teamColors.getRight() != null)
            {
                style.setFillBackgroundColor(new XSSFColor(teamColors.getRight()));
                font.setColor(new XSSFColor(teamColors.getRight()));
                style.setFont(font);
            } else
            {
                font.setColor(new XSSFColor(Color.BLACK));
                style.setFont(font);
            }
        }

        style.setAlignment(HorizontalAlignment.CENTER);
        cell.setCellStyle(style);
    }

    private static void writeByeWeekRow(XSSFSheet sheet, LocalDate byeWeekDate, int rowNumber, int numberOfBlankCells)
    {
        XSSFRow byeRow = sheet.createRow(rowNumber);
        int cellIndex = 0;
        XSSFCell date, bye;

        date = byeRow.createCell(cellIndex++);
        XSSFCellStyle centerAligned = workbook.createCellStyle();
        centerAligned.setAlignment(HorizontalAlignment.CENTER);
        date.setCellStyle(centerAligned);
        date.setCellValue(byeWeekDate.toString());

        bye = byeRow.createCell(cellIndex++);
        styleTeamColors("none", bye);
        bye.setCellValue("BYE");

        for (int i = 0; i < numberOfBlankCells; i++)
        {
            XSSFCell blankCell = byeRow.createCell(cellIndex++);
            styleStripe(blankCell);
            blankCell.setCellValue("-");
        }
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

    private static void writeGameRow(XSSFSheet sheet, Game game, Team opponentTeam, List<SeasonPrediction> predictions, ParticipantScores scores, int rowNumber)
    {
        XSSFRow gameRow = sheet.createRow(rowNumber);
        int cellIndex = 0;
        XSSFCell date, opponent, outcome, us, them, spread, overUnder;
        int ourScore = game.getOurScore() == -1 ? 0 : game.getOurScore();
        int theirScore = game.getTheirScore() == -1 ? 0 : game.getTheirScore();
        int spreadNumber = Math.abs(ourScore - theirScore);
        int overUnderNumber = ourScore + theirScore;

        date = gameRow.createCell(cellIndex++);
        XSSFCellStyle centerAligned = workbook.createCellStyle();
        centerAligned.setAlignment(HorizontalAlignment.CENTER);
        date.setCellStyle(centerAligned);
        date.setCellValue(game.getDate().toString());

        opponent = gameRow.createCell(cellIndex++);
        String opponentAndLocation = game.getAwayTeam().equalsIgnoreCase("mich") ? "AT " + opponentTeam.getFullTeamName() : "VS " + opponentTeam.getFullTeamName();
        styleTeamColors(opponentTeam.getFullTeamName(), opponent);
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
            styleStripe(weeklyScore);
            weeklyScore.setCellValue(scores.getScoreForGame(game, prediction.getParticipant()));

            predictedOutcome = gameRow.createCell(cellIndex++);
            styleStripe(predictedOutcome);
            predictedOutcome.setCellValue(predictedScore.getPredictedOutcome().toString());

            predictedUs = gameRow.createCell(cellIndex++);
            styleStripe(predictedUs);
            predictedUs.setCellValue(predictedScore.getOurScore());

            predictedThem = gameRow.createCell(cellIndex++);
            styleStripe(predictedThem);
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
            XSSFCell blankStripe = columnHeadersRow.createCell(cellIndex - 1);
            styleStripe(blankStripe);
            XSSFCell predictedOutcomeCell = columnHeadersRow.createCell(cellIndex);
            styleStripe(predictedOutcomeCell);
            predictedOutcomeCell.setCellValue("Outcome");
            cellIndex++;

            XSSFCell predictedOurScoreCell = columnHeadersRow.createCell(cellIndex);
            styleStripe(predictedOurScoreCell);
            predictedOurScoreCell.setCellValue("Us");
            cellIndex++;

            XSSFCell predictedTheirScoreCell = columnHeadersRow.createCell(cellIndex);
            styleStripe(predictedTheirScoreCell);
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
            styleStripe(weeklyResultCell);
            weeklyResultCell.setCellValue(WEEKLY_RESULT);

            XSSFCell blankStripe1 = namesRow.createCell(cellIndex + 1);
            styleStripe(blankStripe1);
            cellIndex += 2;

            XSSFCell nameCell = namesRow.createCell(cellIndex);
            styleStripe(nameCell);
            nameCell.setCellValue(prediction.getParticipant());

            XSSFCell blankStripe2 = namesRow.createCell(cellIndex + 1);
            styleStripe(blankStripe2);
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

        styleTeamColors("michigan", cell);
        cell.setCellValue(title);
    }
}
