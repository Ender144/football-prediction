package org.clarke.api;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.Credentials;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

public class JsonRestMessenger
{
    private static final Logger logger = LoggerFactory.getLogger(JsonRestMessenger.class);
    
    private static ResponseHandler<JsonResponse> responseHandler = new JsonResponseHandler();
    private static Executor authExecutor = Executor.newInstance();

    public static void addAuthContext(HttpHost host, Credentials credentials)
    {
        authExecutor.auth(host, credentials);
    }

    public static JsonResponse get(String url, List<NameValuePair> formParameters) throws IOException
    {
        url += "?" + formParameters.stream().map(nvp -> nvp.getName() + "=" + nvp.getValue()).collect(Collectors.joining("&"));
        return authExecutor.execute(
            Request.Get(url)
        ).handleResponse(responseHandler);
    }

    public static JsonResponse get(String url) throws IOException
    {
        logger.info("Fetching URL with GET: " + url);
        return authExecutor.execute(
            Request.Get(url)
        ).handleResponse(responseHandler);
    }

    public static JsonResponse post(String url, String bodyString, ContentType contentType) throws IOException
    {
        return authExecutor.execute(
            Request.Post(url).bodyString(bodyString, contentType)
        ).handleResponse(responseHandler);
    }

    public static JsonResponse post(String address, String path, String bodyString, ContentType contentType) throws IOException
    {
        JsonResponse response = null;

        try
        {
            URI uri = new URI("http", null, address, 80, path, null, null);
            logger.info("URI: " + uri.toURL().toExternalForm());
            logger.info("Body: " + bodyString);
            response = authExecutor.execute(
                Request.Post(uri).bodyString(bodyString, contentType)
            ).handleResponse(responseHandler);
        } catch (URISyntaxException e)
        {
            e.printStackTrace();
        }

        return response;
    }

    public static JsonResponse post(String url, List<NameValuePair> formParameters) throws IOException
    {
        return authExecutor.execute(
            Request.Post(url).bodyForm(formParameters)
        ).handleResponse(responseHandler);
    }
}
