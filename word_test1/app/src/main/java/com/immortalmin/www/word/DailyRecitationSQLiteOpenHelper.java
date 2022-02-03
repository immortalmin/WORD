package com.immortalmin.www.word;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DailyRecitationSQLiteOpenHelper extends SQLiteOpenHelper {
    private static String name = "/data/data/com.immortalmin.www.word/databases/daily_recitation.db";
    private static Integer version = 1;
    public DailyRecitationSQLiteOpenHelper(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table daily_recitation(rid integer primary key autoincrement,uid integer not null,record_date date not null,review_num integer default 0,recite_num integer default 0,grasp_num integer default 0,is_synchronized bool default 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
