package com.immortalmin.www.word;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DbDao {
    private Context context;
    private RecordSQLiteOpenHelper helper;
    private SQLiteDatabase db;

    public DbDao(Context context){
        this.context = context;
        init();
    }

    private void init(){
        helper = new RecordSQLiteOpenHelper(context);
        queryData("");
    }

    public List<String> queryData(String tempName){
        List<String> data = new ArrayList<>();
        //模糊查询
        Cursor cursor = helper.getReadableDatabase().rawQuery("select id as _id,name from records where name like '%"+tempName+"%' order by id desc",null);
        while(cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex("name"));
            data.add(name);
        }
        cursor.close();
        return data;
    }

    /**
     * 检查数据库中是否已经有该条数据
     * @param tempName
     * @return
     */
    public boolean hasData(String tempName){
        //从Record这个表里找到name=tempName的id
        Cursor cursor = helper.getReadableDatabase().rawQuery("select id as _id,name from records where name = ?",new String[]{tempName});
        //判断是否有下一个
        return cursor.moveToNext();
    }

    /**
     * 插入数据
     * @param tempName
     */
    public void insertData(String tempName){
        db = helper.getWritableDatabase();
        db.execSQL("insert into records(name) values('"+tempName+"')");
        db.close();
    }

    /**
     * 删除数据
     * @param name
     * @return
     */
    public int delete(String name){
        //获取数据
        SQLiteDatabase db = helper.getWritableDatabase();
        //执行SQL
        int delete = db.delete("records","name=?",new String[]{name});
        db.close();
        return delete;
    }

    public void deleteData(){
        db = helper.getWritableDatabase();
        db.execSQL("delete from records");
        db.close();
    }

}
