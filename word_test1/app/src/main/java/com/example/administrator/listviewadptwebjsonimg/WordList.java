package com.example.administrator.listviewadptwebjsonimg;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WordList implements Parcelable {

    private String id;
    private String word_group;
    private String C_meaning;
    private int correct_times;
    private int error_times;
    private int prof_flag;


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getWord_group() {
        return word_group;
    }

    public void setWord_group(String word_group) {
        this.word_group = word_group;
    }

    public String getC_meaning() {
        return C_meaning;
    }

    public void setC_meaning(String c_meaning) {
        C_meaning = c_meaning;
    }

    public int getCorrect_times() {
        return correct_times;
    }

    public void setCorrect_times(int correct_times) {
        this.correct_times = correct_times;
    }

    public int getError_times() {
        return error_times;
    }

    public void setError_times(int error_times) {
        this.error_times = error_times;
    }

    public int getProf_flag() {
        return prof_flag;
    }

    public void setProf_flag(int prof_flag) {
        this.prof_flag = prof_flag;
    }

    @Override
    public String toString() {
        return "WordList{" +
                "id='" + id + '\'' +
                ", word_group='" + word_group + '\'' +
                ", C_meaning='" + C_meaning + '\'' +
                ", correct_times=" + correct_times +
                ", error_times=" + error_times +
                ", prof_flag=" + prof_flag +
                '}';
    }

    public Map<String,Object> toMap() {
        Map<String,Object> word = new HashMap<String,Object>();
        word.put("id",id);
        word.put("word_group",word_group);
        word.put("C_meaning",C_meaning);
        word.put("correct_times",correct_times);
        word.put("error_times",error_times);
        word.put("prof_flag",prof_flag);
        return word;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    /*
    将想要传递的数据写入到 Parcel 容器中
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(word_group);
        parcel.writeString(C_meaning);
        parcel.writeInt(correct_times);
        parcel.writeInt(error_times);
        parcel.writeInt(prof_flag);
    }

    public static final Creator<WordList> CREATOR = new Creator<WordList>() {
        /*
        用于将写入 Parcel 容器中的数据读出来，用读出来的数据实例化一个对象，并且返回
         */
        @Override
        public WordList createFromParcel(Parcel in) {
            WordList wordList = new WordList();
            wordList.setId(in.readString());
            wordList.setWord_group(in.readString());
            wordList.setC_meaning(in.readString());
            wordList.setCorrect_times(in.readInt());
            wordList.setError_times(in.readInt());
            wordList.setProf_flag(in.readInt());
            return wordList;
        }

        /*
        创建一个长度为 size 的数组并且返回,供外部类反序列化本类数组使用
         */
        @Override
        public WordList[] newArray(int size) {
            return new WordList[size];
        }
    };
}