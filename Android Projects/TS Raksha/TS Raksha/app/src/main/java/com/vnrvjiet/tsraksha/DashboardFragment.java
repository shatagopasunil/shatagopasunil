package com.vnrvjiet.tsraksha;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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

import static com.vnrvjiet.tsraksha.Constants.ACTIVE;
import static com.vnrvjiet.tsraksha.Constants.CASES;
import static com.vnrvjiet.tsraksha.Constants.CONFIRMED;
import static com.vnrvjiet.tsraksha.Constants.DATE;
import static com.vnrvjiet.tsraksha.Constants.DECEASED;
import static com.vnrvjiet.tsraksha.Constants.GLOBAL;
import static com.vnrvjiet.tsraksha.Constants.INDIA;
import static com.vnrvjiet.tsraksha.Constants.RECOVERED;
import static com.vnrvjiet.tsraksha.Constants.TELANGANA;
import static com.vnrvjiet.tsraksha.Constants.TIME;
import static com.vnrvjiet.tsraksha.Constants.UPDATED;
import static com.vnrvjiet.tsraksha.Constants.UPLOAD;


public class DashboardFragment extends Fragment {

    private TextView tsConfirmed, tsActive, tsRecovered, tsDeceased, indConfirmed, indActive,
            indRecovered, indDeceased, worldConfirmed, worldActive, worldRecovered, worldDeceased,
            updatedTime;
    private DatabaseReference casesRef;

    public DashboardFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        Context context = getContext();
        Application.getInstance().initAppLanguage(context);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.show();
        actionBar.setTitle(getResources().getString(R.string.dashboard));
        initializeFields(view);
        retrieveCases();
        return view;
    }

    private void retrieveCases() {
        casesRef.child(UPDATED).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String date = snapshot.child(DATE).getValue().toString();
                String time = snapshot.child(TIME).getValue().toString();
                updatedTime.setText(getResources().getString(R.string.updated_dash, time, date));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        casesRef.child(TELANGANA).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tsRecovered.setText(snapshot.child(RECOVERED).getValue().toString());
                tsConfirmed.setText(snapshot.child(CONFIRMED).getValue().toString());
                tsActive.setText(snapshot.child(ACTIVE).getValue().toString());
                tsDeceased.setText(snapshot.child(DECEASED).getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        casesRef.child(INDIA).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                indRecovered.setText(snapshot.child(RECOVERED).getValue().toString());
                indConfirmed.setText(snapshot.child(CONFIRMED).getValue().toString());
                indActive.setText(snapshot.child(ACTIVE).getValue().toString());
                indDeceased.setText(snapshot.child(DECEASED).getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        casesRef.child(GLOBAL).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                worldRecovered.setText(snapshot.child(RECOVERED).getValue().toString());
                worldConfirmed.setText(snapshot.child(CONFIRMED).getValue().toString());
                worldActive.setText(snapshot.child(ACTIVE).getValue().toString());
                worldDeceased.setText(snapshot.child(DECEASED).getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initializeFields(View view) {
        updatedTime = view.findViewById(R.id.updated_time);
        tsConfirmed = view.findViewById(R.id.ts_confirmed);
        tsActive = view.findViewById(R.id.ts_active);
        tsRecovered = view.findViewById(R.id.ts_recovered);
        tsDeceased = view.findViewById(R.id.ts_deceased);
        indConfirmed = view.findViewById(R.id.ind_confirmed);
        indActive = view.findViewById(R.id.ind_active);
        indRecovered = view.findViewById(R.id.ind_recovered);
        indDeceased = view.findViewById(R.id.ind_deceased);
        worldConfirmed = view.findViewById(R.id.world_confirmed);
        worldActive = view.findViewById(R.id.world_active);
        worldRecovered = view.findViewById(R.id.world_recovered);
        worldDeceased = view.findViewById(R.id.world_deceased);
        casesRef = FirebaseDatabase.getInstance().getReference().child(UPLOAD).child(CASES);
    }

}
