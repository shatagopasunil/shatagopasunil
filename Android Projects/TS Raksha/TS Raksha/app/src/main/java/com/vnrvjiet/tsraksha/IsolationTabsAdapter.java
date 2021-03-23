package com.vnrvjiet.tsraksha;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import static com.vnrvjiet.tsraksha.Constants.EMPTY;

public class IsolationTabsAdapter extends FragmentPagerAdapter {
    private Context context;
    private IsolationCentersHospitals isolationCentersHospitals;

    public IsolationTabsAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                isolationCentersHospitals = new IsolationCentersHospitals(true);
                return  isolationCentersHospitals;
            case 1:
                isolationCentersHospitals = new IsolationCentersHospitals(false);
                return isolationCentersHospitals;
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
        switch (position) {
            case 0:
                return context.getResources().getString(R.string.government);
            case 1:
                return context.getResources().getString(R.string.privates);
        }
        return EMPTY;
    }
}
