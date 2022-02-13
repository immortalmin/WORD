package com.immortalmin.www.word;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class DailyRecitationDbDao {
    private Context context;
    private DailyRecitationSQLiteOpenHelper helper;
    private SQLiteDatabase db;

    DailyRecitationDbDao(Context context){
        this.context = context;
        init();
    }

    private void init(){
        helper = new DailyRecitationSQLiteOpenHelper(context);
    }

    ArrayList<TwoTuple<String,Integer>> getTotalNums(String date){
        ArrayList<TwoTuple<String,Integer>> res = new ArrayList<>();
        Cursor cursor = helper.getReadableDatabase().rawQuery("select record_date,review_num+recite_num as total_num from daily_recitation where record_date>=\""+date+"\" order by record_date;",null);
        while(cursor.moveToNext()){
            res.add(new TwoTuple(cursor.getString(cursor.getColumnIndex("record_date")),cursor.getInt(cursor.getColumnIndex("total_num"))));
        }
        cursor.close();
        return res;
    }

    /**
     * 不上传今天的数据，仅上传今天以前的数据
     */
    ArrayList<DailyRecitation> getSyncList(){
        ArrayList<DailyRecitation> res = new ArrayList<>();
        Cursor cursor = helper.getReadableDatabase().rawQuery("select * from daily_recitation where is_synchronized=0 and record_date!=current_date;",null);
        DailyRecitation dailyRecitation;
        while(cursor.moveToNext()){
            dailyRecitation = new DailyRecitation();
            dailyRecitation.setRid(cursor.getInt(cursor.getColumnIndex("rid")));
            dailyRecitation.setReview_num(cursor.getInt(cursor.getColumnIndex("review_num")));
            dailyRecitation.setRecite_num(cursor.getInt(cursor.getColumnIndex("recite_num")));
            dailyRecitation.setGrasp_num(cursor.getInt(cursor.getColumnIndex("grasp_num")));
            dailyRecitation.setRecord_date(cursor.getString(cursor.getColumnIndex("record_date")));
            res.add(dailyRecitation);
        }
        return res;
    }

    void insertSingleData(DailyRecitation dailyRecitation){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("insert into daily_recitation(uid,record_date,review_num,recite_num,grasp_num,is_synchronized)values("+dailyRecitation.getUid()+",\""+dailyRecitation.getRecord_date()+"\","+dailyRecitation.getReview_num()+","+dailyRecitation.getRecite_num()+","+dailyRecitation.getGrasp_num()+","+dailyRecitation.isIs_synchronized()+");");
        db.close();
    }


    void insertTodayData(DailyRecitation dailyRecitation){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("insert into daily_recitation(uid,record_date,review_num,recite_num,grasp_num,is_synchronized)values("+dailyRecitation.getUid()+",current_date,"+dailyRecitation.getReview_num()+","+dailyRecitation.getRecite_num()+","+dailyRecitation.getGrasp_num()+","+dailyRecitation.isIs_synchronized()+");");
        db.close();
    }


    boolean isExist(int uid){
        Cursor cursor = helper.getReadableDatabase().rawQuery("select * from daily_recitation where record_date=current_date and uid="+uid,null);
        return cursor.moveToNext();
    }

    /**
     * 增性update
     * @param what 0:recite     1:review
     */
    void update(int what,DailyRecitation dailyRecitation){
        if(!isExist(dailyRecitation.getUid())){
            insertTodayData(dailyRecitation);
            return ;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        if(what==0){
            db.execSQL("update daily_recitation set recite_num=recite_num+"+dailyRecitation.getRecite_num()+",grasp_num=grasp_num+"+dailyRecitation.getGrasp_num()+" where uid="+dailyRecitation.getUid()+" and record_date=current_date;");
        }else{
            db.execSQL("update daily_recitation set review_num=review_num+"+dailyRecitation.getReview_num()+",grasp_num=grasp_num+"+dailyRecitation.getGrasp_num()+" where uid="+dailyRecitation.getUid()+" and record_date=current_date;");
        }
        db.close();
    }

    /**
     * 直接更新
     */
    void updateByRecordDate(DailyRecitation dailyRecitation){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("update daily_recitation set review_num="+dailyRecitation.getReview_num()+",recite_num="+dailyRecitation.getRecite_num()+",grasp_num="+dailyRecitation.getGrasp_num()+",is_synchronized=1 where record_date=\""+dailyRecitation.getRecord_date()+"\"");
        db.close();
    }

    void updateSyncByRecordDate(String record_date,boolean is_synchronized){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_synchronized",is_synchronized);
        db.update("daily_recitation",values,"record_date=?",new String[]{String.valueOf(record_date)});
    }

    void deleteByRid(int rid){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from daily_recitation where rid="+rid);
        db.close();
    }

    void deleteAll(){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from daily_recitation;");
        db.close();
    }


    /**
     * 仅测试用
     */
    void queryDatabase(){
        String s="";
        Cursor cursor = helper.getReadableDatabase().rawQuery("select * from daily_recitation;",null);
        Log.i("ccc","----------数据库查询----------");
        Log.i("ccc","rid       uid       review_num     recite_num     grasp_num     record_date     is_synchronized");
        while(cursor.moveToNext()){
            s="";
            s = s.concat(cursor.getString(cursor.getColumnIndex("rid"))).concat("        ")
                    .concat(cursor.getString(cursor.getColumnIndex("uid"))).concat("         ")
                    .concat(cursor.getString(cursor.getColumnIndex("review_num"))).concat("            ")
                    .concat(cursor.getString(cursor.getColumnIndex("recite_num"))).concat("             ")
                    .concat(cursor.getString(cursor.getColumnIndex("grasp_num"))).concat("             ")
                    .concat(cursor.getString(cursor.getColumnIndex("record_date"))).concat("      ")
                    .concat(cursor.getString(cursor.getColumnIndex("is_synchronized")));
            Log.i("ccc",s);
        }
    }

}
