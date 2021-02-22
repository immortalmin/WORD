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
        db.execSQL("create table collect(id integer primary key autoincrement,cid integer,gid integer default 0,wid integer,word_en text,word_ch text,correct_times integer default 0,error_times integer default 0,last_date date,review_date date,dict_source int,update_date date,isSynchronized tinyint default 0,isCollect tinyint default 1)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase,int i,int i1){

    }
}
