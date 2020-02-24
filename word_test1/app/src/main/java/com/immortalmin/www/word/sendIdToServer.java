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
public class sendIdToServer implements Runnable{

    Map<String,Object> update_word = new HashMap<String, Object>();
    String update_url="http://47.98.239.237/word/php_file/update_recite.php?";
    public void sendMap(Map<String,Object> update_word){
        this.update_word=update_word;
    }


    @Override
    public void run(){
//        Log.i("sendIdToServer",update_word.toString());
//        HttpGetContext httpGetContext=new HttpGetContext();
//        String id = update_word.get("id").toString();
//        String correct_times = update_word.get("correct_times").toString();
//        String error_times = update_word.get("error_times").toString();
//        String prof_flag = update_word.get("prof_flag").toString();
//        String url=update_url+"id="+id+"&correct_times="+correct_times+"&error_times="+error_times+"&prof_flag="+prof_flag;
//        int res = httpGetContext.update_recite_list(url);
//        Log.i("httpGetContext_res",String.valueOf(res));
//        Log.i("ccc",update_word.toString());
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("uid",update_word.get("uid").toString());
            jsonObject.put("wid",update_word.get("wid").toString());
            jsonObject.put("correct_times",update_word.get("correct_times").toString());
            jsonObject.put("error_times",update_word.get("error_times").toString());
            jsonObject.put("prof_flag",update_word.get("prof_flag").toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
        HttpGetContext httpGetContext = new HttpGetContext();
        httpGetContext.getData("http://47.98.239.237/word/php_file2/update_recite.php",jsonObject);
    }
}
