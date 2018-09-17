package org.clarke.regularSeasonModel;

import java.util.ArrayList;
import java.util.List;

public class MockRegularSeason extends RegularSeason
{
    @Override
    public List<Game> getGamesByTeam(String teamName)
    {
        List<Game> mockGames = new ArrayList<>();

        MockGame mockGame = new MockGame();
        mockGame.setOurTeam("mich");
        mockGame.setOurScore(23);
        mockGame.setTheirTeam("ND");
        mockGame.setTheirScore(21);
        mockGame.setScheduled("2018-09-10T");

        MockGame mockWMCGame = new MockGame();
        mockWMCGame.setOurTeam("mich");
        mockWMCGame.setOurScore(3);
        mockWMCGame.setTheirTeam("WMC");
        mockWMCGame.setTheirScore(100);
        mockWMCGame.setScheduled("2018-09-11T");

        mockGames.add(mockGame);
        mockGames.add(mockWMCGame);
        mockGames.add(getUnplayedGame("SMU", 12));
        mockGames.add(getUnplayedGame("NEB", 13));
        mockGames.add(getUnplayedGame("NW", 14));
        mockGames.add(getUnplayedGame("PSU", 15));
        mockGames.add(getUnplayedGame("MAR", 16));
        mockGames.add(getUnplayedGame("MSU", 17));
        mockGames.add(getUnplayedGame("IU", 18));
        mockGames.add(getUnplayedGame("OSU", 19));
        mockGames.add(getUnplayedGame("RUT", 20));
        mockGames.add(getUnplayedGame("WIS", 21));

        return mockGames;
    }

    @Override
    public String getSeason()
    {
        return "TestSeason2018";
    }

    private Game getUnplayedGame(String opponent, int day)
    {
        MockGame unplayed = new MockGame();

        unplayed.setTheirTeam(opponent);
        unplayed.setTheirScore(-1);
        unplayed.setOurTeam("mich");
        unplayed.setOurScore(-1);
        unplayed.setScheduled("2018-09-" + day + "T");

        return unplayed;
    }
}
