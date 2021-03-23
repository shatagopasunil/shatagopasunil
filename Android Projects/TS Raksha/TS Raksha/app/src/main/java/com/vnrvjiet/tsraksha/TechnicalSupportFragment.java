package com.vnrvjiet.tsraksha;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vnrvjiet.tsraksha.Adapters.HelpLineAdapter;
import com.vnrvjiet.tsraksha.Models.HelpLineModel;

import static com.vnrvjiet.tsraksha.Constants.DIRECT;
import static com.vnrvjiet.tsraksha.Constants.TECHNICAL_SUPPORT;
import static com.vnrvjiet.tsraksha.Constants.UPLOAD;


public class TechnicalSupportFragment extends Fragment {
    private RecyclerView helplineRecycler;
    private DatabaseReference helplineRef,tempRef;
    private HelpLineAdapter adapter;
    private FirebaseRecyclerOptions<HelpLineModel> options;
    private Button controlNumbers;
    private Context context;

    public TechnicalSupportFragment() {
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_technical_support, container, false);
        context = getContext();
        Application.getInstance().initAppLanguage(context);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.technical_support));

        initializeFields(view);
        settingAdapter();
        controlNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,new ControlRoomNumbers(tempRef)).addToBackStack(null).commit();
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
        helplineRecycler.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        tempRef = FirebaseDatabase.getInstance().getReference().child(UPLOAD).child(TECHNICAL_SUPPORT);
        helplineRef = tempRef.child(DIRECT);
        controlNumbers = view.findViewById(R.id.view_control_numbers);
    }

}
