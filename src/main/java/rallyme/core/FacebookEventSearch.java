package rallyme.core;

import java.io.IOException;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.ArrayList;
import org.apache.http.client.fluent.Request;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;

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

        ArrayList<Long> places = getPlaces(url);
    }

}
