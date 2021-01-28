package com.immortalmin.www.word;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
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
    private Button getCodeBtn,commitBtn,returnBtn;
    private MyEditText telephoneEt,codeEt;
    private MyAsyncTask myAsyncTask;
    private JsonRe jsonRe = new JsonRe();
    private Intent intent;
    private boolean gotFlag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_verification);
        getCodeBtn = findViewById(R.id.getCodeBtn);
        commitBtn = findViewById(R.id.commitBtn);
        returnBtn = findViewById(R.id.returnBtn);
        codeEt =  findViewById(R.id.codeEt);
        telephoneEt = findViewById(R.id.telephoneEt);
        getCodeBtn.setOnClickListener(this);
        commitBtn.setOnClickListener(this);
        returnBtn.setOnClickListener(this);
        submitPrivacyGrantResult(true);
        eventHandler=new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                // TODO 此处不可直接处理UI线程，处理后续操作需传到主线程中操作
                if (result == SMSSDK.RESULT_COMPLETE){
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        runOnUiThread(() -> {
                            Toast.makeText(SMSVerificationActivity.this,"验证成功",Toast.LENGTH_SHORT).show();
                            mHandler.obtainMessage(0).sendToTarget();
                        });
                    }else if (event == SMSSDK.EVENT_GET_VOICE_VERIFICATION_CODE){
                        runOnUiThread(() -> {
                            Toast.makeText(SMSVerificationActivity.this,"语音验证发送",Toast.LENGTH_SHORT).show();
                            gotFlag = true;
                        });
                    }
                    else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        runOnUiThread(() -> {
                            Toast.makeText(SMSVerificationActivity.this,"验证码已发送",Toast.LENGTH_SHORT).show();
                            gotFlag = true;
                            mHandler.obtainMessage(1).sendToTarget();
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
                            runOnUiThread(() -> Toast.makeText(SMSVerificationActivity.this,des,Toast.LENGTH_SHORT).show());
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
            case R.id.getCodeBtn:
                if(telephoneEt.getText().length()!=11){
                    Toast.makeText(SMSVerificationActivity.this,"手机号码格式错误",Toast.LENGTH_SHORT).show();
                }else{
                    inspectTel();
                }
                break;
            case R.id.commitBtn:
                String code = codeEt.getText().toString();
                if(!gotFlag){
                    Toast.makeText(SMSVerificationActivity.this,"未获取验证码",Toast.LENGTH_SHORT).show();
                }else if(code.length()==0){
                    Toast.makeText(SMSVerificationActivity.this,"请输入验证码",Toast.LENGTH_SHORT).show();
                }else{
                    SMSSDK.submitVerificationCode("86", telephoneEt.getText().toString(), code);
                }
                break;
            case R.id.returnBtn:
                finish();
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
        }
    }

    /**
     * 检查手机号码是否已经被绑定
     */
    private void inspectTel() {
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("telephone",telephoneEt.getText().toString());
            jsonObject.put("what",14);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            User userData = jsonRe.userData(result);
            if(userData!=null){
                setfocus(codeEt);
                SMSSDK.getVerificationCode("86", telephoneEt.getText().toString());
            }else{
                Toast.makeText(SMSVerificationActivity.this,"该手机号没有绑定账号",Toast.LENGTH_SHORT).show();
            }
        });
        myAsyncTask.execute(jsonObject);
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
                    intent.putExtra("telephone",telephoneEt.getText().toString());
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                    break;
                case 1:
                    ValueAnimator mAnimator = ValueAnimator.ofInt(60,0);
                    mAnimator.setDuration(60000);
                    mAnimator.setInterpolator(input -> input);
                    mAnimator.addUpdateListener(animation -> {
                        Integer value = (Integer) animation.getAnimatedValue();
                        if(value==0){
                            getCodeBtn.setText("重发");
                        }else{
                            getCodeBtn.setText(value+"s");
                        }
                    });
                    mAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            //动画开始后按钮无效
                            getCodeBtn.setEnabled(false);
                        }
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            //动画结束后按钮恢复
                            getCodeBtn.setEnabled(true);
                        }
                    });
                    mAnimator.start();
                    break;
            }
            return false;
        }
    });

    private void setfocus(View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        (SMSVerificationActivity.this).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
