package com.vnrvjiet.tsrakshaadmin;


import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vnrvjiet.tsrakshaadmin.Adapters.HospitalAdapter;
import com.vnrvjiet.tsrakshaadmin.Models.Hospitals;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class IsolationCentersFragment extends Fragment {
    private RecyclerView isolationCentersRecyclerView;
    private RadioButton governmentIsolation, privateIsolation;
    private ImageButton addNew;
    private DatabaseReference isolationRef;
    private LoadingBar loadingBar;
    private EditText hospName, hospAddress, hospLocation, hospLat, hospLong;
    private FirebaseRecyclerOptions<Hospitals> options;
    private HospitalAdapter adapter;

    public IsolationCentersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_isolation_centers, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.isolation_centers));
        initializeFields(view);
        governmentIsolation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveData(getResources().getString(R.string.government));
            }
        });
        privateIsolation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveData(getResources().getString(R.string.privates));
            }
        });
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Dialog_Alert);
                builder.setPositiveButton(getResources().getString(R.string.government), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        enterDetails(getResources().getString(R.string.government));
                    }
                });
                builder.setNeutralButton(getResources().getString(R.string.privates), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        enterDetails(getResources().getString(R.string.privates));
                    }
                });
                builder.show();
            }
        });
        return view;
    }

    private void enterDetails(final String string) {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.hospital_details,null);
        hospName = view.findViewById(R.id.enter_hosp_name);
        hospAddress = view.findViewById(R.id.enter_hosp_address);
        hospLocation = view.findViewById(R.id.enter_hosp_location);
        hospLat = view.findViewById(R.id.enter_hosp_latitude);
        hospLong = view.findViewById(R.id.enter_hosp_longitude);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setPositiveButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loadingBar.showLoadingBar(2);
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("name", hospName.getText().toString());
                hashMap.put("address", hospAddress.getText().toString());
                hashMap.put("location", hospLocation.getText().toString());
                hashMap.put("latitude", hospLat.getText().toString());
                hashMap.put("longitude", hospLong.getText().toString());
                isolationRef.child(string).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private void retrieveData(String st) {
        options = new FirebaseRecyclerOptions.Builder<Hospitals>().setQuery(isolationRef.child(st), Hospitals.class).build();
        adapter = new HospitalAdapter(options);
        isolationCentersRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void initializeFields(View view) {
        isolationCentersRecyclerView = view.findViewById(R.id.isolation_centers_recycler);
        isolationCentersRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),LinearLayoutManager.VERTICAL,false));
        isolationCentersRecyclerView.setHasFixedSize(true);
        governmentIsolation = view.findViewById(R.id.government);
        privateIsolation = view.findViewById(R.id.privates);
        addNew = view.findViewById(R.id.add_new);
        loadingBar = new LoadingBar(getActivity());
        isolationRef = FirebaseDatabase.getInstance().getReference().child("Upload").child("Health Facilities").child("Isolation Centers");
    }
}
