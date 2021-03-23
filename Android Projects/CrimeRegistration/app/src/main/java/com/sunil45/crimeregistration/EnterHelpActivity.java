package com.sunil45.crimeregistration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class EnterHelpActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_help);
        findViewById(R.id.file_complaint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EnterHelpActivity.this,FileComplaintActivity.class));
            }
        });
        findViewById(R.id.track_status).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EnterHelpActivity.this,ComplaintLists.class));
            }
        });
        findViewById(R.id.sos_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EnterHelpActivity.this,SosActivity.class));
            }
        });
    }
}
