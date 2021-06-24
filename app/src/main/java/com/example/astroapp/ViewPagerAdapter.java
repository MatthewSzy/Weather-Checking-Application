package com.example.astroapp;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        titleList.add(title);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        if (position == 0) {
            SunFragment fragment = (SunFragment) super.instantiateItem(container, position);
            fragmentList.set(position, fragment);
            return fragment;
        }
        else if (position == 1) {
            MoonFragment fragment = (MoonFragment) super.instantiateItem(container, position);
            fragmentList.set(position, fragment);
            return fragment;
        }
        else if (position == 2) {
            WeatherFragment fragment = (WeatherFragment) super.instantiateItem(container, position);
            fragmentList.set(position, fragment);
            return fragment;
        }
        else if (position == 3) {
            WindFragment fragment = (WindFragment) super.instantiateItem(container, position);
            fragmentList.set(position, fragment);
            return fragment;
        }
        else if (position == 4) {
            ForecastFragment fragment = (ForecastFragment) super.instantiateItem(container, position);
            fragmentList.set(position, fragment);
            return fragment;
        }

        return null;
    }
}
