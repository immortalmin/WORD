package com.immortalmin.www.word;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private Button return_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        return_btn = (Button)findViewById(R.id.return_btn);
        return_btn.setOnClickListener(this);
    }


    public void onClick(View view){
        switch (view.getId()){
            case R.id.return_btn:
                finish();
                overridePendingTransition(R.anim.slide_right_in,R.anim.slide_to_left);
                break;
        }
    }









    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            overridePendingTransition(R.anim.slide_right_in,R.anim.slide_to_left);
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
