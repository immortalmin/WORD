package com.example.administrator.listviewadptwebjsonimg;

import android.os.Parcel;
import android.os.Parcelable;

public class WordList implements Parcelable {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        parcel.writeString(name);
    }

    public static final Creator<WordList> CREATOR = new Creator<WordList>() {
        /*
        用于将写入 Parcel 容器中的数据读出来，用读出来的数据实例化一个对象，并且返回
         */
        @Override
        public WordList createFromParcel(Parcel in) {
            WordList wordList = new WordList();
            wordList.setName(in.readString());
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