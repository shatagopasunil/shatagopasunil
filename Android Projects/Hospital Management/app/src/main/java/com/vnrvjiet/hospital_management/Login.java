package com.vnrvjiet.hospital_management;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.vnrvjiet.hospital_management.Desktop_Admin.Desktop_Admin;
import com.vnrvjiet.hospital_management.Doctor.Doctor;
import com.vnrvjiet.hospital_management.Patient.Patient;
import com.vnrvjiet.hospital_management.Staff_Member.Staff_Member;

public class Login extends AppCompatActivity {

    EditText username, password;
    String usernames, passwords;
    Button Bregister, Blogin;
    DatabaseHelper dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        username = (EditText) findViewById(R.id.etusername);
        password = (EditText) findViewById(R.id.etpassword);
        Bregister = (Button) findViewById(R.id.bregister);
        Blogin = (Button) findViewById(R.id.blogin);
        dbh = new DatabaseHelper(this);

        Bregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, Register.class);
                startActivity(i);
            }
        });

        Blogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernames = username.getText().toString();
                passwords = password.getText().toString();

                Cursor y = dbh.checkduplicates_in_user_credentials(usernames, passwords, getResources().getString(R.string.user_credentials));

                if (y.moveToFirst()) {
                    String ut = y.getString(7);
                    Message.message(Login.this, "Welcome");

                    Bundle b = new Bundle();
                    b.putString("username", usernames);
                    b.putString("password", passwords);
                    b.putString("user_type", ut);

                    Intent i;
                    if (ut.equals("Doctor")) {
                        i = new Intent(Login.this, Doctor.class);
                    } else if (ut.equals("Patient")) {
                        i = new Intent(Login.this, Patient.class);
                    } else if (ut.equals("Staff Member")) {
                        i = new Intent(Login.this, Staff_Member.class);
                    } else {
                        i = new Intent(Login.this, Desktop_Admin.class);
                    }
                    i.putExtras(b);
                    startActivity(i);
                } else {
                    Message.message(Login.this, "No Such User exist! Please Register Your self");
                }
                y.close();
            }
        });
        dbh.close();
    }
}
