package com.sunil45.crimeregistration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class TrackComplaintActivity extends AppCompatActivity {
    private ProgressBar mProgressBar;
    private TextView complaintText,trackText;
    private FirebaseAuth myAuth;
    private DatabaseReference rootRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_complaint);
        loadingBar = new ProgressDialog(this);
        mProgressBar=(ProgressBar)findViewById(R.id.progress_bar);
        complaintText=(TextView)findViewById(R.id.complaint_text);
        trackText=(TextView)findViewById(R.id.tracking_text);
        Intent intent=getIntent();
        String complaint = intent.getStringExtra("category");
        String tracking = intent.getStringExtra("track_id");
        complaintText.setText(complaint);
        trackText.setText(tracking);
    }

}
