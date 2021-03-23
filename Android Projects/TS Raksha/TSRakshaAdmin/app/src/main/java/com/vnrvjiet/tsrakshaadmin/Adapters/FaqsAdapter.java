package com.vnrvjiet.tsrakshaadmin.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.vnrvjiet.tsrakshaadmin.Models.FaqsModel;
import com.vnrvjiet.tsrakshaadmin.R;

public class FaqsAdapter extends FirebaseRecyclerAdapter<FaqsModel, FaqsAdapter.FaqsHolder> {

    private Context context;

    public FaqsAdapter(@NonNull FirebaseRecyclerOptions<FaqsModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final FaqsHolder holder, final int position, @NonNull FaqsModel model) {
        holder.questionText.setText(model.getQuestion());
        holder.answerText.setText(model.getAnswer());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getRef(position).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();
                            }
                        });
                    }
                });
                builder.show();
                return true;
            }
        });
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
        context = view.getContext();
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
