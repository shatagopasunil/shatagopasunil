package com.vnrvjiet.tsraksha;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vnrvjiet.tsraksha.Adapters.HomeQuarantineAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.vnrvjiet.tsraksha.Constants.HOME_QUARANTINE;
import static com.vnrvjiet.tsraksha.Constants.IMAGES;
import static com.vnrvjiet.tsraksha.Constants.UPLOAD;

public class HomeQuarantineFragment extends Fragment {

    private List<String> urls = new ArrayList<>();
    private DatabaseReference homeQuarantineRef;
    private HomeQuarantineAdapter adapter;
    private String s;
    private ActionBar actionBar;
    private LoadingBar loadingBar;
    private Context context;


    public HomeQuarantineFragment(String s) {
        this.s = s;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home_quarantine, container, false);
        context = getContext();
        Application.getInstance().initAppLanguage(context);
        actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.hide();
        initializeFields(view);
        return view;
    }

    private void initializeFields(View view) {
        loadingBar = new LoadingBar(getActivity());
        loadingBar.showLoadingBar(1);
        homeQuarantineRef = FirebaseDatabase.getInstance().getReference().child(UPLOAD).child(HOME_QUARANTINE).child(IMAGES).child(s);
        adapter = new HomeQuarantineAdapter(context,urls);
        ViewPager viewPager = view.findViewById(R.id.home_quarantine_view_pager);
        viewPager.setAdapter(adapter);
        retrieveUrls();
    }

    private void retrieveUrls() {
        homeQuarantineRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    urls.add(dataSnapshot.getValue().toString());
                adapter.notifyDataSetChanged();
                loadingBar.dismissLoadingBar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        actionBar.show();
    }
}
