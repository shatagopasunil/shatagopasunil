package com.sunil45.crimeregistration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class ViewMembers extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference sosReference;
    private ProgressDialog loadingBar;
    private String name, phone, currentUserId;
    private TextView memberText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_members);
        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);
        currentUserId = mAuth.getCurrentUser().getUid();
        memberText = (TextView) findViewById(R.id.member_text);
        sosReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("SosList");
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadingBar.setMessage("Please wait while fetching your details  ");
        loadingBar.setCancelable(false);
        loadingBar.show();
        sosReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    displayMembers(dataSnapshot);
                else
                    memberText.append("No members found ...\n");
                loadingBar.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        memberText.setText("");
    }

    public void displayMembers(DataSnapshot dataSnapshot) {
        for (DataSnapshot child: dataSnapshot.getChildren()) {
            memberText.append("--> "+child.getKey()+"  :  "+child.getValue()+"\n\n");
        }
    }
}
