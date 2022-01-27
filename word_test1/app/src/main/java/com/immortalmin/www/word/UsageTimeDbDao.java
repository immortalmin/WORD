package com.immortalmin.www.word;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;

/**
 * 用户使用时间的数据库操作类
 */
public class UsageTimeDbDao {
    private Context context;
    private UsageTimeSQLiteOpenHelper helper;
    private SQLiteDatabase db;

    UsageTimeDbDao(Context context){
        this.context = context;
        init();
    }

    private void init(){
        helper = new UsageTimeSQLiteOpenHelper(context);
    }

    /**
     * 获取使用时间
     */
    ArrayList<Integer> getUsageTime(){
        ArrayList<Integer> timeList = new ArrayList<>();
        Cursor cursor = helper.getReadableDatabase().rawQuery("select utime from usageTime order by udate desc limit 300",null);
        while(cursor.moveToNext()){
            timeList.add(cursor.getInt(cursor.getColumnIndex("utime")));
        }
        cursor.close();
        return timeList;
    }

    /**
     * 获取所有需要同步的数据
     * @return
     */
    ArrayList<UsageTime> getSyncList() {
        ArrayList<UsageTime> wordList = new ArrayList<>();
        Cursor cursor = helper.getReadableDatabase().rawQuery("select id,udate,utime from usageTime where isSynchronized=0",null);
        while(cursor.moveToNext()){
            UsageTime usageTime = new UsageTime();
            usageTime.setId(cursor.getString(cursor.getColumnIndex("id")));
            usageTime.setUdate(cursor.getString(cursor.getColumnIndex("udate")));
            usageTime.setUtime(cursor.getInt(cursor.getColumnIndex("utime")));
            wordList.add(usageTime);
        }
        cursor.close();
        return wordList;
    }

    /**
     * 添加使用时间
     */
    void insertUsageTime(UsageTime usageTime,int isSynchronized){
        db = helper.getWritableDatabase();
        String update_date = DateTransUtils.getDateAfterToday(0);
        try {
            //FIXME:没有避免插入udate相同的数据，现在只是捕捉了异常而已
            db.execSQL("INSERT INTO usageTime(udate,utime,update_date,isSynchronized)VALUES(\""+usageTime.getUdate()+"\",\""+usageTime.getUtime()+"\",\""+update_date+"\","+isSynchronized+") ");
        }catch (SQLException e){
//            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
            Log.i("ccc",e.toString());
            db.close();
        }
        db.close();
    }

    /**
     * 通过id修改数据为已上传
     */
    void updateIsSyncById(String id){
        ContentValues values = new ContentValues();
        values.put("isSynchronized",1);
        values.put("update_date",DateTransUtils.getDateAfterToday(0));
        helper.getReadableDatabase().update("usageTime",values,"id=?",new String[]{id});
    }

    /**
     * 通过udate修改数据为已上传
     */
    void updateIsSyncByUdate(String udate){
        ContentValues values = new ContentValues();
        values.put("isSynchronized",1);
        values.put("update_date",DateTransUtils.getDateAfterToday(0));
        helper.getReadableDatabase().update("usageTime",values,"udate=?",new String[]{udate});
    }

    /**
     * 通过udate来更新utime
     * 主要是因为本地的数据与服务器上的数据不一致，本地的数据需要向服务器同步
     */
    void updateUtimeByUdate(String udate,String utime){
        ContentValues values = new ContentValues();
        values.put("isSynchronized",1);
        values.put("utime",utime);
        values.put("update_date",DateTransUtils.getDateAfterToday(0));
        helper.getReadableDatabase().update("usageTime",values,"udate=?",new String[]{udate});
    }

    /**
     * 删除所有的数据
     */
    void deleteData() {
        db = helper.getWritableDatabase();
        db.execSQL("delete from usageTime");
        db.close();
    }

    /**
     * 执行SQL语句
     * @param s SQL语句
     */
    void execCommonSQL(String s) {
        db = helper.getWritableDatabase();
        db.execSQL(s);
        db.close();
    }

}
