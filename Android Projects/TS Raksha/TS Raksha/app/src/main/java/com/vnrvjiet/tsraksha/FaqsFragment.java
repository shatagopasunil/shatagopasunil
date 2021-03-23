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
import com.vnrvjiet.tsraksha.Adapters.FaqsAdapter;
import com.vnrvjiet.tsraksha.Models.FaqsModel;

import static com.vnrvjiet.tsraksha.Constants.FAQS;
import static com.vnrvjiet.tsraksha.Constants.UPLOAD;


public class FaqsFragment extends Fragment {
    private RecyclerView faqsRecyclerView;
    private DatabaseReference faqsRef;
    private Context context;


    public FaqsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faqs, container, false);
        context = getContext();
        Application.getInstance().initAppLanguage(context);
        initializeFields(view);
        settingAdapter();
        return view;
    }

    private void settingAdapter() {
        FirebaseRecyclerOptions<FaqsModel> options = new FirebaseRecyclerOptions.Builder<FaqsModel>().setQuery(faqsRef, FaqsModel.class).build();
        FaqsAdapter adapter = new FaqsAdapter(options);
        faqsRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void initializeFields(View view) {
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.show();
        actionBar.setTitle(getResources().getString(R.string.faqs));
        faqsRecyclerView = view.findViewById(R.id.faqs_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        faqsRecyclerView.setLayoutManager(linearLayoutManager);
        faqsRecyclerView.setHasFixedSize(true);
        faqsRef = FirebaseDatabase.getInstance().getReference().child(UPLOAD).child(FAQS);
    }

}
