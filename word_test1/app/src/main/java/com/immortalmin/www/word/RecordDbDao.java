package com.immortalmin.www.word;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 搜索栏的历史记录
 */
public class RecordDbDao {
    private Context context;
    private RecordSQLiteOpenHelper helper;
    private SQLiteDatabase db;

    RecordDbDao(Context context){
        this.context = context;
        init();
    }

    private void init(){
        helper = new RecordSQLiteOpenHelper(context);
        queryData("");
    }

    /**
     * 历史记录 模糊查询
     * @param queryString
     * @return
     */
    public List<DetailWord> queryData(String queryString){
        String wordQuery="%";
        for(int i=0;i<queryString.length();i++){
            wordQuery=wordQuery+queryString.charAt(i)+"%";
        }
        wordQuery = wordQuery.replaceAll("\"","\"\"");
        List<DetailWord> wordList = new ArrayList<>();
        Cursor cursor = helper.getReadableDatabase().rawQuery("select id,wid,word_en,word_ch,dict_source,cid,gid,correct_times,error_times,last_date from records where word_en like \""+wordQuery+"\" order by query_date desc limit 10",null);
        while(cursor.moveToNext()){
            DetailWord word = new DetailWord();
            word.setHid(cursor.getString(cursor.getColumnIndex("id")));
            word.setWid(cursor.getString(cursor.getColumnIndex("wid")));
            word.setWord_en(cursor.getString(cursor.getColumnIndex("word_en")));
            word.setWord_ch(cursor.getString(cursor.getColumnIndex("word_ch")));
            word.setDict_source(cursor.getString(cursor.getColumnIndex("dict_source")));
            word.setCid(cursor.getString(cursor.getColumnIndex("cid")));
            word.setGid(cursor.getString(cursor.getColumnIndex("gid")));
            word.setCorrect_times(cursor.getString(cursor.getColumnIndex("correct_times")));
            word.setError_times(cursor.getString(cursor.getColumnIndex("error_times")));
            word.setLast_date(cursor.getString(cursor.getColumnIndex("last_date")));

            /*word.put("id",cursor.getString(cursor.getColumnIndex("id")));
            word.put("wid",cursor.getString(cursor.getColumnIndex("wid")));
            word.put("word_en",cursor.getString(cursor.getColumnIndex("word_en")));
            word.put("word_ch",cursor.getString(cursor.getColumnIndex("word_ch")));
            word.put("dict_source",cursor.getString(cursor.getColumnIndex("dict_source")));
            word.put("cid",cursor.getString(cursor.getColumnIndex("cid")));
            word.put("gid",cursor.getString(cursor.getColumnIndex("gid")));
            word.put("correct_times",cursor.getString(cursor.getColumnIndex("correct_times")));
            word.put("error_times",cursor.getString(cursor.getColumnIndex("error_times")));
            word.put("last_date",cursor.getString(cursor.getColumnIndex("last_date")));*/
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
     * 添加历史记录
     * @param word
     */
    public void insertData(DetailWord word){
        db = helper.getWritableDatabase();
        int wid = Integer.valueOf(word.getWid());
        String word_en = word.getWord_en().replaceAll("\"","\"\"");
        String word_ch = word.getWord_ch().replaceAll("\"","\"\"");
        int dict_source = Integer.valueOf(word.getDict_source());
        String cid = word.getCid();
        String gid = word.getGid();
        String correct_times = String.valueOf(word.getCorrect_times());
        String error_times = String.valueOf(word.getError_times());
        String last_date = word.getLast_date();
        String query_date = String.valueOf(System.currentTimeMillis());
        db.execSQL("insert into records(wid,word_en,word_ch,dict_source,cid,gid,correct_times,error_times,last_date,query_date) " +
                "values("+wid+",\""+word_en+"\",\""+word_ch+"\","+dict_source+","+cid+","+gid+","+correct_times+","+error_times+","+last_date+","+query_date+")");
        db.close();
    }

    /**
     * 删除单条记录
     * @param wid 单词id
     * @param dict_source 单词来源
     * @return
     */
    public int deleteSingleData(String wid,String dict_source){
        SQLiteDatabase db = helper.getWritableDatabase();
        int delete = db.delete("records","wid=? and dict_source=?",new String[]{wid,dict_source});
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

    /**
     * 更新单词
     * @param wid 单词id
     * @param dict_source 单词来源
     * @param word_en 英文单词
     * @param word_ch 中文释义
     */
    public void updateData(String wid,String dict_source,String word_en,String word_ch){
        db = helper.getWritableDatabase();
        db.execSQL("update records set word_en=\""+word_en+"\",word_ch=\""+word_ch+"\" where wid="+wid+" and dict_source="+dict_source);
        db.close();
    }

    /**
     * 更新查询时间
     * @param wid 单词id
     * @param dict_source 单词来源
     */
    public void updateQueryDate(String wid,String dict_source){
        db = helper.getWritableDatabase();
        db.execSQL("update records set query_date=\""+System.currentTimeMillis()+"\" where wid="+wid+" and dict_source="+dict_source);
        db.close();
    }

}
