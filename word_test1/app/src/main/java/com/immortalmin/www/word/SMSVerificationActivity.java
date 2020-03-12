package com.immortalmin.www.word;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mob.MobSDK;
import com.mob.OperationCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class SMSVerificationActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "UpdatePwdActivity";
    private EventHandler eventHandler;
    private Button get_btn,commit_btn,return_btn;
    private EditText telephone,verification;
    private String phone;
    private Intent intent;
    private boolean got_flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_verification);
        get_btn = (Button)findViewById(R.id.get_btn);
        commit_btn = (Button)findViewById(R.id.commit_btn);
        return_btn = (Button)findViewById(R.id.return_btn);
        verification = (EditText) findViewById(R.id.verification);
        telephone = (EditText) findViewById(R.id.telephone);
        get_btn.setOnClickListener(this);
        commit_btn.setOnClickListener(this);
        return_btn.setOnClickListener(this);

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
                if (result == SMSSDK.RESULT_COMPLETE){

                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SMSVerificationActivity.this,"验证成功",Toast.LENGTH_SHORT).show();
                                mHandler.obtainMessage(0).sendToTarget();
                            }
                        });
                    }else if (event == SMSSDK.EVENT_GET_VOICE_VERIFICATION_CODE){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SMSVerificationActivity.this,"语音验证发送",Toast.LENGTH_SHORT).show();
                                got_flag = true;
                            }
                        });
                    }
                    else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SMSVerificationActivity.this,"验证码已发送",Toast.LENGTH_SHORT).show();
                                got_flag = true;
                                mHandler.obtainMessage(1).sendToTarget();
                            }
                        });
                    }else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                        Log.i("test","test");
                    }
                }else{
                    ((Throwable)data).printStackTrace();
                    Throwable throwable = (Throwable) data;
                    throwable.printStackTrace();
                    Log.i("ccc",throwable.toString());
                    try {
                        JSONObject obj = new JSONObject(throwable.getMessage());
                        final String des = obj.optString("detail");
                        if (!TextUtils.isEmpty(des)){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SMSVerificationActivity.this,des,Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        };

        //注册一个事件回调监听，用于处理SMSSDK接口请求的结果
        SMSSDK.registerEventHandler(eventHandler);


    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.get_btn:
                phone = telephone.getText().toString();
                if(phone.length()!=11){
                    Toast.makeText(SMSVerificationActivity.this,"手机号码格式错误",Toast.LENGTH_SHORT).show();
                }else{
                    SMSSDK.getVerificationCode("86", phone);
                }
                break;
            case R.id.commit_btn:
                String code = verification.getText().toString();
                if(!got_flag){
                    Toast.makeText(SMSVerificationActivity.this,"未获取验证码",Toast.LENGTH_SHORT).show();
                }else if(code.length()==0){
                    Toast.makeText(SMSVerificationActivity.this,"请输入验证码",Toast.LENGTH_SHORT).show();
                }else{
                    SMSSDK.submitVerificationCode("86", phone, code);
                }
                break;
            case R.id.return_btn:
                intent = new Intent(SMSVerificationActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
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
            switch (message.what){
                case 0:
                    intent = new Intent(SMSVerificationActivity.this,UpdatePwdActivity.class);
                    intent.putExtra("telephone",telephone.getText().toString());
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                    break;
                case 1:
                    ValueAnimator mAnimator = ValueAnimator.ofInt(60,0);
                    mAnimator.setDuration(60000);
                    mAnimator.setInterpolator(new TimeInterpolator() {
                        @Override
                        public float getInterpolation(float input) {
                            return input;
                        }
                    });
                    mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            Integer value = (Integer) animation.getAnimatedValue();
                            if(value==0){
                                get_btn.setText("重发");
                            }else{
                                get_btn.setText(value+"s");
                            }
                        }
                    });
                    mAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            //动画开始后按钮无效
                            get_btn.setEnabled(false);
                        }
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            //动画结束后按钮恢复
                            get_btn.setEnabled(true);

                        }
                    });
                    mAnimator.start();
                    break;
            }



//            int event = message.arg1;
//            int result = message.arg2;
//            Object data = message.obj;
//            if (result == SMSSDK.RESULT_COMPLETE) {
//                // 处理成功的结果
//                HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
//                // 国家代码，如“86”
//                String country = (String) phoneMap.get("country");
//                // 手机号码，如“13800138000”
//                String phone = (String) phoneMap.get("phone");
//                // TODO 利用国家代码和手机号码进行后续的操作
//            } else{
//                // TODO 处理错误的结果
//            }
            return false;
        }
    });

    // 使用完EventHandler需注销，否则可能出现内存泄漏
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            intent = new Intent(SMSVerificationActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
