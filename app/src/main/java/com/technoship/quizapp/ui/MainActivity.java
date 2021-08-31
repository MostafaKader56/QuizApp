package com.technoship.quizapp.ui;

import android.os.Bundle;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.technoship.quizapp.R;


public class MainActivity extends LocalizationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}