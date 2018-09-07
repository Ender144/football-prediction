package org.clarke.parsers;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TeamColorsManagerTests
{
    private static final Color UM_PRIMARY = new Color(255, 203, 5);
    private static final Color UM_SECONDARY = new Color(0, 39, 76);
    private static final Color AU_PRIMARY = new Color(232, 119, 34);
    private static final Color AU_SECONDARY = new Color(12, 35, 64);

    private static final String UM_NAME = "Michigan";
    private static final String AU_NAME = "Auburn";

    private final TeamColorsManager teamColorsManager = TeamColorsManager.getInstance();

    @Test
    public void testAuburnColors()
    {
        Pair<Color, Color> auColors = teamColorsManager.getTeamColors(AU_NAME);

        assertNotNull(auColors);
        assertEquals(AU_PRIMARY, auColors.getLeft());
        assertEquals(AU_SECONDARY, auColors.getRight());
    }

    @Test
    public void testMichiganColors()
    {
        Pair<Color, Color> umColors = teamColorsManager.getTeamColors(UM_NAME);

        assertNotNull(umColors);
        assertEquals(UM_PRIMARY, umColors.getLeft());
        assertEquals(UM_SECONDARY, umColors.getRight());
    }
}
