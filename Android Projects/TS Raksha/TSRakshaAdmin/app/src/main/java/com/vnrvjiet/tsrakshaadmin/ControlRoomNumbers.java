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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.vnrvjiet.tsrakshaadmin.Adapters.HelpLineAdapter;
import com.vnrvjiet.tsrakshaadmin.Models.HelpLineModel;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class ControlRoomNumbers extends Fragment {
    private RecyclerView controlRecycler;
    private DatabaseReference controlRef;
    private HelpLineAdapter adapter;
    private FirebaseRecyclerOptions<HelpLineModel> options;
    private ImageButton addNew;

    public ControlRoomNumbers(DatabaseReference controlRef) {
        // Required empty public constructor
        this.controlRef = controlRef.child("Indirect");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_control_room_numbers, container, false);
        initializeFields(view);
        settingAdapter();
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.new_help_line,null);
                final EditText title = view1.findViewById(R.id.enter_helpline_title);
                final EditText phone1 = view1.findViewById(R.id.enter_helpline_phone1);
                final EditText phone2 = view1.findViewById(R.id.enter_helpline_phone2);
                final EditText email = view1.findViewById(R.id.enter_helpline_email);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(view1);
                builder.setPositiveButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("phone1",phone1.getText().toString().trim());
                        hashMap.put("phone2",phone2.getText().toString().trim());
                        hashMap.put("email",email.getText().toString().trim());
                        controlRef.child(title.getText().toString()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(getContext(), "Details uploaded successfully...", Toast.LENGTH_SHORT).show();
                                }
                                else
                                    Toast.makeText(getContext(), "Error..Try again", Toast.LENGTH_SHORT).show();
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
        });
        return view;
    }

    private void settingAdapter() {
        options = new FirebaseRecyclerOptions.Builder<HelpLineModel>().setQuery(controlRef,HelpLineModel.class).build();
        adapter = new HelpLineAdapter(options);
        controlRecycler.setAdapter(adapter);
        adapter.startListening();
    }

    private void initializeFields(View view) {
        controlRecycler = view.findViewById(R.id.control_room_recycler);
        controlRecycler.setHasFixedSize(true);
        controlRecycler.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        addNew = view.findViewById(R.id.add_new_control);
    }
}
