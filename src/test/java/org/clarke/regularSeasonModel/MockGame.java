package org.clarke.regularSeasonModel;

class MockGame extends Game
{
    public int getOurScore()
    {
        return Integer.parseInt(home_points);
    }

    void setOurScore(int score)
    {
        home_points = String.valueOf(score);
    }

    public int getTheirScore()
    {
        return Integer.parseInt(away_points);
    }

    void setTheirScore(int score)
    {
        away_points = String.valueOf(score);
    }

    public void setScheduled(String scheduled)
    {
        this.scheduled = scheduled;
    }

    void setOurTeam(String team)
    {
        home = team;
    }

    void setTheirTeam(String team)
    {
        away = team;
    }
}
