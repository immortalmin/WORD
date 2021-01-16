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

    DetailWord update_word;
    int what = 0;
    public void sendMap(DetailWord update_word,int what){
        this.update_word=update_word;
        this.what = what;
    }


    @Override
    public void run(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("cid",update_word.getCid());
            jsonObject.put("what",what);
            jsonObject.put("correct_times",update_word.getCorrect_times());
            jsonObject.put("error_times",update_word.getError_times());
        }catch (JSONException e) {
            e.printStackTrace();
        }
        HttpGetContext httpGetContext = new HttpGetContext();
        httpGetContext.getData("http://47.98.239.237/word/php_file2/update_recite.php",jsonObject);
    }
}
