package com.sunil45.crimeregadmin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.Map;

public class ViewComplaintFragment extends Fragment {
    private ComplaintsModel complaintsModel;
    private String trackKey;
    private DatabaseReference userNameRef;
    private Button showStatus, updateProgress;
    private TextView viewId, viewCategory, viewName, viewProgress, viewAddress, viewAddInfo, viewDateTime, viewVictims;

    public ViewComplaintFragment(ComplaintsModel model, String trackKey) {
        complaintsModel = model;
        this.trackKey = trackKey;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_complaint, container, false);
        initializeFields(view);
        setTexts();
        showStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cnt = 1;
                String ans = "";
                for(int i = 1; i < 10; ++i){
                    String idx = String.valueOf(i);
                    if(complaintsModel.getStatus().containsKey(idx)){
                        ++cnt;
                        ans += idx + ". " + complaintsModel.getStatus().get(idx);
                        ans += '\n';
                    }
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                int finalCnt = cnt;
                builder.setTitle("Status of Complaint").setMessage(ans).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setNegativeButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                        EditText editText = new EditText(getContext());
                        editText.setHint("Enter status to update");
                        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        View view1 = editText;
                        builder1.setView(view1).setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String t = editText.getText().toString().trim();
                                if(t.isEmpty()){
                                    Toast.makeText(getContext(), "Enter update status", Toast.LENGTH_SHORT).show();
                                }
                                FirebaseDatabase.getInstance().getReference().child("Complaints").child(trackKey).child("Status").child(String.valueOf(finalCnt)).setValue(t).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getContext(), "Complaint Updated successfully", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(getContext(), "Something error occurred", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }).show();
                    }
                }).show();
            }
        });
        updateProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int progress = Integer.parseInt(complaintsModel.getStatus().get("progress").toString());
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                EditText editText = new EditText(getContext());
                editText.setHint("Enter progress");
                editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                View view1 = editText;
                builder.setView(view1).setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int val = Integer.parseInt(editText.getText().toString());
                        if(val <= progress || val > 100){
                            Toast.makeText(getContext(), "Progress value must be grater than previous value and do not exceed 100", Toast.LENGTH_SHORT).show();
                        }else{
                            FirebaseDatabase.getInstance().getReference().child("Complaints").child(trackKey).child("Status").child("progress").setValue(val).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete( Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getContext(), "Progress Updated Successfully", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(getContext(), "Error Occured", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }).show();
            }
        });
        return view;
    }

    private void setTexts() {
        viewId.append(trackKey.substring(trackKey.length() - 4));
        viewCategory.append(complaintsModel.getCategory());
        viewProgress.append(complaintsModel.getStatus().get("progress").toString() + "%");
        viewAddress.append(complaintsModel.getAddress() + ", " + complaintsModel.getPincode());
        viewAddInfo.append(complaintsModel.getAdditional());
        viewDateTime.append(complaintsModel.getTime() + ", " + complaintsModel.getDate());
        viewVictims.append(complaintsModel.getVictim());
    }

    private void initializeFields(View view) {
        userNameRef = FirebaseDatabase.getInstance().getReference().child("Users").child(complaintsModel.getUserid());
        viewId = view.findViewById(R.id.view_id);
        viewCategory = view.findViewById(R.id.view_category);
        viewName = view.findViewById(R.id.view_name);
        viewProgress = view.findViewById(R.id.view_progress);
        viewAddress = view.findViewById(R.id.view_address);
        viewAddInfo= view.findViewById(R.id.view_add_info);
        viewDateTime = view.findViewById(R.id.view_date_time);
        viewVictims = view.findViewById(R.id.view_victims);
        updateProgress = view.findViewById(R.id.update_progress);
        userNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                viewName.append(snapshot.child("Name").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        showStatus = view.findViewById(R.id.status_of_complaint);
    }
}