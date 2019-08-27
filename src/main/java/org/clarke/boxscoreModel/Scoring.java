package org.clarke.boxscoreModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Scoring {
	@SerializedName("quarter")
	@Expose
	private int quarter;
	@SerializedName("points")
	@Expose
	private int points;

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getQuarter() {
		return quarter;
	}

	public void setQuarter(int quarter) {
		this.quarter = quarter;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("quarter", quarter).append("points", points).toString();
	}
}
