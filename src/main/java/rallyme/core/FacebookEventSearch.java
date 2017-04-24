package rallyme.core;

import java.io.IOException;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

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

import rallyme.model.Rally;

public class FacebookEventSearch {

    private static final String FACEBOOK_APP_TOKEN = "603035693228441|KEVQ5aA2wr98kfQtjPysf1tAkao";
    private static final String FACEBOOK_API_VER = "v2.9";

    private float lat;
    private float lng;
    private int distance;

    public FacebookEventSearch(float lat, float lng, int distance) {
        this.lat = lat;
        this.lng = lng;
        this.distance = distance;
    }

    private ArrayList<Long> getPlacesRecursive(ArrayList<Long> results, String url, String after) {
        String tempurl;
        if(after.length() == 0) {
            tempurl = url + "&after=" + after;
        } else {
            tempurl = url;
        }

        String jsonString;
        try {
            jsonString = Request.Get(tempurl)
                .setHeader("Accept", "application/json")
                .execute()
                .returnContent().asString();
        } catch(IOException ex) {
            System.out.println("Facebook HTTP request failed: " + ex.getMessage());
            return results;
        }
        System.out.println(jsonString);

        JsonParser jsonParser = new JsonParser();
        JsonObject json = jsonParser.parse(jsonString).getAsJsonObject();
        JsonArray data = json.get("data").getAsJsonArray();
        for(JsonElement dataEle : data) {
            long id = Long.parseLong(dataEle.getAsJsonObject().get("id").getAsString());
            results.add(id);
        }

        JsonElement paging = json.get("paging");
        if(paging != null && paging.getAsJsonObject().get("cursors") != null) { 
            JsonObject cursors = paging.getAsJsonObject().get("cursors").getAsJsonObject();
            JsonElement newAfter = cursors.get("after");
            if(newAfter != null && newAfter.getAsString().length() > 0) {
                return getPlacesRecursive(results, url, newAfter.getAsString());
            }
        }

        return results;
    }

    private ArrayList<Long> getPlaces(String url) {
        ArrayList<Long> results = new ArrayList<Long>();
        return getPlacesRecursive(results, url, "");
    }

    public void search() {
        String url;
        try {
            url = "https://graph.facebook.com/" + FACEBOOK_API_VER + "/search" +
                "?type=place" +
                "&center=" + String.valueOf(this.lat) + "," + String.valueOf(this.lng) +
                "&distance=" + String.valueOf(this.distance) + 
                "&limit=100" +
                "&fields=id" +
                "&access_token=" + URLEncoder.encode(FACEBOOK_APP_TOKEN, "UTF-8");
        } catch(UnsupportedEncodingException ex) {
            return;
        }

        System.out.println(url);
        ArrayList<Long> places = getPlaces(url);
        System.out.println(places.size());
        List<List<Long>> placesChunked = Lists.partition(places, 50);

        String[] eventFields = new String[] {
            "id", "type", "name", "picture.type(large)", "description", 
            "start_time", "end_time", "category", "attending_count", 
            "declined_count", "maybe_count", "noreply_count"
        };

        String eventFieldsStr = String.join(",", eventFields);
        String[] fields = new String[] {
            "id", "name", "about", "emails", "picture.type(large)", 
            "category", "location", "events.fields(" + eventFieldsStr + ")"
        };

        ArrayList<String> eventsUrls = new ArrayList<String>();
        for(List<Long> chunk : placesChunked) {
            try {
                String eventsUrl = "https://graph.facebook.com/" + FACEBOOK_API_VER + "/" +
                    "?ids=" + String.join(",", Lists.transform(chunk, Functions.toStringFunction())) +
                    "&fields=" + String.join(",", fields) +
                    "&access_token=" + URLEncoder.encode(FACEBOOK_APP_TOKEN, "UTF-8") +
                    ".since(" + String.valueOf(Instant.now().getEpochSecond()) + ")";
                eventsUrls.add(eventsUrl);
                System.out.println(eventsUrl);
            } catch(UnsupportedEncodingException ex) {
                continue;
            }
        }

        /*RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(3000)
            .setConnectTimeout(3000).build();
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom()
            .setDefaultRequestConfig(requestConfig)
            .build();
        try {
            httpclient.start();
            
            HttpGet[] requests = new HttpGet[eventsUrls.size()];
            for(int i = 0; i < eventsUrls.size(); i++) {
                requests[i] = new HttpGet(eventsUrls.get(i));
            }

            final CountDownLatch latch = new CountDownLatch(requests.length);
            for(final HttpGet request: requests) {
                httpclient.execute(request, new FutureCallback<HttpResponse>() {

                    @Override
                    public void completed(final HttpResponse response) {
                        latch.countDown();
                        String jsonString = new BasicResponseHandler().handleResponse(response);

                        JsonParser jsonParser = new JsonParser();
                        JsonObject json = jsonParser.parse(jsonString).getAsJsonObject();
                    }

                    @Override
                    public void failed(final Exception ex) {
                        latch.countDown();
                        System.out.println(request.getRequestLine() + "->" + ex);
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
            System.out.println(ex.getMessage());
            return;
        } finally {
            try {
                httpclient.close();
            } catch(IOException ex) {
                return;
            }
        }*/
    }

}
