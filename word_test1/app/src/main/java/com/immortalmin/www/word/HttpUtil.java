package com.immortalmin.www.word;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 写不出来，以后再写
 * 网络请求工具类
 */
public class HttpUtil {

    private JsonRe jsonRe = new JsonRe();
    private List<HashMap<String,Object>> word_list= new ArrayList<HashMap<String,Object>>();

    /**
     * 模糊查询的线程
     * @param word
     * @param uid
     * @return
     */
    public List<HashMap<String,Object>> fuzzyquery(final String word,String uid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                word_list.clear();
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("uid",uid);
                    jsonObject.put("word",word);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpGetContext httpGetContext = new HttpGetContext();
                String recitejson = httpGetContext.getData("http://47.98.239.237/word/php_file2/getsearchlist.php",jsonObject);
                word_list = jsonRe.allwordData(recitejson);
//                Log.i("ccc",word_list.toString());
//                mHandler.obtainMessage(0,word_list).sendToTarget();
            }
        }).start();
        return word_list;
    }


    /**
     * 添加单词和例句
     * @param jsonObject
     */
    public void add_wordandexample(final JSONObject jsonObject){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                httpGetContext.getData("http://47.98.239.237/word/php_file2/addword.php",jsonObject);
//                fuzzyquery(fuzzy_str);
            }
        }).start();
    }
}
