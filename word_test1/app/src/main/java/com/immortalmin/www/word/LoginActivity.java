package com.immortalmin.www.word;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    EditText username,password;
    Button login_btn,reg_btn;
    private HashMap<String,Object> userdata=null;
    private HashMap<String,Object> userSetting=null;
    JsonRe jsonRe = new JsonRe();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        login_btn = (Button)findViewById(R.id.login_btn);
        reg_btn = (Button)findViewById(R.id.reg_btn);
        login_btn.setOnClickListener(this);
        reg_btn.setOnClickListener(this);

        //快速登录
//        username.setText("luo");
//        password.setText("123");
        SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        username.setText(sp.getString("username", null));
        password.setText(sp.getString("password", null));
//        if(sp.getString("username", null)!=null&&sp.getString("password", null)!=null){
//            login();
//        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                login();
                break;
            case R.id.reg_btn:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent,1);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
//                LoginActivity.this.finish();
                break;
        }
    }

    private void login(){
        String uname = username.getText().toString();
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("username",uname);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        getuserdata(jsonObject);
    }

    private void getuserdata(final JSONObject jsonObject) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                String wordjson = httpGetContext.getData("http://47.98.239.237/word/php_file2/getuserdata.php",jsonObject);
                userdata = jsonRe.userData(wordjson);
                judge();
//                mHandler.obtainMessage(0).sendToTarget();
            }
        }).start();

    }

    private void get_setting(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("uid",userdata.get("uid").toString());
                }catch (JSONException e){
                    e.printStackTrace();
                }
                HttpGetContext httpGetContext = new HttpGetContext();
                String s = httpGetContext.getData("http://47.98.239.237/word/php_file2/getsetting.php",jsonObject);
                userSetting = jsonRe.userSetting(s);
                SharedPreferences sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
                sp.edit().putString("uid",userSetting.get("uid").toString())
                        .putInt("recite_num",Integer.valueOf(userSetting.get("recite_num").toString()))
                        .putInt("recite_scope",Integer.valueOf(userSetting.get("recite_scope").toString()))
                        .apply();
            }
        }).start();
    }

    private void judge(){
        String pwd = password.getText().toString();
        Looper.prepare();
        if(userdata.size()==0){
            Toast.makeText(LoginActivity.this,"用户不存在",Toast.LENGTH_SHORT).show();
        }else{
            if(pwd.equals(userdata.get("pwd"))){
                SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                sp.edit().putString("username", username.getText().toString())
                        .putString("password", password.getText().toString())
                        .putString("profile_photo", userdata.get("profile_photo").toString())
                        .putString("status","1")
                        .apply();
                get_setting();
                Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                LoginActivity.this.finish();
            }else{
                Toast.makeText(LoginActivity.this,"密码错误",Toast.LENGTH_SHORT).show();
            }
        }
        Looper.loop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
        if (requestCode == 1 && resultCode == 1) {
            username.setText(data.getExtras().getString("username"));
            password.setText("");
        }
    }
}
