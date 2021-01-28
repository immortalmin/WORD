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
import android.util.Log;
import android.view.KeyEvent;
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

public class UpdatePwdActivity extends AppCompatActivity implements View.OnClickListener{

    private Button commitBtn,returnBtn;
    private MyEditText newPwdEt,confirmPwdEt;
    private TextView pwdWarn,confirmWarn;
    private JsonRe jsonRe = new JsonRe();
    private MD5Utils md5Utils = new MD5Utils();
    private MyAsyncTask myAsyncTask;
    private String telephone = null;
    private Intent intent;
    private boolean pwdFlag = false,confirmFlag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pwd);
        commitBtn = findViewById(R.id.commitBtn);
        returnBtn = findViewById(R.id.returnBtn);
        newPwdEt = findViewById(R.id.newPwdEt);
        confirmPwdEt = findViewById(R.id.confirmPwdEt);
        pwdWarn = findViewById(R.id.pwdWarn);
        confirmWarn = findViewById(R.id.confirmWarn);
        commitBtn.setOnClickListener(this);
        returnBtn.setOnClickListener(this);
        init();
    }

    private void init(){
        Intent intent = getIntent();
        telephone = intent.getStringExtra("telephone");
        newPwdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String now_pwd = newPwdEt.getText().toString();
                if(!isPassword(now_pwd)){
                    mHandler.obtainMessage(0).sendToTarget();
                    pwdFlag = false;
                }else{
                    mHandler.obtainMessage(1).sendToTarget();
                    pwdFlag = true;
                }
            }
        });

        confirmPwdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(confirmPwdEt.getText().toString().equals(newPwdEt.getText().toString())){
                    mHandler.obtainMessage(2).sendToTarget();
                    confirmFlag = true;
                }else{
                    mHandler.obtainMessage(3).sendToTarget();
                    confirmFlag = false;
                }
            }
        });
    }

    public boolean isPassword(String password){
        String regex="^(?![0-9])(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";
        Pattern p= Pattern.compile(regex);
        Matcher m=p.matcher(password);
        return m.matches();
    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 0:
                    pwdWarn.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    pwdWarn.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    confirmWarn.setVisibility(View.INVISIBLE);
                    break;
                case 3:
                    confirmWarn.setVisibility(View.VISIBLE);
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
            case R.id.commitBtn:
                if(pwdFlag&&confirmFlag){
                    updatePwd();
                }else if(!pwdFlag){
                    Toast.makeText(UpdatePwdActivity.this,"密码格式错误",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(UpdatePwdActivity.this,"密码不一致",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.returnBtn:
                intent = new Intent(UpdatePwdActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
        }
    }

    private void updatePwd(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("what",19);
            jsonObject.put("telephone",telephone);
            jsonObject.put("pwd",md5Utils.getMD5Code(confirmPwdEt.getText().toString()));
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            User user = jsonRe.userData(result);
            set_sp(user);
        });
        myAsyncTask.execute(jsonObject);
    }

    private void set_sp(User user){
        SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        sp.edit().putString("username",user.getUsername())
                .putString("profile_photo",user.getProfile_photo())
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
