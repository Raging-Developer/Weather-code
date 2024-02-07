package app.personal_weather.data;

import org.json.JSONObject;

/**
 * This is today's weather
 * Created by Christopher D. Harte on 12/06/2021.
 */
public class Current
{
    Condition condition;
    private long last_updated_epoch;
    private String last_updated;
    private String is_day;
    private String temp_c;
    private String wind_mph;
    private String wind_dir;
    private String pressure_mb;
    private String precip_in;
    private String humidty;
    private String feelslike_c;
    private String vis_miles;
    private String UV;
    private String gust_mph;

    public long getLast_updated_epoch(){
        return last_updated_epoch;
    }
    public String getLast_updated() {
        return last_updated;
    }
    public String getIs_day()
    {
        return is_day;
    }
    public String getTemp_c()
    {
        return temp_c;
    }
    public String getWind_mph() {
        return wind_mph;
    }
    public String getWind_dir() {
        return wind_dir;
    }
    public String getPressure_mb() {
        return pressure_mb;
    }
    public String getPrecip_in() {
        return precip_in;
    }
    public String getHumidty() {
        return humidty;
    }
    public String getFeelslike_c() {
        return feelslike_c;
    }
    public String getVis_miles() {
        return vis_miles;
    }
    public String getUV() {
        return UV;
    }
    public String getGust_mph() {
        return gust_mph;
    }

    public Condition getCondition() {
        return condition;
    }

    public void populate (JSONObject data) throws Exception
    {
        condition = new Condition();
        condition.populate(data.getJSONObject("condition"));

        last_updated_epoch = data.getLong("last_updated_epoch");
        last_updated = data.optString("last_updated");
        is_day = data.optString("is_day");
        temp_c = data.optString("temp_c");
        wind_mph = data.optString("wind_mph");
        wind_dir = data.optString("wind_dir");
        pressure_mb = data.optString("pressure_mb");
        precip_in = data.optString("precip_in");
        humidty = data.optString("humidity");
        feelslike_c = data.optString("feelslike_c");
        vis_miles = data.optString("vis_miles");
        UV = data.optString("uv");
        gust_mph = data.optString("gust_mph");
    }
}


