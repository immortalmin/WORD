package com.immortalmin.www.word;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mob.MobSDK;
import com.mob.OperationCallback;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class SMSVerificationActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "UpdatePwdActivity";
    private EventHandler eventHandler;
    private Button get_btn,commit_btn;
    private EditText telephone,verification;
    private String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_verification);
        get_btn = (Button)findViewById(R.id.get_btn);
        commit_btn = (Button)findViewById(R.id.commit_btn);
        verification = (EditText) findViewById(R.id.verification);
        telephone = (EditText) findViewById(R.id.telephone);
        get_btn.setOnClickListener(this);
        commit_btn.setOnClickListener(this);

        submitPrivacyGrantResult(true);


        eventHandler=new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                // TODO 此处不可直接处理UI线程，处理后续操作需传到主线程中操作
//                Message msg = new Message();
//                msg.arg1 = event;
//                msg.arg2 = result;
//                msg.obj = data;
//                mHandler.sendMessage(msg);
                //回调完成
                if(result == SMSSDK.RESULT_COMPLETE){
                    if(event==SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){
                        Log.i("ccc","验证成功");
                    }else if(event==SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        Log.i("ccc","获取验证码成功");
                    }else if(event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){

                    }
                }else{
                    ((Throwable)data).printStackTrace();
                }

            }
        };

        //注册一个事件回调监听，用于处理SMSSDK接口请求的结果
        SMSSDK.registerEventHandler(eventHandler);


    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.get_btn:
                // 请求验证码，其中country表示国家代码，如“86”；phone表示手机号码，如“13800138000”
                phone = telephone.getText().toString();
                SMSSDK.getVerificationCode("86", phone);
                break;
            case R.id.commit_btn:
                String code = verification.getText().toString();
                SMSSDK.submitVerificationCode("86", phone, code);
                break;
        }
    }

    private void submitPrivacyGrantResult(boolean granted) {
        MobSDK.submitPolicyGrantResult(granted, new OperationCallback<Void>() {
            @Override
            public void onComplete(Void data) {
                Log.d(TAG, "隐私协议授权结果提交：成功");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "隐私协议授权结果提交：失败");
            }
        });
    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            int event = message.arg1;
            int result = message.arg2;
            Object data = message.obj;
            if (result == SMSSDK.RESULT_COMPLETE) {
                // 处理成功的结果
                HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
                // 国家代码，如“86”
                String country = (String) phoneMap.get("country");
                // 手机号码，如“13800138000”
                String phone = (String) phoneMap.get("phone");
                // TODO 利用国家代码和手机号码进行后续的操作
            } else{
                // TODO 处理错误的结果
            }
            return false;
        }
    });

    // 使用完EventHandler需注销，否则可能出现内存泄漏
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }
}
