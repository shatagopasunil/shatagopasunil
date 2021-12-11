package com.vnrvjiet.qrattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AdminActivity extends AppCompatActivity {
    private Button addTeacher;
    private RecyclerView teacherRecycler;
    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        addTeacher = findViewById(R.id.addTeacher);
        teacherRecycler = findViewById(R.id.teacher_recycler);
        teacherRecycler.setHasFixedSize(true);
        teacherRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        reference = FirebaseDatabase.getInstance().getReference().child("Teacher");
        settingAdapter();
        addTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1 = LayoutInflater.from(AdminActivity.this).inflate(R.layout.add_credentials, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this).setCancelable(true)
                        .setPositiveButton("Add", null)
                        .setNegativeButton("Cancel", null)
                        .setView(view1);
                EditText name = view1.findViewById(R.id.add_name), pass = view1.findViewById(R.id.add_password), email = view1.findViewById(R.id.add_email);
                name.setVisibility(View.VISIBLE);
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String e = email.getText().toString().trim(), n = name.getText().toString().trim(), p = pass.getText().toString().trim();
                        if(e.isEmpty()){
                            email.setError("Enter email");
                            return;
                        }
                        if(p.isEmpty()){
                            pass.setError("Enter password");
                            return;
                        }
                        if(n.isEmpty()){
                            name.setError("Enter name");
                            return;
                        }
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("name", n);
                        hashMap.put("password", p);
                        hashMap.put("email", e);
                        reference.push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull  Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(AdminActivity.this, "Teacher added successfully", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(AdminActivity.this, "Something went wrong. Try Again", Toast.LENGTH_SHORT).show();
                                }
                                dialog.cancel();
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
        });
    }

    private void settingAdapter() {
        FirebaseRecyclerOptions<TeacherModel> options = new FirebaseRecyclerOptions.Builder<TeacherModel>().setQuery(reference, TeacherModel.class).build();
        TeacherAdapter adapter = new TeacherAdapter(options);
        teacherRecycler.setAdapter(adapter);
        adapter.startListening();
    }
}