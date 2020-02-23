package com.immortalmin.www.word;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{

    private Button return_btn,logout_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        return_btn = (Button)findViewById(R.id.return_btn);
        logout_btn = (Button)findViewById(R.id.logout_btn);
        return_btn.setOnClickListener(this);
        logout_btn.setOnClickListener(this);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.return_btn:
//                Intent intent = new Intent(SettingActivity.this,MainActivity.class);
//                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_right_in,R.anim.slide_to_left);
                break;
            case R.id.logout_btn:
                SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                sp.edit().putString("status","0").apply();
                Intent intent = new Intent();
                intent.setAction("com.immortalmin.www.MainActivity");
                sendBroadcast(intent);
                intent = new Intent(SettingActivity.this,LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                finish();
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
