package app.personal_weather.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * this has nothing in it except ForecastDay.
 * Created by Christopher D. Harte on 16/06/2021.
 */
public class Forecast
{
    JSONArray forecast_day;
    Astronomy astro;
    String date;

    public JSONArray getForecast_day(){
        return forecast_day;
    }
    public Astronomy getAstro(){
        return astro;
    }

    public String getDate() {
        return date;
    }

    public void populate (JSONObject data) throws JSONException
    {
        forecast_day = data.getJSONArray("forecastday");

        date = forecast_day.getJSONObject(1).getString("date");

        astro = new Astronomy();
        astro.populate(forecast_day.getJSONObject(0).optJSONObject("astro"));
    }
}
