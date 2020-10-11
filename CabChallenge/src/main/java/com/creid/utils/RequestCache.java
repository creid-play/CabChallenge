package com.creid.utils;

import org.json.JSONObject;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestCache {
    private static final int MAX_CACHED_REQUESTS = 4;

    private static RequestCache requestCache = new RequestCache();

    //Collections will make the cache map threadsafe, but will cause lock issues at high volume
    private static Map requests = Collections.synchronizedMap(new LinkedHashMap<String, String>() {
                protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                    return size() > MAX_CACHED_REQUESTS;
                }
            });

    public RequestCache() {}

    public static RequestCache getInstance() {
        return requestCache;
    }

    public static JSONObject checkRequest(String request) {
        if (requests.containsKey(request)) {
            return (JSONObject) requests.get(request);
        }
        return null;
    }

    public static void cacheRequest(String request, JSONObject response) {
        requests.put(request, response);
    }

    public static String getCacheContent() {
        JSONObject jsonObject = new JSONObject();
        for (Object keyObj : requests.keySet()) {
            String key = keyObj.toString();
            jsonObject.put(key, requests.get(key));
        }
        return jsonObject.toString();
    }

    public static void clearCache() {
        requests.clear();
    }
}
