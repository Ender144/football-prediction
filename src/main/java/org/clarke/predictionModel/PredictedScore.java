package org.clarke.predictionModel;

public class PredictedScore {
	private int ourScore, theirScore;

	public PredictedScore(int ourScore, int theirScore) {
		this.ourScore = ourScore;
		this.theirScore = theirScore;
	}

	public int getOurScore() {
		return ourScore;
	}

	public Outcome getPredictedOutcome() {
		if (ourScore == 0 && theirScore == 0) {
			return Outcome.UNPLAYED;
		}

		return ourScore > theirScore ? Outcome.WIN : Outcome.LOSE;
	}

	public int getTheirScore() {
		return theirScore;
	}
}
