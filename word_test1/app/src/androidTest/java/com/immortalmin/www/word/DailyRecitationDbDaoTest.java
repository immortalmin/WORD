package com.immortalmin.www.word;

import android.support.test.InstrumentationRegistry;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class DailyRecitationDbDaoTest{
    private DailyRecitationDbDao dailyRecitationDbDao = new DailyRecitationDbDao(InstrumentationRegistry.getContext());

    @org.junit.Test
    public void getTotalNums() {
        ArrayList<TwoTuple<String,Integer>> ans =  dailyRecitationDbDao.getTotalNums(10);
        Log.i("ccc",ans.toString());
        dailyRecitationDbDao.queryDatabase();
    }

    @org.junit.Test
    public void insert() {
        DailyRecitation dailyRecitation = new DailyRecitation(4,101,20,0,"2021/1/31",false);
        dailyRecitationDbDao.insert(dailyRecitation);
        dailyRecitationDbDao.queryDatabase();
    }

    @org.junit.Test
    public void isExist() {
        Log.i("ccc",""+dailyRecitationDbDao.isExist(4));
    }

    @org.junit.Test
    public void delete() {
        dailyRecitationDbDao.delete(2);
        dailyRecitationDbDao.queryDatabase();
    }

    @org.junit.Test
    public void update() {
        DailyRecitation dailyRecitation = new DailyRecitation(4,10,30,2,"",false);
        dailyRecitationDbDao.update(1,dailyRecitation);
        dailyRecitationDbDao.queryDatabase();
    }
}