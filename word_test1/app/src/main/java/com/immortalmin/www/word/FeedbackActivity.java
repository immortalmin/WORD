package com.immortalmin.www.word;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener{

    Button commit_feedback_btn,return_btn;
    private TextView feedback_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        commit_feedback_btn = (Button)findViewById(R.id.commit_feedback_btn);
        return_btn = (Button)findViewById(R.id.return_btn);
        feedback_tv = (TextView)findViewById(R.id.feedback_tv);
        commit_feedback_btn.setOnClickListener(this);
        return_btn.setOnClickListener(this);
        feedback_tv.setOnClickListener(this);
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
//                LayoutInflater inflater = getLayoutInflater();
//                View view1 = inflater.inflate(R.layout.imglayout,null);
//                TextView tv1 = view1.findViewById(R.id.tv1);
//                tv1.setText(String.valueOf(count++));
//                img_group.addView(view1,0);
                break;
        }
    }

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
