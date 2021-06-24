package com.example.astroapp;

import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimerThread extends Thread {

    TextView clockView;

    public TimerThread(TextView clockView) {
        this.clockView = clockView;
    }

    @Override
    public void run() {
        while(!isInterrupted()) {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            String formattedDate = df.format(c.getTime());

            clockView.setText(formattedDate);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
