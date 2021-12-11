package com.vnrvjiet.qrattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reference = FirebaseDatabase.getInstance().getReference();
    }

    public void teacherLogin(View view) {
        checkLogin("Teacher");
    }

    public void adminLogin(View view) {
        checkLogin("Admin");
    }

    private void checkLogin(String teacher) {
        View addView = LayoutInflater.from(this).inflate(R.layout.add_credentials, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(addView)
                .setMessage(teacher + " Login")
                .setCancelable(true)
                .setPositiveButton("Login", null).setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText emailText = addView.findViewById(R.id.add_email), passText = addView.findViewById(R.id.add_password);
                String e = emailText.getText().toString().trim();
                String p = passText.getText().toString().trim();
                if(e.isEmpty()){
                    emailText.setError("Enter email");
                    return;
                }
                if(p.isEmpty()){
                    passText.setError("Enter password");
                    return;
                }
                reference.child(teacher).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        long i = 0, cnt = snapshot.getChildrenCount();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            ++i;
                            if(dataSnapshot.child("email").getValue(String.class).equals(e)){
                                if(dataSnapshot.child("password").getValue(String.class).equals(p)){
                                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                    if(teacher.equals("Admin"))
                                        sendUserToActivity(AdminActivity.class, "");
                                    else
                                        sendUserToActivity(TeacherActivity.class, dataSnapshot.getKey());
                                }else{
                                    Toast.makeText(MainActivity.this, "Enter correct password", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }
                            if(i == cnt){
                                Toast.makeText(MainActivity.this, "Enter valid email address", Toast.LENGTH_SHORT).show();
                            }
                        }
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


    private void sendUserToActivity(Class cls, String s) {
        Intent intent = new Intent(this, cls);
        if(!s.isEmpty()){
            intent.putExtra("key", s);
        }
        startActivity(intent);
    }

    public void studentLogin(View view) {
        sendUserToActivity(StudentActivity.class, "");
    }
}