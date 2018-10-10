package org.clarke;

import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.templ.FreeMarkerTemplateEngine;
import io.vertx.ext.web.templ.TemplateEngine;
import org.clarke.api.JsonResponse;
import org.clarke.api.JsonRestMessenger;
import org.clarke.boxscoreModel.Boxscore;
import org.clarke.configuration.SR_API_Configuration;
import org.clarke.parsers.TeamColorsManager;
import org.clarke.predictionModel.ParticipantScores;
import org.clarke.predictionModel.SeasonPrediction;
import org.clarke.regularSeasonModel.Game;
import org.clarke.regularSeasonModel.RegularSeason;
import org.clarke.rosterModel.Opponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.clarke.Main.initializePredictionModel;
import static org.clarke.Main.initializeSeasonModel;
import static org.clarke.Main.initializeTeam;

@SuppressWarnings("unused")
public class MainVerticle extends AbstractVerticle
{
    private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);
    private static final SR_API_Configuration SR_API_CONFIGURATION = SR_API_Configuration.getInstance();

    private static RegularSeason season2018;
    private static ParticipantScores scores;
    private static List<SeasonPrediction> seasonPredictions;
    private static List<Opponent> opponents;
    private static TeamColorsManager colors;
    private static TemplateEngine engine;

    public static Boxscore getBoxscore(Game game)
    {
        Boxscore boxscore = new Boxscore();

        logger.info("Loading boxscore from API...");
        JsonResponse response = null;
        try
        {
            System.out.println(SR_API_CONFIGURATION.getBoxScore(season2018, game));
            response = JsonRestMessenger.get(SR_API_CONFIGURATION.getBoxScore(season2018, game));
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        if (response != null)
        {
            boxscore = new Gson().fromJson(response.getResponseJSON(), Boxscore.class);
        }

        return boxscore;
    }

    @Override
    public void start(Future<Void> future)
    {
        vertx.executeBlocking(blockingFuture -> {
            initializeModels(false);
            colors = TeamColorsManager.getInstance();
            blockingFuture.complete(colors);
        }, result -> System.out.println("Models initialized and colors loaded..."));

        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        vertx.executeBlocking(blockingFuture -> {
            initializeRoutes(router);
            blockingFuture.complete(router);
        }, result -> System.out.println("Deployment finished"));

        engine = FreeMarkerTemplateEngine.create();
        router.route().handler(StaticHandler.create());

        server.requestHandler(router::accept)
            .listen(8080, result -> {
                if (result.succeeded())
                {
                    future.complete();
                } else
                {
                    future.fail(result.cause());
                }
            });
    }

    private void gamePage(RoutingContext context, Game game)
    {
        initializeModels(false);

        int ourScore = game.getOurScore();
        int theirScore = game.getTheirScore();

        Boxscore gameScore = new Boxscore();
        if (game.getDate().isEqual(LocalDate.now()))
        {
            gameScore = getBoxscore(game);
            logger.info(gameScore.toString());
            if (gameScore.getAwayTeam().getId().equalsIgnoreCase("mich"))
            {
                ourScore = gameScore.getAwayTeam().getPoints();
                theirScore = gameScore.getHomeTeam().getPoints();
            } else
            {
                ourScore = gameScore.getHomeTeam().getPoints();
                theirScore = gameScore.getAwayTeam().getPoints();
            }
            logger.info("boxscore points: us={}, them={}", ourScore, theirScore);
        }

        context.put("boxscore", gameScore);
        context.put("ourScore", ourScore);
        context.put("theirScore", theirScore);
        context.put("game", game);
        context.put("predictions", seasonPredictions);
        context.put("scores", scores);
        context.put("season", season2018);
        context.put("opponents", opponents);
        context.put("colors", colors);
        engine.render(context, "templates/", "game_body.ftl", response -> {
            if (response.succeeded())
            {
                context.response().end(response.result());
            } else
            {
                context.fail(response.cause());
            }
        });
    }

    private void index(RoutingContext context, boolean rebuildSeason)
    {
        initializeModels(rebuildSeason);
        context.put("scores", scores);
        context.put("season", season2018);
        context.put("predictions", seasonPredictions);
        context.put("opponents", opponents);
        context.put("colors", colors);
        engine.render(context, "templates/", "index_body.ftl", response -> {
            if (response.succeeded())
            {
                context.response().end(response.result());
            } else
            {
                context.fail(response.cause());
            }
        });
    }

    private void initializeModels(boolean rebuildSeason)
    {
        if (season2018 == null || rebuildSeason)
        {
            System.out.println("Building season model...");
            season2018 = initializeSeasonModel();
        }

        if (seasonPredictions == null)
        {
            System.out.println("Building prediction model...");
            seasonPredictions = initializePredictionModel(season2018);
        }

        if (scores == null || rebuildSeason)
        {
            System.out.println("Calculating participant scores...");
            scores = new ParticipantScores(seasonPredictions, season2018);
        }

        if (opponents == null)
        {
            System.out.println("Initializing opponents list...");
            opponents = new ArrayList<>();
            season2018.getMichiganGamesThisSeason().forEach(game -> opponents.add(new Opponent(game.them(), initializeTeam(game.them()))));
        }
    }

    private void initializeRoutes(Router router)
    {
        router.get("/").blockingHandler(context -> index(context, false));

        for (SeasonPrediction prediction : seasonPredictions)
        {
            router.get("/" + prediction.getParticipant() + "/").blockingHandler(context -> playerPage(context, prediction));
        }

        for (Game game : season2018.getMichiganGamesThisSeason())
        {
            router.get("/" + game.them() + "/").blockingHandler(context -> gamePage(context, game));
        }

        router.get("/rebuildSeason/").blockingHandler(context -> index(context, true));
        router.get("/weekly-scores/").blockingHandler(this::weeklyScores);
    }

    private void playerPage(RoutingContext context, SeasonPrediction prediction)
    {
        initializeModels(false);

        context.put("prediction", prediction);
        context.put("predictions", seasonPredictions);
        context.put("scores", scores);
        context.put("season", season2018);
        context.put("opponents", opponents);
        context.put("colors", colors);
        engine.render(context, "templates/", "player_body.ftl", response -> {
            if (response.succeeded())
            {
                context.response().end(response.result());
            } else
            {
                context.fail(response.cause());
            }
        });
    }

    private void weeklyScores(RoutingContext context)
    {
        //        Map<Game, >
        //        context.response()
        //            .putHeader("content-type", "application/json; charset=utf-8")
        //            .end(Json.encodePrettily(/*Some map content*/));
    }
}
