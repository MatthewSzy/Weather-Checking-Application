package com.example.astroapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.astrocalculator.AstroCalculator;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MoonFragment extends Fragment {

    public static MoonFragment getInstance() {
        MoonFragment moonFragment = new MoonFragment();
        return moonFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.view_moon, container, false);
    }

    public void displayMoonInfo(View moonView, AstroCalculator astroCalculator) {

        DecimalFormat decimalFormat = new DecimalFormat("#.###");

        TextView moonRise = (TextView) moonView.findViewById(R.id.moonRise);
        TextView moonSet = (TextView) moonView.findViewById(R.id.weatherView1);
        String moonRiseInfo = String.format("%02d:%02d", astroCalculator.getMoonInfo().getMoonrise().getHour(), astroCalculator.getMoonInfo().getMoonrise().getMinute());
        String moonSetInfo = String.format("%02d:%02d", astroCalculator.getMoonInfo().getMoonset().getHour(), astroCalculator.getMoonInfo().getMoonset().getMinute());
        moonRise.setText(moonRiseInfo);
        moonSet.setText(moonSetInfo);

        TextView newMoon = (TextView) moonView.findViewById(R.id.weatherView2);
        TextView fullMoon = (TextView) moonView.findViewById(R.id.weatherView5);
        String newMoonInfo = String.format("%d.%02d.%02d", astroCalculator.getMoonInfo().getNextNewMoon().getYear(), astroCalculator.getMoonInfo().getNextNewMoon().getMonth(), astroCalculator.getMoonInfo().getNextNewMoon().getDay());
        String fullMoonInfo = String.format("%d.%02d.%02d", astroCalculator.getMoonInfo().getNextFullMoon().getYear(), astroCalculator.getMoonInfo().getNextFullMoon().getMonth(), astroCalculator.getMoonInfo().getNextFullMoon().getDay());
        newMoon.setText(newMoonInfo);
        fullMoon.setText(fullMoonInfo);

        TextView moonPhase= (TextView) moonView.findViewById(R.id.pressure);
        TextView synodicDay = (TextView) moonView.findViewById(R.id.description);
        moonPhase.setText(decimalFormat.format(astroCalculator.getMoonInfo().getIllumination() * 100.0) + "%");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
        Date newMoonDate = null;

        try {
            newMoonDate = formatter.parse(newMoonInfo);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date actualDate = new Date();
        long diff = newMoonDate.getTime() - actualDate.getTime();
        long diffDay = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        if (diffDay < 0) {
            synodicDay.setText(Math.abs(diffDay) + " day");
        }
        else {
            synodicDay.setText(29 - diffDay + " day");
        }
    }
}
