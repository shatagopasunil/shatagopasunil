package com.sunil45.crimeregistration;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class FileComplaintActivity extends AppCompatActivity {
    private Spinner spinner;
    private FirebaseAuth myAuth;
    private DatabaseReference rootRef;
    private String date,time,categoryComplaint,ampm;
    private Calendar c;
    private EditText otherCategory,address,mVictim,addInfo;
    private Button dateSelector,timeSelector;
    private int mYear,mMonth,mDay,mHour,mMinute;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_complaint);
        c=Calendar.getInstance();
        myAuth=FirebaseAuth.getInstance();
        String uid=myAuth.getCurrentUser().getUid();
        otherCategory=(EditText)findViewById(R.id.other_category);
        addInfo=(EditText)findViewById(R.id.add_info);
        rootRef= FirebaseDatabase.getInstance().getReference().child("Complaints").child(uid).push();
        loadingBar = new ProgressDialog(this);
        address=(EditText)findViewById(R.id.address);
        spinner=(Spinner)findViewById(R.id.category_complaint);
        dateSelector=(Button) findViewById(R.id.date_selector);
        timeSelector=(Button) findViewById(R.id.time_selector);
        mVictim=(EditText)findViewById(R.id.victim);
        dateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog=new DatePickerDialog(FileComplaintActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        date=i2+"/"+i1+"/"+i;
                        dateSelector.setText(date);
                    }
                },
                        mYear = c.get(Calendar.YEAR),
                        mMonth = c.get(Calendar.MONTH),
                        mDay = c.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        timeSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog=new TimePickerDialog(FileComplaintActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        ampm = (i < 12) ? "AM" : "PM";
                        if(ampm.equals("PM"))
                            i-=12;
                        if(i==0)
                            i=12;
                        if(i1<10)
                            time=i+":0"+i1+" "+ampm;
                        else
                            time=i+":"+i1+" "+ampm;
                        timeSelector.setText(time);
                    }
                },
                        mHour = c.get(Calendar.HOUR_OF_DAY),
                        mMinute = c.get(Calendar.MINUTE),false);
                timePickerDialog.show();
            }
        });
        findViewById(R.id.complaint_file_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitComplaint();
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getSelectedItem().toString().equals("Others"))
                    otherCategory.setVisibility(View.VISIBLE);
                else
                    otherCategory.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void submitComplaint() {
        String add=address.getText().toString();
        String category=otherCategory.getText().toString();
        String addInformation=addInfo.getText().toString();
        categoryComplaint = spinner.getSelectedItem().toString();
        String victim=mVictim.getText().toString();
        if(categoryComplaint.equals("Others")) {
            if(category.isEmpty())
                Toast.makeText(this, "Enter Category", Toast.LENGTH_SHORT).show();
            else
            categoryComplaint = category;
        }
        if(categoryComplaint.equals("--Select--"))
            Toast.makeText(this, "Select Category of Complaint", Toast.LENGTH_SHORT).show();
        else if(categoryComplaint.equals("Others") && category.equals(""))
                Toast.makeText(this, "Enter Category", Toast.LENGTH_SHORT).show();
        else if(dateSelector.getText().equals("Select Date"))
            Toast.makeText(this, "Select Date", Toast.LENGTH_SHORT).show();
        else if(timeSelector.getText().equals("Select Time"))
            Toast.makeText(this, "Select Time", Toast.LENGTH_SHORT).show();
        else if(add.isEmpty())
            Toast.makeText(this, "Enter Address", Toast.LENGTH_SHORT).show();
        else if(add.length()<10)
            Toast.makeText(this, "Address must be atleast of length 10", Toast.LENGTH_SHORT).show();
        else
        {
            loadingBar.setCancelable(false);
            loadingBar.show();
            loadingBar.setMessage("Please wait while filing your complaint");
            rootRef.child("Category").setValue(categoryComplaint);
            rootRef.child("Date").setValue(date);
            rootRef.child("Time").setValue(time);
            rootRef.child("Victim").setValue(victim);
            rootRef.child("Address").setValue(add);
            rootRef.child("Additional").setValue(addInformation);
            loadingBar.dismiss();
            Toast.makeText(this, "Data Submitted Successfully....", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(FileComplaintActivity.this,HomeActivity.class));
        }
    }
}
