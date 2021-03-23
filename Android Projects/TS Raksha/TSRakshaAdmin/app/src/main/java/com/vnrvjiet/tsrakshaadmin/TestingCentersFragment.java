package com.vnrvjiet.tsrakshaadmin;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class TestingCentersFragment extends Fragment {

    private TabLayout myTabs;
    private ViewPager myViewPager;

    public TestingCentersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_testing_centers, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.testing_centers));
        ((AppCompatActivity)getActivity()).getSupportActionBar().setElevation(0);
        initializeFields(view);
        return view;
    }

    private void initializeFields(View view) {
        myTabs = view.findViewById(R.id.testing_tabs);
        myViewPager = view.findViewById(R.id.testing_view_pager);
        TestingCentersAdapter adapter = new TestingCentersAdapter(getChildFragmentManager(),getContext());
        myViewPager.setAdapter(adapter);
        myTabs.setupWithViewPager(myViewPager);
    }

}
