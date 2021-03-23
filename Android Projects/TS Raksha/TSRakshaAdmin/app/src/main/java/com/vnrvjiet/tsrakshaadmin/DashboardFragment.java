package com.vnrvjiet.tsrakshaadmin;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {
    private TextView tsConfirmed, tsActive, tsRecovered, tsDeceased, indConfirmed, indActive,
            indRecovered, indDeceased, worldConfirmed, worldActive, worldRecovered, worldDeceased;
    private DatabaseReference casesRef;
    private LoadingBar loadingBar;


    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.dashboard));
        initializeFields(view);
        retrieveCases();
        return view;
    }

    private void retrieveCases() {
        loadingBar.showLoadingBar(1);
        casesRef.child("Telangana").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tsRecovered.setText(snapshot.child("Recovered").getValue().toString());
                tsConfirmed.setText(snapshot.child("Confirmed").getValue().toString());
                tsActive.setText(snapshot.child("Active").getValue().toString());
                tsDeceased.setText(snapshot.child("Deceased").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        casesRef.child("India").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                indRecovered.setText(snapshot.child("Recovered").getValue().toString());
                indConfirmed.setText(snapshot.child("Confirmed").getValue().toString());
                indActive.setText(snapshot.child("Active").getValue().toString());
                indDeceased.setText(snapshot.child("Deceased").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        casesRef.child("Global").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                worldRecovered.setText(snapshot.child("Recovered").getValue().toString());
                worldConfirmed.setText(snapshot.child("Confirmed").getValue().toString());
                worldActive.setText(snapshot.child("Active").getValue().toString());
                worldDeceased.setText(snapshot.child("Deceased").getValue().toString());
                loadingBar.dismissLoadingBar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initializeFields(View view) {
        tsConfirmed = view.findViewById(R.id.ts_confirmed);
        tsActive = view.findViewById(R.id.ts_active);
        tsRecovered = view.findViewById(R.id.ts_recovered);
        tsDeceased = view.findViewById(R.id.ts_deceased);
        indConfirmed = view.findViewById(R.id.ind_confirmed);
        indActive = view.findViewById(R.id.ind_active);
        indRecovered = view.findViewById(R.id.ind_recovered);
        indDeceased = view.findViewById(R.id.ind_deceased);
        worldConfirmed = view.findViewById(R.id.world_confirmed);
        loadingBar = new LoadingBar(getActivity());
        worldActive = view.findViewById(R.id.world_active);
        worldRecovered = view.findViewById(R.id.world_recovered);
        worldDeceased = view.findViewById(R.id.world_deceased);
        casesRef = FirebaseDatabase.getInstance().getReference().child("Upload").child("Cases");
    }

}
