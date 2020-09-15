package com.immortalmin.www.word;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DbDao {
    private Context context;
    private RecordSQLiteOpenHelper helper;
    private SQLiteDatabase db;

    public DbDao(Context context){
        this.context = context;
        if(context==null){
            Log.i("ccc","context is null");
        }else{
            Log.i("ccc","context is not null");
        }
        init();
    }

    private void init(){
        helper = new RecordSQLiteOpenHelper(context);
        queryData("");
    }

    public List<HashMap<String,Object>> queryData(String tempName){

//        List<String> data = new ArrayList<>();
////        模糊查询
//        Cursor cursor = helper.getReadableDatabase().rawQuery("select id as _id,name from records where name like '%"+tempName+"%' order by id desc",null);
//        while(cursor.moveToNext()){
//            String name = cursor.getString(cursor.getColumnIndex("name"));
//            data.add(name);
//        }
//        cursor.close();
//        return data;
        List<HashMap<String,Object>> wordList = new ArrayList<>();
        Cursor cursor = helper.getReadableDatabase().rawQuery("select id,wid,word_en,word_ch,dict_source from records where word_en like '%"+tempName+"%' order by id desc limit 10",null);
        while(cursor.moveToNext()){
            HashMap<String,Object> word = new HashMap<>();
            word.put("id",cursor.getString(cursor.getColumnIndex("id")));
            word.put("wid",cursor.getString(cursor.getColumnIndex("wid")));
            word.put("word_en",cursor.getString(cursor.getColumnIndex("word_en")));
            word.put("word_ch",cursor.getString(cursor.getColumnIndex("word_ch")));
            word.put("dict_source",cursor.getString(cursor.getColumnIndex("dict_source")));
            wordList.add(word);
        }
        cursor.close();
        return wordList;
    }

    /**
     * 检查数据库中是否已经有该条数据（未使用过）
     * @param tempName
     * @return
     */
    public boolean hasData(String tempName){
        //从Record这个表里找到name=tempName的id
//        Cursor cursor = helper.getReadableDatabase().rawQuery("select id as _id,name from records where name = ?",new String[]{tempName});
        Cursor cursor = helper.getReadableDatabase().rawQuery("select wid from records where name=?",new String[]{tempName});
        //判断是否有下一个
        return cursor.moveToNext();
    }

    /**
     * 插入数据
     * @param word
     */
    public void insertData(HashMap<String,Object> word){
        db = helper.getWritableDatabase();
//        db.execSQL("insert into records(name) values('"+tempName+"')");
        int wid = Integer.valueOf(word.get("wid").toString());
        String word_en = word.get("word_en").toString();
        String word_ch = word.get("word_ch").toString();
        int dict_source = Integer.valueOf(word.get("dict_source").toString());
        db.execSQL("insert into records(wid,word_en,word_ch,dict_source) values("+wid+",'"+word_en+"','"+word_ch+"',"+dict_source+")");
        db.close();
    }

    /**
     * 删除一条数据（未使用过）
     * @param name
     * @return
     */
    public int delete(String name){
        //获取数据
        SQLiteDatabase db = helper.getWritableDatabase();
        //执行SQL
        int delete = db.delete("records","name=?",new String[]{name});
//        int delete = db.delete("records","id="+id,null);
        db.close();
        return delete;
    }

    /**
     * 清空所有历史记录
     */
    public void deleteData(){
        db = helper.getWritableDatabase();
        db.execSQL("delete from records");
        db.close();
    }

}
