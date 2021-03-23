package com.vnrvjiet.tsrakshaadmin;


import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vnrvjiet.tsrakshaadmin.Adapters.GoAdapter;
import com.vnrvjiet.tsrakshaadmin.Models.GoModel;

import java.util.Calendar;
import java.util.HashMap;

public class GovernmentOrders extends Fragment {
    private RecyclerView goRecycler;
    private ImageButton addGo;
    private Calendar c;
    private int mYear,mDay,mMonth;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private String date = "";
    private String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    private DatabaseReference goRef;
    private GoAdapter adapter;
    private FirebaseRecyclerOptions<GoModel> options;


    public GovernmentOrders() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_government_orders, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.government_orders));
        initializeFields(view);
        settingAdapter();
        addGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1 = LayoutInflater.from(getContext()).inflate(R.layout.add_go_layout,null);
                final EditText goTitle = view1.findViewById(R.id.go_title);
                final EditText goUrl = view1.findViewById(R.id.go_url);
                final Button goDate = view1.findViewById(R.id.go_date);
                goDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar calendar = Calendar.getInstance();
                        mYear = calendar.get(Calendar.YEAR);
                        mMonth = calendar.get(Calendar.MONTH);
                        mDay = calendar.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),onDateSetListener,mYear,mMonth,mDay);
                        datePickerDialog.show();
                    }
                });
                onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        date = d + " " + months[m] + " " + y;
                        goDate.setText(date);
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(view1);
                builder.setPositiveButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(date.isEmpty())
                            Toast.makeText(getContext(), "Select Date", Toast.LENGTH_SHORT).show();
                        else
                        {
                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("title",goTitle.getText().toString().trim());
                            hashMap.put("url",goUrl.getText().toString().trim());
                            hashMap.put("date",date);
                            goRef.push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                        Toast.makeText(getContext(), "Details uploaded successfully", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(getContext(), "Error... Try Again", Toast.LENGTH_SHORT).show();
                                }
                            });
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        return view;
    }

    private void settingAdapter() {
        options = new FirebaseRecyclerOptions.Builder<GoModel>().setQuery(goRef,GoModel.class).build();
        adapter = new GoAdapter(options);
        goRecycler.setAdapter(adapter);
        adapter.startListening();
    }

    private void initializeFields(View view) {
        goRecycler = view.findViewById(R.id.go_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,true);
        goRecycler.setLayoutManager(layoutManager);
        goRecycler.setHasFixedSize(true);
        addGo = view.findViewById(R.id.add_go);
        goRef = FirebaseDatabase.getInstance().getReference().child("Upload").child("Government Orders");
    }

}
