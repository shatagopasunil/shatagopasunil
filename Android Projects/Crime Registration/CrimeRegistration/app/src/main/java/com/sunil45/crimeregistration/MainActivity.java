package com.sunil45.crimeregistration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private EditText mPhoneNumber,mVerifyNumber;
    private Button mNext,mVerify;
    private FirebaseAuth myAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private ProgressDialog loadingBar;
    private LinearLayout mMobileId,mVerifyId;
    private String verificationCode;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPhoneNumber = (EditText) findViewById(R.id.mobileNumberEditText);
        mNext = (Button) findViewById(R.id.mobileNumberNextEditText);
        mMobileId = (LinearLayout) findViewById(R.id.mobileLinearId);
        mVerifyId = (LinearLayout) findViewById(R.id.verifyLinearId);
        mVerify = (Button) findViewById(R.id.verifyButton);
        mVerifyNumber = (EditText) findViewById(R.id.verifyNumberEditText);
        loadingBar = new ProgressDialog(this);
        myAuth = FirebaseAuth.getInstance();
        rootRef= FirebaseDatabase.getInstance().getReference();
        mVerifyId.setVisibility(View.INVISIBLE);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String phoneNumber = mPhoneNumber.getText().toString();
                if (phoneNumber.isEmpty())
                    Toast.makeText(MainActivity.this, "Please enter Phone Number", Toast.LENGTH_SHORT).show();
                else if (phoneNumber.length() != 10)
                    Toast.makeText(MainActivity.this, "Enter 10 digit valid mobile number", Toast.LENGTH_SHORT).show();
                else {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setMessage("You entered  +91 " + phoneNumber + "\n\nIs this OK, or would you like to edit the number?");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadingBar.setTitle("Phone verification");
                            loadingBar.setMessage("Please wait, while we are authenticating with your phone");
                            loadingBar.setCancelable(false);
                            loadingBar.show();
                            mVerifyId.setVisibility(View.VISIBLE);
                            mMobileId.setVisibility(View.INVISIBLE);
                            PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + phoneNumber, 60, TimeUnit.SECONDS, MainActivity.this, mCallbacks);
                        }
                    });
                    alertDialog.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();
                }
            }
        });
        mVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = mVerifyNumber.getText().toString();
                if (code.isEmpty())
                    Toast.makeText(MainActivity.this, "Enter Verification Code", Toast.LENGTH_SHORT).show();
                else {
                    loadingBar.setTitle("Verification Code");
                    loadingBar.setMessage("Please wait, while we are verifying code that has been sent to your phone");
                    loadingBar.setCancelable(false);
                    loadingBar.show();
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationCode, code);
                    signInWithPhoneCredential(phoneAuthCredential);
                }
            }
        });
        mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(MainActivity.this, e+"" , Toast.LENGTH_SHORT).show();
                mVerifyId.setVisibility(View.INVISIBLE);
                mMobileId.setVisibility(View.VISIBLE);
                loadingBar.dismiss();
            }


            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                loadingBar.dismiss();
                verificationCode=s;
                Toast.makeText(MainActivity.this, "Code sent to your phone", Toast.LENGTH_SHORT).show();
                mMobileId.setVisibility(View.INVISIBLE);
                mVerifyId.setVisibility(View.VISIBLE);
                mVerifyNumber.requestFocus();
            }
        };
    }
    private void signInWithPhoneCredential(PhoneAuthCredential phoneAuthCredential) {
        myAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String uid=myAuth.getCurrentUser().getUid();
                    rootRef.child("Users").child(uid).setValue("");
                    loadingBar.dismiss();
                    Intent intent=new Intent(MainActivity.this,ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                else{
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this, "Enter correct verification code", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
