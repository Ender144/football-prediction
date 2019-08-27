package org.clarke.json;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.ContentResponseHandler;

import java.io.IOException;

class ResponseHandler implements org.apache.http.client.ResponseHandler<Response> {
	private ContentResponseHandler contentResponseHandler = new ContentResponseHandler();

	@Override
	public Response handleResponse(HttpResponse response) throws IOException {
		return new Response(response.getStatusLine().getStatusCode(), contentResponseHandler.handleResponse(response).asString());
	}
}
