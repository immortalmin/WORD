package com.immortalmin.www.word;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePwdActivity extends AppCompatActivity implements View.OnClickListener{

    private Button return_btn,confirm_btn;
    private MyEditText oldPwd_et,newPwd_et,confirmPwd_et;
    private TextView oldPwd_warn,pwd_warn,confirm_warn;
    private UserData userData;
    private DataUtil dataUtil;
    private MD5Utils md5Utils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        return_btn = (Button)findViewById(R.id.return_btn);
        confirm_btn = (Button)findViewById(R.id.confirm_btn);
        oldPwd_et = (MyEditText)findViewById(R.id.oldPwd_et);
        newPwd_et = (MyEditText)findViewById(R.id.newPwd_et);
        confirmPwd_et = (MyEditText)findViewById(R.id.confirmPwd_et);
        oldPwd_warn = (TextView)findViewById(R.id.oldPwd_warn);
        pwd_warn = (TextView)findViewById(R.id.pwd_warn);
        confirm_warn = (TextView)findViewById(R.id.confirm_warn);
        return_btn.setOnClickListener(this);
        confirm_btn.setOnClickListener(this);
        dataUtil = new DataUtil(this);
        md5Utils = new MD5Utils();
        init();
    }

    public void init(){
        //初始化userData
        dataUtil.getdata(new DataUtil.HttpCallbackStringListener() {
            @Override
            public void onFinish(UserData userdata) {
                userData = userdata;
                Log.i("ccc",userData.toString());
            }

            @Override
            public void onError(Exception e) {

            }
        });

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
        }
    }

    private void judge() {
        boolean res = true;
        if(!userData.getPassword().equals(md5Utils.getMD5Code(oldPwd_et.getText().toString()))){
            Log.i("ccc","旧密码输入错误");
            res = false;
        }
        if(!isPassword(oldPwd_et.getText().toString())){
            Log.i("ccc","新密码格式错误");
            res = false;
        }
        //TODO:总是密码不一致
        if(!oldPwd_et.getText().toString().equals(confirmPwd_et.getText().toString())){
            Log.i("ccc","密码不一致");
            res = false;
        }
        if(res){
            Log.i("ccc","修改密码成功");
        }
    }

    public boolean isPassword(String password){
        String regex="^(?![0-9])(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";
        Pattern p= Pattern.compile(regex);
        Matcher m=p.matcher(password);
        boolean isMatch=m.matches();
        return isMatch;
    }
}
