package org.clarke.regularSeasonModel;

import java.util.List;

class Week
{
    private String id;
    private String number;
    private List<Game> games;

    List<Game> getGames()
    {
        return games;
    }
}