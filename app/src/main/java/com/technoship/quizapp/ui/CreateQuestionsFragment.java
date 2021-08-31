package com.technoship.quizapp.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.technoship.quizapp.R;
import com.technoship.quizapp.adapter.QuestionsCreationAdapter;
import com.technoship.quizapp.model.QuestionsModel;
import com.technoship.quizapp.model.QuizListModel;
import com.technoship.quizapp.viewmodel.QuizListViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CreateQuestionsFragment extends Fragment {

    private NavController navController;

    public CreateQuestionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private QuizListModel quizItem;
    private Button submitBtn;
    private QuestionsCreationAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_questions, container, false);
        submitBtn = view.findViewById(R.id.submit_btn);
        return view;
    }

    private QuizListViewModel quizListViewModel;
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        quizItem = CreateQuestionsFragmentArgs.fromBundle(getArguments()).getQuizItem();

        quizListViewModel = new ViewModelProvider(requireActivity()).get(QuizListViewModel.class);

        submitBtn.setOnClickListener(view1 -> {
            if (adapter.isQuestionsReady()){
                startBatchWrite(adapter.getQuestions());
            }
            else {
                Toast.makeText(requireContext(), adapter.getMistakeToToast(), Toast.LENGTH_SHORT).show();
            }
        });

        adapter = new QuestionsCreationAdapter(requireContext(), quizItem.getQuestionsNum());

        RecyclerView listView = view.findViewById(R.id.listView);
        listView.setLayoutManager(new LinearLayoutManager(requireContext()));
        listView.setHasFixedSize(true);

        listView.setAdapter(adapter);
        navController =  Navigation.findNavController(view);
    }

    private void startBatchWrite(ArrayList<QuestionsModel> questions) {

        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(R.string.uploading));
        progressDialog.setMessage(getString(R.string.uploading_quiz));
        progressDialog.show();

        quizListViewModel.getQuizUploadData().observe(getViewLifecycleOwner(), done -> {
            if (done){
                new AlertDialog.Builder(requireContext())
                        .setTitle(R.string.alert_quiz_uploaded_title)
                        .setMessage(R.string.alert_quiz_uploaded_message)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok, (dialogInterface, i) -> navController.popBackStack())
                        .create()
                        .show();
            }
            else {
                Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
            progressDialog.hide();
        });
        quizListViewModel.setQuiz(quizItem, questions);
    }
}