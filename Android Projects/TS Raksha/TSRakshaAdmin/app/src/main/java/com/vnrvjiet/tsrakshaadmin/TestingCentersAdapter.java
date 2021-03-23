package com.vnrvjiet.tsrakshaadmin;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TestingCentersAdapter extends FragmentPagerAdapter {

    private Context context;
    private ThreeTestingCenters threeTestingCenters;
    private RapidAntigenTestingCenters rapidAntigenTestingCenters;


    public TestingCentersAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                threeTestingCenters = new ThreeTestingCenters();
                return  threeTestingCenters;
            case 1:
                rapidAntigenTestingCenters = new RapidAntigenTestingCenters();
                return rapidAntigenTestingCenters;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0:
                return context.getResources().getString(R.string.rt);
            case 1:
                return context.getResources().getString(R.string.anti);
        }
        return "";
    }
}