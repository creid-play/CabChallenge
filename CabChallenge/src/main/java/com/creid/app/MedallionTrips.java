package com.creid.app;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.*;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.creid.utils.ResponseBuilder;
import org.json.JSONException;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.creid.utils.RequestCache;
import org.json.JSONObject;

@Path("/medalliontrips")
public class MedallionTrips {

    @GET
    @Produces("application/json")
    public Response parrotParams(@Context UriInfo ui) throws JSONException {
        //Initialize response
        ResponseBuilder responseBuilder = new ResponseBuilder();
        RequestCache cache = RequestCache.getInstance();
        boolean ignoreCacheBool = false;
        boolean paramsError = false;

        //Validate parameters
        MultivaluedMap<String, String> map = ui.getQueryParameters();
        String medallions = map.getFirst("medallions");
        String pickupDate = map.getFirst("pickupDate");
        String ignoreCache = map.getFirst("ignoreCache");

        if (medallions == null || medallions.equals("")) {
            responseBuilder = responseBuilder.addError("Missing required parameter: medallions");
            paramsError = true;
        }
        if (pickupDate == null || pickupDate.equals("")) {
            responseBuilder = responseBuilder.addError("Missing required parameter: pickupDate");
            paramsError = true;
        } else {
            //Check format of date string
            boolean dateFormatError;
            try {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate dateObj = LocalDate.parse(pickupDate, dateFormatter);
                dateFormatError = dateObj == null;
            } catch (DateTimeParseException e) {
                dateFormatError = true;
            }
            if (dateFormatError) {
                responseBuilder = responseBuilder.addError("Invalid pickupDate. Enter pickupDate in yyyy-MM-dd format");
                paramsError = true;
            }
        }
        if (ignoreCache != null && !ignoreCache.equals("")) {
            String ignoreCacheLC = ignoreCache.toLowerCase();
            if (!ignoreCacheLC.equals("true") && !ignoreCacheLC.equals("false")) {
                responseBuilder = responseBuilder.addError("Invalid value for ignoreCache. Expected true or false");
                paramsError = true;
            } else {
                ignoreCacheBool = ignoreCacheLC.equals("true");
            }
        }

        //Return response with full list of params errors
        if (paramsError) {
            return responseBuilder.setHttpCode(400)
                    .build();
        }

        //Check cache for matching response
        String uriParamsString = ui.getRequestUri().toString();
        if (!ignoreCacheBool) {
            JSONObject cachedResponse = cache.checkRequest(uriParamsString);
            if (cachedResponse != null) {
                return responseBuilder.setResponseMessage(cachedResponse)
                        .setIsCached(true)
                        .setHttpCode(200)
                        .build();
            }
        }

        //Get data from database
        JSONObject data;
        try {
            data = getFromDatabase(medallions, pickupDate);
        } catch (Exception e) {
            //TODO Log exception to server logs
            return responseBuilder.addError("Error getting data from database")
                    .setHttpCode(500)
                    .build();
        }

        //Cache response
        cache.cacheRequest(uriParamsString, data);

        //Return success response
        return responseBuilder.setResponseMessage(data)
                .setIsCached(false)
                .setHttpCode(200)
                .build();

    }

    private JSONObject getFromDatabase(String medallions, String pickupDate) throws ClassNotFoundException, SQLException {
        JSONObject responseJson = new JSONObject();
        List<JSONObject> medallionResults = new ArrayList();

        if (medallions.contains("[")) {
            medallions = medallions.replaceAll("(\\[|\\])", "");
        }
        String[] medallionsArr = medallions.split(",");

        int totalTrips = 0;

        String myDriver = "com.mysql.jdbc.Driver";
        String myUrl = "jdbc:mysql://localhost:3306/ny_cab_data?useTimeZone=true&serverTimezone=UTC&autoReconnect=true&useSSL=false";
        Class.forName(myDriver);
        Connection conn = DriverManager.getConnection(myUrl, "creid", "mysqladmin");

        for (String medallion : medallionsArr) {
            Statement st = conn.createStatement();
            String query = "SELECT COUNT(CASE WHEN (`medallion` = '" + medallion + "' AND DATE(`pickup_datetime`) = '" + pickupDate + "') THEN 1 ELSE null END) as Trips FROM `ny_cab_data`.`cab_trip_data`;";
            ResultSet rs = st.executeQuery(query);
            rs.next();
            int trips = rs.getInt("Trips");
            st.close();

            JSONObject medallionResult = new JSONObject();
            medallionResult.put("medallion", medallion);
            medallionResult.put("trips", trips);
            medallionResults.add(medallionResult);

            totalTrips += trips;
        }

        responseJson.put("medallions", medallionResults);
        responseJson.put("totalTrips", totalTrips);

        return responseJson;
    }
}
