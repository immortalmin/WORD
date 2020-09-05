//package com.immortalmin.www.word;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.util.Log;
//
//public class WordDAO {
//    String dabaseName;
//    SQLiteDatabase db;
//    DBOpenHelper openHelper;
//    public void creatdb(Context con){
//        dabaseName = "word_test.db";
//        openHelper = new DBOpenHelper(con,dabaseName,null,1);
//        try{
//            db = openHelper.getWritableDatabase();
//        }catch (Exception e){
//            db = openHelper.getReadableDatabase();
//        }
//    }
//    public String query(String sql){
//        String resultset = "";
//        try{
//            Cursor cursor = db.rawQuery(sql,null);
//            while(cursor.moveToNext()){
//                int id = cursor.getInt(0);
//                String wordinfo = cursor.getString(1);
//                resultset = resultset + "\n" + id + " " + wordinfo;
//            }
//            cursor.close();
//        }catch (Exception exp){
//            Log.i("Exp",exp.toString());
//        }
//        return resultset;
//    }
////    public String querybyid(String id){
////        try{
////            String sql = "SELECT * FROM words WHERE id = " + id;
////        }catch(Exception exp){
////            Log.i("sql_error",exp.toString());
////            return "查询失败";
////        }
////    }
//    public String querybyid(String id){
//        String sql = "SELECT * FROM words WHERE id = " + id;
//        Log.i("in the querybyid",sql);
//        return query(sql);
//    }
//}
