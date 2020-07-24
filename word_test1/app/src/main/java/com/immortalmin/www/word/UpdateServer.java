package com.immortalmin.www.word;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 传入map
 * 更新数据库
 */
public class UpdateServer implements Runnable{

    Map<String,Object> update_word = new HashMap<String, Object>();
    int what = 0;
    public void sendMap(Map<String,Object> update_word,int what){
        this.update_word=update_word;
        this.what = what;
    }


    @Override
    public void run(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("cid",update_word.get("cid").toString());
            jsonObject.put("what",what);
            jsonObject.put("correct_times",update_word.get("correct_times").toString());
            jsonObject.put("error_times",update_word.get("error_times").toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
        HttpGetContext httpGetContext = new HttpGetContext();
        httpGetContext.getData("http://47.98.239.237/word/php_file2/update_recite.php",jsonObject);
    }
}
