package com.sunil45.crimeregistration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth myAuth;
    private FirebaseUser currentUser;
    public static SharedPreferences sharedPreferences;
    public static final String MY_SHARED_PREF = "myPreferences";
    public static final String HAS_PROFILE = "profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sharedPreferences = getSharedPreferences(MY_SHARED_PREF, MODE_PRIVATE);
        ActivityCompat.requestPermissions(this,new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.SEND_SMS}, 1);
        myAuth = FirebaseAuth.getInstance();
        currentUser = myAuth.getCurrentUser();
        findViewById(R.id.enter_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, EnterHelpActivity.class));
            }
        });
    }

    protected void onStart() {
        super.onStart();
        if(currentUser==null)
            sendUserToActivity(MainActivity.class);
        else if(!getRegistered())
            sendUserToActivity(ProfileActivity.class);
    }
    private void sendUserToActivity(Class cls) {
        startActivity(new Intent(HomeActivity.this, cls));
        finish();
    }
    private boolean getRegistered(){
        return sharedPreferences.getBoolean(HAS_PROFILE, false);
    }
}
