package com.technoship.quizapp.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.technoship.quizapp.R;
import com.technoship.quizapp.viewmodel.QuizListViewModel;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

public class ResultFragment extends Fragment {

    public ResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    private ProgressBar progressBar;
    private TextView correctText, wrongText, missedText, progressText;
    private Button backHomeBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        progressBar = view.findViewById(R.id.results_progress);
        correctText = view.findViewById(R.id.results_correct_text);
        wrongText = view.findViewById(R.id.results_wrong_text);
        missedText = view.findViewById(R.id.results_missed_text);
        progressText = view.findViewById(R.id.results_precent);
        backHomeBtn = view.findViewById(R.id.results_home_btn);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int correctAnswers = ResultFragmentArgs.fromBundle(getArguments()).getCorrectAnswer();
        int wrongAnswers = ResultFragmentArgs.fromBundle(getArguments()).getWrongAnswer();
        int missedQuestions = ResultFragmentArgs.fromBundle(getArguments()).getMissedAnswer();
        String quizId = ResultFragmentArgs.fromBundle(getArguments()).getQuizId();

        int percentage = correctAnswers * 100 / (correctAnswers + wrongAnswers + missedQuestions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progressBar.setProgress(percentage, true);
        }
        else {
            progressBar.setProgress(percentage);
        }
        correctText.setText(String.valueOf(correctAnswers));
        wrongText.setText(String.valueOf(wrongAnswers));
        missedText.setText(String.valueOf(missedQuestions));
        progressText.setText(percentage+"%");
        backHomeBtn.setOnClickListener(view1 -> Navigation.findNavController(view).popBackStack());

        QuizListViewModel quizListViewModel = new ViewModelProvider(requireActivity()).get(QuizListViewModel.class);
        quizListViewModel.setQuizLastScore(quizId, percentage +"%");
    }
}