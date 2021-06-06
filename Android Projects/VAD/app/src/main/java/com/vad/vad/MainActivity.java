package com.vad.vad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private EditText arduinoKey;
    private Button enterKey;
    private DatabaseReference rootRef;
    private SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeFields();

        enterKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = arduinoKey.getText().toString().trim();
                Toast.makeText(MainActivity.this, "Please wait while fetching your key", Toast.LENGTH_SHORT).show();
                rootRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        View view1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.password_layout, null);
                        EditText createPassword = view1.findViewById(R.id.create_password);
                        EditText confirmPassword = view1.findViewById(R.id.confirm_password);
                        Button loginButton = view1.findViewById(R.id.login_button);
                        boolean registered = false;
                        if(snapshot.child("Registered").getValue(String.class).equals("No")){
                            builder.setView(view1);
                            builder.show();
                        }else{
                            createPassword.setHint("Enter Password");
                            registered = true;
                            confirmPassword.setVisibility(View.GONE);
                            builder.setView(view1);
                            builder.show();
                        }
                        boolean finalRegistered = registered;
                        loginButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(finalRegistered){
                                    String enteredPassword = createPassword.getText().toString();
                                    String password = snapshot.child("Password").getValue(String.class);
                                    if(enteredPassword.equals(password)){
                                        sharedPreferenceManager.setUserReg(true);
                                        sharedPreferenceManager.setBikeKey(key);
                                        startHomeActivity();
                                        Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();

                                    }else{
                                        Toast.makeText(MainActivity.this, "Enter correct password", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    String password = createPassword.getText().toString().trim();
                                    String password1 = confirmPassword.getText().toString().trim();
                                    if(password.equals(password1)){
                                        if(password.length() < 8){
                                            Toast.makeText(MainActivity.this, "Enter length upto 8 characters", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("Registered", "Yes");
                                        hashMap.put("Password", password);
                                        rootRef.child(key).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    sharedPreferenceManager.setUserReg(true);
                                                    sharedPreferenceManager.setBikeKey(key);
                                                    startHomeActivity();
                                                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    Toast.makeText(MainActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }else{
                                        Toast.makeText(MainActivity.this, "Passwords not matched", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }

    private void initializeFields() {
        sharedPreferenceManager = new SharedPreferenceManager(MainActivity.this);
        if(sharedPreferenceManager.getUserReg()){
                startHomeActivity();
        }
        arduinoKey = findViewById(R.id.arduino_key);
        enterKey = findViewById(R.id.enter_user);
        rootRef = FirebaseDatabase.getInstance().getReference().child("Users");
    }


    private void startHomeActivity(){
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}