package com.vnrvjiet.tsrakshaadmin;


import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vnrvjiet.tsrakshaadmin.Adapters.HelpLineAdapter;
import com.vnrvjiet.tsrakshaadmin.Models.HelpLineModel;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class TechnicalSupport extends Fragment {
    private RecyclerView helplineRecycler;
    private String name;
    private DatabaseReference helplineRef,tempRef;
    private HelpLineAdapter adapter;
    private FirebaseRecyclerOptions<HelpLineModel> options;
    private Button controlNumbers;
    private ImageButton addNew;

    public TechnicalSupport() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_technical_support, container, false);
        name = getResources().getString(R.string.technical_support);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(name);

        initializeFields(view);
        settingAdapter();
        controlNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,new ControlRoomNumbers(tempRef)).addToBackStack(null).commit();
            }
        });
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
                        helplineRef.child(title.getText().toString()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        options = new FirebaseRecyclerOptions.Builder<HelpLineModel>().setQuery(helplineRef,HelpLineModel.class).build();
        adapter = new HelpLineAdapter(options);
        helplineRecycler.setAdapter(adapter);
        adapter.startListening();
    }

    private void initializeFields(View view) {
        helplineRecycler = view.findViewById(R.id.helpline_recycler);
        helplineRecycler.setHasFixedSize(true);
        helplineRecycler.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        tempRef = FirebaseDatabase.getInstance().getReference().child("Upload").child(name);
        helplineRef = tempRef.child("Direct");
        controlNumbers = view.findViewById(R.id.view_control_numbers);
        addNew = view.findViewById(R.id.add_new_helpline);
    }
}
