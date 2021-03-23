package com.vnrvjiet.tsraksha;


import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;


public class TestingCentersFragment extends Fragment {

    private TabLayout myTabs;
    private ViewPager myViewPager;
    private Context context;

    public TestingCentersFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_testing_centers, container, false);
        context = getContext();
        Application.getInstance().initAppLanguage(context);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.show();
        actionBar.setTitle(getResources().getString(R.string.testing_centers));
        actionBar.setElevation(0);
        initializeFields(view);
        return view;
    }

    private void initializeFields(View view) {
        myTabs = view.findViewById(R.id.testing_tabs);
        myViewPager = view.findViewById(R.id.testing_view_pager);
        TestingCentersAdapter adapter = new TestingCentersAdapter(getChildFragmentManager(),context);
        myViewPager.setAdapter(adapter);
        myTabs.setupWithViewPager(myViewPager);
    }

}
