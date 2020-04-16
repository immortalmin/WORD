package com.immortalmin.www.word;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText login_username_edit,login_password_edit;
    private Button login_btn,reg_btn,forget_pwd,clean_btn1,clean_btn2,switch_btn;
    private CircleImageView login_profile_photo;
    private HashMap<String,Object> userdata=null;
    private HashMap<String,Object> userSetting=null;
    private JsonRe jsonRe = new JsonRe();
    private MD5Utils md5Utils = new MD5Utils();
    private ImageUtils imageUtils = new ImageUtils();
    private Intent intent;
    private Runnable toMain;
    private Context context;
    private boolean unseen_flag = true;
    private Drawable d;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        login_username_edit = (EditText)findViewById(R.id.login_username_edit);
        login_password_edit = (EditText)findViewById(R.id.login_password_edit);
        login_btn = (Button)findViewById(R.id.login_btn);
        reg_btn = (Button)findViewById(R.id.reg_btn);
        forget_pwd = (Button)findViewById(R.id.forget_pwd);
        clean_btn1 = (Button)findViewById(R.id.clean_btn1);
        clean_btn2 = (Button)findViewById(R.id.clean_btn2);
        switch_btn = (Button)findViewById(R.id.switch_btn);
        login_profile_photo = (CircleImageView)findViewById(R.id.login_profile_photo);
        login_btn.setOnClickListener(this);
        reg_btn.setOnClickListener(this);
        forget_pwd.setOnClickListener(this);
        clean_btn1.setOnClickListener(this);
        clean_btn2.setOnClickListener(this);
        switch_btn.setOnClickListener(this);
        login_profile_photo.setOnClickListener(this);

        //快速登录
        SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        login_username_edit.setText(sp.getString("username", null));
        login_password_edit.setText(sp.getString("password", null));
        getImage(sp.getString("profile_photo",null));
        login();
        init();
    }

    private void init() {
        toMain = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                LoginActivity.this.finish();
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
            }
        };
        login_username_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                login();
                if(login_username_edit.getText().toString().length()==0){
                    mHandler.obtainMessage(4,clean_btn1).sendToTarget();
                }else{
                    mHandler.obtainMessage(5,clean_btn1).sendToTarget();
                }
            }
        });
        login_password_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(login_password_edit.getText().toString().length()==0){
                    mHandler.obtainMessage(4,clean_btn2).sendToTarget();
                    mHandler.obtainMessage(4,switch_btn).sendToTarget();
                }else{
                    mHandler.obtainMessage(5,clean_btn2).sendToTarget();
                    mHandler.obtainMessage(5,switch_btn).sendToTarget();
                }
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
//                login();
                judge();
                break;
            case R.id.reg_btn:
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent,1);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
//                LoginActivity.this.finish();
                break;
            case R.id.login_profile_photo:

                break;
            case R.id.forget_pwd:
                intent = new Intent(LoginActivity.this, SMSVerificationActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
            case R.id.clean_btn1:
                login_username_edit.setText("");
                break;
            case R.id.clean_btn2:
                login_password_edit.setText("");
                break;
            case R.id.switch_btn:
                if(unseen_flag){
                    mHandler.obtainMessage(2).sendToTarget();
                    login_password_edit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    mHandler.obtainMessage(3).sendToTarget();
                    login_password_edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                login_password_edit.setSelection(login_password_edit.getText().length());
                unseen_flag = !unseen_flag;
                break;
        }
    }

    /**
     * 写好jsonObject后，获取userdata
     * 名字难取
     */
    private void login(){
        String uname = login_username_edit.getText().toString();
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
                }else{
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.unload);
                    mHandler.obtainMessage(0,bitmap).sendToTarget();
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
                SharedPreferences sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
                sp.edit().putString("uid",userSetting.get("uid").toString())
                        .putInt("recite_num",Integer.valueOf(userSetting.get("recite_num").toString()))
                        .putInt("recite_scope",Integer.valueOf(userSetting.get("recite_scope").toString()))
                        .apply();
            }
        }).start();
    }

    private void judge(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String pwd =  md5Utils.getMD5Code(login_password_edit.getText().toString());
                Looper.prepare();
                if(userdata.size()==0){
                    Toast.makeText(LoginActivity.this,"用户不存在",Toast.LENGTH_SHORT).show();
                }else{
                    if(pwd.equals(userdata.get("password")) || login_password_edit.getText().toString().equals(userdata.get("password"))){
                        SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                        sp.edit().putString("username", userdata.get("username").toString())
                                .putString("password", userdata.get("password").toString())
                                .putString("profile_photo", userdata.get("profile_photo").toString())
                                .putString("status","1")
                                .putString("email",userdata.get("email").toString())
                                .putString("telephone",userdata.get("telephone").toString())
                                .putString("motto",userdata.get("motto").toString())
                                .putLong("last_login",Long.valueOf(userdata.get("last_login").toString()))
                                .apply();

                        get_setting();
                        Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                        mHandler.obtainMessage(1).sendToTarget();
                    }else{
                        Toast.makeText(LoginActivity.this,"密码错误",Toast.LENGTH_SHORT).show();
                    }
                }
                Looper.loop();
            }
        }).start();

    }

    private void setImage(String pic) {
        Bitmap bitmap=imageUtils.getPhotoFromStorage(pic);
        if(bitmap==null){
//            Log.i("ccc","照片不存在");
            getImage(pic);
        }else{
//            Log.i("ccc","照片存在");
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

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 0:
                    login_profile_photo.setImageBitmap((Bitmap)message.obj);
                    break;
                case 1:
//                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                    LoginActivity.this.finish();
//                    overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                    mHandler.postDelayed(toMain,1000);
                    break;
                case 2:
                    d = ResourcesCompat.getDrawable(context.getResources(), R.drawable.unseen_icon, null);
                    switch_btn.setBackground(d);
                    break;
                case 3:
                    d = ResourcesCompat.getDrawable(context.getResources(), R.drawable.seen_icon, null);
                    switch_btn.setBackground(d);
                    break;
                case 4:
                    View view = (View)message.obj;
                    view.setVisibility(View.INVISIBLE);
                    break;
                case 5:
                    view = (View)message.obj;
                    view.setVisibility(View.VISIBLE);
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
        if (requestCode == 1 && resultCode == 1) {
            login_username_edit.setText(data.getExtras().getString("username"));
            login_password_edit.setText("");
        }
    }
}
