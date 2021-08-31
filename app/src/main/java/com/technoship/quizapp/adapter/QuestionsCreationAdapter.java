package com.technoship.quizapp.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.technoship.quizapp.R;
import com.technoship.quizapp.model.QuestionsModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class QuestionsCreationAdapter extends RecyclerView.Adapter<QuestionsCreationAdapter.MyHolder> {

    private final Context context;
    private final int size;
    private final ArrayList<QuestionsModel> models;

    public QuestionsCreationAdapter(Context context, int size) {
        this.context = context;
        this.size = size;
        this.models = new ArrayList<>();
        for (int i = 0; i < size ; i++) models.add(new QuestionsModel());
    }

    @NonNull
    @NotNull
    @Override
    public QuestionsCreationAdapter.MyHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.single_question_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull QuestionsCreationAdapter.MyHolder holder, int position) {
        holder.title.setText(holder.itemView.getContext().getString(R.string.questionTitle, String.valueOf(position+1)));
        holder.questionEditText.setText(models.get(position).getQuestion());
        holder.rightAnswerEditText.setText(models.get(position).getAnswer());
        holder.wrong1EditText.setText(models.get(position).getOption_a());
        holder.wrong2EditText.setText(models.get(position).getOption_b());
        holder.timeEditText.setText(String.valueOf(models.get(position).getTime()));
    }

    @Override
    public int getItemCount() {
        return size;
    }

    private String mistakeToToast;

    public String getMistakeToToast() {
        return mistakeToToast;
    }

    public boolean isQuestionsReady(){
        for (int index = 0; index < models.size(); index++){
            QuestionsModel question = models.get(index);
            if (question.getQuestion().isEmpty()){
                mistakeToToast = context.getString(R.string.wrong_question_header, String.valueOf(index+1));
                return false;
            }
            else if (question.getAnswer().isEmpty()){
                mistakeToToast = context.getString(R.string.wrong_question_right_answer, String.valueOf(index+1));
                return false;
            }
            else if (question.getOption_a().isEmpty()){
                mistakeToToast = context.getString(R.string.wrong_question_wrong1, String.valueOf(index+1));
                return false;
            }
            else if (question.getOption_b().isEmpty()){
                mistakeToToast = context.getString(R.string.wrong_question_wrong2, String.valueOf(index+1));
                return false;
            }
            else if (question.getTime() <= 0 || question.getTime() > 600){
                mistakeToToast = context.getString(R.string.wrong_question_wrong_time, String.valueOf(index+1));
                return false;
            }
        }
        return true;
    }

    public ArrayList<QuestionsModel> getQuestions(){
        return models;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextInputEditText questionEditText, rightAnswerEditText, wrong1EditText, wrong2EditText, answerInterpretation, timeEditText;
        public MyHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.questionTitle);
            answerInterpretation = itemView.findViewById(R.id.inputEditTextAnswerInterpretation);
            questionEditText = itemView.findViewById(R.id.inputEditTextQuestion);
            rightAnswerEditText = itemView.findViewById(R.id.inputEditTextRightAnswer);
            wrong1EditText = itemView.findViewById(R.id.inputEditTextWrong1);
            wrong2EditText = itemView.findViewById(R.id.inputEditTextWrong2);
            timeEditText = itemView.findViewById(R.id.inputEditTextTime);

            answerInterpretation.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    models.get(getAdapterPosition()).setAnswerInterpretation(editable.toString());
                }
            });

            questionEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    models.get(getAdapterPosition()).setQuestion(editable.toString());
                }
            });
            rightAnswerEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    models.get(getAdapterPosition()).setAnswer(editable.toString());
                }
            });
            wrong1EditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    models.get(getAdapterPosition()).setOption_a(editable.toString());
                }
            });
            wrong2EditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    models.get(getAdapterPosition()).setOption_b(editable.toString());
                }
            });
            timeEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.toString().isEmpty()){
                        models.get(getAdapterPosition()).setTime(0);
                    }
                    else {
                        models.get(getAdapterPosition()).setTime(Integer.parseInt(Objects.requireNonNull(timeEditText.getText()).toString()));
                    }
                }
            });
        }
    }
}
