package com.example.administrator.listviewadptwebjsonimg;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

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
        Log.i("sendIdToServer",update_word.toString());
        HttpGetContext httpGetContext=new HttpGetContext();
        String id = update_word.get("id").toString();
        String correct_times = update_word.get("correct_times").toString();
        String error_times = update_word.get("error_times").toString();
        String prof_flag = update_word.get("prof_flag").toString();
        httpGetContext.update_recite_list(update_url+"id="+id+"&correct_times="+correct_times+"&error_times="+error_times+"&prof_flag="+prof_flag);
    }
}
