package com.vnrvjiet.tsrakshaadmin;


import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vnrvjiet.tsrakshaadmin.Adapters.TestingCenterAdapter;
import com.vnrvjiet.tsrakshaadmin.Models.TestingCenterModel;

import java.util.HashMap;


public class ThreeTestingCenters extends Fragment {

    private RadioGroup testingGroup;
    private RadioButton govt,pvt;
    private RecyclerView recyclerView;
    private int checkedRadioButtonId;
    private DatabaseReference testingRef,customRef;
    private FirebaseRecyclerOptions<TestingCenterModel> options;
    private TestingCenterAdapter adapter;
    private ImageButton addTestingCenter;
    private EditText hospName,testType;
    private LoadingBar loadingBar;

    public ThreeTestingCenters() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_three_testing_centers, container, false);
        initializeFields(view);
        checkedRadioButtonId = testingGroup.getCheckedRadioButtonId();
        testingGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                checkedRadioButtonId = i;
                setRecyclerView(checkedRadioButtonId);
            }
        });
        addTestingCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Dialog_Alert);
                builder.setPositiveButton(getResources().getString(R.string.government), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addNewTestingCenter(getResources().getString(R.string.government));
                    }
                });
                builder.setNeutralButton(getResources().getString(R.string.privates), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addNewTestingCenter(getResources().getString(R.string.privates));
                    }
                });
                builder.show();
            }
        });
        setRecyclerView(checkedRadioButtonId);
        return view;
    }

    private void addNewTestingCenter(final String s) {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.add_testing_center,null);
        hospName = view.findViewById(R.id.enter_test_name);
        testType = view.findViewById(R.id.enter_test_type);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setPositiveButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loadingBar.showLoadingBar(2);
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("name", hospName.getText().toString());
                hashMap.put("type", testType.getText().toString());
                testingRef.child(s).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(getActivity(), getResources().getString(R.string.updated), Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                        loadingBar.dismissLoadingBar();
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


    private void setRecyclerView(int checkedRadioButtonId) {
        if(checkedRadioButtonId == R.id.government_testing)
            showRecycler(getResources().getString(R.string.government));
        else if(checkedRadioButtonId == R.id.private_testing)
            showRecycler(getResources().getString(R.string.privates));
    }

    private void showRecycler(String s) {
        customRef = testingRef.child(s);
        options = new FirebaseRecyclerOptions.Builder<TestingCenterModel>().setQuery(customRef,TestingCenterModel.class).build();
        adapter = new TestingCenterAdapter(options);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void initializeFields(View view) {
        testingGroup = view.findViewById(R.id.testing_radio_group);
        govt = view.findViewById(R.id.government_testing);
        pvt = view.findViewById(R.id.private_testing);
        recyclerView = view.findViewById(R.id.testing_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setHasFixedSize(true);
        loadingBar = new LoadingBar(getActivity());
        addTestingCenter = view.findViewById(R.id.add_new_testing_center);
        testingRef = FirebaseDatabase.getInstance().getReference().child("Upload").child(getResources().getString(R.string.testing_centers)).child("RT");
    }

}
