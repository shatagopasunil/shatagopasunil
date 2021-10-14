package com.sunil45.crimeregistration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
        headingComplaint = (TextView) findViewById(R.id.complaints_heading);
        complaintLayout = (LinearLayout) findViewById(R.id.complaint_layout);
        myAuth = FirebaseAuth.getInstance();
        String uid = myAuth.getCurrentUser().getUid();
        final ArrayList<String> arrayList = new ArrayList<>();
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.keepSynced(true);
        rootRef.child("Users").child(uid).child("Complaints").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long cnt = 0, tcount = dataSnapshot.getChildrenCount();
                if (!dataSnapshot.exists()) {
                    headingComplaint.setText("No Complaints Found...");
                    loadingBar.dismiss();
                    return;
                }
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ++cnt;
                    arrayList.add(snapshot.getKey());
                    if (cnt == tcount) {
                        headingComplaint.setText("Complaints List");
                        rootRef.child("Complaints").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (String s : arrayList) {
                                    showComplaints(dataSnapshot, s);
                                }
                                loadingBar.dismiss();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void showComplaints(DataSnapshot dataSnapshot, String s) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View _itemRow = inflater.inflate(R.layout.new_row, null);
        String track_id = s;
        final ComplaintsModel complaintsModel = dataSnapshot.child(s).getValue(ComplaintsModel.class);
        track_id = track_id.substring(track_id.length() - 4);
        final String finalTrack = track_id;
        ((TextView) _itemRow.findViewById(R.id.tracking_id)).setText(track_id);
        ((TextView) _itemRow.findViewById(R.id.complaint_category_name)).setText(complaintsModel.getCategory());
        ((TextView) _itemRow.findViewById(R.id.date_time)).setText(complaintsModel.getTime() + ", " + complaintsModel.getDate());
        ((TextView) _itemRow.findViewById(R.id.tracking_click)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ComplaintLists.this, TrackComplaintActivity.class);
                intent.putExtra("complaint", (Serializable) complaintsModel);
                intent.putExtra("track_id", finalTrack);
                startActivity(intent);
            }
        });
        complaintLayout.addView(_itemRow);
    }
}
