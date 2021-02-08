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
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private MyEditText username_et,password_et;
    private Button login_btn,reg_btn,forget_pwd;
    private CircleImageView login_profile_photo,QQLoginBtn;
    private User user;
    private HashMap<String,Object> userSetting=null;
    private JsonRe jsonRe = new JsonRe();
    private MD5Utils md5Utils = new MD5Utils();
    private MyAsyncTask myAsyncTask;
    private Intent intent;
    private Runnable toMain;
    private Context context;
    private Tencent tencent;
    private UserInfo userInfo;
    private IUiListener iUiListener;
    private boolean canDirectLogin=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        username_et = findViewById(R.id.username_et);
        password_et = findViewById(R.id.password_et);
        login_btn = findViewById(R.id.login_btn);
        QQLoginBtn = findViewById(R.id.QQLoginBtn);
        reg_btn = findViewById(R.id.reg_btn);
        forget_pwd = findViewById(R.id.forget_pwd);
        login_profile_photo = findViewById(R.id.login_profile_photo);
        login_btn.setOnClickListener(this);
        QQLoginBtn.setOnClickListener(this);
        reg_btn.setOnClickListener(this);
        forget_pwd.setOnClickListener(this);
        login_profile_photo.setOnClickListener(this);
        init();
    }

    private void init() {
        initUser();
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
                if(canDirectLogin) password_et.setText("");
                canDirectLogin = false;
                getUserDataForTradition();
            }
        });
        password_et.setOnVisibleActionListener(() -> {
            if(canDirectLogin){
                password_et.setText("");
                canDirectLogin = false;
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
                canDirectLogin = false;
            }
        });

    }

    /**
     * 从SharedPreferences文件中获取用户数据
     */
    private void initUser() {
        user = new User();
        SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        user.setUid(sp.getString("uid",null));
        user.setUsername(sp.getString("username",null));
        user.setPassword(sp.getString("password",null));
        user.setLogin_mode(sp.getInt("login_mode",0));
        user.setLast_login(sp.getLong("last_login",0L));
        user.setMotto(sp.getString("motto",null));
        user.setStatus(sp.getInt("status",0));
        user.setProfile_photo(sp.getString("profile_photo",null));

        if (user.getLogin_mode() == 0) {
            username_et.setText(user.getUsername());
            if(user.getPassword()!=null){
                password_et.setText("********");
                canDirectLogin = true;
            } else{
                password_et.setText("");
                canDirectLogin = false;
            }
            getUserDataForTradition();
        }
        //设置用户头像
        setImage(user.getUid()+".jpg");
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                judge();
                break;
            case R.id.reg_btn:
                intent = new Intent(LoginActivity.this, Register2Activity.class);
                startActivityForResult(intent,1);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
            case R.id.login_profile_photo:

                break;
            case R.id.forget_pwd:
                intent = new Intent(LoginActivity.this, SMSVerificationActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
            case R.id.QQLoginBtn:
                loginForQQ();
                break;
        }
    }

    private void loginForQQ() {
        tencent = Tencent.createInstance("101933564", this.getApplicationContext());
        if (!tencent.isSessionValid()) {
            iUiListener = new BaseUiListener();
            tencent.login(this, "all", iUiListener);
        }else{
            Log.i("ccc","已登录");
        }
    }

    /**
     * 调用SDK封装好的借口，需要传入回调的实例 会返回服务器的消息
     */
    private class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object response) {
            JSONObject obj = (JSONObject) response;
            try {
                String open_id = obj.getString("openid");
                String accessToken = obj.getString("access_token");
                String expires = obj.getString("expires_in");
                tencent.setOpenId(open_id);
                tencent.setAccessToken(accessToken,expires);
                //将数据保存至本地
                SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                sp.edit().putString("access_token",accessToken)
                        .putString("expires_in",expires)
                        .apply();
                QQToken qqToken = tencent.getQQToken();
                userInfo = new UserInfo(getApplicationContext(),qqToken);
                userInfo.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object response) {
                        JSONObject jsonObject = new JSONObject();
                        try{
                            jsonObject.put("what",14);
                            jsonObject.put("login_mode",1);
                            jsonObject.put("open_id",open_id);
                            jsonObject.put("username",((JSONObject)response).getString("nickname"));
                            jsonObject.put("profile_photo",((JSONObject)response).getString("figureurl_qq"));
                            getUserDataForQQ(jsonObject);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(UiError uiError) {

                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onWarning(int i) {

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError e) {
            Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(LoginActivity.this, "cancel", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWarning(int i) {
        }
    }


    /**
     * 根据用户名来判断用户是否存在
     * 上传的数据有login_mode,username
     */
    private void getUserDataForTradition(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("what",14);
            jsonObject.put("login_mode",0);
            jsonObject.put("username",username_et.getText());
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            user = jsonRe.userData(result);
            if(user!=null){
                setImage(user.getProfile_photo());
            }else{
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.unload);
                mHandler.obtainMessage(0,bitmap).sendToTarget();
            }
        });
        myAsyncTask.execute(jsonObject);
    }

    /**
     * 根据open_id来判断用户是否是新用户
     * 上传的数据有login_mode,open_id,username,profile_photo
     * 如果用户不存在的话，就会新注册一个账号
     * @param jsonObject
     */
    private void getUserDataForQQ(final JSONObject jsonObject){
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            User user = jsonRe.userData(result);
            SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
            sp.edit().putString("uid",user.getUid())
                    .putString("open_id",user.getOpen_id())
                    .putInt("login_mode",user.getLogin_mode())
                    .putString("username",user.getUsername())
                    .putString("profile_photo",user.getProfile_photo())
                    .putLong("last_login",user.getLast_login())
                    .apply();
            Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessage(1);
        });
        myAsyncTask.execute(jsonObject);
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
                if(canDirectLogin){
                    SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                    sp.edit().putInt("status",1).apply();
                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    mHandler.obtainMessage(1).sendToTarget();
                }else if(pwd.equals(user.getPassword())){
                    SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                    sp.edit().putString("uid", user.getUid())
                            .putString("username", user.getUsername())
                            .putString("password", user.getPassword())
                            .putString("profile_photo", user.getProfile_photo())
                            .putInt("status",1)
                            .putLong("last_login",user.getLast_login())
                            .putInt("login_mode",0)
                            .apply();
                    get_setting();
                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    mHandler.obtainMessage(1).sendToTarget();
                }else{
                    Toast.makeText(LoginActivity.this,"密码错误",Toast.LENGTH_SHORT).show();
                }
            }
            Looper.loop();
        }).start();

    }

    private void setImage(String pic) {
        Bitmap bitmap=ImageUtils.getPhotoFromStorage(pic);
        if(bitmap==null){
            getImage(pic);
        }else{
            mHandler.obtainMessage(0,bitmap).sendToTarget();
        }
    }

    private void getImage(final String pic){
        new Thread(() -> {
            HttpGetContext httpGetContext = new HttpGetContext();
            Bitmap bitmap = null;
            if(user.getLogin_mode()==0){
                bitmap = httpGetContext.HttpclientGetImg("http://47.98.239.237/word/img/profile/"+pic,0);
            }else{
                bitmap = HttpGetContext.getbitmap(user.getProfile_photo());
            }
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
            User u = (User)(Objects.requireNonNull(data.getExtras()).get("user"));
            assert u != null;
            username_et.setText(u.getUsername());
            password_et.setText("");
            setfocus(password_et);
        }
        if(requestCode == Constants.REQUEST_LOGIN){
            Tencent.onActivityResultData(requestCode,resultCode,data,iUiListener);
        }
    }
}