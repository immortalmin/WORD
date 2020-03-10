package com.immortalmin.www.word;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
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
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginFragment extends Fragment implements View.OnClickListener{
    private OnFragmentInteractionListener mListener;
    private ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);
    private EditText username,password;
    private Button login_btn,reg_btn,forget_pwd;
    private CircleImageView profile_photo;
    private HashMap<String,Object> userdata=null;
    private HashMap<String,Object> userSetting=null;
    private JsonRe jsonRe = new JsonRe();
    private ImageUtils imageUtils = new ImageUtils();
    private Intent intent;
    private UserData userData = new UserData();

    /**
     * Activity绑定上Fragment时，调用该方法
     * 这个是第一次被调用的
     * @param context
     */
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Fragment显示的内容是怎样的，就是通过下面这个方法返回回去的(view)
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_login,null);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        username = (EditText)getActivity().findViewById(R.id.username);
        password = (EditText)getActivity().findViewById(R.id.password);
        login_btn = (Button)getActivity().findViewById(R.id.login_btn);
        reg_btn = (Button)getActivity().findViewById(R.id.reg_btn);
        forget_pwd = (Button)getActivity().findViewById(R.id.forget_pwd);
        profile_photo = (CircleImageView)getActivity().findViewById(R.id.profile_photo);
        login_btn.setOnClickListener(this);
        reg_btn.setOnClickListener(this);
        forget_pwd.setOnClickListener(this);
        profile_photo.setOnClickListener(this);
        /**
         * 接受来自activity的数据
         */
        Bundle bundle = getArguments();
        userData = (UserData)bundle.getSerializable("userData");

        username.setText(userData.getUsername());
        password.setText(userData.getPassword());
        getImage(userData.getProfile_photo());
        login();
        init();

    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void loginFragmentInteraction(HashMap<String,Object> data);
    }


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 0:
                    profile_photo.setImageBitmap((Bitmap)message.obj);
                    break;
            }
            return false;
        }
    });


    private void init() {
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                login();
            }
        });
    }
    /**
     * 选项按钮点击事件
     * @param view
     */
    public void onClick(View view){
        switch (view.getId()){
            case R.id.login_btn:
//                login();
                judge();
                break;
            case R.id.reg_btn:
                send_to_activity(1);
//                intent = new Intent(LoginActivity.this, RegisterActivity.class);
//                startActivityForResult(intent,1);
//                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
//                LoginActivity.this.finish();
                break;
            case R.id.profile_photo:

                break;
            case R.id.forget_pwd:
//                intent = new Intent(LoginActivity.this, SMSVerificationActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
        }
    }

    /**
     * 写好jsonObject后，获取userdata
     * 名字难取
     */
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
                if(userdata.size()!=0){
                    setImage(userdata.get("profile_photo").toString());
                }
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
                userData.setUid(userSetting.get("uid").toString());
                userData.setRecite_num(Integer.valueOf(userSetting.get("recite_num").toString()));
                userData.setRecite_scope(Integer.valueOf(userSetting.get("recite_scope").toString()));
                send_to_activity(0);
            }
        }).start();
    }

    private void judge(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String pwd = password.getText().toString();
                Looper.prepare();
                if(userdata.size()==0){
                    Toast.makeText(getActivity(),"用户不存在",Toast.LENGTH_SHORT).show();
                }else{
                    if(pwd.equals(userdata.get("pwd"))){
                        userData.setUsername(userdata.get("username").toString());
                        userData.setPassword(userdata.get("pwd").toString());
                        userData.setProfile_photo(userdata.get("profile_photo").toString());
                        userData.setStatus("1");

                        get_setting();
                        Toast.makeText(getActivity(),"登录成功",Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(getActivity(),"密码错误",Toast.LENGTH_SHORT).show();
                    }
                }
                Looper.loop();
            }
        }).start();

    }

    private void setImage(String pic) {
        Bitmap bitmap=imageUtils.getPhotoFromStorage(pic);
        if(bitmap==null){
            Log.i("ccc","照片不存在");
            getImage(pic);
        }else{
            Log.i("ccc","照片存在");
            mHandler.obtainMessage(0,bitmap).sendToTarget();
        }
    }

    private void getImage(final String pic){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                Bitmap bitmap = httpGetContext.HttpclientGetImg("http://47.98.239.237/word/img/"+pic);
                imageUtils.savePhotoToStorage(bitmap,pic);
                mHandler.obtainMessage(0,bitmap).sendToTarget();
            }
        }).start();
    }

    /**
     * 向activity回送数据
     */
    public void send_to_activity(int what){
        if (mListener != null) {
            HashMap<String,Object> data = new HashMap<>();
            data.put("what",what);
            switch (what){
                //登录成功
                case 0:
                    data.put("userData",userData);
                    break;
                //按钮点击事件
                case 1:
                    data.put("btn","register");
                    break;
            }
            mListener.loginFragmentInteraction(data);
        }
    }



}
