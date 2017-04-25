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

    public FacebookEventSearch(float lat, float lng, int distance) {
        this.lat = lat;
        this.lng = lng;
        this.distance = distance;
    }

    private ArrayList<Long> getPlacesRecursive(ArrayList<Long> results, String url) {
        String jsonString;
        try {
            jsonString = Request.Get(url)
                .setHeader("Accept", "application/json")
                .execute()
                .returnContent().asString();
        } catch(IOException ex) {
            System.out.println("Facebook HTTP request failed: " + ex.getMessage());
            return results;
        }

        JsonParser jsonParser = new JsonParser();
        JsonObject json = jsonParser.parse(jsonString).getAsJsonObject();
        JsonArray data = json.get("data").getAsJsonArray();
        for(JsonElement dataEle : data) {
            long id = Long.parseLong(dataEle.getAsJsonObject().get("id").getAsString());
            results.add(id);
        }

        JsonElement paging = json.get("paging");
        if(paging != null && paging.getAsJsonObject().get("next") != null) {
            String nextUrl = paging.getAsJsonObject().get("next").getAsString();
            if(!nextUrl.equals(url)) {
                return getPlacesRecursive(results, nextUrl);
            }
        }

        return results;
    }

    private ArrayList<Long> getPlaces() throws FacebookEventException {
        String url = "";
        try {
            url = "https://graph.facebook.com/" + FACEBOOK_API_VER + "/search" +
                "?type=place" +
                "&center=" + String.valueOf(this.lat) + "," + String.valueOf(this.lng) +
                "&distance=" + String.valueOf(this.distance) + 
                "&limit=100" +
                "&fields=id" +
                "&access_token=" + URLEncoder.encode(FACEBOOK_APP_TOKEN, "UTF-8");
        } catch(UnsupportedEncodingException ex) {
            throw new FacebookEventException("Failed to encode app access token.");
        }

        ArrayList<Long> results = new ArrayList<Long>();
        return getPlacesRecursive(results, url);
    }

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
                    "?ids=" + String.join(",", Lists.transform(chunk, Functions.toStringFunction())) +
                    "&access_token=" + URLEncoder.encode(FACEBOOK_APP_TOKEN, "UTF-8") +
                    "&fields=" + String.join(",", fields) +
                    ".since(" + String.valueOf(Instant.now().getEpochSecond()) + ")";
                eventsUrls.add(eventsUrl);
            } catch(UnsupportedEncodingException ex) {
                continue;
            }
        }

        HttpGet[] requests = new HttpGet[eventsUrls.size()];
        for(int i = 0; i < eventsUrls.size(); i++) {
            requests[i] = new HttpGet(eventsUrls.get(i));
        }

        return requests;
    }

    private Rally processEvent(JsonObject location, JsonObject event) {
        String name = event.get("name").getAsString();
        String description = event.get("description").getAsString();
        String startTimeStr = event.get("start_time").getAsString();
        String address = location.get("street").getAsString() + ", " +
            location.get("city").getAsString() + ", " +
            location.get("state").getAsString();
        float latitude = location.get("latitude").getAsFloat();
        float longitude = location.get("longitude").getAsFloat();

        Timestamp startTime;
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(startTimeStr);
            startTime = new Timestamp(date.getTime());
        } catch(ParseException ex) {
            startTime = new Timestamp((new Date()).getTime());
        }
        

        Rally newRally = new Rally(name, RallyType.LOCAL, startTime, address, latitude, longitude);
        newRally.setDescription(description);
        return newRally;
    }

    public Rally[] search() throws FacebookEventException {
        ArrayList<Rally> rallies = new ArrayList<Rally>();

        ArrayList<Long> places = getPlaces();
        List<List<Long>> placesChunked = Lists.partition(places, 50);

        RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(3000)
            .setConnectTimeout(3000).build();
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom()
            .setDefaultRequestConfig(requestConfig)
            .build();
        try {
            httpclient.start();
            HttpGet[] requests = buildEventRequests(placesChunked);
            final CountDownLatch latch = new CountDownLatch(requests.length);

            for(final HttpGet request: requests) {
                httpclient.execute(request, new FutureCallback<HttpResponse>() {

                    @Override
                    public void completed(final HttpResponse response) {
                        latch.countDown();

                        String jsonString = "{}";
                        try {
                            jsonString = new BasicResponseHandler().handleResponse(response);
                        } catch(IOException ex) {
                            return;
                        }

                        Pattern catRegex = Pattern.compile("(FUNDRAISER|MEETUP|VOLUNTEERING)");
                        JsonParser jsonParser = new JsonParser();
                        JsonObject json = jsonParser.parse(jsonString).getAsJsonObject();
                        for(Map.Entry<String,JsonElement> venueEntry : json.entrySet()) {
                            JsonObject venue = venueEntry.getValue().getAsJsonObject();
                            
                            if(venue.get("events") != null) {
                                JsonObject eventsObj = venue.get("events").getAsJsonObject();
                                JsonElement eventData = eventsObj.get("data");
                                if(eventData != null && eventData.getAsJsonArray().size() > 0) {
                                    JsonArray events = eventData.getAsJsonArray();
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

        return (Rally[]) rallies.toArray(new Rally[rallies.size()]);
    }

}
