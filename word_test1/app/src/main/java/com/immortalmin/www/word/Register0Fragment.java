package com.immortalmin.www.word;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 设置头像、用户名、密码
 */
public class Register0Fragment extends Fragment implements View.OnClickListener{

    private Context context;
    private OnFragmentInteractionListener mListener;
    private CircleImageView profile_photo;
    private MyEditText username_tv,password_tv,confirm_tv;
    private TextView user_warn,pwd_warn,confirm_warn;
    private Button nextBtn;
    private User user = new User();
    private JsonRe jsonRe = new JsonRe();
    private MD5Utils md5Utils = new MD5Utils();
    private MyAsyncTask myAsyncTask;
    private Runnable skipToNext;
    private String profilePhotoPath="null";
    private boolean IsUsername=false,IsPassword=false,IsConfirm=false;
    private HashMap<String,Object> returnedData = new HashMap<>();

    public Register0Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_register0, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        profile_photo = Objects.requireNonNull(getActivity()).findViewById(R.id.profile_photo);
        username_tv = Objects.requireNonNull(getActivity()).findViewById(R.id.username_tv);
        user_warn = Objects.requireNonNull(getActivity()).findViewById(R.id.user_warn);
        password_tv = Objects.requireNonNull(getActivity()).findViewById(R.id.password_tv);
        pwd_warn = Objects.requireNonNull(getActivity()).findViewById(R.id.pwd_warn);
        confirm_tv = Objects.requireNonNull(getActivity()).findViewById(R.id.confirm_tv);
        confirm_warn = Objects.requireNonNull(getActivity()).findViewById(R.id.confirm_warn);

        nextBtn = Objects.requireNonNull(getActivity()).findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(this);
        profile_photo.setOnClickListener(this);

        init();
    }

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
            case R.id.nextBtn:
//                returnedData.clear();
//                returnedData.put("what","1");
//                mListener.Register0FragmentInteraction(returnedData);
                if(!judge()){
                    break;
                }
                //使“下一步”按钮无法点击
                mHandler.sendEmptyMessage(7);
                //注册用户
                registerUser();
                break;
            case R.id.profile_photo:
                if (mListener != null) {
                    returnedData.clear();
                    returnedData.put("what",0);
                    mListener.Register0FragmentInteraction(returnedData);
                }
                break;
        }
    }

    /**
     * 用户注册
     */
    private void registerUser() {
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("what",101);
            jsonObject.put("username",username_tv.getText().toString());
            jsonObject.put("pwd",md5Utils.getMD5Code(password_tv.getText().toString()));
            jsonObject.put("imgpath",profilePhotoPath);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            user = jsonRe.userData(result);
            if(user!=null){
                Toast.makeText(context,"注册成功",Toast.LENGTH_SHORT).show();
                mHandler.postDelayed(skipToNext,2000);
            }else{
                Toast.makeText(context,"注册失败:"+result,Toast.LENGTH_SHORT).show();
                Log.i("ccc","error:"+result);
            }

        });
        myAsyncTask.execute(jsonObject);
    }

    private void init(){

        skipToNext = () -> {
            if (mListener != null) {
                returnedData.clear();
                returnedData.put("what",1);
                returnedData.put("user",user);
                mListener.Register0FragmentInteraction(returnedData);
            }
        };
        username_tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mHandler.obtainMessage(0).sendToTarget();
                inspectUsername();
            }
        });
        password_tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String now_pwd = password_tv.getText().toString();
                if(!isPassword(now_pwd)){
                    IsPassword = false;
                    mHandler.obtainMessage(3).sendToTarget();
                }else{
                    IsPassword = true;
                    mHandler.obtainMessage(2).sendToTarget();
                }
            }
        });
        confirm_tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(confirm_tv.getText().toString().equals(password_tv.getText().toString())){
                    IsConfirm = true;
                    mHandler.obtainMessage(4).sendToTarget();
                }else{
                    IsConfirm = false;
                    mHandler.obtainMessage(5).sendToTarget();
                }
            }
        });
    }

    private boolean judge(){
        if(username_tv.getText().toString().length()==0){
            Toast.makeText(context,"用户名为空",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!IsUsername){
            Toast.makeText(context,"用户名已存在",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password_tv.getText().toString().length()==0){
            Toast.makeText(context,"请输入密码",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!IsPassword){
            Toast.makeText(context,"密码不合法",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!IsConfirm){
            Toast.makeText(context,"密码不一致",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private Handler mHandler = new Handler(message -> {
        switch (message.what){
            case 0:
                user_warn.setVisibility(View.INVISIBLE);
                break;
            case 1:
                user_warn.setVisibility(View.VISIBLE);
                break;
            case 2:
                pwd_warn.setVisibility(View.INVISIBLE);
                break;
            case 3:
                pwd_warn.setVisibility(View.VISIBLE);
                break;
            case 4:
                confirm_warn.setVisibility(View.INVISIBLE);
                break;
            case 5:
                confirm_warn.setVisibility(View.VISIBLE);
                break;
            case 6:
                profile_photo.setImageBitmap((Bitmap)message.obj);
                break;
            case 7:
                nextBtn.setText("注册中...");
                nextBtn.setClickable(false);
                break;
        }
        return false;
    });

    /**
     * 检查用户名是否已存在
     */
    private void inspectUsername() {
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("username",username_tv.getText().toString());
            jsonObject.put("what",14);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            User userData = jsonRe.userData(result);
            if(userData!=null){
                IsUsername = false;
                mHandler.obtainMessage(1).sendToTarget();
            }else{
                IsUsername = true;
            }
        });
        myAsyncTask.execute(jsonObject);
    }

    public boolean isPassword(String password){
        String regex="^(?![0-9])(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";
        Pattern p= Pattern.compile(regex);
        Matcher m=p.matcher(password);
        boolean isMatch=m.matches();
        return isMatch;
    }

    public void showProfilePhoto(Bitmap bitmap,String imgPath){
        mHandler.obtainMessage(6,bitmap).sendToTarget();
        profilePhotoPath = imgPath;
    }

    public interface OnFragmentInteractionListener {
        void Register0FragmentInteraction(HashMap<String,Object> data);
    }
}
