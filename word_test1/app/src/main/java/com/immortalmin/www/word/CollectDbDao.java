package com.immortalmin.www.word;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户背单词的记录
 */
public class CollectDbDao {
    private Context context;
    private CollectSQLiteOpenHelper helper;
    private SQLiteDatabase db;

    CollectDbDao(Context context){
        this.context = context;
        init();
    }

    private void init(){
        helper = new CollectSQLiteOpenHelper(context);
    }

    /**
     * 获取背新单词的数据
     * @param mount 单词数
     * @return 单词列表
     */
    public ArrayList<DetailWord> getReciteData(int mount){
        ArrayList<DetailWord> wordList = new ArrayList<>();
        Cursor cursor = helper.getReadableDatabase().rawQuery("select id,cid,gid,wid,word_en,word_ch,correct_times,error_times,last_date,review_date,dict_source from collect where correct_times<5 order by correct_times,error_times DESC limit "+mount,null);
        while(cursor.moveToNext()){
            DetailWord word = new DetailWord();
            word.setHid(cursor.getString(cursor.getColumnIndex("id")));
            word.setCid(cursor.getString(cursor.getColumnIndex("cid")));
            word.setGid(cursor.getString(cursor.getColumnIndex("gid")));
            word.setWid(cursor.getString(cursor.getColumnIndex("wid")));
            word.setWord_en(cursor.getString(cursor.getColumnIndex("word_en")));
            word.setWord_ch(cursor.getString(cursor.getColumnIndex("word_ch")));
            word.setCorrect_times(cursor.getString(cursor.getColumnIndex("correct_times")));
            word.setError_times(cursor.getString(cursor.getColumnIndex("error_times")));
            word.setLast_date(cursor.getString(cursor.getColumnIndex("last_date")));
            word.setReview_date(cursor.getString(cursor.getColumnIndex("review_date")));
            word.setDict_source(cursor.getString(cursor.getColumnIndex("dict_source")));
            wordList.add(word);
        }
        cursor.close();
        return wordList;
    }

//    /**
//     * 检查数据库中是否已经有该条数据（未使用过）
//     * @param tempName
//     * @return
//     */
    /*public boolean hasData(String tempName){
        //从Record这个表里找到name=tempName的id
//        Cursor cursor = helper.getReadableDatabase().rawQuery("select id as _id,name from records where name = ?",new String[]{tempName});
        Cursor cursor = helper.getReadableDatabase().rawQuery("select wid from records where name=?",new String[]{tempName});
        //判断是否有下一个
        return cursor.moveToNext();
    }*/


    public void insertData(DetailWord word){
        db = helper.getWritableDatabase();
        int wid = Integer.valueOf(word.getWid());
        String cid = word.getCid();
        String gid = word.getGid();
        String word_en = word.getWord_en().replaceAll("\"","\"\"");
        String word_ch = word.getWord_ch().replaceAll("\"","\"\"");
        String correct_times = String.valueOf(word.getCorrect_times());
        String error_times = String.valueOf(word.getError_times());
        String last_date = word.getLast_date();
        String review_date = word.getReview_date();
        int dict_source = Integer.valueOf(word.getDict_source());
        String update_date = DateTransUtils.getDateAfterToday(0);
        db.execSQL("insert into collect(wid,cid,gid,word_en,word_ch,correct_times,error_times,last_date,review_date,dict_source,update_date) " +
                "values("+wid+","+cid+","+gid+",\""+word_en+"\",\""+word_ch+"\","+correct_times+","+error_times+",\""+last_date+"\",\""+review_date+"\","+dict_source+",\""+update_date+"\")");
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
        int delete = db.delete("collect","wid=? and dict_source=?",new String[]{wid,dict_source});
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


    public void updateData(DetailWord word){
        db = helper.getWritableDatabase();
//        String update_date = String.valueOf(System.currentTimeMillis());
        String update_date = DateTransUtils.getDateAfterToday(0);
        db.execSQL("update collect set gid="+word.getGid()+",word_en=\""+word.getWord_en()+"\",word_ch=\""+word.getWord_ch()+"\",correct_times="+word.getCorrect_times()+",error_times="+word.getError_times()+",last_date=\""+word.getLast_date()+"\",review_date=\""+word.getReview_date()+"\",update_date=\""+update_date+"\" where id="+word.getHid());
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
