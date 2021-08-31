package com.technoship.quizapp.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.technoship.quizapp.model.QuestionsModel;
import com.technoship.quizapp.repo.FirebaseRepository;

import java.util.List;

public class QuestionsViewModel extends ViewModel {
    private final FirebaseRepository firebaseRepository;
    private final MutableLiveData<List<QuestionsModel>> questionsModelData;

    public QuestionsViewModel() {
        questionsModelData = new MutableLiveData<>();
        firebaseRepository = new FirebaseRepository();
        firebaseRepository.setOnLoadQuestionsLoaded(new FirebaseRepository.OnLoadQuestionsLoaded() {
            @Override
            public void questionModelsLoaded(List<QuestionsModel> questionsModels) {
                questionsModelData.setValue(questionsModels);
            }

            @Override
            public void onError(Exception e) {
                // TODO: will handle it next time
            }
        });
    }

    public void getQuestions(String quizId){
        firebaseRepository.getQuestionsList(quizId);
    }

    public MutableLiveData<List<QuestionsModel>> getQuestionsModelData() {
        return questionsModelData;
    }
}
