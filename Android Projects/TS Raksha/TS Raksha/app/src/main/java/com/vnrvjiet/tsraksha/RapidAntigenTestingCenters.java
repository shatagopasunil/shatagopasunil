package com.vnrvjiet.tsraksha;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vnrvjiet.tsraksha.Adapters.RapidTestingAdapter;
import com.vnrvjiet.tsraksha.Models.TestingCenterModel;

import static com.vnrvjiet.tsraksha.Constants.TESTING_CENTERS;
import static com.vnrvjiet.tsraksha.Constants.UPLOAD;


public class RapidAntigenTestingCenters extends Fragment {
    private RecyclerView rapidRecycler;
    private DatabaseReference rapidRef;
    private RapidTestingAdapter adapter;
    private FirebaseRecyclerOptions<TestingCenterModel> options;
    private Context context;

    public RapidAntigenTestingCenters() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_rapid_antigen_testing_centers, container, false);
        context = getContext();
        Application.getInstance().initAppLanguage(context);
        initializeFields(view);
        settingAdapter();
        return view;
    }

    private void settingAdapter() {
        options = new FirebaseRecyclerOptions.Builder<TestingCenterModel>().setQuery(rapidRef,TestingCenterModel.class).build();
        adapter = new RapidTestingAdapter(options);
        rapidRecycler.setAdapter(adapter);
        adapter.startListening();
    }

    private void initializeFields(View view) {
        rapidRecycler = view.findViewById(R.id.rapid_recycler_view);
        rapidRecycler.setHasFixedSize(true);
        rapidRecycler.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        rapidRef = FirebaseDatabase.getInstance().getReference().child(UPLOAD).child(TESTING_CENTERS).child("Rapid");
    }

}
