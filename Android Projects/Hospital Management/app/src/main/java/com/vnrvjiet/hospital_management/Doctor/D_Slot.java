package com.vnrvjiet.hospital_management.Doctor;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vnrvjiet.hospital_management.DatabaseHelper;
import com.vnrvjiet.hospital_management.Message;
import com.vnrvjiet.hospital_management.R;

import java.util.ArrayList;


public class D_Slot extends AppCompatActivity {

    Spinner start;
    String s, e, username, password, user_type;
    TextView tvs,tve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_slot);

        Bundle bb = getIntent().getExtras();
        username = bb.getString("username");
        password = bb.getString("password");
        user_type = bb.getString("user_type");

        start = (Spinner) findViewById(R.id.start_time);
        tvs = (TextView) findViewById(R.id.tv_current_slot_s);
        tve = (TextView) findViewById(R.id.tv_current_slot_e);

        DatabaseHelper db = new DatabaseHelper(this);
        Cursor y = db.checkduplicates_in_user_credentials(username, password, getResources().getString(R.string.doctor_slot));
        if (y.moveToFirst()){
            tvs.setText(y.getString(3));
            tve.setText(y.getString(4));
        }

        ArrayList<String> hour = new ArrayList<>();

        hour.add("Morning 9 - 12");
        hour.add("Afternoon 1 - 4");
        hour.add("Evening 5 - 9");

        ArrayAdapter<String> adapterh = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hour);
        adapterh.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        start.setAdapter(adapterh);
    }

    public void onClick(View view) {
        int idx1 = start.getSelectedItemPosition();

        if(idx1 == 0){
            s = "9 AM";
            e = "12 PM";
        }else if(idx1 == 1){
            s = "1 PM";
            e = "4 PM";
        }else{
            s = "5 PM";
            e = "9 PM";
        }
        tvs.setText(s);
        tve.setText(e);

        if(idx1 == 1){
            s = "13 PM";
            e = "16 PM";
        }else if(idx1 == 2){
            s = "17 PM";
            e = "21 PM";
        }

        DatabaseHelper db = new DatabaseHelper(this);
        Cursor y = db.checkduplicates_in_user_credentials(username, password, getResources().getString(R.string.doctor_slot));

        if (y.moveToFirst()) {
            boolean b = db.update_slot(username, password, y.getString(2), s, e, "Y");
            if (b) {
                Message.message(D_Slot.this, "Your Slot Has been Updated");
            } else {
                Message.message(D_Slot.this, "Some Error Occured, Try Again");
            }
        } else {
            boolean b = db.insert_slot(username, password, "", s, e, "Y");
            if (b) {
                Message.message(D_Slot.this, "Your Slot Has been Inserted");
            } else {
                Message.message(D_Slot.this, "Some Error Occured, Try Again");
            }
        }

        db.close();
    }
}
