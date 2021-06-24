package com.example.astroapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ForecastFragment extends Fragment {

    final String url = "http://api.openweathermap.org/data/2.5/forecast";
    final String appid = "574bbc5475c85ad30392c705cd37d9f2";

    public static ForecastFragment getInstance() {
        ForecastFragment forecastFragment = new ForecastFragment();
        return forecastFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.view_forecast, container, false);
    }

    public void displayWeatherInfo(View forecastView, JSONObject jsonObject, boolean temperatureType) {

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        try {
            JSONArray weatherArray = jsonObject.getJSONArray("list");
            ArrayList<TextView> dateViews = new ArrayList<>();
            dateViews.add(forecastView.findViewById(R.id.dateView1));
            dateViews.add(forecastView.findViewById(R.id.dateView2));
            dateViews.add(forecastView.findViewById(R.id.dateView3));
            dateViews.add(forecastView.findViewById(R.id.dateView4));
            dateViews.add(forecastView.findViewById(R.id.dateView5));

            ArrayList<TextView> weatherViews = new ArrayList<>();
            weatherViews.add(forecastView.findViewById(R.id.weatherView1));
            weatherViews.add(forecastView.findViewById(R.id.weatherView2));
            weatherViews.add(forecastView.findViewById(R.id.weatherView3));
            weatherViews.add(forecastView.findViewById(R.id.weatherView4));
            weatherViews.add(forecastView.findViewById(R.id.weatherView5));

            ArrayList<ImageView> iconViews = new ArrayList<>();
            iconViews.add(forecastView.findViewById(R.id.iconView1));
            iconViews.add(forecastView.findViewById(R.id.iconView2));
            iconViews.add(forecastView.findViewById(R.id.iconView3));
            iconViews.add(forecastView.findViewById(R.id.iconView4));
            iconViews.add(forecastView.findViewById(R.id.iconView5));

            int index = 0;
            for(int i = 0; i < 40; i = i + 8) {
                JSONObject day = weatherArray.getJSONObject(i);
                String date = day.getString("dt_txt");
                String outputDate = date.substring(0, 10);
                dateViews.get(index).setText(outputDate);

                JSONObject main = day.getJSONObject("main");
                if (!temperatureType) {
                    weatherViews.get(index).setText(decimalFormat.format(main.getDouble("temp") - 273.15) + "\u00B0" + "C");
                }
                else {
                    weatherViews.get(index).setText(decimalFormat.format((9 * ((main.getDouble("temp") - 273.15) / 5) + 32)) + "\u00B0" + "F");
                }

                JSONArray weathers = day.getJSONArray("weather");
                JSONObject weather = weathers.getJSONObject(0);
                String iconId = weather.getString("icon");
                String iconURL = "http://openweathermap.org/img/wn/" + iconId + ".png";
                Picasso.get().load(iconURL).into(iconViews.get(index++));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
