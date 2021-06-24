package com.example.astroapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class WeatherFragment extends Fragment {

    public static WeatherFragment getInstance() {
        WeatherFragment weatherFragment = new WeatherFragment();
        return weatherFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_weather, container, false);
    }

    @SuppressLint("SetTextI18n")
    public void displayWeatherInfo(View weatherView, JSONObject jsonObject, String responseDate, boolean temperatureType) {

        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        TextView downloadDate = (TextView) weatherView.findViewById(R.id.weatherView1);
        downloadDate.setText(responseDate);
        try {
            ImageView weatherIcon = (ImageView) weatherView.findViewById(R.id.weatherIcon);
            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            JSONObject weather = weatherArray.getJSONObject(0);
            String iconId = weather.getString("icon");
            String iconURL = "http://openweathermap.org/img/wn/" + iconId + ".png";
            Picasso.get().load(iconURL).into(weatherIcon);

            JSONObject main = jsonObject.getJSONObject("main");
            if(!temperatureType) {
                TextView temperature = (TextView) weatherView.findViewById(R.id.weatherView2);
                temperature.setText(decimalFormat.format(main.getDouble("temp") - 273.15) + "\u00B0" + "C");

                TextView sensedTemp = (TextView) weatherView.findViewById(R.id.weatherView5);
                sensedTemp.setText(decimalFormat.format(main.getDouble("feels_like") - 273.15) + "\u00B0" + "C");

                TextView minTemp = (TextView) weatherView.findViewById(R.id.weatherView3);
                minTemp.setText(decimalFormat.format(main.getDouble("temp_min") - 273.15) + "\u00B0" + "C");

                TextView maxTemp = (TextView) weatherView.findViewById(R.id.weatherView4);
                maxTemp.setText(decimalFormat.format(main.getDouble("temp_max") - 273.15) + "\u00B0" + "C");
            }
            else {
                TextView temperature = (TextView) weatherView.findViewById(R.id.weatherView2);
                temperature.setText(decimalFormat.format((9 * ((main.getDouble("temp") - 273.15) / 5) + 32)) + "\u00B0" + "F");

                TextView sensedTemp = (TextView) weatherView.findViewById(R.id.weatherView5);
                sensedTemp.setText(decimalFormat.format((9 * ((main.getDouble("feels_like") - 273.15) / 5) + 32)) + "\u00B0" + "F");

                TextView minTemp = (TextView) weatherView.findViewById(R.id.weatherView3);
                minTemp.setText(decimalFormat.format((9 * ((main.getDouble("temp_min") - 273.15) / 5) + 32)) + "\u00B0" + "F");

                TextView maxTemp = (TextView) weatherView.findViewById(R.id.weatherView4);
                maxTemp.setText(decimalFormat.format((9 * ((main.getDouble("temp_max") - 273.15) / 5) + 32)) + "\u00B0" + "F");
            }

            TextView pressure = (TextView) weatherView.findViewById(R.id.pressure);
            pressure.setText(decimalFormat.format(main.getDouble("pressure")) + "hPa");

            TextView description = (TextView) weatherView.findViewById(R.id.description);
            String str = weather.getString("description");
            String output = str.substring(0, 1).toUpperCase() + str.substring(1);
            description.setText(output);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
