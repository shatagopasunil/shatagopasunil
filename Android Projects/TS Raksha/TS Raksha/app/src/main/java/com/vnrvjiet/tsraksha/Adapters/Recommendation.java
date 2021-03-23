package com.vnrvjiet.tsraksha.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vnrvjiet.tsraksha.R;

import java.util.ArrayList;

public class Recommendation extends RecyclerView.Adapter<Recommendation.RecommendationViewHolder> {
    private ArrayList<String> arrayList;

    public Recommendation(ArrayList<String> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public RecommendationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommendation_layout,parent,false);
        return new RecommendationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendationViewHolder holder, int position) {
        holder.recommendation.setText(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class RecommendationViewHolder extends RecyclerView.ViewHolder {
        private TextView recommendation;
        public RecommendationViewHolder(@NonNull View itemView) {
            super(itemView);
            recommendation = itemView.findViewById(R.id.title_recommendation);
        }
    }
}
