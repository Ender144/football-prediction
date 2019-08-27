package org.clarke.predictionModel;

import org.clarke.regularSeasonModel.Game;

import java.util.Map;

public class SeasonPrediction {
	private String participant;
	private Map<Game, PredictedScore> gamePredictions;
	private boolean printUnplayedGames;

	public SeasonPrediction(String predictorName, Map<Game, PredictedScore> gamePredictions, boolean printUnplayedGames) {
		this.participant = predictorName;
		this.gamePredictions = gamePredictions;
		this.printUnplayedGames = printUnplayedGames;
	}

	public PredictedScore getGamePrediction(Game game) {
		return gamePredictions.get(game);
	}

	public Map<Game, PredictedScore> getGamePredictions() {
		return gamePredictions;
	}

	public String getParticipant() {
		return participant;
	}

	@Override
	public String toString() {
		StringBuilder gamePredictionsString = new StringBuilder();

		for (Map.Entry<Game, PredictedScore> predictedGameScore : gamePredictions.entrySet()) {
			Game game = predictedGameScore.getKey();
			if (game.getActualOutcome() != Outcome.UNPLAYED || printUnplayedGames) {
				PredictedScore predictedScore = predictedGameScore.getValue();

				gamePredictionsString.append("\n\t\t{\t").append(game.getHomeTeam()).append(" VS ").append(game.getAwayTeam()).append("\n")
					.append("\t\t\tPredicted:\t")
					.append(predictedScore.getOurScore()).append("\t(US)\t-\t").append(predictedScore.getTheirScore()).append("\t(THEM) (").append(predictedScore.getPredictedOutcome()).append(")\n")
					.append("\t\t\tActual:\t\t")
					.append(game.getOurScore()).append("\t(US)\t-\t").append(game.getTheirScore()).append("\t(THEM) (").append(game.getActualOutcome()).append(")")
					.append("\n\t\t},");
			}
		}

		return "SeasonPrediction {" +
			"\n\tpredictor = '" + participant + '\'' +
			"\n\tgamePredictions = " + gamePredictionsString +
			"\n}";
	}
}
