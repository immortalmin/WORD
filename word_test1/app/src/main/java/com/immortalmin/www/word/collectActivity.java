package com.immortalmin.www.word;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class collectActivity extends AppCompatActivity {

    private JsonRe jsonRe = new JsonRe();
    private UserData userData = new UserData();
    private List<HashMap<String,Object>> word_list=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        init_user();
        getrecitelist();
    }

    private void init_user(){
        SharedPreferences sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
        userData.setUid(sp.getString("uid",null));
        userData.setRecite_num(sp.getInt("recite_num",20));
        userData.setRecite_scope(sp.getInt("recite_scope",10));
        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        userData.setUsername(sp.getString("username",null));
    }

    private void getrecitelist() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("uid",userData.getUid());
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                String jsonString = httpGetContext.getData("http://47.98.239.237/word/php_file2/getcollect.php",jsonObject);
                word_list = jsonRe.allwordData(jsonString);
                Log.i("ccc",word_list.toString());
            }
        }).start();
    }
}
