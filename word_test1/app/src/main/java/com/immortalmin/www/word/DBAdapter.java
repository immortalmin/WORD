package com.immortalmin.www.word;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBAdapter {
    private static final String DB_NAME = "word_test.db";
    private static final String DB_TABLE = "words";
    private static final int DB_VERSION = 1;
    private SQLiteDatabase db;
    private final Context context;
    private DBOpenHelper dbOpenHelper;
    public DBAdapter(Context _context){
        context = _context;
    }
    public void open() throws SQLException{
        dbOpenHelper = new DBOpenHelper(context,DB_NAME,null,DB_VERSION);
        try{
            db = dbOpenHelper.getWritableDatabase();
        }catch (SQLException exp){
            db = dbOpenHelper.getReadableDatabase();
        }
    }

}
