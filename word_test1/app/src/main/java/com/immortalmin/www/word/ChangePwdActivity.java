package com.immortalmin.www.word;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePwdActivity extends AppCompatActivity implements View.OnClickListener{

    private Button return_btn,confirm_btn,SMS_btn;
    private MyEditText oldPwd_et,newPwd_et,confirmPwd_et;
    private User user;
    private UserDataUtil userDataUtil;
    private MD5Utils md5Utils;
    private MyAsyncTask myAsyncTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        return_btn = findViewById(R.id.return_btn);
        confirm_btn = findViewById(R.id.confirm_btn);
        SMS_btn = findViewById(R.id.SMS_btn);
        oldPwd_et = findViewById(R.id.oldPwd_et);
        newPwd_et = findViewById(R.id.newPwd_et);
        confirmPwd_et = findViewById(R.id.confirmPwd_et);
        return_btn.setOnClickListener(this);
        confirm_btn.setOnClickListener(this);
        SMS_btn.setOnClickListener(this);
        userDataUtil = new UserDataUtil(this);
        md5Utils = new MD5Utils();
        init();
    }

    public void init(){
//2021/3/15
//        //初始化userData
//        userDataUtil.getdata(new UserDataUtil.HttpCallbackStringListener() {
//            @Override
//            public void onFinish(User userdata) {
//                user = userdata;
//            }
//
//            @Override
//            public void onError(Exception e) {
//
//            }
//        });
        user = userDataUtil.getUserDataFromSP();
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.return_btn:
                finish();
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
            case R.id.confirm_btn:
                judge();
                break;
            case R.id.SMS_btn:
                Intent intent = new Intent(ChangePwdActivity.this, SMSVerificationActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
        }
    }

    private void judge() {
        boolean res = true;
        if(!user.getPassword().equals(md5Utils.getMD5Code(oldPwd_et.getText().toString()))){
            Toast.makeText(ChangePwdActivity.this,"旧密码输入错误",Toast.LENGTH_SHORT).show();
            res = false;
        }else if(newPwd_et.getText().toString().length()==0){
            Toast.makeText(ChangePwdActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
            res = false;
        }else if(!isPassword(newPwd_et.getText().toString())){
            Toast.makeText(ChangePwdActivity.this,"新密码格式错误",Toast.LENGTH_SHORT).show();
            res = false;
        }else if(!newPwd_et.getText().toString().equals(confirmPwd_et.getText().toString())){
            Toast.makeText(ChangePwdActivity.this,"密码不一致",Toast.LENGTH_SHORT).show();
            res = false;
        }else if(oldPwd_et.getText().toString().equals(newPwd_et.getText().toString())){
            Toast.makeText(ChangePwdActivity.this,"新密码和旧密码一样",Toast.LENGTH_SHORT).show();
            res = false;
        }
        if(res){
            updatePwd(newPwd_et.getText().toString());
        }
    }

    private void updatePwd(String pwd){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("what",19);
            jsonObject.put("uid", user.getUid());
            jsonObject.put("pwd",md5Utils.getMD5Code(pwd));
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            Toast.makeText(ChangePwdActivity.this,"密码修改成功",Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessageDelayed(0,1000);
        });
        myAsyncTask.execute(jsonObject);
    }

    Handler mHandler = new Handler(message -> {
        switch (message.what){
            case 0:
                SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                sp.edit().putInt("status",0).putString("password",null).apply();
                Intent intent = new Intent(ChangePwdActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
        }
        return false;
    });

    public boolean isPassword(String password){
        String regex="^(?![0-9])(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";
        Pattern p= Pattern.compile(regex);
        Matcher m=p.matcher(password);
        boolean isMatch=m.matches();
        return isMatch;
    }
}
