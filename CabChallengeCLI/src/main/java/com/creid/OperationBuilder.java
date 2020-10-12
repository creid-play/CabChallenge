package com.creid;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import javax.ws.rs.core.UriBuilder;

import java.io.IOException;

public class OperationBuilder {
    private static final String BASE_URL = "http://Cabchallenge-env.eba-d2xicuvi.us-east-2.elasticbeanstalk.com/";


    public OperationBuilder() {

    }

    public HttpResponse sendMedalliontripsRequest(String date, String medallions, boolean ignoreCache) throws IOException {
        String medallionsString = "[" + medallions + "]";
        String ignoreCacheString = ignoreCache ? "true" : "false";

        String uri = UriBuilder.fromUri(BASE_URL + "medalliontrips")
                .queryParam("medallions", medallionsString)
                .queryParam("pickupDate", date)
                .queryParam("ignoreCache", ignoreCacheString)
                .build()
                .toString();

        HttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);

        return response;
    }

    public String sendClearCacheRequest() {
        try {
            HttpClient client = HttpClients.createDefault();
            HttpGet request = new HttpGet(BASE_URL + "clearCache");
            HttpResponse response = client.execute(request);
        } catch (Exception e) {
            return "Failed to clear cache: " + e.toString();
        }
        return "Successfully cleared cache";
    }
}
