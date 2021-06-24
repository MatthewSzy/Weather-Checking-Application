package com.example.astroapp;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.TimeZone;

public class SunFragment extends Fragment {

    public static SunFragment getInstance() {
        SunFragment sunFragment = new SunFragment();
        return sunFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_sun, container, false);
    }

    public void displaySunInfo(View sunView, AstroCalculator astroCalculator) {

        DecimalFormat decimalFormat = new DecimalFormat("#.###");

        TextView sunRiseOne = (TextView) sunView.findViewById(R.id.sunRiseOne);
        TextView sunRiseTwo = (TextView) sunView.findViewById(R.id.sunRiseTwo);
        String sunRiseOneInfo = String.format("%02d:%02d", astroCalculator.getSunInfo().getSunrise().getHour(), astroCalculator.getSunInfo().getSunrise().getMinute());
        sunRiseOne.setText(sunRiseOneInfo);
        sunRiseTwo.setText(decimalFormat.format(astroCalculator.getSunInfo().getAzimuthRise()) + "\u00B0");

        TextView sunSetOne = (TextView) sunView.findViewById(R.id.sunSetOne);
        TextView sunSetTwo = (TextView) sunView.findViewById(R.id.sunSetTwo);
        String sunSetOneInfo = String.format("%02d:%02d", astroCalculator.getSunInfo().getSunset().getHour(), astroCalculator.getSunInfo().getSunset().getMinute());
        sunSetOne.setText(sunSetOneInfo);
        sunSetTwo.setText(decimalFormat.format(astroCalculator.getSunInfo().getAzimuthSet()) + "\u00B0");

        TextView dawnView = (TextView) sunView.findViewById(R.id.dawnView);
        TextView duskView = (TextView) sunView.findViewById(R.id.duskView);
        String dawnViewInfo = String.format("%02d:%02d", astroCalculator.getSunInfo().getTwilightMorning().getHour(), astroCalculator.getSunInfo().getTwilightMorning().getMinute());
        String duskViewInfo = String.format("%02d:%02d", astroCalculator.getSunInfo().getTwilightEvening().getHour(), astroCalculator.getSunInfo().getTwilightEvening().getMinute());
        dawnView.setText(dawnViewInfo);
        duskView.setText(duskViewInfo);
    }
}
