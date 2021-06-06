package com.vad.vad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    private Button user, register;
    private SharedPreferenceManager sharedPreferenceManager;
    private DatabaseReference keyRef, licenceRef, flagRef;
    private ValueEventListener listener;
    private int id = 0;
    private TextView adminName, adminLicence;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initializeFields();
        setUserNameLicence();
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, UsersActivity.class);
                startActivity(intent);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyRef.child("Users").child("Admin").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(HomeActivity.this, "User already exist", Toast.LENGTH_SHORT).show();
                        } else {
                            View view1 = LayoutInflater.from(HomeActivity.this).inflate(R.layout.register_layout, null);
                            EditText userName = view1.findViewById(R.id.user_name);
                            EditText userLicence = view1.findViewById(R.id.user_licence);
                            TextView note = view1.findViewById(R.id.show_finger_note);
                            Button save = view1.findViewById(R.id.save_user_btn);
                            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                            builder.setView(view1);
                            alertDialog = builder.create();
                            alertDialog.show();
                            save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String name = userName.getText().toString().trim();
                                    String licence = userLicence.getText().toString();
                                    if (save.getText().equals("Save")) {
                                        save.setEnabled(false);
                                        Toast.makeText(HomeActivity.this, "Please wait while saving your information....", Toast.LENGTH_SHORT).show();
                                        flagRef.removeEventListener(listener);
                                        flagRef.child("id").setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    flagRef.child("take").setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                flagRef.child("status").setValue("Please put fingerprint on fingerprint sensor").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            HashMap<String, Object> hashMap = new HashMap<>();
                                                                            hashMap.put("name", name);
                                                                            hashMap.put("licence", licence);
                                                                            hashMap.put("id", id);
                                                                            hashMap.put("status", "Active");
                                                                            keyRef.child("Users").child("Admin").updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        boolean[] haveId = new boolean[130];
                                                                                        keyRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                            @Override
                                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                int cnt = (int) snapshot.getChildrenCount(), i = 0;
                                                                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                                                                    haveId[dataSnapshot.child("id").getValue(Integer.class)] = true;
                                                                                                    ++i;
                                                                                                    if (i == cnt) {
                                                                                                        for (i = 1; i < 130; i++) {
                                                                                                            if (haveId[i] == false) {
                                                                                                                break;
                                                                                                            }
                                                                                                        }
                                                                                                        keyRef.child("default").setValue(i).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                keyRef.child("Detect").child(licence).setValue(id).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                    @Override
                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                        alertDialog.dismiss();
                                                                                                                        sharedPreferenceManager.setUserName(name);
                                                                                                                        sharedPreferenceManager.setLicence(licence);
                                                                                                                        Toast.makeText(HomeActivity.this, "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
                                                                                                                        finish();
                                                                                                                        startActivity(getIntent());
                                                                                                                    }
                                                                                                                });
                                                                                                            }
                                                                                                        });
                                                                                                    }
                                                                                                }
                                                                                            }

                                                                                            @Override
                                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                                            }
                                                                                        });
                                                                                    } else
                                                                                        Toast.makeText(HomeActivity.this, "Error Occured! Try again", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                        } else {
                                                                            Toast.makeText(HomeActivity.this, "Error occured ! Try again", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                            } else {
                                                                Toast.makeText(HomeActivity.this, "Error occured ! Try again", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    Toast.makeText(HomeActivity.this, "Error occured ! Try again", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        alertDialog.dismiss();
                                        return;
                                    }
                                    if (name.isEmpty()) {
                                        Toast.makeText(HomeActivity.this, "Enter name", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    if (licence.isEmpty()) {
                                        Toast.makeText(HomeActivity.this, "Enter licence number", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    if (licence.length() != 16) {
                                        Toast.makeText(HomeActivity.this, "Enter valid licence number", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    Toast.makeText(HomeActivity.this, "Please wait while validating your licence", Toast.LENGTH_SHORT).show();
                                    licenceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.child(licence).exists()) {
                                                if (snapshot.child(licence).getValue(String.class).equals(name)) {
                                                    Toast.makeText(HomeActivity.this, "Details Matched... Please wait", Toast.LENGTH_SHORT).show();
                                                    flagRef = keyRef.child("flag");
                                                    flagRef.child("take").setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            userLicence.setEnabled(false);
                                                            userName.setEnabled(false);
                                                            save.setEnabled(false);
                                                            save.setText("Save");
                                                            note.setVisibility(View.VISIBLE);
                                                            listener = flagRef.addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    note.setText(snapshot.child("status").getValue(String.class));
                                                                    id = snapshot.child("id").getValue(Integer.class);
                                                                    if (id != 0) {
                                                                        save.setEnabled(true);
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });
                                                        }
                                                    });
                                                } else {
                                                    Toast.makeText(HomeActivity.this, "Name unmatched", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(HomeActivity.this, "Invalid Licence Number", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void setUserNameLicence() {
        if(sharedPreferenceManager.getLicence().equals("Unregistered")){
            keyRef.child("Users").child("Admin").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        sharedPreferenceManager.setUserName(snapshot.child("name").getValue(String.class));
                        sharedPreferenceManager.setLicence(snapshot.child("licence").getValue(String.class));
                        adminName.append(sharedPreferenceManager.getUserName());
                        adminLicence.append(sharedPreferenceManager.getLicence());
                    }else{
                        adminName.append(sharedPreferenceManager.getUserName());
                        adminLicence.append(sharedPreferenceManager.getLicence());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            adminName.append(sharedPreferenceManager.getUserName());
            adminLicence.append(sharedPreferenceManager.getLicence());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out_option:
                startMainActivity();
                break;
        }
        return  true;
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        Toast.makeText(this, "Sign out successful", Toast.LENGTH_SHORT).show();
        sharedPreferenceManager.setUserReg(false);
        sharedPreferenceManager.setLicence("Unregistered");
        sharedPreferenceManager.setUserName("Unregistered");
        startActivity(intent);
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    private void initializeFields() {
        user = findViewById(R.id.users_btn);
        register = findViewById(R.id.register_btn);
        adminLicence = findViewById(R.id.display_admin_licence);
        adminName = findViewById(R.id.display_admin_name);
        sharedPreferenceManager = new SharedPreferenceManager(HomeActivity.this);
        licenceRef = FirebaseDatabase.getInstance().getReference().child("Licences");
        keyRef = FirebaseDatabase.getInstance().getReference().child("Users").child(sharedPreferenceManager.getBikeKey());
    }
}