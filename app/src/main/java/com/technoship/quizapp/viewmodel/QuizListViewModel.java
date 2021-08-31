package com.technoship.quizapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.technoship.quizapp.model.QuestionsModel;
import com.technoship.quizapp.model.QuizListModel;
import com.technoship.quizapp.repo.FirebaseRepository;
import com.technoship.quizapp.repo.SharedPreferencesRepository;

import java.util.ArrayList;
import java.util.List;

public class QuizListViewModel extends AndroidViewModel {

    private final FirebaseRepository firebaseRepository;
    private final SharedPreferencesRepository sharedPreferencesRepository;
    private final MutableLiveData<Boolean> quizUploadData;
    private final MutableLiveData<List<QuizListModel>> quizListModelData;
    private final MutableLiveData<Integer> increaseOrderData;
    private final MutableLiveData<String> quizLastScore;

    public QuizListViewModel(@NonNull Application application) {
        super(application);
        quizListModelData = new MutableLiveData<>();
        increaseOrderData = new MutableLiveData<>();
        quizLastScore = new MutableLiveData<>();
        quizUploadData = new MutableLiveData<>();
        firebaseRepository = new FirebaseRepository();
        sharedPreferencesRepository = new SharedPreferencesRepository(getApplication());
        firebaseRepository.setOnLoadQuizListLoaded(new FirebaseRepository.OnLoadQuizListLoaded() {
            @Override
            public void quizListDataAdded(List<QuizListModel> quizListModels) {
                quizListModelData.setValue(quizListModels);
            }

            @Override
            public void onError(Exception e) {
                // TODO: will handle it next time
            }
        });

        firebaseRepository.setOnIncreaseQuizOrder(new FirebaseRepository.OnIncreaseQuizOrder() {
            @Override
            public void onOrderIncreased(int check) {
                increaseOrderData.setValue(check);
            }

            @Override
            public void onError(Exception e) {
                // TODO: will handle it next time
                increaseOrderData.setValue(-1);
            }
        });
        firebaseRepository.setOnUploadQuiz(new FirebaseRepository.OnUploadQuiz() {
            @Override
            public void onUploadQuiz() {
                quizUploadData.setValue(true);
            }

            @Override
            public void onError(Exception e) {
                quizUploadData.setValue(false);
            }
        });

        /* if you use this view-model for other needs in another fragment or activity should
        remove this method call from here and make method for calling it in QuizListViewModel
        class but for now this view-model used only for listFragment. */
//        firebaseRepository.getQuizList();
//        i removed it because this view-model used on Quiz fragment
    }

    public void orderQuizListData(){
        firebaseRepository.getQuizList();
    }

    public void orderIncreaseOrderOfThisQuiz(String quizId, int oldOrder)
        { firebaseRepository.increaseOrderOfThisQuiz(quizId, oldOrder); }

    public LiveData<List<QuizListModel>> getQuizListModelData() {
        return quizListModelData;
    }

    public MutableLiveData<Boolean> getQuizUploadData() {
        return quizUploadData;
    }

    public MutableLiveData<Integer> getIncreaseOrderData() {
        return increaseOrderData;
    }

    public MutableLiveData<String> getQuizLastScore() {
        return quizLastScore;
    }

    public void setQuiz(QuizListModel quiz, ArrayList<QuestionsModel> questions){
        firebaseRepository.setQuiz(quiz, questions);
    }

    public void getQuizLastScore(String quizId) {
        quizLastScore.setValue(sharedPreferencesRepository.getQuizLastScore(quizId));
    }

    public void setQuizLastScore(String quizId, String value) {
        sharedPreferencesRepository.setQuizLastScore(quizId, value);
    }
}
