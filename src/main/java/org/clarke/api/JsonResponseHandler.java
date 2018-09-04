package org.clarke.api;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.ContentResponseHandler;

import java.io.IOException;

class JsonResponseHandler implements ResponseHandler<JsonResponse>
{
    private ContentResponseHandler contentResponseHandler = new ContentResponseHandler();

    @Override
    public JsonResponse handleResponse(HttpResponse response) throws IOException
    {
        return new JsonResponse(response.getStatusLine().getStatusCode(), contentResponseHandler.handleResponse(response).asString());
    }
}
