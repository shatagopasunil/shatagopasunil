package com.vnrvjiet.tsraksha;


import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vnrvjiet.tsraksha.Adapters.Recommendation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static com.vnrvjiet.tsraksha.Constants.AVERAGE;
import static com.vnrvjiet.tsraksha.Constants.DESC;
import static com.vnrvjiet.tsraksha.Constants.HINDI;
import static com.vnrvjiet.tsraksha.Constants.ENGLISH;
import static com.vnrvjiet.tsraksha.Constants.OPTIONS;
import static com.vnrvjiet.tsraksha.Constants.RECOMMENDATIONS;
import static com.vnrvjiet.tsraksha.Constants.RISK;
import static com.vnrvjiet.tsraksha.Constants.SAFE;
import static com.vnrvjiet.tsraksha.Constants.SELF_ASSESSMENT;
import static com.vnrvjiet.tsraksha.Constants.TELUGU;
import static com.vnrvjiet.tsraksha.Constants.TITLE;
import static com.vnrvjiet.tsraksha.Constants.UPLOAD;
import static com.vnrvjiet.tsraksha.Constants.VALUE;


public class SelfAssessmentFragment extends Fragment {

    private DatabaseReference questionsRef,recommendationRef;
    private int total;
    private float illnessScore, riskScore;
    private Resources res;
    private LinearLayout fourOptionsLayout, twoOptionsLayout, startLayout, questionLayout, resultLayout, choseLanguage;
    private Button tb1, tb2, fb1, fb2, fb3, fb4, english, telugu, hindi;
    private TextView questionView, healthTitle, healthDescription;
    private RecyclerView recommendationRecycler;
    private ArrayList<Pair<String, ArrayList<Pair<String, Integer>>>> values;
    private String question;
    private static final int MAX_CORONA = 104;

    public SelfAssessmentFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_self_assessment, container, false);
        Context context = getContext();
        res = getResources();
        Application.getInstance().initAppLanguage(context);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.show();
        actionBar.setTitle(res.getString(R.string.covid_self_assessment));
        initializeFields(view);
        return view;
    }

    private void initializeFields(final View view) {
        total = 0;
        illnessScore = 1;
        riskScore = 1;
        values = new ArrayList<>();
        questionsRef = FirebaseDatabase.getInstance().getReference().child(UPLOAD);
        startLayout = view.findViewById(R.id.start_self_layout);
        questionLayout = view.findViewById(R.id.question_layout);
        fourOptionsLayout = view.findViewById(R.id.four_options_layout);
        twoOptionsLayout = view.findViewById(R.id.two_options_layout);
        resultLayout = view.findViewById(R.id.result_layout);
        healthTitle = view.findViewById(R.id.health_title);
        hindi = view.findViewById(R.id.chose_hindi);
        english = view.findViewById(R.id.chose_english);
        telugu = view.findViewById(R.id.chose_telugu);
        healthDescription = view.findViewById(R.id.health_description);
        choseLanguage = view.findViewById(R.id.chose_language);
        recommendationRecycler = view.findViewById(R.id.recommendation_recycler);
        tb1 = view.findViewById(R.id.tb1);
        tb2 = view.findViewById(R.id.tb2);
        fb1 = view.findViewById(R.id.fb1);
        fb2 = view.findViewById(R.id.fb2);
        fb3 = view.findViewById(R.id.fb3);
        fb4 = view.findViewById(R.id.fb4);
        questionView = view.findViewById(R.id.question_sa);
        recommendationRef = questionsRef.child(RECOMMENDATIONS);
        questionsRef = questionsRef.child(SELF_ASSESSMENT);
        questionsRef.keepSynced(true);
        recommendationRef.keepSynced(true);

        view.findViewById(R.id.start_raksha_sa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLayout.setVisibility(View.GONE);
                choseLanguage.setVisibility(View.VISIBLE);
                telugu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadQuestions(TELUGU);
                    }
                });
                english.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadQuestions(ENGLISH);
                    }
                });
                hindi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadQuestions(HINDI);
                    }
                });
            }
        });

    }

    private void loadQuestions(final String s) {
        recommendationRef = recommendationRef.child(s);
        choseLanguage.setVisibility(View.GONE);
        questionLayout.setVisibility(View.VISIBLE);
        questionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ArrayList<Pair<String, Integer>> currentValues = new ArrayList<>();
                    question = dataSnapshot.child(s).getValue(String.class);
                    for (DataSnapshot snapshot1 : dataSnapshot.child(OPTIONS).getChildren()) {
                        currentValues.add(new Pair(snapshot1.child(s).getValue().toString(), snapshot1.child(VALUE).getValue(Integer.class)));
                    }
                    values.add(new Pair(question, currentValues));
                }
                showQuestions();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showQuestions() {
        if (total < 15) {
            questionView.setText(values.get(total).first);
            if (values.get(total).second.size() == 4) {
                twoOptionsLayout.setVisibility(View.GONE);
                fourOptionsLayout.setVisibility(View.VISIBLE);
                fb1.setText(values.get(total).second.get(0).first);
                fb2.setText(values.get(total).second.get(1).first);
                fb3.setText(values.get(total).second.get(2).first);
                fb4.setText(values.get(total).second.get(3).first);
                fb1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        calculateScore(values.get(total).second.get(0).second);
                    }
                });
                fb2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        calculateScore(values.get(total).second.get(1).second);
                    }
                });
                fb3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        calculateScore(values.get(total).second.get(2).second);
                    }
                });
                fb4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        calculateScore(values.get(total).second.get(3).second);
                    }
                });
            } else {
                fourOptionsLayout.setVisibility(View.GONE);
                twoOptionsLayout.setVisibility(View.VISIBLE);
                tb1.setText(values.get(total).second.get(0).first);
                tb2.setText(values.get(total).second.get(1).first);
                tb1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        calculateScore(values.get(total).second.get(0).second);
                    }
                });
                tb2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        calculateScore(values.get(total).second.get(1).second);
                    }
                });
            }
        } else {
            resultLayout.setVisibility(View.VISIBLE);
            questionLayout.setVisibility(View.GONE);
            illnessScore /= MAX_CORONA;
            recommendationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                String title,description;
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (illnessScore > 0.7) {
                        title = snapshot.child(RISK).child(TITLE).getValue(String.class);
                        description = snapshot.child(RISK).child(DESC).getValue(String.class);
                    } else if (illnessScore > 0.4) {
                        title = snapshot.child(AVERAGE).child(TITLE).getValue(String.class);
                        description = snapshot.child(AVERAGE).child(DESC).getValue(String.class);
                    }
                    else{
                        title = snapshot.child(SAFE).child(TITLE).getValue(String.class);
                        description = snapshot.child(SAFE).child(DESC).getValue(String.class);
                    }
                    healthTitle.setText(title);
                    healthDescription.setText(description);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            uploadRecommendations();
        }
    }


    private void uploadRecommendations() {
        ArrayList<Integer> recommendations = new ArrayList<>();
        if (illnessScore < 0.2) {
            recommendations.add(1);
            recommendations.add(2);
            recommendations.add(3);
            recommendations.add(4);
            if (riskScore > 1) {
                recommendations.add(5);
                recommendations.add(6);
                recommendations.add(7);
            }
        } else if (illnessScore < 0.6) {
            if (riskScore > 1) {
                recommendations.add(8);
            }
            recommendations.add(1);
            recommendations.add(9);
            recommendations.add(4);
            recommendations.add(6);
            recommendations.add(10);
            recommendations.add(5);
            recommendations.add(11);
            recommendations.add(12);
        } else {
            if (riskScore <= 1) {
                recommendations.add(8);
            } else {
                recommendations.add(13);
            }
            recommendations.add(1);
            recommendations.add(9);
            recommendations.add(4);
            recommendations.add(6);
            recommendations.add(10);
            recommendations.add(5);
            recommendations.add(14);
            recommendations.add(8);
        }
        loadRecommendations(recommendations);
    }

    private void loadRecommendations(final ArrayList<Integer> recommendations) {
        final ArrayList<String> recommendationStrings = new ArrayList<>();
        recommendationRef.child(RECOMMENDATIONS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(Integer i: recommendations){
                    recommendationStrings.add(snapshot.child(String.valueOf(i)).getValue(String.class));
                }
                Recommendation recommendation = new Recommendation(recommendationStrings);
                recommendationRecycler.setAdapter(recommendation);
                recommendationRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void calculateScore(Integer second) {
        if (total < 6) {
            if (second != 0)
                riskScore *= second * 1.5;
        } else {
            illnessScore += second;
        }
        ++total;
        showQuestions();
    }
}
