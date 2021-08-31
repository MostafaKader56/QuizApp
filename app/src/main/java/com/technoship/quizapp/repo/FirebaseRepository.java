package com.technoship.quizapp.repo;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.technoship.quizapp.model.QuestionsModel;
import com.technoship.quizapp.model.QuizListModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

public class FirebaseRepository {

    private OnLoadQuizListLoaded onLoadQuizListLoaded;
    private OnLoadQuestionsLoaded onLoadQuestionsLoaded;
    private OnIncreaseQuizOrder onIncreaseQuizOrder;
    private OnUploadQuiz onUploadQuiz;
    private final FirebaseFirestore db;
    private final CollectionReference quizRef;

    public FirebaseRepository() {
        db = FirebaseFirestore.getInstance();
        quizRef = db.collection("QuizList");
    }

    public void setOnLoadQuestionsLoaded(OnLoadQuestionsLoaded onLoadQuestionsLoaded) {
        this.onLoadQuestionsLoaded = onLoadQuestionsLoaded;
    }

    public void setOnLoadQuizListLoaded(OnLoadQuizListLoaded onLoadQuizListLoaded) {
        this.onLoadQuizListLoaded = onLoadQuizListLoaded;
    }

    public void setOnIncreaseQuizOrder(OnIncreaseQuizOrder onIncreaseQuizOrder) {
        this.onIncreaseQuizOrder = onIncreaseQuizOrder;
    }

    public void setOnUploadQuiz(OnUploadQuiz onUploadQuiz) {
        this.onUploadQuiz = onUploadQuiz;
    }

    public void getQuizList(){
        quizRef.orderBy("order", Query.Direction.DESCENDING).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                onLoadQuizListLoaded.quizListDataAdded(task.getResult().toObjects(QuizListModel.class));
            }
            else {
                onLoadQuizListLoaded.onError(task.getException());
            }
        });
    }

    public void getQuestionsList(String quizId){
        quizRef.document(quizId).collection("questions")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        onLoadQuestionsLoaded.questionModelsLoaded(task.getResult().toObjects(QuestionsModel.class));
                    }
                    else {
                        onLoadQuestionsLoaded.onError(task.getException());
                    }
                });
    }

    public void increaseOrderOfThisQuiz(String quizId, int oldOrder) {
        AtomicInteger check = new AtomicInteger();
        DocumentReference quizToIncreaseOrder = quizRef.document(quizId);
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            transaction.update(quizToIncreaseOrder, "order", oldOrder+1);
            check.set(1);
            return null;
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                onIncreaseQuizOrder.onOrderIncreased(check.get());
            }
            else {
                onIncreaseQuizOrder.onError(task.getException());
            }
        });
    }

    public void setQuiz(QuizListModel quiz, ArrayList<QuestionsModel> questions) {
        String quizId = dateForFileStorageReference();

        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        batch.set(quizRef.document(quizId), quiz);

        for (QuestionsModel question: questions){
            batch.set(quizRef.document(quizId).collection("questions").document(), question);
        }

        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                onUploadQuiz.onUploadQuiz();
            }
            else {
                onUploadQuiz.onError(task.getException());
            }
        });
    }

    public interface OnLoadQuizListLoaded {
        void quizListDataAdded(List<QuizListModel> quizListModels);
        void onError(Exception e);
    }

    public interface OnLoadQuestionsLoaded {
        void questionModelsLoaded(List<QuestionsModel> questionsModels);
        void onError(Exception e);
    }
    public interface OnIncreaseQuizOrder {
        void onOrderIncreased(int check);
        void onError(Exception e);
    }

    public interface OnUploadQuiz {
        void onUploadQuiz();
        void onError(Exception e);
    }

    public static String dateForFileStorageReference() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhsss", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(new Date());
    }
}
