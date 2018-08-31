package org.clarke.parsers;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ColorsParserTests
{
    private static final String UM_PRIMARY = "#f5d130";
    private static final String UM_SECONDARY = "#093161";
    private static final String AU_PRIMARY = "#dd550c";
    private static final String AU_SECONDARY = "#03244d";

    private static final String UM_NAME = "Michigan";
    private static final String AU_NAME = "Auburn";

    private final ColorsParser colorsParser = ColorsParser.getInstance();

    @Test
    public void testAuburnColors()
    {
        Pair<String, String> auColors = colorsParser.getTeamColors(AU_NAME);

        assertNotNull(auColors);
        assertEquals(AU_PRIMARY, auColors.getLeft());
        assertEquals(AU_SECONDARY, auColors.getRight());
    }

    @Test
    public void testMichiganColors()
    {
        Pair<String, String> umColors = colorsParser.getTeamColors(UM_NAME);

        assertNotNull(umColors);
        assertEquals(UM_PRIMARY, umColors.getLeft());
        assertEquals(UM_SECONDARY, umColors.getRight());
    }
}
