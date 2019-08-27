package org.clarke;

import org.clarke.predictionModel.ParticipantScores;
import org.clarke.predictionModel.SeasonPrediction;
import org.clarke.rosterModel.Opponent;
import org.clarke.rosterModel.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		Map<String, Team> opponents = ModelManager.getOpponents().stream().collect(Collectors.toMap(Opponent::getName, Opponent::getTeam));

		List<SeasonPrediction> seasonPredictions = ModelManager.getSeasonPredictions();
		seasonPredictions.forEach(System.out::println);

		ParticipantScores scores = ModelManager.getScores();
		for (String participant : ModelManager.getParticipants()) {
			System.out.println(participant + " current score: " + scores.getCurrentParticipantScore(participant));
			logger.info(participant + " current score: " + scores.getCurrentParticipantScore(participant));
		}

		logger.info("Printing excel sheet...");
		try {
			ExcelSeasonOutput.printExcelSheet(ModelManager.getSeason(), seasonPredictions, opponents);
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("Finished writing excel sheet...");
		System.out.println("Finished writing excel sheet...");
		System.exit(0);

		//        logger.info("Grabbing google sheet...");
		//        try
		//        {
		//            new GoogleSheetsConfiguration().retrieveSheet();
		//        } catch (IOException | GeneralSecurityException e)
		//        {
		//            e.printStackTrace();
		//        }
	}
}
