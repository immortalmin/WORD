package com.immortalmin.www.word;

import android.app.Activity;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

//XXX:这段代码太烂了，待完善。特别是isVisible和isChanged
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private MyEditText username_et,password_et;
    private Button login_btn,reg_btn,forget_pwd;
    private CircleImageView login_profile_photo;
    private User user;
    private HashMap<String,Object> userSetting=null;
    private JsonRe jsonRe = new JsonRe();
    private MD5Utils md5Utils = new MD5Utils();
    private ImageUtils imageUtils = new ImageUtils();
    private Intent intent;
    private Runnable toMain;
    private Context context;
    private boolean unseen_flag = true;
    private Drawable d;
    private int action_mode = 0;
    /**
     * isVisible:如果是退出登录的，将无法显示密码
     * isChanged:是否修改用户名或者密码
     */
    private boolean isVisible = true,isChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        username_et = findViewById(R.id.username_et);
        password_et = findViewById(R.id.password_et);
        login_btn = findViewById(R.id.login_btn);
        reg_btn = findViewById(R.id.reg_btn);
        forget_pwd = findViewById(R.id.forget_pwd);
        login_profile_photo = findViewById(R.id.login_profile_photo);
        login_btn.setOnClickListener(this);
        reg_btn.setOnClickListener(this);
        forget_pwd.setOnClickListener(this);
        login_profile_photo.setOnClickListener(this);
        SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        username_et.setText(sp.getString("username", null));
        if(sp.getString("password", null)!=null){
            password_et.setText("********");
            isVisible = false;
        } else{
            password_et.setText("");
            isVisible = true;
        }
        getImage(sp.getString("profile_photo",null));
        login();
        init();
    }

    private void init() {
        toMain = () -> {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            LoginActivity.this.finish();
            overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
        };
        username_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isChanged = true;
                if(!isVisible){
                    password_et.setText("");
                }
                login();
            }
        });
        password_et.setOnVisibleActionListener(() -> {
            if(!isVisible){
                password_et.setText("");
                isVisible = true;
            }
        });
        password_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isChanged = true;
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
                intent = new Intent(LoginActivity.this, Register2Activity.class);
                startActivityForResult(intent,1);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
//                LoginActivity.this.finish();
                break;
            case R.id.login_profile_photo:

                break;
            case R.id.forget_pwd:
                intent = new Intent(LoginActivity.this, SMSVerificationActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
        }
    }

    /**
     * 写好jsonObject后，获取userdata
     * 名字难取
     */
    private void login(){
        String uname = username_et.getText().toString();
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("username",uname);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        getuserdata(jsonObject);
    }

    private void getuserdata(final JSONObject jsonObject) {
        new Thread(() -> {
            HttpGetContext httpGetContext = new HttpGetContext();
            String wordjson = httpGetContext.getData("http://47.98.239.237/word/php_file2/getuserdata.php",jsonObject);
            user = jsonRe.userData(wordjson);
            if(user!=null){
                setImage(user.getProfile_photo());
            }else{
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.unload);
                mHandler.obtainMessage(0,bitmap).sendToTarget();
            }
        }).start();

    }

    private void get_setting(){
        new Thread(() -> {
            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("uid",user.getUid());
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
        }).start();
    }

    private void judge(){
        new Thread(() -> {
            String pwd =  md5Utils.getMD5Code(password_et.getText().toString());
            Looper.prepare();
            if(user==null){
                Toast.makeText(LoginActivity.this,"用户不存在",Toast.LENGTH_SHORT).show();
            }else{
                //用户名和密码未修改并且用户没有点击过密码可见的按钮
                if(!isVisible&&!isChanged){
                    SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                    sp.edit().putString("status","1").apply();
                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    mHandler.obtainMessage(1).sendToTarget();
                }else if(pwd.equals(user.getPassword())){
                    SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                    sp.edit().putString("username", user.getUsername())
                            .putString("password", user.getPassword())
                            .putString("profile_photo", user.getProfile_photo())
                            .putString("status","1")
                            .putString("email",user.getEmail())
                            .putString("telephone",user.getTelephone())
                            .putString("motto",user.getMotto())
                            .putLong("last_login",user.getLast_login())
                            .apply();
                    get_setting();
                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    mHandler.obtainMessage(1).sendToTarget();
                }else{
                    Toast.makeText(LoginActivity.this,"密码错误",Toast.LENGTH_SHORT).show();
                }

                //old
//                    if(pwd.equals(userdata.get("password")) || password_et.getText().toString().equals(userdata.get("password"))){
//                        SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
//                        sp.edit().putString("username", userdata.get("username").toString())
//                                .putString("password", userdata.get("password").toString())
//                                .putString("profile_photo", userdata.get("profile_photo").toString())
//                                .putString("status","1")
//                                .putString("email",userdata.get("email").toString())
//                                .putString("telephone",userdata.get("telephone").toString())
//                                .putString("motto",userdata.get("motto").toString())
//                                .putLong("last_login",Long.valueOf(userdata.get("last_login").toString()))
//                                .apply();
//
//                        get_setting();
//                        Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
//                        mHandler.obtainMessage(1).sendToTarget();
//                    }else{
//                        Toast.makeText(LoginActivity.this,"密码错误",Toast.LENGTH_SHORT).show();
//                    }
            }
            Looper.loop();
        }).start();

    }

    private void setImage(String pic) {
        Bitmap bitmap=imageUtils.getPhotoFromStorage(pic);
        if(bitmap==null){
            getImage(pic);
        }else{
            mHandler.obtainMessage(0,bitmap).sendToTarget();
        }
    }

    private void getImage(final String pic){
        new Thread(() -> {
            HttpGetContext httpGetContext = new HttpGetContext();
            Bitmap bitmap = httpGetContext.HttpclientGetImg("http://47.98.239.237/word/img/profile/"+pic,0);
            ImageUtils.savePhotoToStorage(bitmap,pic);
            mHandler.obtainMessage(0,bitmap).sendToTarget();
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
                    mHandler.postDelayed(toMain,1000);
                    break;
            }
            return false;
        }
    });

    private void setfocus(View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        ((Activity)context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
        if (requestCode == 1 && resultCode == 1) {
            assert data != null;
//            username_et.setText(Objects.requireNonNull(data.getExtras()).getString("username"));
            User u = (User)(Objects.requireNonNull(data.getExtras()).get("user"));
            assert u != null;
            username_et.setText(u.getUsername());
            password_et.setText("");
            setfocus(password_et);
        }
    }
}
