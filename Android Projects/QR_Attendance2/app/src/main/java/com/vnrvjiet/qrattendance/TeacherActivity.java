package com.vnrvjiet.qrattendance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

public class TeacherActivity extends AppCompatActivity {
    private Spinner selectSubject;
    private String[] courses = { "Maths", "Physics"};
    private Button markAttendance, viewAttendance, addStudent;
    private String key;
    private TextView teacherName;
    private DatabaseReference reference, tempRef, teacherRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
        key = getIntent().getStringExtra("key");
        selectSubject = findViewById(R.id.select_subject);
        markAttendance = findViewById(R.id.mark_attendance);
        viewAttendance = findViewById(R.id.view_attendance);
        addStudent = findViewById(R.id.add_students);
        teacherName = findViewById(R.id.teacher_name);
        teacherRef = FirebaseDatabase.getInstance().getReference().child("Teacher").child(key);
        tempRef = teacherRef.child("Attendance");;
        teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                teacherName.append(snapshot.child("name").getValue(String.class));
            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });
        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addingStudent();
            }
        });
        ArrayAdapter ad
                = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                courses);
        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        selectSubject.setAdapter(ad);
        markAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference = tempRef.child(selectSubject.getSelectedItem().toString());
                openScanner();
            }
        });
        viewAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference = tempRef.child(selectSubject.getSelectedItem().toString());
                getAttendance();
            }
        });
    }

    private void addingStudent() {
        View addView = LayoutInflater.from(this).inflate(R.layout.add_credentials, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(addView)
                .setMessage("Add Student")
                .setCancelable(true)
                .setPositiveButton("Add", null).setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
        EditText emailText = addView.findViewById(R.id.add_email), passText = addView.findViewById(R.id.add_password);
        emailText.setHint("Enter name");
        passText.setHint("Enter rollno");
        passText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String e = emailText.getText().toString().trim();
                String p = passText.getText().toString().trim();
                if(e.isEmpty()){
                    emailText.setError("Enter name");
                    return;
                }
                if(p.isEmpty()){
                    passText.setError("Enter rollno");
                    return;
                }
                teacherRef.child("Students").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            if(dataSnapshot.getKey().equals(p)){
                                Toast.makeText(TeacherActivity.this, "Student already exists", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                                return;
                            }
                        }
                        teacherRef.child("Students").child(p).setValue(e).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(TeacherActivity.this, "Student added successfully", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(TeacherActivity.this, "Something went wrong. Try again", Toast.LENGTH_SHORT).show();
                                }
                                dialog.cancel();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });
            }
        });
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }

    private void getAttendance() {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String currTime = df.format(Calendar.getInstance().getTime());
        HashSet<String> hashSet = new HashSet<>();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    Toast.makeText(TeacherActivity.this, "No attendance marked today.", Toast.LENGTH_SHORT).show();
                    return;
                }
                long cnt = 0, tot = snapshot.getChildrenCount();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(!dataSnapshot.getKey().equals("time"))
                        hashSet.add(dataSnapshot.getValue(String.class));
                    ++cnt;
                    if(cnt == tot){
                        showAttendance(hashSet);
                    }
                }
            }
            @Override
            public void onCancelled( DatabaseError error) {

            }
        });
    }

    private void showAttendance(HashSet<String> hashSet) {
        ArrayList<String> pr = new ArrayList<>(), ab = new ArrayList<>();
        teacherRef.child("Students").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                long cnt = 0, tot = snapshot.getChildrenCount();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ++cnt;
                    if(hashSet.contains(dataSnapshot.getKey())){
                        pr.add(dataSnapshot.getKey());
                    }else{
                        ab.add(dataSnapshot.getKey());
                    }
                    if(cnt == tot){
                        displayAtt(pr, ab);
                    }
                }
            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });
    }

    private void displayAtt(ArrayList<String> pr, ArrayList<String> ab) {
        int p = pr.size(), a = ab.size();
        View view = LayoutInflater.from(this).inflate(R.layout.display_attendance, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(view)
                .setCancelable(true);
        TextView present = view.findViewById(R.id.present_att), absent = view.findViewById(R.id.absent_att), total = view.findViewById(R.id.total_att);
        total.append("" + (p + a));
        present.append("" + p + "\n\n");
        absent.append("" + a + "\n\n");
        for(String s : pr){
            present.append(s + "\n");
        }
        for(String s : ab){
            absent.append(s + "\n");
        }
        builder.show();
    }

    private void openScanner() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(intentResult.getContents() != null){
            updateResults(intentResult.getContents());
        }
    }

    private void updateResults(String s) {
        String[] results = s.split(" ");
        teacherRef.child("Students").child(results[0]).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                if(!snapshot.child("time").getValue(String.class).equals(results[2])) {
                                    reference.removeValue();
                                }else if(snapshot.child(results[1]).exists()){
                                    Toast.makeText(TeacherActivity.this, "Attendance already marked", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                            addAttendance(results);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else{
                    Toast.makeText(TeacherActivity.this, "Invalid roll no", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });
    }

    private void addAttendance(String[] results) {
        reference.child("time").setValue(results[2]).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    reference.child(results[1]).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                Toast.makeText(TeacherActivity.this, "Attendance marked already", Toast.LENGTH_SHORT).show();
                            }else{
                                reference.child(results[1]).setValue(results[0]).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(TeacherActivity.this, "Attendance marked successfully", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(TeacherActivity.this, "Something went wrong. Try again", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else{
                    Toast.makeText(TeacherActivity.this, "Something went wrong. Try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}