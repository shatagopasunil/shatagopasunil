package com.vnrvjiet.attendancemarker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TeacherActivity extends AppCompatActivity {
    private Button addClass, viewAttendance;
    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
        addClass = findViewById(R.id.add_class);
        viewAttendance = findViewById(R.id.view_attendance);
        reference = FirebaseDatabase.getInstance().getReference();
        addClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                View view = LayoutInflater.from(TeacherActivity.this).inflate(R.layout.add_class_view, null);
                Spinner selectClass = view.findViewById(R.id.select_class);
                EditText enterSubject = view.findViewById(R.id.subject_name);
                reference.child("Locations").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long i = 0, cnt = snapshot.getChildrenCount();
                        ArrayList<String> arrayList = new ArrayList<>();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            ++i;
                            arrayList.add(dataSnapshot.getKey());
                            if(i == cnt){
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                        TeacherActivity.this, android.R.layout.simple_spinner_item, arrayList);
                                selectClass.setAdapter(adapter);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                AlertDialog.Builder builder = new AlertDialog.Builder(TeacherActivity.this);
                builder.setView(view).setCancelable(true).setMessage("Add Class")
                        .setPositiveButton("Add", null).setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String subName = enterSubject.getText().toString().trim();
                        if(subName.isEmpty()){
                            enterSubject.setError("Enter subject name");
                            return;
                        }
                        Toast.makeText(TeacherActivity.this, "Please wait", Toast.LENGTH_SHORT).show();
                        reference.child("Classes").child(selectClass.getSelectedItem().toString())
                                .child(subName).child("status").setValue("Inactive").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(TeacherActivity.this, "Subject added successfully", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                    }
                });
            }
        });
        viewAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                ArrayList<String> arrayList = new ArrayList<>();
                View view = LayoutInflater.from(TeacherActivity.this).inflate(R.layout.view_attendance_layout, null);
                Spinner selectClass = view.findViewById(R.id.select_class);
                Spinner selectSubject = view.findViewById(R.id.select_subject);
                reference.child("Classes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long i = 0, cnt = snapshot.getChildrenCount();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            ++i;
                            arrayList.add(dataSnapshot.getKey());
                            if(i == cnt){
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                        TeacherActivity.this, android.R.layout.simple_spinner_item, arrayList);
                                selectClass.setAdapter(adapter);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                selectClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        Toast.makeText(TeacherActivity.this, "Please wait", Toast.LENGTH_SHORT).show();
                        reference.child("Classes").child(arrayList.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ArrayList<String> classes = new ArrayList<>();
                                long i = 0, cnt = snapshot.getChildrenCount();
                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    ++i;
                                    classes.add(dataSnapshot.getKey());
                                    if(i == cnt){
                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                                TeacherActivity.this, android.R.layout.simple_spinner_item, classes);
                                        selectSubject.setAdapter(adapter);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                AlertDialog.Builder builder = new AlertDialog.Builder(TeacherActivity.this);
                builder.setView(view).setCancelable(true).setMessage("View Attendance")
                        .setPositiveButton("View", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String cls = selectClass.getSelectedItem().toString(), subject = selectSubject.getSelectedItem().toString();
                                reference.child("Classes").child(cls).child(subject).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                                        boolean status = snapshot.child("status").getValue(String.class).equals("active");
                                        startActivity(new Intent(TeacherActivity.this, ViewAttendanceActivity.class).putExtra("subject", subject)
                                                .putExtra("class", cls).putExtra("status", status));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull  DatabaseError error) {

                                    }
                                });
                            }
                        }).setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }
}