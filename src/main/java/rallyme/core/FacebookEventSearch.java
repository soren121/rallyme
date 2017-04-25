/**
    RallyMe
    CSCI 4300, CRN 41126, Group 5

    rallyme.core.FacebookEventSearch

    This class is largely ported from the Node.js library
    "facebook-events-by-location-core", licensed under the MIT/Expat License.
    <https://github.com/tobilg/facebook-events-by-location-core>
 */

package rallyme.core;

import rallyme.exception.FacebookEventException;
import rallyme.model.Rally;
import rallyme.model.RallyType;

import java.io.IOException;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.http.client.fluent.Request;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.client.BasicResponseHandler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;

public class FacebookEventSearch {

    private static final String FACEBOOK_APP_TOKEN = "603035693228441|KEVQ5aA2wr98kfQtjPysf1tAkao";
    private static final String FACEBOOK_API_VER = "v2.8";

    private float lat;
    private float lng;
    private int distance;

    /**
        Constructor for FacebookEventSearch. Instantiate the class to 
        perform a search by geocoordinates.

        @param lat The center latitude of the search area.
        @param lng The center longitude of the search area.
        @param distance The radius of the search area.
     */
    public FacebookEventSearch(float lat, float lng, int distance) {
        this.lat = lat;
        this.lng = lng;
        this.distance = distance;
    }

    /**
        Recursively retrieves venue ID's from the Facebook Graph API.
        This is a helper method for getPlaces() and should not be called independently.

        Recursion is necessary because the Graph API uses paging, which means
        only some results are returned in each request. To retrieve further 
        requests, we have to make an additional request with a provided "after" ID.

        @param results The results list to save venue ID's to.
        @param url The URL to request from.
        @return An ArrayList of venue ID's.
     */
    private ArrayList<Long> getPlacesRecursive(ArrayList<Long> results, String url) {
        String jsonString;
        try {
            // Initialize Fluent HttpClient and buffer response
            jsonString = Request.Get(url)
                .setHeader("Accept", "application/json")
                .execute()
                .returnContent().asString();
        } catch(IOException ex) {
            System.out.println("Facebook HTTP request failed: " + ex.getMessage());
            return results;
        }

        // Parse response as JSON
        JsonParser jsonParser = new JsonParser();
        JsonObject json = jsonParser.parse(jsonString).getAsJsonObject();
        JsonArray data = json.get("data").getAsJsonArray();
        // Iterate through results array
        for(JsonElement dataEle : data) {
            // Parse each ID as as long type
            long id = Long.parseLong(dataEle.getAsJsonObject().get("id").getAsString());
            results.add(id);
        }

        // Check if the "paging" object is available in the root JSON
        JsonElement paging = json.get("paging");
        // If "paging" is available and a "next" key exists, recurse with next request
        if(paging != null && paging.getAsJsonObject().get("next") != null) {
            String nextUrl = paging.getAsJsonObject().get("next").getAsString();
            // Ensure that we're not making the same request repeatedly
            if(!nextUrl.equals(url)) {
                return getPlacesRecursive(results, nextUrl);
            }
        }

        // Base-case: if no more pages exist, return results
        return results;
    }

    /**
        Retrieves a list of venue ID's from the Facebook Graph API.
        It builds a Graph API request URL and utilizes getPlacesRecursive()
        to retrieve the results.
        
        @return A list of venue ID's.
     */
    private ArrayList<Long> getPlaces() throws FacebookEventException {
        String url = "";
        try {
            url = "https://graph.facebook.com/" + FACEBOOK_API_VER + "/search" +
                "?type=place" +
                "&center=" + String.valueOf(this.lat) + "," + String.valueOf(this.lng) +
                "&distance=" + String.valueOf(this.distance) + 
                "&limit=100" +
                "&fields=id" +
                // Encode access token, as it can have URL-unsafe characters
                "&access_token=" + URLEncoder.encode(FACEBOOK_APP_TOKEN, "UTF-8");
        } catch(UnsupportedEncodingException ex) {
            throw new FacebookEventException("Failed to encode app access token.");
        }

        ArrayList<Long> results = new ArrayList<Long>();
        return getPlacesRecursive(results, url);
    }

    /**
        Builds Graph API event requests, given a nested list of venue ID's.

        @param chunks A nested list of venue ID's.
        @return An array of HttpGet request objects to process.
     */
    private HttpGet[] buildEventRequests(List<List<Long>> chunks) {
        ArrayList<String> eventsUrls = new ArrayList<String>();

        String[] eventFields = new String[] {
            "id", "type", "name", "picture.type(large)", "description", 
            "start_time", "end_time", "category", "attending_count", 
            "declined_count", "maybe_count", "noreply_count"
        };

        String[] fields = new String[] {
            "id", "name", "about", "emails", "picture.type(large)", 
            "category", "location", "events.fields(" + String.join(",", eventFields) + ")"
        };
        
        for(List<Long> chunk : chunks) {
            try {
                String eventsUrl = "https://graph.facebook.com/" + FACEBOOK_API_VER + "/" +
                    // Use Lists.transform to convert each long type to a String
                    "?ids=" + String.join(",", Lists.transform(chunk, Functions.toStringFunction())) +
                    // Encode the access token, as it can have URL-unsafe characters
                    "&access_token=" + URLEncoder.encode(FACEBOOK_APP_TOKEN, "UTF-8") +
                    "&fields=" + String.join(",", fields) +
                    // Use current Unix timestamp
                    ".since(" + String.valueOf(Instant.now().getEpochSecond()) + ")";

                // Add URL to list of requests
                eventsUrls.add(eventsUrl);
            } catch(UnsupportedEncodingException ex) {
                continue;
            }
        }

        // Create an HttpGet request for each URL
        HttpGet[] requests = new HttpGet[eventsUrls.size()];
        for(int i = 0; i < eventsUrls.size(); i++) {
            requests[i] = new HttpGet(eventsUrls.get(i));
        }

        return requests;
    }

    /**
        Processes an JSON event object from the Graph API and returns it 
        as a Rally object.

        @param location The "location" object, from the venue object.
        @param event The "event" object, from the venue object.
        @return A Rally object representing the event.
     */
    private Rally processEvent(JsonObject location, JsonObject event) {
        String name = event.get("name").getAsString();
        String description = event.get("description").getAsString();
        // Retrieve start_time to convert to Timestamp later
        String startTimeStr = event.get("start_time").getAsString();
        // Compile address from street, city, and state keys
        String address = location.get("street").getAsString() + ", " +
            location.get("city").getAsString() + ", " +
            location.get("state").getAsString();
        float latitude = location.get("latitude").getAsFloat();
        float longitude = location.get("longitude").getAsFloat();

        // Facebook returns times in ISO8601 format
        // Parse using SimpleDateFormat and then convert the Date object to a Timestamp
        Timestamp startTime;
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(startTimeStr);
            startTime = new Timestamp(date.getTime());
        } catch(ParseException ex) {
            // If time could not be parsed, set start_time to now as a fallback
            startTime = new Timestamp((new Date()).getTime());
        }
        
        // Instantiate Rally object
        Rally newRally = new Rally(name, RallyType.LOCAL, startTime, address, latitude, longitude);
        newRally.setDescription(description);
        return newRally;
    }

    /**
        Searches the Facebook Graph API for rallies in the location specified 
        in the constructor.

        @return An array of Rally objects, each representing an event found 
                via Facebook.
     */
    public Rally[] search() throws FacebookEventException {
        // Create list for rallies
        ArrayList<Rally> rallies = new ArrayList<Rally>();

        // Retrieve venue ID's.
        ArrayList<Long> places = getPlaces();
        // Chunk into lists of 50 ID's each, because the Facebook events API can 
        // only handle 50 ID's at a time.
        List<List<Long>> placesChunked = Lists.partition(places, 50);

        // Set default AsyncHTTPClient configuration
        RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(3000)
            .setConnectTimeout(3000).build();
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom()
            .setDefaultRequestConfig(requestConfig)
            .build();
        
        try {
            // Initialize AsyncHTTPClient
            httpclient.start();
            HttpGet[] requests = buildEventRequests(placesChunked);
            final CountDownLatch latch = new CountDownLatch(requests.length);

            // Process each request in parallel asynchronously
            for(final HttpGet request: requests) {
                httpclient.execute(request, new FutureCallback<HttpResponse>() {

                    @Override
                    public void completed(final HttpResponse response) {
                        latch.countDown();

                        // Buffer response
                        String jsonString = "{}";
                        try {
                            jsonString = new BasicResponseHandler().handleResponse(response);
                        } catch(IOException ex) {
                            return;
                        }

                        // Define regex to filter events
                        Pattern catRegex = Pattern.compile("(FUNDRAISER|MEETUP|VOLUNTEERING)");
                        // Parse response as JSON
                        JsonParser jsonParser = new JsonParser();
                        JsonObject json = jsonParser.parse(jsonString).getAsJsonObject();
                        // Iterate over each object inside the root JSON object
                        // Each object represents a venue
                        for(Map.Entry<String,JsonElement> venueEntry : json.entrySet()) {
                            JsonObject venue = venueEntry.getValue().getAsJsonObject();
                            
                            // Check if the venue has any events
                            if(venue.get("events") != null) {
                                JsonObject eventsObj = venue.get("events").getAsJsonObject();
                                JsonElement eventData = eventsObj.get("data");
                                // Verify that the venue has active events
                                if(eventData != null && eventData.getAsJsonArray().size() > 0) {
                                    JsonArray events = eventData.getAsJsonArray();
                                    // For each event, filter by category and process the relevant ones
                                    for(JsonElement eventEle : events) {
                                        JsonObject event = eventEle.getAsJsonObject();
                                        JsonObject location = venue.get("location").getAsJsonObject();
                                        JsonElement eventCategory = event.get("category");
                                        if(eventCategory != null && catRegex.matcher(eventCategory.getAsString()).find()) {
                                            rallies.add(processEvent(location, event));
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void failed(final Exception ex) {
                        latch.countDown();
                        ex.printStackTrace();
                    }

                    @Override
                    public void cancelled() {
                        latch.countDown();
                        System.out.println(request.getRequestLine() + " cancelled");
                    }

                });
            }

            // Wait for all requests to complete
            latch.await();
        } catch(InterruptedException ex) {
            throw new FacebookEventException("Parallel event requests interrupted: " + ex.getMessage());
        } finally {
            try {
                httpclient.close();
            } catch(IOException ex) {
                throw new FacebookEventException(ex.getMessage());
            }
        }

        // Return rallies as a static array
        return (Rally[]) rallies.toArray(new Rally[rallies.size()]);
    }

}
