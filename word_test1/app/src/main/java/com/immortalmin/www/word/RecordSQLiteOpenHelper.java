package com.immortalmin.www.word;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecordSQLiteOpenHelper extends SQLiteOpenHelper {
    private static String name = "record.db";
    private static Integer version = 1;
    public RecordSQLiteOpenHelper(Context context){
        super(context, name,null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        //db.execSQL("create table records(id integer primary key autoincrement,wid integer,word_en text,word_ch text,dict_source integer)");
        db.execSQL("create table records(id integer primary key autoincrement,wid integer,word_en text,word_ch text,dict_source integer,cid integer,gid integer,correct_times integer,error_times integer,last_date date)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase,int i,int i1){

    }
}
