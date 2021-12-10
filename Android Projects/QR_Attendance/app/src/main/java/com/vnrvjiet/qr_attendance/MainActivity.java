package com.vnrvjiet.qr_attendance;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private View addView;
    private EditText email, password;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addView = LayoutInflater.from(this).inflate(R.layout.add_credentials, null);
        email = addView.findViewById(R.id.add_email);
        password = addView.findViewById(R.id.add_password);
        builder = new AlertDialog.Builder(this).setView(addView);
    }

    public void teacherLogin(View view) {
        builder.setMessage("Admin Login").setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).show();
    }

    public void adminLogin(View view) {
    }

    public void studentLogin(View view) {
    }
}