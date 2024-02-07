package app.personal_weather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.personal_weather.data.Astronomy;
import app.personal_weather.data.Condition;
import app.personal_weather.data.Current;
import app.personal_weather.data.Forecast;


/**
 * Turns out LocationListener is an interface, this means it cannot be instantiated, only implemented.
 * Undocumented and registered as two seperate bugs on google.
 * No longer relevant, now fully deprecated.
 */
public class Weather_Activity extends AppCompatActivity {
    private ImageView weather_icon;
    private TextView temperature;
    private TextView conditions;
    private TextView location_text_view;
    private TextView chill_factor;
    private TextView tomorrow;
    private TextView astro;
    private RelativeLayout rel_back;
    private final int REQ_CODE = 111;
    private LocationCallback callback;
    private AlertDialog dialog;
    private List<Address> address_info;
    FusedLocationProviderClient fused_client;
    LocationRequest locRequest;
    private double latitude;
    private double longitude;
//    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageView logo_link;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        rel_back            = findViewById(R.id.rel_back);
        weather_icon        = findViewById(R.id.weather_icon);
        temperature         = findViewById(R.id.temperature_tv);
        conditions          = findViewById(R.id.conditions_tv);
        location_text_view  = findViewById(R.id.location_tv);
        chill_factor        = findViewById(R.id.chill_factor);
        tomorrow            = findViewById(R.id.forecast);
        astro               = findViewById(R.id.sun_up_down);
        logo_link           = findViewById(R.id.logoView1);

//        actionBar = getSupportActionBar();
//        ColorDrawable barColour = new ColorDrawable(Color.parseColor("#2414bd"));
//        actionBar.setBackgroundDrawable(barColour);

        callback = new LocationCallback() {
            @Override public void onLocationResult(LocationResult locationResult){
                if (locationResult == null){
                    return;
                }
            }
        };

        logo_link.setImageResource(R.drawable.weatherapi_logo);
        logo_link.setOnClickListener(v -> {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.addCategory(Intent.CATEGORY_BROWSABLE);
            i.setData(Uri.parse("https://www.weatherapi.com/"));
            startActivity(i);
        });

        //Here be your permissions request, everybody else gets suppressed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int cluckup1 = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

            if (cluckup1 != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE},
                        REQ_CODE);
            }
        }

        // This might take some time
        dialog = new AlertDialog.Builder(this).create();
        dialog.setMessage("Give me a few secs...");
        dialog.show();

        //We now need a location request which is set in onResume
        locRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 30000)
                .setWaitForAccurateLocation(false)
                .build();

        fused_client = LocationServices.getFusedLocationProviderClient(Weather_Activity.this);
        fused_client.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>()
        {
            @Override public void onSuccess(Location l) {
                if (l != null) {
                    latitude = l.getLatitude();
                    longitude = l.getLongitude();
                    use_lat_and_long();
                }
            }
        });
    }

    private void use_lat_and_long() {
        API_feed api_feed = new API_feed(this);
        Geocoder geo = new Geocoder(this, Locale.getDefault());

        //The location settings were not working on the emulator, so do this.
        //Ho Chi Minh city just because it is unusual
//        double latitude = 10.777416;
//        double longitude = 106.639366;
        //new york for the sceptic tanks
//        double latitude = 40.7127;
//        double longitude = -74.0059;
        //New zealand because I am cyclone chasing
//        double latitude = -38.140693;
//        double longitude = 176.253784;
        //Local
//        double latitude = 53.5333;
//        double longitude = -2.2833;

        address_info = null;

        try {
            address_info = geo.getFromLocation(latitude, longitude, 1);
        }
        catch (IOException e) {
            dialog.dismiss();
            e.printStackTrace();
        }

        if (address_info.isEmpty()) {
            dialog.dismiss();
            Toast.makeText(this, "Unable to acquire the GPS signal", Toast.LENGTH_LONG)
                    .show();
            onDestroy();
        }
        else {
            String api_location = latitude + "," + longitude;
            api_feed.refresh(api_location);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fused_client.requestLocationUpdates(locRequest, callback, Looper.getMainLooper());
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * When you request permissions you get a call back code, this will make sure everything is allowed without crashing.
     */
    @Override
    public void onRequestPermissionsResult(int req_code, String[] perms, int[] grants) {
        switch (req_code)
        {
            case REQ_CODE:
                if(grants[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Location access denied", Toast.LENGTH_LONG).show();
                }
            break;
            default:
                super.onRequestPermissionsResult(req_code, perms, grants);
        }
    }

    /**
     * If the return from the weather feed was successful load up the views.
     */
    public void feed_success(Current current, Forecast forecast) {
        dialog.dismiss();

        Astronomy astron = forecast.getAstro();
        Condition cond = current.getCondition();
        JSONArray forecast_day = forecast.getForecast_day();
        Resources res = getResources();

        String sunrise = astron.getSunrise();
        String sunset = astron.getSunset();
        String chill = current.getFeelslike_c();
        String speed = current.getWind_mph();
        String direction = current.getWind_dir();
        String temp = current.getTemp_c();
        String desc = cond.getText();
        String cond_code = cond.getCode();
        String condIcon = cond.getIcon();
        String forecast_date = forecast.getDate();
        String forecast_NameDay = "";

        // Not everything of use comes from the weather api...
        String city = address_info.get(0).getLocality();
        String town = address_info.get(0).getSubLocality();
        String street = address_info.get(0).getThoroughfare();
        String post_code = address_info.get(0).getPostalCode();
        String place = street;

        //We do not always get a street or a town, but we should have a city.
        if (street == null) {
            place = town;

            if (town == null) {
                place = city;
            }

            //if all else fails and city is null
            if (city == null) {
                place = post_code;
            }
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int device_width = metrics.widthPixels;
        int device_height = metrics.heightPixels;

        //Relative on portrait, inside a scroll on landscape but still rel_back (Very clever)
        Drawable draw_back_image = res.getDrawable(R.drawable.night_time, getTheme());
        rel_back.setBackground(draw_back_image);

        //this sets the drawable icon in the format icon_1.png All res files have to start with a letter.
        String condString = condIcon.substring(condIcon.length() - 7);
        condIcon = condString.substring(0, 3);
        int icon_res = res.getIdentifier("icon_" + condIcon, "drawable", getPackageName());
        Drawable icon = AppCompatResources.getDrawable(this, icon_res);

//        String[] for_locals = res.getStringArray(R.array.local_conditions);
//        String display_for_locals = for_locals[Integer.parseInt(cond_code)];

        weather_icon.setImageDrawable(icon);
        temperature.setText(temp + "\u00B0");
        chill_factor.setText("(but feels like " + chill + "\u00B0 " + " in a " + speed + " mph wind coming from " + direction + ")");
//        conditions.setText(display_for_locals);
        conditions.setText(desc);
        location_text_view.setText("Which is not bad for " + place + "\n");
        astro.setText("Sunrise is at " + sunrise + "\nand sunset is " + sunset);

        //Get the strings then concatenate them into an arraylist
        ArrayList<String> fore_array = new ArrayList<>();

        for (int i = 0; i < forecast_day.length(); i++) {
            try {
                JSONObject o = (JSONObject) forecast_day.get(i);

                String oday = new SimpleDateFormat("EEEE").format(o.getLong("date_epoch") * 1000);
                String odate = o.getString("date");
                String otext = o.getJSONObject("day").getJSONObject("condition").getString("text");

                fore_array.add(oday + " " + odate + " " + otext);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        int i = fore_array.get(1).indexOf(" ");
        forecast_NameDay = fore_array.get(1).substring(0, i);
        tomorrow.setText("Tomorrow, " + forecast_NameDay + " " + forecast_date + " will be\n" + desc);

         /* Depending on orientation we need to use different layouts.
         * Landscape is a textView that goes inside the scrollView layout.
         * Portrait is a listView that goes inside the relativeView layout.
         * (Because you cannot have one scroll item inside another).
         */
        if (device_width > device_height) {
            //Landscape, use string builder with line breaks
            StringBuilder non_scroll_view = new StringBuilder();

            //Here is your php: foreach ($fore_array as $forcs)
            for (String forcs : fore_array) {
                non_scroll_view.append(forcs + "\n");
            }

            TextView text_forcs = findViewById(R.id.text_forcs);
            text_forcs.setText(non_scroll_view);
        }
        else { //Portrait, use my listView
            ArrayAdapter<String> fore_adapter = new ArrayAdapter<>(this,
                    R.layout.list_text,
                    fore_array);

            ListView fore_hi_low = findViewById(R.id.fore_hi_low);
            fore_hi_low.setAdapter(fore_adapter);
        }
    }

    /**
     * If there is a problem with the feed and an incoming message toast them.
     */
    public void feed_failure(Exception e) {
        dialog.dismiss();
        Toast.makeText(this, "Ooops... " + e.getMessage(), Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.about_app) {
            Intent a = new Intent("app.personal_weather.ABOUT");
            a.putExtra("title", "Your weather (sort of)");
            a.putExtra("body", "The weather where you are, with some graphics from http://vclouds.deviantart.com, "
                    + "the responses are from weatherapi.com (Which is only three days, including today)\n"
                    + "\nThis is just a test piece that takes your location from the gps and uses it to "
                    + "query the weather api. Rotating your device will cause it to reload and use a different layout.\n");
            startActivity(a);

            //No home icon as per the new evil rules from googling
        }
        else if (itemId == android.R.id.home) {
            onDestroy();
        }
        return false;
    }
}







