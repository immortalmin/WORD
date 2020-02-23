package com.immortalmin.www.word;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {


    String create_tb_users="CREATE TABLE tb_shopinfo (" +
            "    _id          INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    shop_name    TEXT," +
            "    shop_address TEXT," +
            "    shop_price   DOUBLE," +
            "    shop_tel     TEXT" +
            ");";
    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_tb_users);//执行创建表的ddl语句
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
