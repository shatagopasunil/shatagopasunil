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
    private TextView complaintText,trackText, dateTimeDisplay, locationText, displayStatus, displayProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_complaint);
        displayStatus = findViewById(R.id.display_status);
        mProgressBar=(ProgressBar)findViewById(R.id.progress_bar);
        complaintText=(TextView)findViewById(R.id.complaint_text);
        dateTimeDisplay = findViewById(R.id.date_time_display);
        displayProgress = findViewById(R.id.display_progress);
        locationText = findViewById(R.id.location_text);
        trackText=(TextView)findViewById(R.id.tracking_text);
        String track_id = getIntent().getStringExtra("track_id");
        ComplaintsModel complaintsModel = (ComplaintsModel) getIntent().getSerializableExtra("complaint");
        for(int i = 1; i < 10; ++i){
            String idx = String.valueOf(i);
            if(complaintsModel.getStatus().containsKey(idx)){
                displayStatus.append("\n--> " + complaintsModel.getStatus().get(idx));
            }else{
                break;
            }
        }
        trackText.setText(track_id);
        displayProgress.setText(complaintsModel.getStatus().get("progress").toString() + "%");
        mProgressBar.setProgress(Integer.parseInt(complaintsModel.getStatus().get("progress").toString()));
        complaintText.setText(complaintsModel.getCategory());
        locationText.setText(complaintsModel.getAddress());
        locationText.append(", " + complaintsModel.getPincode());
        dateTimeDisplay.append(complaintsModel.getDate() + ", " + complaintsModel.getTime() + ".");
    }

}
