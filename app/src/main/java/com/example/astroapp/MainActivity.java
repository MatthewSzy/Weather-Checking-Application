package com.example.astroapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    TextView clockView;
    TextView cityView;
    TextView longitudeView;
    TextView latitudeView;

    EditText cityText;

    Spinner citySpinner;
    int cityPosition;

    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    boolean used = false;
    boolean refreshSunAndMoon = false;
    boolean refreshWeatherAndWind = false;
    boolean refreshForecast = false;
    boolean temperatureType = false;
    int refreshValue = 5;
    double longitude = 0;
    double latitude = 0;
    String actualCity = "";
    String city = "";
    Thread refreshingThread;

    final String url = "http://api.openweathermap.org/data/2.5/weather";
    final String urlForecast = "http://api.openweathermap.org/data/2.5/forecast";
    final String appid = "574bbc5475c85ad30392c705cd37d9f2";
    ArrayList<String> addedCity = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        clockView = (TextView) findViewById(R.id.clockView);
        longitudeView = (TextView) findViewById(R.id.longitudeView);
        latitudeView = (TextView) findViewById(R.id.latitudeView);
        cityView = (TextView) findViewById(R.id.cityView);

        cityText = (EditText) findViewById(R.id.cityText);

        loadCitiesFromPreferences();
        if(addedCity.size() == 0) {
            addedCity.add("Warsaw");
            saveCitiesToPreferences(addedCity);
        }

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.times, R.layout.my_selected_item);
        adapter.setDropDownViewResource(R.layout.my_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        citySpinner = findViewById(R.id.spinner2);
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this, R.layout.my_selected_item, addedCity);
        cityAdapter.setDropDownViewResource(R.layout.my_dropdown_item);
        citySpinner.setAdapter(cityAdapter);
        citySpinner.setOnItemSelectedListener(this);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(5);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(SunFragment.getInstance(), "SUN");
        viewPagerAdapter.addFragment(MoonFragment.getInstance(), "MOON");
        viewPagerAdapter.addFragment(WeatherFragment.getInstance(), "WEATHER");
        viewPagerAdapter.addFragment(WindFragment.getInstance(), "WIND");
        viewPagerAdapter.addFragment(ForecastFragment.getInstance(), "FORECAST");
        viewPager.setAdapter(viewPagerAdapter);

        if (savedInstanceState != null) {
            refreshSunAndMoon = true;
            refreshWeatherAndWind = true;
            refreshForecast= true;

            cityView.setText(savedInstanceState.getString("cityValue"));
            longitudeView.setText(savedInstanceState.getString("longitudeValue"));
            latitudeView.setText(savedInstanceState.getString("latitudeValue"));
            cityText.setText(savedInstanceState.getString("cityText"));
            actualCity = savedInstanceState.getString("actualCity");
            city = savedInstanceState.getString("city");
            refreshValue = savedInstanceState.getInt("refreshingTime");
            longitude = savedInstanceState.getDouble("longitude");
            latitude = savedInstanceState.getDouble("latitude");
        }

        Thread timerThread = new TimerThread(clockView);
        timerThread.setDaemon(true);
        timerThread.start();
        refreshingInfo();
    }

    public void showToast(final String toast) {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("cityValue", cityView.getText().toString());
        outState.putString("longitudeValue", longitudeView.getText().toString());
        outState.putString("latitudeValue", latitudeView.getText().toString());
        outState.putString("cityText", cityText.getText().toString());
        outState.putString("actualCity", actualCity);
        outState.putString("city", city);
        outState.putInt("refreshingTime", refreshValue);
        outState.putDouble("longitude", longitude);
        outState.putDouble("latitude", latitude);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        savedInstanceState.getString("cityValue");
        savedInstanceState.getString("longitudeValue");
        savedInstanceState.getString("latitudeValue");
        savedInstanceState.getString("cityText");
        savedInstanceState.getString("actualCity");
        savedInstanceState.getString("city");
        savedInstanceState.getInt("refreshingTime");
        savedInstanceState.getDouble("longitude");
        savedInstanceState.getDouble("latitude");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.spinner) {
            String value = parent.getItemAtPosition(position).toString();
            if (value.equals("5 min")) refreshValue = 5;
            else if (value.equals("10 min")) refreshValue = 10;
            else if (value.equals("15 min")) refreshValue = 15;
            else if (value.equals("20 min")) refreshValue = 20;
            else if (value.equals("25 min")) refreshValue = 25;
        }
        else if(parent.getId() == R.id.spinner2){
            cityPosition = position;
            actualCity = parent.getItemAtPosition(position).toString();
        }
    }

    public void deleteCity(View view) {

        addedCity.remove(citySpinner.getItemAtPosition(cityPosition).toString());
        saveCitiesToPreferences(addedCity);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void saveCitiesToPreferences(ArrayList<String> cityList) {
        Gson gson = new Gson();
        String jsonStrings = gson.toJson(cityList);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Cities", jsonStrings);
        editor.apply();
    }

    public void loadCitiesFromPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String jsonStrings = sharedPreferences.getString("Cities", "");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> sharedPreferencesList = gson.fromJson(jsonStrings, type);
        if (sharedPreferencesList == null) return;
        addedCity = sharedPreferencesList;
    }

    public void searchCity(View view) {

        if (cityText.getText().toString().equals("")) return;
        String tempURL = url + "?q=" + cityText.getText().toString() + "&appid=" + appid;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, tempURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                addedCity.add(cityText.getText().toString());
                saveCitiesToPreferences(addedCity);
                cityText.setText("");
                cityText.clearFocus();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showToast("The city cannot be found!");
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void searchButton2(View view) {

        if (cityText.getText().toString().equals("")) return;
        actualCity = cityText.getText().toString();
        cityText.setText("");
        cityText.clearFocus();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void refreshData(View view) {

        if (cityView.getText().equals("")) return;
        weatherRequest();
        forecastRequest();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void changeTemperature(View view) {

        if (!temperatureType) temperatureType = true;
        else temperatureType = false;
        weatherRequest();
        forecastRequest();
    }

    public void refreshingInfo() {
        refreshingThread = new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                Date startDate = new Date();
                long startTime = startDate.getTime();

                while(true) {
                    if (!actualCity.equals(city)) {
                        weatherRequest();
                        forecastRequest();

                        city = actualCity;
                        startTime = startDate.getTime();
                    }
                    else if (refreshForecast && !city.equals("")) {
                        weatherRequest();
                        forecastRequest();
                    }
                    else if (!city.equals("")){
                        Date endDate = new Date();
                        long endTime = endDate.getTime();
                        long diffTime = endTime - startTime;
                        //long diffSeconds = (diffTime / 1000) % 60;
                        long diffMinutes = (diffTime / (1000 * 60)) % 60;
                        if (diffMinutes >= refreshValue) {
                            showToast("Refreshing...");
                            weatherRequest();
                            forecastRequest();

                            startDate = new Date();
                            startTime = startDate.getTime();
                        }
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) { }
                }
            }
        };
        refreshingThread.setDaemon(true);
        refreshingThread.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void weatherRequest() {
        String tempURL = url + "?q=" + actualCity + "&appid=" + appid;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, tempURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    cityView.setText(actualCity);
                    JSONObject jsonObject = new JSONObject(response);

                    Date responseDate = new Date();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                    String responseDateInString = simpleDateFormat.format(responseDate);
                    saveLastWeatherResponse(response, actualCity, responseDateInString);

                    JSONObject coord = jsonObject.getJSONObject("coord");
                    longitude = coord.getDouble("lon");
                    latitude = coord.getDouble("lat");

                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    longitudeView.setText(decimalFormat.format(longitude));
                    latitudeView.setText(decimalFormat.format(latitude));

                    displaySunAndMoonInfo(latitude, longitude);
                    displayWeatherAndWindInfo(jsonObject, responseDateInString);
                    used = true;
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!used) {
                    showToast("Weather data cannot be downloaded!");
                    ArrayList<String> response = loadLastWeatherResponse();
                    cityView.setText(response.get(0));

                    try {
                        JSONObject jsonObject = new JSONObject(response.get(2));
                        JSONObject coord = jsonObject.getJSONObject("coord");
                        longitude = coord.getDouble("lon");
                        latitude = coord.getDouble("lat");

                        DecimalFormat decimalFormat = new DecimalFormat("#.##");
                        longitudeView.setText(decimalFormat.format(longitude));
                        latitudeView.setText(decimalFormat.format(latitude));
                        displaySunAndMoonInfo(latitude, longitude);
                        displayWeatherAndWindInfo(jsonObject, response.get(1));
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void forecastRequest() {
        String tempURLForecast = urlForecast + "?q=" + actualCity + "&appid=" + appid;
        StringRequest stringRequestForecast = new StringRequest(Request.Method.POST, tempURLForecast, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    saveLastForecastResponse(response);
                    JSONObject jsonObject = new JSONObject(response);
                    displayForecast(jsonObject);
                    used = true;
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!used) {
                    showToast("Weather forecast data cannot be downloaded!");
                    String response = loadLastForecastResponse();

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        displayForecast(jsonObject);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequestForecast);
    }

    public void saveLastWeatherResponse(String response, String city, String responseDate) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("WeatherCity", city);
        editor.putString("WeatherResponseDate", responseDate);
        editor.putString("WeatherResponse", response);
        editor.apply();
    }

    public ArrayList<String> loadLastWeatherResponse() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ArrayList<String> response = new ArrayList<>();
        response.add(sharedPreferences.getString("WeatherCity", ""));
        response.add(sharedPreferences.getString("WeatherResponseDate", ""));
        response.add(sharedPreferences.getString("WeatherResponse", ""));
        return response;
    }

    public void saveLastForecastResponse(String response) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ForecastResponse", response);
        editor.apply();
    }

    public String loadLastForecastResponse() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPreferences.getString("ForecastResponse", "");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void displaySunAndMoonInfo(double latitude, double longitude) {

        SunFragment sunFragment = (SunFragment)viewPagerAdapter.getItem(0);
        MoonFragment moonFragment = (MoonFragment) viewPagerAdapter.getItem(1);
        if (sunFragment.getView() == null || moonFragment.getView() == null) return;

        LocalDateTime now = LocalDateTime.now();
        Calendar c = Calendar.getInstance();
        TimeZone timeZone = c.getTimeZone();
        int zone = timeZone.getRawOffset();

        AstroDateTime astroDateTime = new AstroDateTime(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), now.getHour(), now.getMinute(), now.getSecond(), zone, true);
        AstroCalculator.Location location = new AstroCalculator.Location(latitude, longitude);

        AstroCalculator calculator = new AstroCalculator(astroDateTime, location);

        sunFragment.displaySunInfo(sunFragment.getView(), calculator);
        moonFragment.displayMoonInfo(moonFragment.getView(), calculator);
        refreshSunAndMoon = false;
    }

    public void displayWeatherAndWindInfo(JSONObject jsonObject, String responseDate) {

        WeatherFragment weatherFragment = (WeatherFragment)viewPagerAdapter.getItem(2);
        WindFragment windFragment = (WindFragment)viewPagerAdapter.getItem(3);
        if (weatherFragment.getView() == null || windFragment.getView() == null) return;

        weatherFragment.displayWeatherInfo(weatherFragment.getView(), jsonObject, responseDate, temperatureType);
        windFragment.displayWindInfo(windFragment.getView(), jsonObject, responseDate);
        refreshWeatherAndWind = false;
    }

    public void displayForecast(JSONObject jsonObject) {

        ForecastFragment forecastFragment = (ForecastFragment)viewPagerAdapter.getItem(4);
        if (forecastFragment.getView()== null) return;

        forecastFragment.displayWeatherInfo(forecastFragment.getView(), jsonObject, temperatureType);
        refreshForecast = false;
    }
}