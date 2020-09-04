package com.immortalmin.www.word;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdatePwdActivity extends AppCompatActivity implements View.OnClickListener{

    private Button commit_btn,return_btn;
    private EditText new_pwd,confirm_pwd;
    private TextView pwd_warn,confirm_warn;
    private JsonRe jsonRe = new JsonRe();
    private MD5Utils md5Utils = new MD5Utils();
    private String telephone = null;
    private Intent intent;
//    private HashMap<String,Object> userdata=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pwd);
        commit_btn = (Button)findViewById(R.id.commit_btn);
        return_btn = (Button)findViewById(R.id.return_btn);
        new_pwd = (EditText)findViewById(R.id.new_pwd);
        confirm_pwd = (EditText)findViewById(R.id.confirm_pwd);
        pwd_warn = (TextView)findViewById(R.id.pwd_warn);
        confirm_warn = (TextView)findViewById(R.id.confirm_warn);
        commit_btn.setOnClickListener(this);
        return_btn.setOnClickListener(this);
        init();

        Intent intent = getIntent();
        telephone = intent.getStringExtra("telephone");
    }

    private void init(){
        new_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String now_pwd = new_pwd.getText().toString();
                if(!isPassword(now_pwd)){
                    mHandler.obtainMessage(0).sendToTarget();
                }else{
                    mHandler.obtainMessage(1).sendToTarget();
                }
            }
        });

        confirm_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(confirm_pwd.getText().toString().equals(new_pwd.getText().toString())){
                    mHandler.obtainMessage(2).sendToTarget();
                }else{
                    mHandler.obtainMessage(3).sendToTarget();
                }
            }
        });
    }

    public boolean isPassword(String password){
        String regex="^(?![0-9])(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";
        Pattern p= Pattern.compile(regex);
        Matcher m=p.matcher(password);
        boolean isMatch=m.matches();
        return isMatch;
    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 0:
                    pwd_warn.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    pwd_warn.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    confirm_warn.setVisibility(View.INVISIBLE);
                    break;
                case 3:
                    confirm_warn.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    intent = new Intent(UpdatePwdActivity.this,LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                    break;
            }

            return false;
        }
    });


    public void onClick(View view){
        switch (view.getId()){
            case R.id.commit_btn:

                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("telephone",telephone);
                    jsonObject.put("pwd",md5Utils.getMD5Code(confirm_pwd.getText().toString()));
                }catch (JSONException e){
                    e.printStackTrace();
                }
                update_password(jsonObject);
                break;
            case R.id.return_btn:
                intent = new Intent(UpdatePwdActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
        }
    }

    private void update_password(final JSONObject jsonObject) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                String jsonString = httpGetContext.getData("http://47.98.239.237/word/php_file2/update_password.php",jsonObject);
                HashMap<String,Object> userdata = jsonRe.userData(jsonString);
                set_sp(userdata);
            }
        }).start();
    }

    private void set_sp(HashMap<String,Object> userdata){
        SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        sp.edit().putString("username",userdata.get("username").toString())
//                .putString("password",userdata.get("pwd").toString())
                .putString("profile_photo",userdata.get("profile_photo").toString())
                .putString("password",null)
                .putString("status","0")
                .apply();
        mHandler.obtainMessage(4).sendToTarget();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            intent = new Intent(UpdatePwdActivity.this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
