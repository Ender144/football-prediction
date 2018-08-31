package org.clarke.parsers;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ColorsParser
{
    private static final String COLORS_SHEET = "/team_colors.xlsx";

    private static ColorsParser instance;

    private Map<String, Pair<String, String>> teamColorMap = new HashMap<>();

    private ColorsParser()
    {
        parseColors();
    }

    public static ColorsParser getInstance()
    {
        if (instance == null)
        {
            instance = new ColorsParser();
        }

        return instance;
    }

    public Pair<String, String> getTeamColors(String teamName)
    {
        return teamColorMap.get(teamName.toLowerCase());
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

                        teamColorMap.put(teamName, Pair.of(primaryColor, secondaryColor));
                    }
                }
            }
        }
    }
}
