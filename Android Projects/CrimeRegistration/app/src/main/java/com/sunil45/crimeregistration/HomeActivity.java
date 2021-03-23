package com.sunil45.crimeregistration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth myAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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
            sendUserToMainActivity();
}
    private void sendUserToMainActivity() {
        startActivity(new Intent(HomeActivity.this,MainActivity.class));
        finish();
    }
}
