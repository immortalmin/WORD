package com.immortalmin.www.word;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UsageTimeSQLiteOpenHelper extends SQLiteOpenHelper {
    private static String name = "usageTime.db";
    private static Integer version = 1;
    UsageTimeSQLiteOpenHelper(Context context){
        super(context, name,null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table usageTime(id integer primary key autoincrement,udate date not null,utime int default 0,update_date date,isSynchronized tinyint default 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase,int i,int i1){

    }
}
