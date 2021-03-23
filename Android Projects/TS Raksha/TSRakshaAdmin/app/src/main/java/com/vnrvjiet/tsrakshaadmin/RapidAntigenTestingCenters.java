package com.vnrvjiet.tsrakshaadmin;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vnrvjiet.tsrakshaadmin.Adapters.RapidTestingAdapter;
import com.vnrvjiet.tsrakshaadmin.Models.TestingCenterModel;

import java.util.HashMap;


public class RapidAntigenTestingCenters extends Fragment {
    private RecyclerView rapidRecycler;
    private DatabaseReference rapidRef;
    private RapidTestingAdapter adapter;
    private FirebaseRecyclerOptions<TestingCenterModel> options;
    private ImageButton addRapidTest;

    public RapidAntigenTestingCenters() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_rapid_antigen_testing_centers, container, false);
        initializeFields(view);
        addRapidTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRapidTestDetails();
            }
        });
        settingAdapter();
        return view;
    }

    private void addRapidTestDetails() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.add_rapid_center,null);
        final EditText testName = view.findViewById(R.id.enter_rapid_name);
        final EditText testType = view.findViewById(R.id.enter_rapid_type);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        builder.setPositiveButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = testName.getText().toString();
                String type = testType.getText().toString();
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("name",name);
                hashMap.put("type",type);
                rapidRef.push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        Toast.makeText(getContext(), getResources().getString(R.string.updated), Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    private void settingAdapter() {
        options = new FirebaseRecyclerOptions.Builder<TestingCenterModel>().setQuery(rapidRef,TestingCenterModel.class).build();
        adapter = new RapidTestingAdapter(options);
        rapidRecycler.setAdapter(adapter);
        adapter.startListening();
    }

    private void initializeFields(View view) {
        rapidRecycler = view.findViewById(R.id.rapid_recycler_view);
        rapidRecycler.setHasFixedSize(true);
        addRapidTest = view.findViewById(R.id.add_rapid_test);
        rapidRecycler.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        rapidRef = FirebaseDatabase.getInstance().getReference().child("Upload").child(getResources().getString(R.string.testing_centers)).child("Rapid");
    }

}

