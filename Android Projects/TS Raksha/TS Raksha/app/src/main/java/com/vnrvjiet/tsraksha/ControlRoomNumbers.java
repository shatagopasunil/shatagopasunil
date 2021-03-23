package com.vnrvjiet.tsraksha;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.vnrvjiet.tsraksha.Adapters.HelpLineAdapter;
import com.vnrvjiet.tsraksha.Models.HelpLineModel;

import static com.vnrvjiet.tsraksha.Constants.INDIRECT;


public class ControlRoomNumbers extends Fragment {

    private RecyclerView controlRecycler;
    private DatabaseReference controlRef;
    private Context context;
    ControlRoomNumbers(DatabaseReference controlRef) {
        this.controlRef = controlRef.child(INDIRECT);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control_room_numbers, container, false);
        context = getContext();
        Application.getInstance().initAppLanguage(context);
        initializeFields(view);
        settingAdapter();
        return view;
    }

    private void settingAdapter() {
        FirebaseRecyclerOptions<HelpLineModel> options = new FirebaseRecyclerOptions.Builder<HelpLineModel>().setQuery(controlRef, HelpLineModel.class).build();
        HelpLineAdapter adapter = new HelpLineAdapter(options);
        controlRecycler.setAdapter(adapter);
        adapter.startListening();
    }

    private void initializeFields(View view) {
        controlRecycler = view.findViewById(R.id.control_room_recycler);
        controlRecycler.setHasFixedSize(true);
        controlRecycler.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
    }

}
