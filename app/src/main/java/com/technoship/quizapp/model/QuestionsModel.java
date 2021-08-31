package com.technoship.quizapp.model;

import com.google.firebase.firestore.DocumentId;

public class QuestionsModel {
    @DocumentId
    private String questionId;
    private String question, answer, option_a, option_b, answerInterpretation;
    private int time;

    public QuestionsModel() { }

    public QuestionsModel(String question, String answer, String option_a, String option_b, String answerInterpretation,int time) {
        this.answerInterpretation = answerInterpretation;
        this.question = question;
        this.answer = answer;
        this.option_a = option_a;
        this.option_b = option_b;
        this.time = time;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getOption_a() {
        return option_a;
    }

    public String getOption_b() {
        return option_b;
    }

    public int getTime() {
        return time;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setOption_a(String option_a) {
        this.option_a = option_a;
    }

    public void setOption_b(String option_b) {
        this.option_b = option_b;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getAnswerInterpretation() {
        return answerInterpretation;
    }

    public void setAnswerInterpretation(String answerInterpretation) {
        this.answerInterpretation = answerInterpretation;
    }
}
