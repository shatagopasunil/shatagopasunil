package com.vnrvjiet.tsraksha;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vnrvjiet.tsraksha.Adapters.TestingCenterAdapter;
import com.vnrvjiet.tsraksha.Models.TestingCenterModel;

import static com.vnrvjiet.tsraksha.Constants.GOVERNMENT;
import static com.vnrvjiet.tsraksha.Constants.PRIVATE;
import static com.vnrvjiet.tsraksha.Constants.TESTING_CENTERS;
import static com.vnrvjiet.tsraksha.Constants.UPLOAD;


public class ThreeTestingCenters extends Fragment {
    
    private RadioGroup testingGroup;
    private RadioButton govt,pvt;
    private RecyclerView recyclerView;
    private int checkedRadioButtonId;
    private DatabaseReference testingRef,customRef;
    private FirebaseRecyclerOptions<TestingCenterModel> options;
    private TestingCenterAdapter adapter;
    private Context context;

    public ThreeTestingCenters() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_three_testing_centers, container, false);
        context = getContext();
        Application.getInstance().initAppLanguage(context);
        initializeFields(view);
        checkedRadioButtonId = testingGroup.getCheckedRadioButtonId();
        testingGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                checkedRadioButtonId = i;
                setRecyclerView(checkedRadioButtonId);
            }
        });
        setRecyclerView(checkedRadioButtonId);
        return view;
    }

    private void setRecyclerView(int checkedRadioButtonId) {
        if(checkedRadioButtonId == R.id.government_testing)
            showRecycler(GOVERNMENT);
        else if(checkedRadioButtonId == R.id.private_testing)
            showRecycler(PRIVATE);
    }

    private void showRecycler(String s) {
        customRef = testingRef.child(s);
        options = new FirebaseRecyclerOptions.Builder<TestingCenterModel>().setQuery(customRef,TestingCenterModel.class).build();
        adapter = new TestingCenterAdapter(options);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void initializeFields(View view) {
        testingGroup = view.findViewById(R.id.testing_radio_group);
        govt = view.findViewById(R.id.government_testing);
        pvt = view.findViewById(R.id.private_testing);
        recyclerView = view.findViewById(R.id.testing_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        recyclerView.setHasFixedSize(true);
        testingRef = FirebaseDatabase.getInstance().getReference().child(UPLOAD).child(TESTING_CENTERS).child("RT");
    }

}
