package com.example.astroapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class WindFragment extends Fragment {

    public static WindFragment getInstance() {
        WindFragment windFragment = new WindFragment();
        return windFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.view_wind, container, false);
    }

    @SuppressLint("SetTextI18n")
    public void displayWindInfo(View windView, JSONObject jsonObject, String responseDate) {

        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        TextView downloadDate = (TextView) windView.findViewById(R.id.weatherView1);
        downloadDate.setText(responseDate);
        try {
            JSONObject wind = jsonObject.getJSONObject("wind");
            TextView windSpeed = (TextView) windView.findViewById(R.id.weatherView2);
            windSpeed.setText(decimalFormat.format(wind.getDouble("speed")) + "m/s");

            TextView windDirection = (TextView) windView.findViewById(R.id.weatherView3);
            windDirection.setText(decimalFormat.format(wind.getDouble("deg")) + "\u00B0");

            TextView visibility = (TextView) windView.findViewById(R.id.weatherView4);
            visibility.setText(decimalFormat.format(jsonObject.getDouble("visibility")) + "m");

            JSONObject main = jsonObject.getJSONObject("main");
            TextView humidity = (TextView) windView.findViewById(R.id.weatherView5);
            humidity.setText(decimalFormat.format(main.getDouble("humidity")) + "%");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
