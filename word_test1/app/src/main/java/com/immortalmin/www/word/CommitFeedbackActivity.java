package com.immortalmin.www.word;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class CommitFeedbackActivity extends AppCompatActivity implements View.OnClickListener{

    Button return_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commit_feedback);
        return_btn = (Button)findViewById(R.id.return_btn);
        return_btn.setOnClickListener(this);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.return_btn:
                finish();
                overridePendingTransition(R.anim.slide_left_in,R.anim.slide_to_right);
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
//            Intent intent = new Intent();
//            setResult(1,intent);
            finish();
            overridePendingTransition(R.anim.slide_left_in,R.anim.slide_to_right);
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
