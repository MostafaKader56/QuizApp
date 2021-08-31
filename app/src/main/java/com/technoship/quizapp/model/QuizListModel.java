package com.technoship.quizapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentId;

public class QuizListModel implements Parcelable {
    @DocumentId
    private String quizId;
    private String name, desc, image, writerName;
    private int questionsNum, order, level;
    private boolean visible;

    public QuizListModel() { }

    public QuizListModel(String name, String desc, String image, int level, String writerName, int questionsNum) {
        this.name = name;
        this.desc = desc;
        this.image = image;
        this.level = level;
        this.writerName = writerName;
        this.questionsNum = questionsNum;
        this.visible = true;
    }

    protected QuizListModel(Parcel in) {
        quizId = in.readString();
        name = in.readString();
        desc = in.readString();
        image = in.readString();
        writerName = in.readString();
        questionsNum = in.readInt();
        order = in.readInt();
        level = in.readInt();
        visible = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(quizId);
        dest.writeString(name);
        dest.writeString(desc);
        dest.writeString(image);
        dest.writeString(writerName);
        dest.writeInt(questionsNum);
        dest.writeInt(order);
        dest.writeInt(level);
        dest.writeByte((byte) (visible ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<QuizListModel> CREATOR = new Creator<QuizListModel>() {
        @Override
        public QuizListModel createFromParcel(Parcel in) {
            return new QuizListModel(in);
        }

        @Override
        public QuizListModel[] newArray(int size) {
            return new QuizListModel[size];
        }
    };

    public int getOrder() {
        return order;
    }

    public String getQuizId() {
        return quizId;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getImage() {
        return image;
    }

    public int getLevel() {
        return level;
    }

    public boolean isVisible() {
        return visible;
    }

    public String getWriterName() {
        return writerName;
    }

    public int getQuestionsNum() { return questionsNum; }
}
