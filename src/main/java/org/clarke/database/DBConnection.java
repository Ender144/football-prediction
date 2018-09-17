package org.clarke.database;

import org.clarke.database.regularSeasonModel.DbBroadcast;
import org.clarke.database.regularSeasonModel.DbGame;
import org.clarke.database.regularSeasonModel.DbRegularSeason;
import org.clarke.database.regularSeasonModel.DbVenue;
import org.clarke.database.regularSeasonModel.DbWeather;
import org.clarke.database.regularSeasonModel.DbWeek;
import org.clarke.database.regularSeasonModel.DbWind;
import org.clarke.database.rosterModel.DbPlayer;
import org.clarke.database.rosterModel.DbTeam;
import org.clarke.regularSeasonModel.Broadcast;
import org.clarke.regularSeasonModel.Game;
import org.clarke.regularSeasonModel.RegularSeason;
import org.clarke.regularSeasonModel.Venue;
import org.clarke.regularSeasonModel.Weather;
import org.clarke.regularSeasonModel.Week;
import org.clarke.regularSeasonModel.Wind;
import org.clarke.rosterModel.Player;
import org.clarke.rosterModel.Team;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.JDBCConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DBConnection
{
    private static final Logger logger = LoggerFactory.getLogger(DBConnection.class);
    private static DBConnection instance;
    private static boolean connected = false;
    private static SessionFactory factory;

    private DBConnection() {}

    public static DBConnection getInstance()
    {
        if (instance == null)
        {
            instance = new DBConnection();
        }
        checkIfConnected();
        return instance;
    }

    private static void checkIfConnected()
    {
        try
        {
            if (factory != null && connected)
            {
                Transaction transaction = null;
                if (factory.getCurrentSession().isOpen())
                {
                    transaction = factory.getCurrentSession().getTransaction();
                }
                if (transaction == null || !transaction.isActive())
                {
                    transaction = factory.getCurrentSession().beginTransaction();
                }

                factory.getCurrentSession().doWork(connection -> {
                    if (!connection.isValid(1))
                    {
                        connected = false;
                        logger.error("Couldn't connect to database!");
                    }
                });
                if (connected)
                {
                    transaction.commit();
                }
            }
        } catch (JDBCConnectionException e)
        {
            e.printStackTrace();
        }
    }

    public void addRegularSeason(RegularSeason regularSeason)
    {
        if (affirmConnection())
        {
            logger.info("Saving " + regularSeason.getSeason() + " to the database...");
            DbRegularSeason dbRegularSeason = new DbRegularSeason();
            dbRegularSeason.setSeason(regularSeason.getSeason());
            dbRegularSeason.setType(regularSeason.getType());
            Session session = factory.getCurrentSession();
            Transaction transaction = session.beginTransaction();
            session.saveOrUpdate(dbRegularSeason);
            transaction.commit();

            List<DbWeek> dbWeeks = new ArrayList<>();
            for (Week week : regularSeason.getWeeks())
            {
                DbWeek dbWeek = new DbWeek();
                dbWeek.setId(week.getId());
                dbWeek.setNumber(week.getNumber());
                dbWeek.setDbSeason(dbRegularSeason);

                session = factory.getCurrentSession();
                transaction = session.beginTransaction();
                session.saveOrUpdate(dbWeek);
                transaction.commit();

                List<DbGame> dbGames = new ArrayList<>();
                for (Game game : week.getGames())
                {
                    DbGame dbGame = new DbGame();
                    dbGame.setDbWeek(dbWeek);

                    dbGame.setScheduled(game.getScheduled());
                    dbGame.setAway(game.getAway());
                    dbGame.setAway_points(game.getAway_points());
                    dbGame.setHome(game.getHome());
                    dbGame.setHome_points(game.getHome_points());
                    dbGame.setId(game.getId());
                    dbGame.setCoverage(game.getCoverage());
                    dbGame.setHome_rotation(game.getHome_rotation());
                    dbGame.setAway_rotation(game.getAway_rotation());
                    dbGame.setStatus(game.getStatus());
                    dbGame.setNeutral_site(game.getNeutral_site());

                    session = factory.getCurrentSession();
                    transaction = session.beginTransaction();
                    session.saveOrUpdate(dbGame);
                    transaction.commit();

                    DbVenue dbVenue = getDbVenue(game.getVenue());
                    session = factory.getCurrentSession();
                    transaction = session.beginTransaction();
                    session.saveOrUpdate(dbVenue);
                    transaction.commit();
                    dbGame.setDbVenue(dbVenue);

                    DbBroadcast dbBroadcast = getDbBroadcast(game.getBroadcast());
                    session = factory.getCurrentSession();
                    transaction = session.beginTransaction();
                    session.saveOrUpdate(dbBroadcast);
                    transaction.commit();
                    dbGame.setDbBroadcast(dbBroadcast);

                    DbWeather dbWeather = getDbWeather(game.getWeather());
                    session = factory.getCurrentSession();
                    transaction = session.beginTransaction();
                    session.saveOrUpdate(dbWeather);
                    transaction.commit();
                    dbGame.setDbWeather(dbWeather);

                    session = factory.getCurrentSession();
                    transaction = session.beginTransaction();
                    session.saveOrUpdate(dbGame);
                    transaction.commit();

                    dbGames.add(dbGame);
                }

                dbWeek.setDbGames(dbGames);

                session = factory.getCurrentSession();
                transaction = session.beginTransaction();
                session.saveOrUpdate(dbWeek);
                transaction.commit();

                dbWeeks.add(dbWeek);
            }

            dbRegularSeason.setDbWeeks(dbWeeks);

            session = factory.getCurrentSession();
            transaction = session.beginTransaction();
            session.saveOrUpdate(dbRegularSeason);
            transaction.commit();
        }
    }

    public void addTeam(Team team)
    {
        if (affirmConnection() && team != null)
        {
            logger.info("Saving " + team.getFullTeamName() + " to the database...");
            DbTeam dbTeam = new DbTeam();
            dbTeam.setId(team.getId());
            dbTeam.setMarket(team.getMarket());
            dbTeam.setName(team.getName());

            Session session = factory.getCurrentSession();
            Transaction transaction = session.beginTransaction();
            session.saveOrUpdate(dbTeam);
            transaction.commit();

            List<DbPlayer> dbPlayers = new ArrayList<>();
            for (Player player : team.getPlayers())
            {
                DbPlayer dbPlayer = getDbPlayer(player);
                dbPlayer.setDbTeam(dbTeam);

                session = factory.getCurrentSession();
                transaction = session.beginTransaction();
                session.saveOrUpdate(dbPlayer);
                transaction.commit();

                dbPlayers.add(dbPlayer);
            }

            dbTeam.setDbPlayers(dbPlayers);

            session = factory.getCurrentSession();
            transaction = session.beginTransaction();
            session.saveOrUpdate(dbTeam);
            transaction.commit();
        }
    }

    public String connect()
    {
        String message = "";

        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:postgresql://127.0.0.1:5432/football_prediction");
        configuration.setProperty("hibernate.connection.username", "tclarke");
        configuration.setProperty("hibernate.connection.password", "tclarkepass");
        configuration.setProperty("hibernate.current_session_context_class", "thread");
        configuration.setProperty("hibernate.transaction.factory_class", "org.hibernate.transaction.JDBCTransactionFactory");
        configuration.setProperty("hibernate.connection.pool_size", "1");
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        configuration.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.internal.NoCacheProvider");
        configuration.setProperty("hibernate.show_sql", "false");
        //        configuration.setProperty("hibernate.hbm2ddl.auto", "update");
        configuration.setProperty("hibernate.temp.use_jdbc_metadata_defaults", "false");
        configuration.addAnnotatedClass(DbPlayer.class);
        configuration.addAnnotatedClass(DbTeam.class);
        configuration.addAnnotatedClass(DbBroadcast.class);
        configuration.addAnnotatedClass(DbGame.class);
        configuration.addAnnotatedClass(DbRegularSeason.class);
        configuration.addAnnotatedClass(DbVenue.class);
        configuration.addAnnotatedClass(DbWeather.class);
        configuration.addAnnotatedClass(DbWeek.class);
        configuration.addAnnotatedClass(DbWind.class);

        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());

        try
        {
            factory = configuration.buildSessionFactory(builder.build());
            connected = true;
        } catch (HibernateException he)
        {
            connected = false;
            message = he.getMessage();
        }

        return message;
    }

    public RegularSeason getRegularSeason(String seasonId)
    {
        RegularSeason season = null;
        if (affirmConnection())
        {
            Session session = factory.getCurrentSession();
            Transaction transaction = session.beginTransaction();
            List results = session.createQuery("from " + DbRegularSeason.class.getName() + " where " +
                DbRegularSeason.SEASON + " = '" + seasonId + "'").list();
            if (results.size() == 1)
            {
                season = createModelRegularSeason((DbRegularSeason) results.get(0));
                logger.info(season.getSeason() + " season retrieved from database...");
            }
            transaction.commit();
        }

        return season;
    }

    public Team getTeam(String teamAbbreviation)
    {
        Team team = null;
        if (affirmConnection())
        {
            Session session = factory.getCurrentSession();
            Transaction transaction = session.beginTransaction();
            List results = session.createQuery("from " + DbTeam.class.getName() + " where " +
                DbTeam.ID + " = '" + teamAbbreviation + "'").list();
            if (results.size() == 1)
            {
                team = createModelTeam((DbTeam) results.get(0));
                logger.info(team.getFullTeamName() + " retrieved from database...");
            }
            transaction.commit();
        }

        return team;
    }

    private boolean affirmConnection()
    {
        if (!connected)
        {
            logger.error("Trying to access database, but connection failed...");
        }

        return connected;
    }

    private RegularSeason createModelRegularSeason(DbRegularSeason dbRegularSeason)
    {
        RegularSeason regularSeason = new RegularSeason();

        regularSeason.setSeason(dbRegularSeason.getSeason());
        regularSeason.setType(dbRegularSeason.getType());

        List<Week> weeks = new ArrayList<>();
        for (DbWeek dbWeek : dbRegularSeason.getDbWeeks())
        {
            Week week = new Week();
            week.setId(dbWeek.getId());
            week.setNumber(dbWeek.getNumber());

            List<Game> games = new ArrayList<>();
            for (DbGame dbGame : dbWeek.getDbGames())
            {
                Game game = new Game();
                game.setScheduled(dbGame.getScheduled());
                game.setAway(dbGame.getAway());
                game.setAway_points(dbGame.getAway_points());
                game.setHome(dbGame.getHome());
                game.setHome_points(dbGame.getHome_points());
                game.setId(dbGame.getId());
                game.setCoverage(dbGame.getCoverage());
                game.setHome_rotation(dbGame.getHome_rotation());
                game.setAway_rotation(dbGame.getAway_rotation());
                game.setStatus(dbGame.getStatus());
                game.setNeutral_site(dbGame.getNeutral_site());

                game.setVenue(getModelVenue(dbGame.getDbVenue()));
                game.setBroadcast(getModelBroadcast(dbGame.getDbBroadcast()));
                game.setWeather(getModelWeather(dbGame.getDbWeather()));

                games.add(game);
            }

            week.setGames(games);

            weeks.add(week);
        }

        regularSeason.setWeeks(weeks);

        return regularSeason;
    }

    private Team createModelTeam(DbTeam dbTeam)
    {
        Team team = new Team();

        if (dbTeam != null)
        {
            team.setId(dbTeam.getId());
            team.setMarket(dbTeam.getMarket());
            team.setName(dbTeam.getName());

            List<Player> players = new ArrayList<>();
            for (DbPlayer dbPlayer : dbTeam.getDbPlayers())
            {
                players.add(getModelPlayer(dbPlayer));
            }

            team.setPlayers(players);
        }

        return team;
    }

    private DbBroadcast getDbBroadcast(Broadcast broadcast)
    {
        DbBroadcast dbBroadcast = new DbBroadcast();

        if (broadcast != null)
        {
            dbBroadcast.setNetwork(broadcast.getNetwork());
            dbBroadcast.setSatellite(broadcast.getSatellite());
        }

        return dbBroadcast;
    }

    private DbPlayer getDbPlayer(Player player)
    {
        DbPlayer dbPlayer = new DbPlayer();

        if (player != null)
        {
            dbPlayer.setId(player.getId());
            dbPlayer.setName_full(player.getName_full());
            dbPlayer.setName_first(player.getName_first());
            dbPlayer.setName_last(player.getName_last());
            dbPlayer.setName_abbr(player.getName_abbr());
            dbPlayer.setBirth_place(player.getBirth_place());
            dbPlayer.setHeight(player.getHeight());
            dbPlayer.setWeight(player.getWeight());
            dbPlayer.setPosition(player.getPosition());
            dbPlayer.setJersey_number(player.getJersey_number());
            dbPlayer.setStatus(player.getStatus());
            dbPlayer.setExperience(player.getExperience());
        }

        return dbPlayer;
    }

    private DbVenue getDbVenue(Venue venue)
    {
        DbVenue dbVenue = new DbVenue();

        if (venue != null)
        {
            dbVenue.setId(venue.getId());
            dbVenue.setCountry(venue.getCountry());
            dbVenue.setName(venue.getName());
            dbVenue.setCity(venue.getCity());
            dbVenue.setState(venue.getState());
            dbVenue.setCapacity(venue.getCapacity());
            dbVenue.setType(venue.getType());
            dbVenue.setZip(venue.getZip());
            dbVenue.setAddress(venue.getAddress());
        }

        return dbVenue;
    }

    private DbWeather getDbWeather(Weather weather)
    {
        DbWeather dbWeather = new DbWeather();

        if (weather != null)
        {
            dbWeather.setTemperature(weather.getTemperature());
            dbWeather.setCondition(weather.getCondition());
            dbWeather.setHumidity(weather.getHumidity());

            DbWind dbWind = new DbWind();
            dbWind.setDirection(weather.getWind().getDirection());
            dbWind.setSpeed(weather.getWind().getSpeed());

            Session session = factory.getCurrentSession();
            Transaction transaction = session.beginTransaction();
            session.saveOrUpdate(dbWind);
            transaction.commit();

            dbWeather.setDbWind(dbWind);
        }

        return dbWeather;
    }

    private Broadcast getModelBroadcast(DbBroadcast dbBroadcast)
    {
        Broadcast broadcast = new Broadcast();

        if (dbBroadcast != null)
        {
            broadcast.setNetwork("unknown");
            broadcast.setSatellite("unknown");
        }
        return broadcast;
    }

    private Player getModelPlayer(DbPlayer dbPlayer)
    {
        Player player = new Player();

        if (dbPlayer != null)
        {
            player.setId(dbPlayer.getId());
            player.setName_full(dbPlayer.getName_full());
            player.setName_first(dbPlayer.getName_first());
            player.setName_last(dbPlayer.getName_last());
            player.setName_abbr(dbPlayer.getName_abbr());
            player.setBirth_place(dbPlayer.getBirth_place());
            player.setHeight(dbPlayer.getHeight());
            player.setWeight(dbPlayer.getWeight());
            player.setPosition(dbPlayer.getPosition());
            player.setJersey_number(dbPlayer.getJersey_number());
            player.setStatus(dbPlayer.getStatus());
            player.setExperience(dbPlayer.getExperience());
        }

        return player;
    }

    private Venue getModelVenue(DbVenue dbVenue)
    {
        Venue venue = new Venue();

        if (dbVenue != null)
        {
            venue.setId(dbVenue.getId());
            venue.setCountry(dbVenue.getCountry());
            venue.setName(dbVenue.getName());
            venue.setCity(dbVenue.getCity());
            venue.setState(dbVenue.getState());
            venue.setCapacity(dbVenue.getCapacity());
            venue.setType(dbVenue.getType());
            venue.setZip(dbVenue.getZip());
            venue.setAddress(dbVenue.getAddress());
        }

        return venue;
    }

    private Weather getModelWeather(DbWeather dbWeather)
    {
        Weather weather = new Weather();
        if (dbWeather != null)
        {
            weather.setTemperature("unknown");
            weather.setCondition("unknown");
            weather.setHumidity("unknown");

            Wind wind = new Wind();
            wind.setDirection("unknown");
            wind.setSpeed("unknown");
            weather.setWind(wind);
        }

        return weather;
    }
}
