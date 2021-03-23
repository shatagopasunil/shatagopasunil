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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vnrvjiet.tsrakshaadmin.Adapters.FaqsAdapter;
import com.vnrvjiet.tsrakshaadmin.Models.FaqsModel;

import java.util.HashMap;
import java.util.Map;

public class FaqsFragment extends Fragment {

    private ImageButton addFaq;
    private EditText question, answer;
    private RecyclerView faqsRecyclerView;
    private DatabaseReference faqsRef;
    private FirebaseRecyclerOptions<FaqsModel> options;
    private FaqsAdapter adapter;


    public FaqsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_faqs, container, false);
        initializeFields(view);
        settingAdapter();
        addFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.add_faq,null);
                question = view1.findViewById(R.id.faq_q);
                answer = view1.findViewById(R.id.faq_a);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(view1);
                builder.setPositiveButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("question",question.getText().toString().trim());
                        hashMap.put("answer",answer.getText().toString().trim());
                        faqsRef.push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private void initializeFields(View view) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.faqs));
        addFaq = view.findViewById(R.id.add_new_faq);
        faqsRef = FirebaseDatabase.getInstance().getReference().child("Upload").child("Faqs");
        faqsRecyclerView = view.findViewById(R.id.faqs_recycler_view);
        faqsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        faqsRecyclerView.setHasFixedSize(true);
    }
    private void settingAdapter() {
        options = new FirebaseRecyclerOptions.Builder<FaqsModel>().setQuery(faqsRef,FaqsModel.class).build();
        adapter = new FaqsAdapter(options);
        faqsRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

}
