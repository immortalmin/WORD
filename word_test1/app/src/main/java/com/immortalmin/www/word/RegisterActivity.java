package com.immortalmin.www.word;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    Button reg_btn;
    EditText username,pwd,confirm_pwd;
    TextView user_warn,pwd_warn,confirm_warn;
    JsonRe jsonRe;
    Runnable toLogin;
    private HashMap<String,Object> userdata=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        reg_btn = (Button)findViewById(R.id.reg_btn);
        username = (EditText)findViewById(R.id.username);
        pwd = (EditText)findViewById(R.id.pwd);
        confirm_pwd = (EditText)findViewById(R.id.confirm_pwd);
        user_warn = (TextView) findViewById(R.id.user_warn);
        pwd_warn = (TextView) findViewById(R.id.pwd_warn);
        confirm_warn = (TextView) findViewById(R.id.confirm_warn);
        reg_btn.setOnClickListener(this);

        jsonRe = new JsonRe();
        init();


    }

    private void init() {
        /**
         * 延迟跳转（等toast结束后跳转）
         */
        toLogin = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.putExtra("username",username.getText().toString());
                setResult(1,intent);
                finish();
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
            }
        };

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mHandler.obtainMessage(0).sendToTarget();
                String uname = username.getText().toString();
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("username",uname);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                query_user(jsonObject);
            }
        });

        pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String now_pwd = pwd.getText().toString();
                if(!isPassword(now_pwd)){
                    mHandler.obtainMessage(4).sendToTarget();
                }else{
                    mHandler.obtainMessage(3).sendToTarget();
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
                if(confirm_pwd.getText().toString().equals(pwd.getText().toString())){
                    mHandler.obtainMessage(5).sendToTarget();
                }else{
                    mHandler.obtainMessage(6).sendToTarget();
                }
            }
        });
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.reg_btn:
                String uname = username.getText().toString();
                String password = pwd.getText().toString();
                Toast.makeText(RegisterActivity.this,"注册成功 即将跳转到主页",Toast.LENGTH_SHORT).show();
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("username",uname);
                    jsonObject.put("pwd",password);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                register(jsonObject);
                break;
        }
    }

    public boolean isPassword(String password){
        String regex="^(?![0-9])(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";
        Pattern p= Pattern.compile(regex);
        Matcher m=p.matcher(password);
        boolean isMatch=m.matches();
        Log.i("ccc", "isPassword: 是否密码正则匹配"+isMatch);
        return isMatch;
    }

    private void register(final JSONObject jsonObject) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                httpGetContext.getData("http://47.98.239.237/word/php_file2/register.php",jsonObject);
                mHandler.postDelayed(toLogin,2000);
            }
        }).start();
    }


    private void query_user(final JSONObject jsonObject) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                String wordjson = httpGetContext.getData("http://47.98.239.237/word/php_file2/getuserdata.php",jsonObject);
                userdata = jsonRe.userData(wordjson);
                if(userdata.size()!=0){
                    mHandler.obtainMessage(1).sendToTarget();
                }
            }
        }).start();
    }
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    user_warn.setVisibility(View.INVISIBLE);
                    reg_btn.setClickable(true);
                    break;
                case 1:
                    user_warn.setVisibility(View.VISIBLE);
                    reg_btn.setClickable(false);
                    break;
                case 2:

                    break;
                case 3:
                    pwd_warn.setVisibility(View.INVISIBLE);
                    reg_btn.setClickable(true);
                    break;
                case 4:
                    pwd_warn.setVisibility(View.VISIBLE);
                    reg_btn.setClickable(false);
                    break;
                case 5:
                    confirm_warn.setVisibility(View.INVISIBLE);
                    reg_btn.setClickable(true);
                    break;
                case 6:
                    confirm_warn.setVisibility(View.VISIBLE);
                    reg_btn.setClickable(false);
                    break;
            }
        }
    };


}
