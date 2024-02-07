package app.personal_weather.data;

import org.json.JSONObject;

/**
 * Created by Christopher D. Harte on 22/06/2021.
 */
public class Astronomy
{
    private String sunrise;
    private String sunset;
    private String moonrise;
    private String moonset;
    private String moonphase;
    private String moon_illumination;
    private int is_moon_up;
    private int is_sun_up;

    public String getSunrise()
    {
        return sunrise;
    }
    public String getSunset()
    {
        return sunset;
    }
    public String getMoonrise()
    {
        return moonrise;
    }
    public String getMoonset()
    {
        return moonset;
    }
    public String getMoonphase()
    {
        return moonphase;
    }
    public String getMoon_illumination()
    {
        return moon_illumination;
    }
    public int getIs_moon_up() {
        return is_moon_up;
    }
    public int getIs_sun_up() {
        return is_sun_up;
    }

    public void populate (JSONObject data)
    {
        sunrise = data.optString("sunrise");
        sunset = data.optString("sunset");
        moonrise = data.optString("moonrise");
        moonset = data.optString("moonset");
        moonphase = data.optString("moon_phase");
        moon_illumination = data.optString("moon_illumination");
        is_moon_up = data.optInt("is_moon_up");
        is_sun_up = data.optInt("is_sun_up");
    }
}
