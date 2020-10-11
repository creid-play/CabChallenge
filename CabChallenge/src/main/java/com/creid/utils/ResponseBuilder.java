package com.creid.utils;

import org.json.JSONObject;

import javax.ws.rs.core.Response;
import java.util.*;

public class ResponseBuilder {
    private static List<String> errors;
    private static JSONObject responseMessage;
    private static String isCached;
    private static int httpCode;

    public ResponseBuilder() {
        this.errors = new ArrayList();
        this.responseMessage = new JSONObject();
        this.isCached = "";
        this.httpCode = 0;
    }

    public ResponseBuilder addError(String error) {
        this.errors.add(error);
        return this;
    }

    public ResponseBuilder setResponseMessage(JSONObject responseMessage) {
        this.responseMessage = responseMessage;
        return this;
    }

    public ResponseBuilder setIsCached(boolean isCached) {
        this.isCached = isCached ? "true" : "false";
        return this;
    }

    public ResponseBuilder setHttpCode(int httpCode) {
        this.httpCode = httpCode;
        return this;
    }

    public Response build() {
        JSONObject responseJson = new JSONObject();
        responseJson.put("message", this.responseMessage);

        if (this.errors.size() > 0) {
            responseJson.put("errors", this.errors);
        }

        if (this.isCached != "") {
            responseJson.put("fromCache", this.isCached == "true");
        }

        String responseString = responseJson.toString(3);

        return Response.status(this.httpCode).entity(responseString).build();
    }
}
