package com.immortalmin.www.word;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener{

    Button commit_feedback_btn,return_btn;
    private TextView feedback_tv;
    private ListView feedback_lv;
    private FeedbackAdapter feedbackAdapter = null;
    private ArrayList<HashMap<String,Object>> feedbackList = null;
    private MyAsyncTask myAsyncTask = new MyAsyncTask();
    private JsonRe jsonRe = new JsonRe();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        commit_feedback_btn = findViewById(R.id.commit_feedback_btn);
        return_btn = findViewById(R.id.return_btn);
        feedback_tv = findViewById(R.id.feedback_tv);
        feedback_lv = findViewById(R.id.feedback_lv);
        commit_feedback_btn.setOnClickListener(this);
        return_btn.setOnClickListener(this);
        feedback_tv.setOnClickListener(this);
        getFeedbackList();
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.commit_feedback_btn:
                Intent intent = new Intent(FeedbackActivity.this,CommitFeedbackActivity.class);
                startActivityForResult(intent,1);
                overridePendingTransition(R.anim.slide_right_in,R.anim.slide_to_left);
                break;
            case R.id.return_btn:
                finish();
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
            case R.id.feedback_tv:

                break;
        }
    }

    /**
     * 获取反馈列表
     */
    private void getFeedbackList(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("what",26);
            jsonObject.put("AllOrPerson",0);
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            if(feedbackAdapter==null){
                feedbackList = jsonRe.feedbackData(result);
                feedbackAdapter = new FeedbackAdapter(FeedbackActivity.this,feedbackList);
                feedback_lv.setAdapter(feedbackAdapter);
            }else{
                feedbackList.clear();
                feedbackList.addAll(jsonRe.feedbackData(result));
                feedbackAdapter.notifyDataSetChanged();
            }
        });
        myAsyncTask.execute(jsonObject);
    }

    private Handler mHandler = new Handler(message -> {
        switch (message.what){
            case 0:

                break;
        }
        return false;
    });


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
//            Intent intent = new Intent();
//            setResult(1,intent);
            finish();
            overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
        if (requestCode == 1 && resultCode == 1) {//如果提交了新的反馈，则更新数据
            getFeedbackList();
        }
    }
}
