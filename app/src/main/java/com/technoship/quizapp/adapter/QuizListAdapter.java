package com.technoship.quizapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.technoship.quizapp.R;
import com.technoship.quizapp.model.QuizListModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.MyViewHolder> {

    private List<QuizListModel> quizListModels;
    private final OnQuizItemClicked quizItemClicked;

    public QuizListAdapter(OnQuizItemClicked quizItemClicked) {
        this.quizItemClicked = quizItemClicked;
    }

    public void setQuizListModels(List<QuizListModel> quizListModels) {
        this.quizListModels = quizListModels;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        holder.title.setText(quizListModels.get(position).getName());
        holder.desc.setText(quizListModels.get(position).getDesc());
        holder.difficulty.setText(String.valueOf(quizListModels.get(position).getLevel()));

        Glide.with(holder.itemView.getContext())
                .load(quizListModels.get(position).getImage())
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if (quizListModels == null){ return 0; }
        else return quizListModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView title, desc, difficulty;
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.list_ImageView);
            title = itemView.findViewById(R.id.list_Title);
            desc = itemView.findViewById(R.id.list_Description);
            difficulty = itemView.findViewById(R.id.list_Difficulty);

            itemView.findViewById(R.id.list_ViewButton).setOnClickListener(view -> {
                int position=getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && quizItemClicked != null){
                    quizItemClicked.clicked(quizListModels.get(position), position);
                }
            });

        }
    }

    public interface OnQuizItemClicked{
        void clicked(QuizListModel quizItem, int position);
    }
}
