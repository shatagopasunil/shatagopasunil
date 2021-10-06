package com.sunil45.crimeregadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private EditText adminName, adminPassword;
    private SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeFields();
    }

    private void initializeFields() {
        sharedPreferenceManager = new SharedPreferenceManager(this);
        adminName = findViewById(R.id.admin_id);
        adminPassword = findViewById(R.id.admin_password);
    }

    public void onLogin(View view) {
        String name = adminName.getText().toString().trim();
        String password = adminPassword.getText().toString().trim();
        if(name.isEmpty()){
            Toast.makeText(this, "Enter Id", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.isEmpty()){
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!name.equals("12345") || !password.equals("admin")){
            Toast.makeText(this, "Enter correct credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Register Successful", Toast.LENGTH_SHORT).show();
        sharedPreferenceManager.putRegister(true);
        sendAdminToMainActivity();

    }

    private void sendAdminToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}