package com.vnrvjiet.attendancemarker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class ViewAttendanceActivity extends AppCompatActivity {
    private Switch toggleButton;
    private TextView addRollNo;
    private String subject, cls;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);
        subject = getIntent().getStringExtra("subject");
        cls = getIntent().getStringExtra("class");
        reference = FirebaseDatabase.getInstance().getReference().child("Classes").child(cls).child(subject);
        toggleButton = findViewById(R.id.toggle_button);
        addRollNo = findViewById(R.id.attendance_list);
        toggleButton.setChecked(getIntent().getBooleanExtra("status", false));
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                fun(b);
            }
        });
        reference.child("Attendance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    addRollNo.setText("No attendance found");
                    return;
                }
                addRollNo.setText("");
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    addRollNo.append(dataSnapshot.getKey() + "\n");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fun(boolean b) {
        String s = (b ? "active" : "inactive");
        reference.child("status").setValue(s).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ViewAttendanceActivity.this, "Status has been changed to " + s, Toast.LENGTH_SHORT).show();
            }
        });
    }
}