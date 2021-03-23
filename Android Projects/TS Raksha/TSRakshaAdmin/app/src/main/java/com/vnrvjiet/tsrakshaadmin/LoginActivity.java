package com.vnrvjiet.tsrakshaadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password, name;
    private Button login;
    private DatabaseReference adminLogin;
    private LoadingBar loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeFields();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String e = email.getText().toString().trim();
                final String p = password.getText().toString();
                final String n = name.getText().toString().trim();
                if(n.isEmpty())
                    name.setError("Enter name");
                else if(e.isEmpty())
                    email.setError("Enter email");
                else if(!validateEmail(e))
                    email.setError("Enter valid email");
                else if(p.isEmpty())
                    password.setError("Enter password");
                else if(!CheckInternetConnection.isNetworkConnected(LoginActivity.this))
                    Toast.makeText(LoginActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                else
                {
                    loadingBar.showLoadingBar(1);
                    adminLogin.child(n).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists())
                            {
                                if(snapshot.child("email").getValue().toString().equals(e)) {
                                    if (snapshot.child("password").getValue().toString().equals(p)) {
                                        loadingBar.dismissLoadingBar();
                                        sendUserToMainActivity();
                                    } else {
                                        loadingBar.dismissLoadingBar();
                                        Toast.makeText(LoginActivity.this, "Enter correct password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else
                                {
                                    loadingBar.dismissLoadingBar();
                                    Toast.makeText(LoginActivity.this, "Enter valid email address as per registered", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                loadingBar.dismissLoadingBar();
                                Toast.makeText(LoginActivity.this, "Enter name as per registered", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            loadingBar.dismissLoadingBar();
                            Toast.makeText(LoginActivity.this, "Error ! Try Again", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void sendUserToMainActivity() {
        SharedPreferences.Editor editor = MainActivity.sharedPreferences.edit();
        editor.putBoolean(MainActivity.HasLogin,true);
        editor.commit();
        Intent homeIntent = new Intent(LoginActivity.this,MainActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
    }

    private void initializeFields() {
        email = findViewById(R.id.enter_email);
        password = findViewById(R.id.enter_password);
        name = findViewById(R.id.enter_name);
        login = findViewById(R.id.login_button);
        loadingBar = new LoadingBar(LoginActivity.this);
        adminLogin = FirebaseDatabase.getInstance().getReference().child("AdminApp").child("Logins");
    }
    private boolean validateEmail(String sEmail) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (!sEmail.trim().matches(emailPattern))
            return false;
        else
            return true;
    }
}
