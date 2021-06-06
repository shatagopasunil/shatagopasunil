package com.vad.vad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UsersActivity extends AppCompatActivity {
    private DatabaseReference keysRef, keyRef, flagRef, licenceRef;
    private SharedPreferenceManager sharedPreferenceManager;
    private RecyclerView recyclerView;
    private ImageView addUser;
    private AlertDialog alertDialog;
    private ValueEventListener listener;
    private  int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        getSupportActionBar().setTitle("Users");
        addUser = findViewById(R.id.addUser);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(UsersActivity.this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        sharedPreferenceManager = new SharedPreferenceManager(UsersActivity.this);
        licenceRef = FirebaseDatabase.getInstance().getReference().child("Licences");
        keyRef = FirebaseDatabase.getInstance().getReference().child("Users").child(sharedPreferenceManager.getBikeKey());
        keysRef = FirebaseDatabase.getInstance().getReference().child("Users").child(sharedPreferenceManager.getBikeKey()).child("Users");
        FirebaseRecyclerOptions<UserRecyclerModel> options = new FirebaseRecyclerOptions.Builder<UserRecyclerModel>().setQuery(keysRef, UserRecyclerModel.class).build();
        UserRecyclerAdapter adapter = new UserRecyclerAdapter(options);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1 = LayoutInflater.from(UsersActivity.this).inflate(R.layout.register_layout, null);
                EditText userName = view1.findViewById(R.id.user_name);
                EditText userLicence = view1.findViewById(R.id.user_licence);
                TextView note = view1.findViewById(R.id.show_finger_note);
                Button save = view1.findViewById(R.id.save_user_btn);
                AlertDialog.Builder builder = new AlertDialog.Builder(UsersActivity.this);
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
                            Toast.makeText(UsersActivity.this, "Please wait while saving your information....", Toast.LENGTH_SHORT).show();
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
                                                                keyRef.child("Users").push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                                                                                            Toast.makeText(UsersActivity.this, "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
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
                                                                            Toast.makeText(UsersActivity.this, "Error Occured! Try again", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            } else {
                                                                Toast.makeText(UsersActivity.this, "Error occured ! Try again", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    Toast.makeText(UsersActivity.this, "Error occured ! Try again", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(UsersActivity.this, "Error occured ! Try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            alertDialog.dismiss();
                            return;
                        }
                        if (name.isEmpty()) {
                            Toast.makeText(UsersActivity.this, "Enter name", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (licence.isEmpty()) {
                            Toast.makeText(UsersActivity.this, "Enter licence number", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (licence.length() != 16) {
                            Toast.makeText(UsersActivity.this, "Enter valid licence number", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(UsersActivity.this, "Please wait while validating your licence", Toast.LENGTH_SHORT).show();
                        licenceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.child(licence).exists()) {
                                    if (snapshot.child(licence).getValue(String.class).equals(name)) {
                                        Query query = keyRef.child("Users").orderByChild("licence").equalTo(licence);
                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(snapshot.exists()){
                                                    Toast.makeText(UsersActivity.this, "User already exist", Toast.LENGTH_SHORT).show();
                                                    alertDialog.dismiss();
                                                }else{
                                                        Toast.makeText(UsersActivity.this, "Details Matched... Please wait", Toast.LENGTH_SHORT).show();
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
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    } else {
                                        Toast.makeText(UsersActivity.this, "Name unmatched", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(UsersActivity.this, "Invalid Licence Number", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
            }
        });
    }
}