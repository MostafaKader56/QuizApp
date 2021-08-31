package com.technoship.quizapp.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.technoship.quizapp.R;
import com.technoship.quizapp.model.QuestionsModel;
import com.technoship.quizapp.model.QuizListModel;
import com.technoship.quizapp.viewmodel.QuestionsViewModel;
import com.technoship.quizapp.viewmodel.QuizListViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuizFragment extends Fragment {

    private List<QuestionsModel> questionToAnswer;

    private ImageButton closeImageButton;
    private Button optionA, optionB, optionC, nextQuestionButton;
    private ProgressBar progress;
    private TextView quizTitle, quizQuestionsNum, progressNum, questionText, quizQuestionFeedback;

    private CountDownTimer counterDownTimer;
    private boolean isCounterRunning;
    private int correctAnswers, wrongAnswers, missedAnswers, currentQuestionIndex;
    private boolean quizFinished;
    private NavController navController;
    private Button rightAnswerBtn;
    private int timeToAnswerCurrentQuestion;
    private AdView mAdView;


    public QuizFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quiz, container, false);
        mAdView = view.findViewById(R.id.adView);
        closeImageButton = view.findViewById(R.id.quiz_closeImageButton);
        optionA = view.findViewById(R.id.quiz_option_one);
        optionB = view.findViewById(R.id.quiz_option_two);
        optionC = view.findViewById(R.id.quiz_option_three);
        nextQuestionButton = view.findViewById(R.id.quiz_next_btn);
        progress = view.findViewById(R.id.quiz_question_progress);
        quizTitle = view.findViewById(R.id.quiz_title);
        quizQuestionsNum = view.findViewById(R.id.quiz_questions_number);
        progressNum = view.findViewById(R.id.quiz_time_progress);
        questionText = view.findViewById(R.id.quiz_question);
        quizQuestionFeedback = view.findViewById(R.id.quiz_question_feedback);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MobileAds.initialize(requireContext(), initializationStatus -> { });
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        QuizListModel quizItem = QuizFragmentArgs.fromBundle(getArguments()).getQuizItem();

        QuestionsViewModel questionsViewModel = new ViewModelProvider(requireActivity()).get(QuestionsViewModel.class);
        questionsViewModel.getQuestionsModelData().observe(getViewLifecycleOwner(), questionsModels -> {
            questionToAnswer = questionsModels;
            Collections.shuffle(questionToAnswer);
            currentQuestionIndex = 0;
            loadUI();
            startTimer(questionToAnswer.get(currentQuestionIndex).getTime());
            quizTitle.setText(quizItem.getName());
        });
        questionsViewModel.getQuestions(quizItem.getQuizId());

        navController = Navigation.findNavController(view);

        optionA.setOnClickListener(view1 -> optionSelected(optionA, questionToAnswer.get(currentQuestionIndex).getAnswer(), questionToAnswer.get(currentQuestionIndex).getAnswerInterpretation()));
        optionB.setOnClickListener(view12 -> optionSelected(optionB, questionToAnswer.get(currentQuestionIndex).getAnswer(), questionToAnswer.get(currentQuestionIndex).getAnswerInterpretation()));
        optionC.setOnClickListener(view13 -> optionSelected(optionC, questionToAnswer.get(currentQuestionIndex).getAnswer(), questionToAnswer.get(currentQuestionIndex).getAnswerInterpretation()));

        closeImageButton.setOnClickListener(view15 -> onBackPressed());

        nextQuestionButton.setOnClickListener(view14 -> {
            if (quizFinished){
                ProgressDialog progressDialog = new ProgressDialog(requireContext());
                progressDialog.setTitle(R.string.loading);
                progressDialog.setMessage(getString(R.string.calculate_result_loading));
                progressDialog.setCancelable(false);

                QuizListViewModel quizListViewModel = new ViewModelProvider(requireActivity()).get(QuizListViewModel.class);
                quizListViewModel.orderIncreaseOrderOfThisQuiz(quizItem.getQuizId(), quizItem.getOrder());
                progressDialog.show();

                quizListViewModel.getIncreaseOrderData().observe(getViewLifecycleOwner(), integer -> {
                    if (integer != 1) {
                        Toast.makeText(requireContext(), getString(R.string.poot_connection), Toast.LENGTH_SHORT).show();
                    }

                    QuizFragmentDirections.ActionQuizFragmentToResultFragment action =
                            QuizFragmentDirections.actionQuizFragmentToResultFragment();
                    action.setCorrectAnswer(correctAnswers);
                    action.setWrongAnswer(wrongAnswers);
                    action.setMissedAnswer(missedAnswers);
                    action.setQuizId(quizItem.getQuizId());
                    navController.navigate(action);

                    progressDialog.hide();
                });
            }
            else {
                currentQuestionIndex++;
                steadyToNextQuestion();
                loadUI();
                startTimer(questionToAnswer.get(currentQuestionIndex).getTime());
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBackPressed();
            }
        });
    }

    private void onBackPressed(){
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.exit_alert_title)
                .setMessage(R.string.exit_alert_message)
                .setPositiveButton(R.string.exit_alert_btn1, (dialogInterface, i) -> navController.popBackStack())
                .setNegativeButton(R.string.exit_alert_btn2, (dialogInterface, i) -> { })
                .create()
                .show();
    }

    private void steadyToNextQuestion() {
        isCounterRunning = false;
        nextQuestionButton.setVisibility(View.GONE);
        quizQuestionFeedback.setVisibility(View.GONE);
        optionA.setBackgroundResource(R.drawable.outline_light_btn_bg);
        optionB.setBackgroundResource(R.drawable.outline_light_btn_bg);
        optionC.setBackgroundResource(R.drawable.outline_light_btn_bg);
        optionA.setTextColor(getResources().getColor(R.color.colorLightText));
        optionB.setTextColor(getResources().getColor(R.color.colorLightText));
        optionC.setTextColor(getResources().getColor(R.color.colorLightText));
    }

    private void optionSelected(Button button, String rightAnswer, String answerInterpretation){
        button.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

        if (questionToAnswer.size()-1 == currentQuestionIndex){
            nextQuestionButton.setText(R.string.finish_button_text);
            quizFinished = true;
        }

        if (button.getText().equals(rightAnswer)){
            quizQuestionFeedback.setText(R.string.correct_answer);
            quizQuestionFeedback.setTextColor(getResources().getColor(R.color.colorPrimary));
            button.setBackgroundResource(R.drawable.correct_answer_btn_bg);
            correctAnswers++;
        }
        else {
            quizQuestionFeedback.setText(answerInterpretation);
            quizQuestionFeedback.setTextColor(getResources().getColor(R.color.colorAccent1));
            button.setBackgroundResource(R.drawable.wrong_answer_btn_bg);
            rightAnswerBtn.setBackgroundResource(R.drawable.correct_answer_unclicked_btn_bg);
            wrongAnswers++;
        }
        isCounterRunning = false;
        counterDownTimer.cancel();
        disableOptions();
    }

    private void loadUI() {
        quizQuestionsNum.setText(String.valueOf(currentQuestionIndex+1));
        enableOptions();

        loadQuestion();
        progress.setVisibility(View.VISIBLE);
        progressNum.setVisibility(View.VISIBLE);
    }

    private void loadQuestion() {
        questionText.setText(questionToAnswer.get(currentQuestionIndex).getQuestion());

        List<Button> buttons = new ArrayList<>();
        buttons.add(optionA);
        buttons.add(optionB);
        buttons.add(optionC);

        int random = new Random().nextInt(3);
        buttons.get(random).setText(questionToAnswer.get(currentQuestionIndex).getAnswer());
        rightAnswerBtn = buttons.get(random);
        buttons.remove(random);
        random = new Random().nextInt(2);
        buttons.get(random).setText(questionToAnswer.get(currentQuestionIndex).getOption_a());
        buttons.remove(random);
        buttons.get(0).setText(questionToAnswer.get(currentQuestionIndex).getOption_b());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isCounterRunning && counterDownTimer != null) {
            counterDownTimer.cancel();
            counterDownTimer = new CountDownTimer(Integer.parseInt(progressNum.getText().toString())*1000, 10) {
                @Override
                public void onTick(long l) {
                    isCounterRunning = true;
                    progressNum.setText(String.valueOf((l/1000)));
                    progress.setProgress((int) (l/(timeToAnswerCurrentQuestion*10)));
                }

                @Override
                public void onFinish() {
                    disableOptions();
                    isCounterRunning = false;
                    missedAnswers++;
                    quizQuestionFeedback.setText(questionToAnswer.get(currentQuestionIndex).getAnswerInterpretation());
                    quizQuestionFeedback.setTextColor(getResources().getColor(R.color.colorAccent1));
                    rightAnswerBtn.setBackgroundResource(R.drawable.correct_answer_unclicked_btn_bg);
                }
            }.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (counterDownTimer != null) counterDownTimer.cancel();
    }

    private void startTimer(long timeToAnswer) {
        timeToAnswerCurrentQuestion = (int) timeToAnswer;
        progressNum.setText(String.valueOf(timeToAnswer));

        progress.setProgress(100);

        if (!isCounterRunning){
            counterDownTimer = new CountDownTimer(timeToAnswer*1000, 10){

                @Override
                public void onTick(long l) {
                    isCounterRunning = true;
                    progressNum.setText(String.valueOf((l/1000)));
                    progress.setProgress((int) (l/(timeToAnswer*10)));
                }

                @Override
                public void onFinish() {
                    disableOptions();
                    isCounterRunning = false;
                    missedAnswers++;
                    quizQuestionFeedback.setText(questionToAnswer.get(currentQuestionIndex).getAnswerInterpretation());
                    quizQuestionFeedback.setTextColor(getResources().getColor(R.color.colorAccent1));
                    rightAnswerBtn.setBackgroundResource(R.drawable.correct_answer_unclicked_btn_bg);
                }
            }.start();
        }
    }

    private void enableOptions() {
        optionA.setVisibility(View.VISIBLE);
        optionB.setVisibility(View.VISIBLE);
        optionC.setVisibility(View.VISIBLE);

        optionA.setEnabled(true);
        optionB.setEnabled(true);
        optionC.setEnabled(true);

        quizQuestionFeedback.setVisibility(View.INVISIBLE);
        nextQuestionButton.setVisibility(View.INVISIBLE);
        nextQuestionButton.setEnabled(false);
    }

    private void disableOptions() {
        optionA.setEnabled(false);
        optionB.setEnabled(false);
        optionC.setEnabled(false);

        quizQuestionFeedback.setVisibility(View.VISIBLE);
        nextQuestionButton.setVisibility(View.VISIBLE);
        nextQuestionButton.setEnabled(true);
    }
}