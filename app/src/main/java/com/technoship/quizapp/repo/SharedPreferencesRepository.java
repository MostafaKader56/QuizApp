package com.technoship.quizapp.repo;

import android.content.Context;
import android.content.SharedPreferences;

import com.technoship.quizapp.R;

public class SharedPreferencesRepository {

    private SharedPreferences lastScoreSharedPref;
    private Context context;

    public SharedPreferencesRepository(Context context){
        lastScoreSharedPref = context.getSharedPreferences("QuizLastScore", Context.MODE_PRIVATE);
        this.context = context;
    }

    public String getQuizLastScore(String quizId) {
        return lastScoreSharedPref.getString(quizId, context.getResources().getString(R.string.na));
    }

    public void setQuizLastScore(String quizId, String value) {
        SharedPreferences.Editor editor = lastScoreSharedPref.edit();
        editor.putString(quizId, value);
        editor.apply();
    }
}
