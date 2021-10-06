package com.sunil45.crimeregistration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MembersList extends AppCompatActivity {
    private FirebaseAuth myAuth;
    private EditText addName,addPhone;
    private DatabaseReference membersReference;
    private LinearLayout ll;
    private String memName,memPhone;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_list);
        myAuth=FirebaseAuth.getInstance();
        String uid=myAuth.getCurrentUser().getUid();
        membersReference= FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("SosList");
        findViewById(R.id.add_member_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MembersList.this);
                alertDialog.setCancelable(false);
                alertDialog.setTitle("Add Member");
                alertDialog.setIcon(R.drawable.add_icon);
                ll = (LinearLayout) findViewById(R.id.linear);
                loadingBar = new ProgressDialog(MembersList.this);
                final View view1 = getLayoutInflater().inflate(R.layout.adding_member, ll);
                alertDialog.setView(view1);
                alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                final AlertDialog dialog = alertDialog.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addName = (EditText)view1.findViewById(R.id.adding_name);
                        addPhone = (EditText)view1.findViewById(R.id.adding_mobile);
                        memName=addName.getText().toString();
                        memPhone=addPhone.getText().toString();
                        if(memName.isEmpty())
                            addName.setError("Enter name");
                        else if(memPhone.isEmpty())
                            addPhone.setError("Enter Number");
                        else if(memPhone.length()<10)
                            Toast.makeText(MembersList.this, "Enter valid mobile number", Toast.LENGTH_SHORT).show();
                        else {
                            loadingBar.setMessage("Please Wait while adding member..");
                            loadingBar.setCancelable(false);
                            loadingBar.show();
                            membersReference.child(memName).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()) {
                                        Toast.makeText(MembersList.this, "Already added this name into members", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            membersReference.child(memName).setValue(memPhone);
                            Toast.makeText(MembersList.this, "Member added successfully", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        findViewById(R.id.view_members).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MembersList.this,ViewMembers.class));
            }
        });
        findViewById(R.id.remove_member).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MembersList.this);
                alertDialog.setCancelable(false);
                alertDialog.setTitle("Remove Member");
                final EditText input = new EditText(MembersList.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                input.setHint("Enter name to remove");
                alertDialog.setView(input);
                alertDialog.setIcon(R.drawable.remove_member);
                alertDialog.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                final AlertDialog dialog = alertDialog.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String value = input.getText().toString();
                        if(value.isEmpty())
                            input.setError("Enter name");
                        else {
                            membersReference.child(value).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        membersReference.child(value).removeValue();
                                        Toast.makeText(MembersList.this, "Member removed successfully", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                    else{
                                        Toast.makeText(MembersList.this, "No member found...", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });
            }
        });
    }
}
