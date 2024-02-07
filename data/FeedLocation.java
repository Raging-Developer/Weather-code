package app.personal_weather.data;

import org.json.JSONObject;

/*
* This is in the feed so leave it in incase I ever need it.
* */

public class FeedLocation {

    private String name;
    private String region;
    private String country;
    private String lat;
    private String lon;
    private String localtime;


    public String getName() {
        return name;
    }
    public String getRegion() {
        return region;
    }
    public String getCountry() {
        return country;
    }
    public String getLat() {
        return lat;
    }
    public String getLon() {
        return lon;
    }
    public String getLocaltime() {
        return localtime;
    }

    public void populate (JSONObject data) throws Exception {
        name = data.optString("name");
        region = data.optString("region");
        country = data.optString("country");
        lat = data.optString("lat");
        lon = data.optString("lon");
        localtime = data.optString("localtime");
    }
}
