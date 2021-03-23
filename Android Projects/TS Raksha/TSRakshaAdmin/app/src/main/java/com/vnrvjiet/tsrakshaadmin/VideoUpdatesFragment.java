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
import com.vnrvjiet.tsrakshaadmin.Adapters.VideoAdapter;
import com.vnrvjiet.tsrakshaadmin.Models.VideoModel;

import java.util.Calendar;
import java.util.HashMap;


public class VideoUpdatesFragment extends Fragment {
    private RecyclerView videoRecycler;
    private VideoAdapter adapter;
    private FirebaseRecyclerOptions<VideoModel> options;
    private DatabaseReference videoRef;
    private ImageButton addNew;
    private EditText title,url;
    private Button vidDate;
    private int mYear,mDay,mMonth;
    private String date = "";
    private String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    private DatePickerDialog.OnDateSetListener onDateSetListener;

    public VideoUpdatesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_updates, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.video_updates));
        initializeFields(view);
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadVideo();
            }
        });
        settingAdapter();
        return view;
    }

    private void uploadVideo() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.video_details,null);
        title = view.findViewById(R.id.vid_title);
        vidDate = view.findViewById(R.id.vid_date);
        url = view.findViewById(R.id.vid_url);
        vidDate.setOnClickListener(new View.OnClickListener() {
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
                vidDate.setText(date);
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setPositiveButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("title",title.getText().toString().trim());
                hashMap.put("url",url.getText().toString().trim());
                hashMap.put("date",vidDate.getText().toString());
                videoRef.push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private void settingAdapter() {
        options = new FirebaseRecyclerOptions.Builder<VideoModel>().setQuery(videoRef,VideoModel.class).build();
        adapter = new VideoAdapter(options);
        videoRecycler.setAdapter(adapter);
        adapter.startListening();
    }

    private void initializeFields(View view) {
        videoRef = FirebaseDatabase.getInstance().getReference().child("Upload").child(getResources().getString(R.string.video_updates));
        videoRecycler = view.findViewById(R.id.video_updates_recycler);
        videoRecycler.setHasFixedSize(true);
        addNew = view.findViewById(R.id.add_new_video);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        videoRecycler.setLayoutManager(manager);
    }

}
