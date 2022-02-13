package com.immortalmin.www.word;

import android.support.test.InstrumentationRegistry;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

public class DailyRecitationDbDaoTest{
    private DailyRecitationDbDao dailyRecitationDbDao = new DailyRecitationDbDao(InstrumentationRegistry.getContext());

    @org.junit.Test
    public void getTotalNums() {
        ArrayList<TwoTuple<String,Integer>> ans =  dailyRecitationDbDao.getTotalNums("2021-01-01");
        Log.i("ccc",ans.toString());
        dailyRecitationDbDao.queryDatabase();
    }

    @org.junit.Test
    public void insertSingleData() {
        DailyRecitation dailyRecitation = new DailyRecitation(4,101,20,0,"2021/1/31",false);
        dailyRecitationDbDao.insertSingleData(dailyRecitation);
        dailyRecitationDbDao.queryDatabase();
    }

    @org.junit.Test
    public void isExist() {
        Log.i("ccc",""+dailyRecitationDbDao.isExist(4));
    }

    @org.junit.Test
    public void deleteByRid() {
        dailyRecitationDbDao.deleteByRid(2);
        dailyRecitationDbDao.queryDatabase();
    }

    @org.junit.Test
    public void update() {
        DailyRecitation dailyRecitation = new DailyRecitation(4,10,30,2,"",false);
        dailyRecitationDbDao.update(1,dailyRecitation);
        dailyRecitationDbDao.queryDatabase();
    }

    @org.junit.Test
    public void getDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Log.i("ccc",year+"/"+month+"/"+day);
    }

    @org.junit.Test
    public void getSyncList() {
        Log.i("ccc","test");
        ArrayList<DailyRecitation> res = dailyRecitationDbDao.getSyncList();
        Log.i("ccc",res.toString());
    }

    @org.junit.Test
    public void updateSyncByRid() {
//        dailyRecitationDbDao.updateSyncByRid(5,true);
//        dailyRecitationDbDao.queryDatabase();
    }

}