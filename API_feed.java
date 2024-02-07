package app.personal_weather;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import app.personal_weather.data.Current;
import app.personal_weather.data.Forecast;

/**
 * Created by Christopher D. Harte on 12/06/2021.
 */
public class API_feed {
    private static Exception error;
    private final Weather_Activity this_weather;

    public API_feed(Weather_Activity weather) {
        this.this_weather = weather;
    }

    public void refresh (String new_location) {
        API_async task = new API_async(this_weather);
        task.execute(new_location);
    }

    private static class API_async extends AsyncTask<String, Void, String> {
        private final String client_id = "391a795e9a804ff4be3235120211006";
        private final String api_url = "https://api.weatherapi.com/v1/";

        private final WeakReference<Weather_Activity> weak_ref;

        API_async(Weather_Activity weather) {
            weak_ref = new WeakReference<>(weather);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                //https://api.weatherapi.com/v1/forecast.json?key=391a795e9a804ff4be3235120211006&q=M25&days=7
                URL url = new URL(api_url + "forecast.json?key=" + client_id + "&q=" + params[0] + "&days=5");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();

                InputStream input = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                return result.toString();
            }
            catch (Exception e) {
                error = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            final Weather_Activity weak_weather = weak_ref.get();

            if (result == null && error != null) {
                weak_weather.feed_failure(error);
                return;
            }

            try {
                JSONObject data = new JSONObject(result);

                //The feed does not have a single root but forecast, current and location.
                //And we don't need location.
                Current current = new Current();
                current.populate(data.optJSONObject("current"));//because current is today
                Forecast forc = new Forecast();
                forc.populate(data.getJSONObject("forecast"));

                //current is todays weather, forc is three days hence including today (?)
                weak_weather.feed_success(current, forc);
            }
            catch (Exception e) {
                weak_weather.feed_failure(e);
            }
        }
    }
}
