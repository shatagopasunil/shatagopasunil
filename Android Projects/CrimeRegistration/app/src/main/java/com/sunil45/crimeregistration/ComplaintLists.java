package com.sunil45.crimeregistration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class ComplaintLists extends AppCompatActivity {
    private LinearLayout complaintLayout;
    private FirebaseAuth myAuth;
    private DatabaseReference rootRef;
    private TextView headingComplaint;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_lists);
        loadingBar = new ProgressDialog(this);
        loadingBar.setCancelable(false);
        loadingBar.setMessage("Please wait while fetching your complaints..");
        loadingBar.show();
        headingComplaint=(TextView)findViewById(R.id.complaints_heading);
        complaintLayout=(LinearLayout)findViewById(R.id.complaint_layout);
        myAuth=FirebaseAuth.getInstance();
        String uid=myAuth.getCurrentUser().getUid();
        rootRef= FirebaseDatabase.getInstance().getReference().child("Complaints").child(uid);
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    headingComplaint.setText("Complaints List");
                    showComplaints(dataSnapshot);
                }
                else
                {
                    headingComplaint.setText("No Complaints Found...");
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showComplaints(DataSnapshot dataSnapshot) {
        for (DataSnapshot child: dataSnapshot.getChildren()) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View _itemRow = inflater.inflate(R.layout.new_row,null);
            final String track_id=child.getKey();
            String additional=child.child("Additional").getValue().toString();
            final String address=child.child("Address").getValue().toString();
            final String category=child.child("Category").getValue().toString();
            final String date_time=child.child("Date").getValue().toString()+" , "+child.child("Time").getValue().toString();
            final String victim=child.child("Victim").getValue().toString();
            ((TextView)_itemRow.findViewById(R.id.tracking_id)).setText(track_id.substring(track_id.length()-4));
            ((TextView)_itemRow.findViewById(R.id.complaint_category_name)).setText(category);
            ((TextView)_itemRow.findViewById(R.id.date_time)).setText(date_time);
            ((TextView)_itemRow.findViewById(R.id.tracking_click)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(ComplaintLists.this,TrackComplaintActivity.class);
                    intent.putExtra("track_id",track_id.substring(track_id.length()-4));
                    intent.putExtra("address",address);
                    intent.putExtra("category",category);
                    intent.putExtra("date_time",date_time);
                    intent.putExtra("victim",victim);
                    startActivity(intent);
                }
            });
            complaintLayout.addView(_itemRow);
            loadingBar.dismiss();
        }
    }
}
