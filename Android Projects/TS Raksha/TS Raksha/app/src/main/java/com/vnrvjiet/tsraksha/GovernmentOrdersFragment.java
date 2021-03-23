package com.vnrvjiet.tsraksha;


import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vnrvjiet.tsraksha.Adapters.GoAdapter;
import com.vnrvjiet.tsraksha.Models.GoModel;

import static com.vnrvjiet.tsraksha.Constants.GOVERNMENT_ORDERS;
import static com.vnrvjiet.tsraksha.Constants.UPLOAD;

public class GovernmentOrdersFragment extends Fragment {
    private RecyclerView goRecyclerView;
    private FirebaseRecyclerOptions<GoModel> options;
    private GoAdapter adapter;
    private DatabaseReference goRef;
    private Context context;

    public GovernmentOrdersFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_government_orders, container, false);
        context = getContext();
        Application.getInstance().initAppLanguage(context);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.show();
        actionBar.setTitle(getResources().getString(R.string.government_orders));
        initializeFields(view);
        settingAdapter();
        return view;
    }

    private void settingAdapter() {
        options = new FirebaseRecyclerOptions.Builder<GoModel>().setQuery(goRef, GoModel.class).build();
        adapter = new GoAdapter(options);
        goRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void initializeFields(View view) {
        goRecyclerView = view.findViewById(R.id.go_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        goRecyclerView.setLayoutManager(layoutManager);
        goRecyclerView.setHasFixedSize(true);
        goRef = FirebaseDatabase.getInstance().getReference().child(UPLOAD).child(GOVERNMENT_ORDERS);
    }

}
