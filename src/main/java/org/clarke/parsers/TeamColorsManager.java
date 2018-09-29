package org.clarke.parsers;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.clarke.regularSeasonModel.Game;
import org.clarke.rosterModel.Opponent;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TeamColorsManager
{
    private static final String COLORS_SHEET = "/team_colors.xlsx";

    private static TeamColorsManager instance;

    private Map<String, Pair<Color, Color>> teamColorMap = new HashMap<>();
    private Map<String, Pair<String, String>> hexTeamColorMap = new HashMap<>();

    private TeamColorsManager()
    {
        parseColors();
    }

    public static TeamColorsManager getInstance()
    {
        if (instance == null)
        {
            instance = new TeamColorsManager();
        }

        return instance;
    }

    public Pair<Color, Color> getTeamColors(String teamName)
    {
        return teamColorMap.get(teamName.toLowerCase());
    }

    public Pair<String, String> getOpponentColors(Game game, List<Opponent> opponents)
    {
        String teamName = "none";
        Opponent indexOpponent = new Opponent(game.them(), null);

        if (opponents != null)
        {
            Opponent gameOpponent = opponents.get(opponents.indexOf(indexOpponent));

            if (gameOpponent != null)
            {
                teamName = gameOpponent.getTeam().getFullTeamName().toLowerCase();
            }
        }

        Pair<String, String> hexColors = hexTeamColorMap.get(teamName);
        return hexColors == null ? Pair.of("#ffffff", "#000000") : hexColors;
    }

    private void parseColors()
    {
        InputStream teamColorsStream = getClass().getResourceAsStream(COLORS_SHEET);

        if (teamColorsStream != null)
        {
            XSSFWorkbook wb = null;
            try
            {
                wb = new XSSFWorkbook(teamColorsStream);
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            if (wb != null)
            {
                XSSFSheet sheet = wb.getSheetAt(0);
                XSSFRow row;

                Iterator rows = sheet.rowIterator();

                while (rows.hasNext())
                {
                    row = (XSSFRow) rows.next();

                    XSSFCell nameCell = row.getCell(0);
                    XSSFCell primaryCell = row.getCell(1);
                    XSSFCell secondaryCell = row.getCell(2);

                    if (nameCell.getCellTypeEnum() == CellType.STRING && primaryCell.getCellTypeEnum() == CellType.STRING)
                    {
                        String teamName = nameCell.getStringCellValue().toLowerCase();
                        String primaryColor = primaryCell.getStringCellValue().toLowerCase();
                        String secondaryColor = "";

                        if (secondaryCell.getCellTypeEnum() == CellType.STRING)
                        {
                            secondaryColor = secondaryCell.getStringCellValue().toLowerCase();
                        }

                        teamColorMap.put(teamName, Pair.of(Color.decode(primaryColor), Color.decode(secondaryColor)));
                        hexTeamColorMap.put(teamName, Pair.of(primaryColor, secondaryColor));
                    }
                }
            }
        }
    }
}
