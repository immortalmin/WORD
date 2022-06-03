package com.immortalmin.www.word;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;

/**
 * 用户背单词的数据库操作类
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
     */
    ArrayList<DetailWord> getReciteData(int mount){
        ArrayList<DetailWord> wordList = new ArrayList<>();
        Cursor cursor = helper.getReadableDatabase().rawQuery("select id,cid,gid,wid,word_en,word_ch,correct_times,error_times,last_date,review_date,dict_source from collect where isCollect=1 AND correct_times<5 order by correct_times ASC,error_times DESC limit "+mount,null);
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

    /**
     * 获取复习的单词列表
     */
    ArrayList<DetailWord> getReviewData() {
        ArrayList<DetailWord> wordList = new ArrayList<>();
        String review_date = DateTransUtils.getDateAfterToday(0);
        Cursor cursor = helper.getReadableDatabase().rawQuery("select id,cid,gid,wid,word_en,word_ch,correct_times,error_times,last_date,review_date,dict_source from collect WHERE isCollect=1 AND correct_times<=5 AND correct_times>0 AND review_date<=\""+review_date+"\"",null);
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

    /**
     * 获取所有收藏的单词
     */
    ArrayList<DetailWord> getCollectList() {
        ArrayList<DetailWord> wordList = new ArrayList<>();
        Cursor cursor = helper.getReadableDatabase().rawQuery("select id,cid,gid,wid,word_en,word_ch,correct_times,error_times,last_date,review_date,dict_source,source from collect where isCollect=1",null);
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
            word.setSource(cursor.getString(cursor.getColumnIndex("source")));
            wordList.add(word);
        }
        cursor.close();
        return wordList;
    }

    /**
     * 获取所有需要同步的单词
     */
    ArrayList<DetailWord> getSyncList() {
        ArrayList<DetailWord> wordList = new ArrayList<>();
        Cursor cursor = helper.getReadableDatabase().rawQuery("select id,cid,gid,wid,correct_times,error_times,last_date,review_date,dict_source,source,isCollect from collect where isSynchronized=0",null);
        while(cursor.moveToNext()){
            DetailWord word = new DetailWord();
            word.setHid(cursor.getString(cursor.getColumnIndex("id")));
            word.setCid(cursor.getString(cursor.getColumnIndex("cid")));
            word.setGid(cursor.getString(cursor.getColumnIndex("gid")));
            word.setWid(cursor.getString(cursor.getColumnIndex("wid")));
            word.setCorrect_times(cursor.getString(cursor.getColumnIndex("correct_times")));
            word.setError_times(cursor.getString(cursor.getColumnIndex("error_times")));
            word.setLast_date(cursor.getString(cursor.getColumnIndex("last_date")));
            word.setReview_date(cursor.getString(cursor.getColumnIndex("review_date")));
            word.setDict_source(cursor.getString(cursor.getColumnIndex("dict_source")));
            word.setSource(cursor.getString(cursor.getColumnIndex("source")));
            word.setCollect("1".equals(cursor.getString(cursor.getColumnIndex("isCollect"))));
            wordList.add(word);
        }
        cursor.close();
        return wordList;
    }

    /**
     * 获取需要复习的单词数量
     */
    int getReviewCount(){
        String review_date = DateTransUtils.getDateAfterToday(0);
        Cursor cursor = helper.getReadableDatabase().rawQuery("select count(*) as count from collect WHERE isCollect=1 AND correct_times<=5 AND correct_times>0 AND review_date<=\""+review_date+"\"",null);
        int count = 0;
        while(cursor.moveToNext()){
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    /**
     * 获取已经掌握的单词数量
     */
    int getFinishCount(){
        Cursor cursor = helper.getReadableDatabase().rawQuery("select count(*) as count from collect WHERE isCollect=1 AND correct_times=6 AND review_date=\"1970-01-01\"",null);
        int count = 0;
        while(cursor.moveToNext()){
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    /**
     * 获取收藏的单词数
     */
    int getCollectCount(){
        Cursor cursor = helper.getReadableDatabase().rawQuery("select count(*) as count from collect where isCollect=1",null);
        int count = 0;
        while(cursor.moveToNext()){
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    /**
     * 根据id获取数据
     * @param id 单词的id（在本地数据库中的id）
     * @return 查询到的单词
     */
    DetailWord getSingleWordById(int id){
        Cursor cursor = helper.getReadableDatabase().rawQuery("select id,cid,gid,wid,word_en,word_ch,correct_times,error_times,last_date,review_date,dict_source,source from collect where id="+id,null);
        DetailWord word = new DetailWord();
        if(cursor.moveToNext()){
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
            word.setSource(cursor.getString(cursor.getColumnIndex("source")));
        }
        return word;
    }

    /**
     * 通过wid和dict_source获取单词
     * @param wid 单词的wid
     * @param dict_source 单词来源
     */
    DetailWord getSingleWordByWidAndSource(String wid,String dict_source){
        Cursor cursor = helper.getReadableDatabase().rawQuery("select id,cid,gid,wid,word_en,word_ch,correct_times,error_times,last_date,review_date,dict_source,source,isCollect from collect where wid="+wid+" and dict_source="+dict_source,null);
        DetailWord word = new DetailWord();
        if(cursor.moveToNext()){
            word.setHid(cursor.getString(cursor.getColumnIndex("id")));
            word.setCid(cursor.getString(cursor.getColumnIndex("cid")));
            word.setCollect("1".equals(cursor.getString(cursor.getColumnIndex("isCollect"))));
            word.setGid(cursor.getString(cursor.getColumnIndex("gid")));
            word.setWid(cursor.getString(cursor.getColumnIndex("wid")));
            word.setWord_en(cursor.getString(cursor.getColumnIndex("word_en")));
            word.setWord_ch(cursor.getString(cursor.getColumnIndex("word_ch")));
            word.setCorrect_times(cursor.getString(cursor.getColumnIndex("correct_times")));
            word.setError_times(cursor.getString(cursor.getColumnIndex("error_times")));
            word.setLast_date(cursor.getString(cursor.getColumnIndex("last_date")));
            word.setReview_date(cursor.getString(cursor.getColumnIndex("review_date")));
            word.setDict_source(cursor.getString(cursor.getColumnIndex("dict_source")));
            word.setSource(cursor.getString(cursor.getColumnIndex("source")));
        }
        return word;
    }

    /**
     * 检查数据库中是否已经有该条数据
     * @param wid 单词wid
     * @param dict_source 单词来源
     */
    public boolean hasData(String wid,String dict_source){
        Cursor cursor = helper.getReadableDatabase().rawQuery("select wid from collect where wid=? and dict_source=?",new String[]{wid,dict_source});
        return cursor.moveToNext();
    }


    /**
     * 新增单词
     * 可能是服务器向本地同步的数据，也可能是用户新收藏的单词
     * @param word 单词
     * @param isSynchronize 是否需要同步到服务器
     */
    void insertData(DetailWord word,boolean isSynchronize){
        db = helper.getWritableDatabase();
        int wid = Integer.valueOf(word.getWid());
        //cid和gid暂时为null
        String cid = word.getCid();
        String gid = word.getGid();
        String word_en = word.getWord_en().replaceAll("\"","\"\"");
        String word_ch = word.getWord_ch().replaceAll("\"","\"\"");
        String correct_times = String.valueOf(word.getCorrect_times());
        String error_times = String.valueOf(word.getError_times());
        String last_date = word.getLast_date();
        String review_date = word.getReview_date();
        String source = word.getSource();
        int dict_source = Integer.valueOf(word.getDict_source());

        String update_date = DateTransUtils.getDateAfterToday(0);
        db.execSQL("insert into collect(wid,cid,gid,word_en,word_ch,correct_times,error_times,last_date,review_date,dict_source,source,update_date,isSynchronized) " +
                "values("+wid+","+cid+","+gid+",\""+word_en+"\",\""+word_ch+"\","+correct_times+","+error_times+",\""+last_date+"\",\""+review_date+"\","+dict_source+",\""+source+"\",\""+update_date+"\","+(isSynchronize?0:1)+")");
        db.close();
    }

    /**
     * 通过wid和dict_source删除单个单词
     * @param wid 单词wid
     * @param dict_source 单词来源
     * @return 删除的单词数量
     */
    public int deleteSingleWordByWidAndSource(String wid,String dict_source){
        int delete = helper.getWritableDatabase().delete("collect","wid=? and dict_source=?",new String[]{wid,dict_source});
        return delete;
    }

    /**
     * 通过id删除单个单词
     * @param id 单词id
     * @return 删除的单词数量
     */
    public int deleteSingleWordById(String id){
        int delete = helper.getWritableDatabase().delete("collect","id=?",new String[]{id});
        db.close();
        return delete;
    }

    /**
     * 通过id修改收藏
     * @param id 单词id
     * @param sel 0：取消收藏  1：收藏
     */
    public void updateCollectById(String id,int sel){
        ContentValues values = new ContentValues();
        values.put("isCollect",sel);
        values.put("isSynchronized",0);
        helper.getReadableDatabase().update("collect",values,"id=?",new String[]{id});
    }

    /**
     * 通过wid和dict_source修改收藏
     * @param wid 单词wid
     * @param dict_source 单词来源
     * @param sel 0：取消收藏  1：收藏
     */
    public void updateCollectByWidAndSource(String wid,String dict_source,int sel){
        ContentValues values = new ContentValues();
        values.put("isCollect",sel);
        values.put("isSynchronized",0);
        helper.getReadableDatabase().update("collect",values,"wid=? and dict_source=?",new String[]{wid,dict_source});
    }

    public void deleteData(){
        db = helper.getWritableDatabase();
        db.execSQL("delete from collect");
        db.close();
    }

    public void updateData(DetailWord word){
        db = helper.getWritableDatabase();
        String update_date = DateTransUtils.getDateAfterToday(0);
        db.execSQL("update collect set gid="+word.getGid()+",word_en=\""+word.getWord_en()+"\",word_ch=\""+word.getWord_ch()+"\",correct_times="+word.getCorrect_times()+",error_times="+word.getError_times()+",last_date=\""+word.getLast_date()+"\",review_date=\""+word.getReview_date()+"\",update_date=\""+update_date+"\",isSynchronized=0 where id="+word.getHid());
        db.close();
    }

    /**
     * 执行SQL语句
     * @param s
     */
    void execCommonSQL(String s){
        db = helper.getWritableDatabase();
        db.execSQL(s);
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
