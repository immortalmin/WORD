package com.immortalmin.www.word;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        commit_feedback_btn = (Button)findViewById(R.id.commit_feedback_btn);
        return_btn = (Button)findViewById(R.id.return_btn);
        feedback_tv = (TextView)findViewById(R.id.feedback_tv);
        feedback_lv = (ListView) findViewById(R.id.feedback_lv);
        commit_feedback_btn.setOnClickListener(this);
        return_btn.setOnClickListener(this);
        feedback_tv.setOnClickListener(this);
        getFeedbackList();
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.commit_feedback_btn:
                Intent intent = new Intent(FeedbackActivity.this,CommitFeedbackActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_in,R.anim.slide_to_left);
                break;
            case R.id.return_btn:
                finish();
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
            case R.id.feedback_tv:
//                getFeedbackList();
                break;
        }
    }

    private void getFeedbackList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("what",0);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                HttpGetContext httpGetContext = new HttpGetContext();
                feedbackList = httpGetContext.getFeedbackList(jsonObject);
                mHandler.sendEmptyMessage(0);
            }
        }).start();

    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 0:
//                    feedbackList = (ArrayList<HashMap<String,Object>>)message.obj;
                    feedbackAdapter = new FeedbackAdapter(FeedbackActivity.this,feedbackList);
                    feedback_lv.setAdapter(feedbackAdapter);
                    break;

            }
            return false;
        }
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
}
