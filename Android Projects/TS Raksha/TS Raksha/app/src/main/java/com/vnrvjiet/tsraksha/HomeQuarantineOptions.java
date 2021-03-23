package com.vnrvjiet.tsraksha;


import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import static com.vnrvjiet.tsraksha.Constants.ENGLISH;
import static com.vnrvjiet.tsraksha.Constants.TELUGU;


public class HomeQuarantineOptions extends Fragment {
    private LinearLayout english,telugu;

    public HomeQuarantineOptions() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_quarantine_options, container, false);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.show();
        actionBar.setTitle(getResources().getString(R.string.home_quarantine));
        Context context = getContext();
        Application.getInstance().initAppLanguage(context);
        initializeFields(view);
        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPdfView(ENGLISH);
            }
        });
        telugu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPdfView(TELUGU);
            }
        });
        return view;
    }

    private void openPdfView(String s) {
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,new HomeQuarantineFragment(s)).addToBackStack(null).commit();
    }

    private void initializeFields(View view) {
        english = view.findViewById(R.id.english_pdf);
        telugu = view.findViewById(R.id.telugu_pdf);
    }

}
