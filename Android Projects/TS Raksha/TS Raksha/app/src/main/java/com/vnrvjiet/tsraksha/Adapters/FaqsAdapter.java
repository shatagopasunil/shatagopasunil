package com.vnrvjiet.tsraksha.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.vnrvjiet.tsraksha.Models.FaqsModel;
import com.vnrvjiet.tsraksha.R;

public class FaqsAdapter extends FirebaseRecyclerAdapter<FaqsModel, FaqsAdapter.FaqsHolder> {

    public FaqsAdapter(@NonNull FirebaseRecyclerOptions<FaqsModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final FaqsHolder holder, int position, @NonNull FaqsModel model) {
        holder.questionText.setText(model.getQuestion());
        holder.answerText.setText(model.getAnswer());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.answerText.getVisibility() == View.GONE) {
                    holder.answerText.setVisibility(View.VISIBLE);
                    holder.imageView.setImageResource(R.drawable.open_details);
                }
                else {
                    holder.answerText.setVisibility(View.GONE);
                    holder.imageView.setImageResource(R.drawable.close_details);

                }
            }
        });
    }

    @NonNull
    @Override
    public FaqsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faq_layout,parent,false);
        return new FaqsHolder(view);
    }

    public class FaqsHolder extends RecyclerView.ViewHolder {
        private TextView questionText, answerText;
        private ImageView imageView;
        private LinearLayout linearLayout;
        public FaqsHolder(@NonNull View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.faq_question);
            answerText = itemView.findViewById(R.id.faq_answer);
            imageView = itemView.findViewById(R.id.open_close_details);
            linearLayout = itemView.findViewById(R.id.expandable_question);
        }
    }
}
