package com.sunil45.crimeregadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeFields();
    }

    private void initializeFields() {
        sharedPreferenceManager = new SharedPreferenceManager(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!sharedPreferenceManager.getRegister()){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    public void showComplaints(View view) {
        startActivity(new Intent(this, ShowComplaintsActivity.class));
    }

    public void showReports(View view) {
        startActivity(new Intent(this, ShowReportsActivity.class));
    }
}