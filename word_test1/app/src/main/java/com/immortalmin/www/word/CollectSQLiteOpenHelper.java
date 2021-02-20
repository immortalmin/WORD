package com.immortalmin.www.word;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CollectSQLiteOpenHelper extends SQLiteOpenHelper {
    private static String name = "collect.db";
    private static Integer version = 1;
    CollectSQLiteOpenHelper(Context context){
        super(context, name,null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table collect(id integer primary key autoincrement,cid integer,gid integer,wid integer,word_en text,word_ch text,correct_times integer,error_times integer,last_date date,review_date date,dict_source int,update_date date)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase,int i,int i1){

    }
}
