package com.sunil45.crimeregistration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class ProfileActivity extends AppCompatActivity {
    private RadioGroup rGroup;
    private RadioButton rButton;
    private EditText pFullName, pEmail, pAge, vAadhar;
    private FirebaseAuth myAuth;
    private DatabaseReference rootRef, aadharReference;
    private String sName, sEmail, sAge;
    private ProgressDialog loadingBar;
    private Button verifyAadhar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        myAuth = FirebaseAuth.getInstance();
        String uid = myAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        aadharReference = FirebaseDatabase.getInstance().getReference().child("AadharCards");
        pFullName = (EditText) findViewById(R.id.name);
        pEmail = (EditText) findViewById(R.id.email);
        vAadhar = (EditText) findViewById(R.id.aadhar_no);
        pAge = (EditText) findViewById(R.id.age);
        verifyAadhar = (Button) findViewById(R.id.verify_aadhar);
        rGroup = (RadioGroup) findViewById(R.id.rGender);
        loadingBar = new ProgressDialog(this);
        verifyAadhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String aadhar = vAadhar.getText().toString();
                if (aadhar.length() != 12) {
                    Toast.makeText(ProfileActivity.this, "Enter valid aadhar number", Toast.LENGTH_SHORT).show();
                    return;
                }
                loadingBar.setCancelable(false);
                loadingBar.setMessage("Verifying Aadhar....");
                loadingBar.show();
                aadharReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                            checkAadhar(dataSnapshot, aadhar);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            private void checkAadhar(DataSnapshot dataSnapshot, String aadhar) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    String adhar = (String) ((DataSnapshot) iterator.next()).getKey();
                    if (adhar.equals(aadhar)) {
                        verifyAadhar.setText("Verified!");
                        vAadhar.setCursorVisible(false);
                        vAadhar.setText(adhar);
                        vAadhar.setKeyListener(null);
                        vAadhar.setEnabled(false);
                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(vAadhar.getWindowToken(), 0);
                        verifyAadhar.setEnabled(false);
                        findViewById(R.id.verify_tick).setVisibility(View.VISIBLE);
                        Toast.makeText(ProfileActivity.this, "Aadhar Verified Successfully...", Toast.LENGTH_SHORT).show();
                    } else {
                        vAadhar.setText("");
                        Toast.makeText(ProfileActivity.this, "Enter valid aadhar number", Toast.LENGTH_SHORT).show();
                    }
                    loadingBar.dismiss();

                }
            }
        });
        findViewById(R.id.submit_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sName = pFullName.getText().toString();
                sEmail = pEmail.getText().toString();
                sAge = pAge.getText().toString();
                if (sName.isEmpty())
                    pFullName.setError("Enter Full Name");
                else if (sName.length() < 2)
                    Toast.makeText(ProfileActivity.this, "Enter Correct Full Name", Toast.LENGTH_SHORT).show();
                else if (sEmail.isEmpty())
                    pEmail.setError("Enter Email Id");
                else if (validateEmail(sEmail) == false)
                    Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
                else if (sAge.isEmpty())
                    pAge.setError("Enter Age");
                else if (validateAge(sAge) == false)
                    Toast.makeText(ProfileActivity.this, "Age must be above or equal to 18", Toast.LENGTH_SHORT).show();
                else if (verifyAadhar.isEnabled() == true)
                    Toast.makeText(ProfileActivity.this, "Please Verify Aadhar", Toast.LENGTH_SHORT).show();
                else {
                    loadingBar.setMessage("Please Wait..");
                    loadingBar.setCancelable(false);
                    loadingBar.show();
                    int selectedId = rGroup.getCheckedRadioButtonId();
                    rButton = (RadioButton) findViewById(selectedId);
                    rootRef.child("Name").setValue(sName);
                    rootRef.child("Email").setValue(sEmail);
                    rootRef.child("Age").setValue(sAge);
                    rootRef.child("Gender").setValue(rButton.getText());
                    loadingBar.dismiss();
                    sendUserToMainActivity();
                }
            }

            private void sendUserToMainActivity() {
                Toast.makeText(ProfileActivity.this, "Welcome..", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }

            private boolean validateAge(String sAge) {
                int age = Integer.parseInt(sAge);
                if (age <= 17)
                    return false;
                else
                    return true;
            }

            private boolean validateEmail(String sEmail) {
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if (!sEmail.trim().matches(emailPattern))
                    return false;
                else
                    return true;
            }

        });
    }

}
