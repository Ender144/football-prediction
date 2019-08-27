package org.clarke.json;

public class Response {
	private int httpStatus;
	private String responseJSON;

	Response(int httpStatus, String responseJSON) {
		this.httpStatus = httpStatus;
		this.responseJSON = responseJSON;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	public String getResponseJSON() {
		return responseJSON;
	}
}
