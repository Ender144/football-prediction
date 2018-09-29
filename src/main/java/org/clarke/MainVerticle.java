package org.clarke;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.templ.FreeMarkerTemplateEngine;
import io.vertx.ext.web.templ.TemplateEngine;
import org.clarke.parsers.TeamColorsManager;
import org.clarke.predictionModel.ParticipantScores;
import org.clarke.predictionModel.SeasonPrediction;
import org.clarke.regularSeasonModel.Game;
import org.clarke.regularSeasonModel.RegularSeason;
import org.clarke.rosterModel.Opponent;

import java.util.ArrayList;
import java.util.List;

import static org.clarke.Main.initializePredictionModel;
import static org.clarke.Main.initializeSeasonModel;
import static org.clarke.Main.initializeTeam;

@SuppressWarnings("unused")
public class MainVerticle extends AbstractVerticle
{
    private RegularSeason season2018;
    private ParticipantScores scores;
    private List<SeasonPrediction> seasonPredictions;
    private List<Opponent> opponents;
    private TeamColorsManager colors;
    private TemplateEngine engine;

    @Override
    public void start(Future<Void> future)
    {
        initializeModels(false);

        colors = TeamColorsManager.getInstance();
        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);
        initializeRoutes(router);

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
}
