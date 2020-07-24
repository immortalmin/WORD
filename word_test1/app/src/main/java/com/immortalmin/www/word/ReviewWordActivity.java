package com.immortalmin.www.word;

import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class ReviewWordActivity extends AppCompatActivity {

    private MyAsyncTask myAsyncTask;
    private JsonRe jsonRe = new JsonRe();
    private List<HashMap<String,Object>> review_list = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_word);
        getReviewList();
    }

    private void getReviewList(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("what",11);
            jsonObject.put("uid",4);
            jsonObject.put("review_date","2020-7-24");
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            review_list =jsonRe.reciteData(result);
            Log.i("ccc",review_list.toString());
        });
        myAsyncTask.execute(jsonObject);
    }
}
