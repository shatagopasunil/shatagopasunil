package com.sunil45.crimeregadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

public class ShowComplaintsActivity extends AppCompatActivity {
    private RecyclerView complaintsRecycler;
    private DatabaseReference complaintsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_complaints);
        initializeFields();
        settingAdapter();
    }

    private void initializeFields() {
        complaintsRecycler = findViewById(R.id.complaints_recycler);
        complaintsRef = FirebaseDatabase.getInstance().getReference().child("Complaints");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        complaintsRecycler.setLayoutManager(linearLayoutManager);
        complaintsRecycler.setHasFixedSize(true);
    }
    private void settingAdapter() {
        Query query = complaintsRef.orderByChild("Status/progress");
        FirebaseRecyclerOptions<ComplaintsModel> options = new FirebaseRecyclerOptions.Builder<ComplaintsModel>().setQuery(query, ComplaintsModel.class).build();
        ComplaintsAdapter adapter = new ComplaintsAdapter(options);
        complaintsRecycler.setAdapter(adapter);
        adapter.startListening();
    }
}