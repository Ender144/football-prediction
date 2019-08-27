package org.clarke;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.common.template.TemplateEngine;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine;
import org.clarke.boxscoreModel.Boxscore;
import org.clarke.parsers.TeamColorsManager;
import org.clarke.predictionModel.ParticipantScores;
import org.clarke.predictionModel.SeasonPrediction;
import org.clarke.regularSeasonModel.Game;
import org.clarke.regularSeasonModel.RegularSeason;
import org.clarke.rosterModel.Opponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@SuppressWarnings("unused")
public class MainVerticle extends AbstractVerticle {
	private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

	private static RegularSeason season;
	private static ParticipantScores scores;
	private static List<SeasonPrediction> seasonPredictions;
	private static List<Opponent> opponents;

	private static TeamColorsManager colors;
	private static TemplateEngine engine;

	@Override
	public void start(Future<Void> future) {
		vertx.executeBlocking(blockingFuture -> {
			initializeModels(false, false);
			colors = TeamColorsManager.getInstance();
			blockingFuture.complete(colors);
		}, result -> System.out.println("Models initialized and colors loaded..."));

		HttpServer server = vertx.createHttpServer();

		Router router = Router.router(vertx);

		vertx.executeBlocking(blockingFuture -> {
			initializeRoutes(router);
			blockingFuture.complete(router);
		}, result -> System.out.println("Deployment finished"));

		engine = FreeMarkerTemplateEngine.create(vertx);
		router.route().handler(StaticHandler.create());

		server.requestHandler(router::accept)
			.listen(8080, result -> {
				if (result.succeeded()) {
					future.complete();
				} else {
					future.fail(result.cause());
				}
			});
	}

	private void gamePage(RoutingContext context, Game game) {
		initializeModels(false, true);

		int ourScore = game.getOurScore();
		int theirScore = game.getTheirScore();

		Boxscore gameScore = ModelManager.getTodaysBoxscore();
		if (!gameScore.getStatus().equals(ModelManager.UNINITIALIZED_BOXSCORE)) {
			if (gameScore.getAwayTeam().getId().equalsIgnoreCase("mich")) {
				ourScore = gameScore.getAwayTeam().getPoints();
				theirScore = gameScore.getHomeTeam().getPoints();
			} else {
				ourScore = gameScore.getHomeTeam().getPoints();
				theirScore = gameScore.getAwayTeam().getPoints();
			}
		}

		context.put("ourScore", ourScore);
		context.put("theirScore", theirScore);
		context.put("game", game);
		context.put("predictions", seasonPredictions);
		context.put("scores", scores);
		context.put("season", season);
		context.put("opponents", opponents);
		context.put("colors", colors);
		engine.render(context.data(), "templates/game_body.ftl", response -> {
			if (response.succeeded()) {
				context.response().end(response.result());
			} else {
				context.fail(response.cause());
			}
		});
	}

	private void index(RoutingContext context, boolean rebuildSeason) {
		initializeModels(rebuildSeason, false);
		context.put("scores", scores);
		context.put("season", season);
		context.put("predictions", seasonPredictions);
		context.put("opponents", opponents);
		context.put("colors", colors);
		System.out.println(context.data().toString());
		engine.render(context.data(), "templates/index_body.ftl", response -> {
			if (response.succeeded()) {
				context.response().end(response.result());
			} else {
				context.fail(response.cause());
			}
		});
	}

	private void initializeModels(boolean rebuildSeason, boolean checkBoxscore) {
		if (rebuildSeason) {
			ModelManager.rebuildSeason();
		}
		if (checkBoxscore) {
			ModelManager.checkBoxscore();
		}

		season = ModelManager.getSeason();
		seasonPredictions = ModelManager.getSeasonPredictions();
		scores = ModelManager.getScores();
		opponents = ModelManager.getOpponents();
	}

	private void initializeRoutes(Router router) {
		router.get("/").blockingHandler(context -> index(context, false));

		for (SeasonPrediction prediction : seasonPredictions) {
			router.get("/" + prediction.getParticipant() + "/").blockingHandler(context -> playerPage(context, prediction));
		}

		for (Game game : season.getMichiganGamesThisSeason()) {
			router.get("/" + game.them() + "/").blockingHandler(context -> gamePage(context, game));
		}

		router.get("/rebuildSeason/").blockingHandler(context -> index(context, true));
		router.get("/weekly-scores/").blockingHandler(this::weeklyScores);
	}

	private void playerPage(RoutingContext context, SeasonPrediction prediction) {
		initializeModels(false, false);

		context.put("prediction", prediction);
		context.put("predictions", seasonPredictions);
		context.put("scores", scores);
		context.put("season", season);
		context.put("opponents", opponents);
		context.put("colors", colors);
		engine.render(context.data(), "templates/player_body.ftl", response -> {
			if (response.succeeded()) {
				context.response().end(response.result());
			} else {
				context.fail(response.cause());
			}
		});
	}

	private void weeklyScores(RoutingContext context) {
		//        Map<Game, >
		//        context.response()
		//            .putHeader("content-type", "application/json; charset=utf-8")
		//            .end(Json.encodePrettily(/*Some map content*/));
	}
}
