package org.clarke.regularSeasonModel;

import java.util.List;

@SuppressWarnings("unused")
public class Week
{
    private String id;
    private String number;
    private List<Game> games;

    public List<Game> getGames()
    {
        return games;
    }

    public void setGames(List<Game> games)
    {
        this.games = games;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getNumber()
    {
        return number;
    }

    public void setNumber(String number)
    {
        this.number = number;
    }
}
