package org.clarke.data;

public class JsonResponse
{
    private int httpStatus;
    private String responseJSON;

    JsonResponse(int httpStatus, String responseJSON)
    {
        this.httpStatus = httpStatus;
        this.responseJSON = responseJSON;
    }

    public int getHttpStatus()
    {
        return httpStatus;
    }

    public String getResponseJSON()
    {
        return responseJSON;
    }
}
