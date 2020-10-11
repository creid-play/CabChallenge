package com.creid.app;

import com.creid.utils.RequestCache;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/cache")
public class Cache {

    @Path("getCacheContent")
    @GET
    @Produces("application/json")
    public Response getCacheContent() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        RequestCache cache = RequestCache.getInstance();
        String result = cache.getCacheContent();

        return Response.status(200).entity(result).build();
    }

    @Path("clearCache")
    @DELETE
    @Produces("application/json")
    public Response clearCache() throws JSONException {
        RequestCache cache = RequestCache.getInstance();
        cache.clearCache();
        String result = "Successfully cleared cache";

        return Response.status(200).build();
    }
}
