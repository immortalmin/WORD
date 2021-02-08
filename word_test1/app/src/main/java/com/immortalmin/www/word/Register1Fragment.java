package com.immortalmin.www.word;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mob.MobSDK;
import com.mob.OperationCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


/**
 * 绑定
 */
public class Register1Fragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "Register1Fragment";
    private OnFragmentInteractionListener mListener;
    private Context context;
    private EventHandler eventHandler;
    private MyAsyncTask myAsyncTask;
    private Runnable skipToLogin;
    private SweetAlertDialog notBindingWarn;
    private User user;
    private JsonRe jsonRe = new JsonRe();
    private CaptureUtil captureUtil = new CaptureUtil();
    private MyEditText telephone_tv,code_tv;
    private Button getCodeBtn;
    private ImageView imgview;
    private boolean got_flag = false;


    public Register1Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register1, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getCodeBtn = Objects.requireNonNull(getActivity()).findViewById(R.id.getCodeBtn);
        Button commitBtn = Objects.requireNonNull(getActivity()).findViewById(R.id.commitBtn);
        Button notBindingBtn = Objects.requireNonNull(getActivity()).findViewById(R.id.notBindingBtn);
        Button otherWaysBtn = Objects.requireNonNull(getActivity()).findViewById(R.id.otherWaysBtn);
        telephone_tv = Objects.requireNonNull(getActivity()).findViewById(R.id.telephone_tv);
        code_tv = Objects.requireNonNull(getActivity()).findViewById(R.id.code_tv);
        imgview = Objects.requireNonNull(getActivity()).findViewById(R.id.imgview);

        getCodeBtn.setOnClickListener(this);
        commitBtn.setOnClickListener(this);
        notBindingBtn.setOnClickListener(this);
        otherWaysBtn.setOnClickListener(this);

        init();
    }

    private void init() {
        skipToLogin = () -> {
            if (mListener != null) {
                mListener.Register1FragmentInteraction(0);
            }
        };
        submitPrivacyGrantResult(true);
        eventHandler=new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                //此处不可直接处理UI线程，处理后续操作需传到主线程中操作
                if (result == SMSSDK.RESULT_COMPLETE){
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        new Thread(()->{
                            Looper.prepare();
                            Toast.makeText(context,"验证成功",Toast.LENGTH_SHORT).show();
                            //绑定用户的手机号，也就是更新数据库中的数据
                            BindingTel();
                            Looper.loop();
                        }).start();
                    }else if (event == SMSSDK.EVENT_GET_VOICE_VERIFICATION_CODE){
                        new Thread(()-> {
                            Looper.prepare();
                            Toast.makeText(context, "语音验证发送", Toast.LENGTH_SHORT).show();
                            got_flag = true;
                            Looper.loop();
                        }).start();
                    }
                    else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        new Thread(()->{
                            Looper.prepare();
                            Toast.makeText(context,"验证码已发送",Toast.LENGTH_SHORT).show();
                            got_flag = true;
                            mHandler.obtainMessage(1).sendToTarget();
                            Looper.loop();
                        }).start();
                    }else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                        //
                    }
                }else{
                    ((Throwable)data).printStackTrace();
                    Throwable throwable = (Throwable) data;
                    throwable.printStackTrace();
//                    Log.i("ccc",throwable.toString());
                    try {
                        JSONObject obj = new JSONObject(throwable.getMessage());
                        final String des = obj.optString("detail");
                        if (!TextUtils.isEmpty(des)){
                            new Thread(()->{
                                Looper.prepare();
                                Toast.makeText(context, des, Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }).start();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        };

        //注册一个事件回调监听，用于处理SMSSDK接口请求的结果
        SMSSDK.registerEventHandler(eventHandler);

        dialog_init();
    }

    private void dialog_init(){
        notBindingWarn = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Warning")
                .setContentText("绑定手机号码是为了在忘记密码后可以重置密码\n如果选择不绑定手机号，您忘记密码后，将无法继续使用该账号")
                .setConfirmText("继续绑定")
                .setCancelText("好的")
                .setConfirmClickListener(sweetAlertDialog -> {
                    mHandler.sendEmptyMessage(3);
                    sweetAlertDialog.dismiss();
                })
                .setCancelClickListener(sweetAlertDialog -> {
                    mListener.Register1FragmentInteraction(1);
                });
        notBindingWarn.setCancelable(false);
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

                    break;
                case 1:
                    ValueAnimator mAnimator = ValueAnimator.ofInt(60,0);
                    mAnimator.setDuration(60000);
                    mAnimator.setInterpolator(input -> input);
                    mAnimator.addUpdateListener(animation -> {
                        Integer value = (Integer) animation.getAnimatedValue();
                        if(value==0){
                            getCodeBtn.setText("重发");
                            code_tv.setText("");
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
                case 2:
                    Glide.with((Activity)context).load(captureUtil.getcapture((Activity)context))
                            .apply(bitmapTransform(new BlurTransformation(25))).into(imgview);
                    imgview.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    imgview.setVisibility(View.INVISIBLE);
                    break;
            }
            return false;
        }
    });

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.getCodeBtn:
                //XXX:之前用的手机号码验证函数有点问题，现在这样只是勉强用着
                if(telephone_tv.getText().toString().length()!=11){
                    Toast.makeText(context,"请输入正确的手机号",Toast.LENGTH_SHORT).show();
                }else{
                    inspectTel();
                }
                break;
            case R.id.commitBtn:
                String code = code_tv.getText().toString();
                if(!got_flag){
                    Toast.makeText(context,"未获取验证码",Toast.LENGTH_SHORT).show();
                }else if(code.length()==0){
                    Toast.makeText(context,"请输入验证码",Toast.LENGTH_SHORT).show();
                }else{
                    SMSSDK.submitVerificationCode("86", telephone_tv.getText().toString(), code);
                }
                break;
            case R.id.notBindingBtn:
                mHandler.sendEmptyMessage(2);
                notBindingWarn.show();
                break;
            case R.id.otherWaysBtn:
                mListener.Register1FragmentInteraction(2);
                break;
        }
    }

    /**
     * 验证手机号码是否合法
     * 但是我的校园卡居然被判断成是不合法的
     * 19883833363
     * 待优化
     * @param str
     * @return
     * @throws PatternSyntaxException
     */
    public static boolean isChinaPhoneLegal(String str) throws PatternSyntaxException {
        String regExp = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(147,145))\\d{8}$";
//        ^(13[0-9]|14[5-9]|15[012356789]|166|17[0-8]|18[0-9]|19[8-9])[0-9]{8}$//还没试过，可能可以
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 绑定用户手机号
     */
    void BindingTel(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("what",23);
            jsonObject.put("uid",user.getUid());
            jsonObject.put("telephone",telephone_tv.getText().toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            mHandler.postDelayed(skipToLogin,2000);
        });
        myAsyncTask.execute(jsonObject);
    }

    /**
     * 检查手机号码是否已经被注册
     */
    private void inspectTel() {
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("telephone",telephone_tv.getText().toString());
            jsonObject.put("what",14);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            User userData = jsonRe.userData(result);
            if(userData!=null){
                Toast.makeText(context,"该手机号已被绑定",Toast.LENGTH_SHORT).show();
            }else{
                setfocus(code_tv);
                SMSSDK.getVerificationCode("86", telephone_tv.getText().toString());
            }
        });
        myAsyncTask.execute(jsonObject);
    }

    public void setUser(User user) {
        this.user = user;
    }

    private void setfocus(View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        ((Activity)context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public interface OnFragmentInteractionListener {
        void Register1FragmentInteraction(int what);
    }
}
