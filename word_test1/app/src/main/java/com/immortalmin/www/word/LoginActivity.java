package com.immortalmin.www.word;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends MyAppCompatActivity implements View.OnClickListener{

    private MyEditText username_et,password_et;
    private Button login_btn,reg_btn,forget_pwd;
    private CircleImageView login_profile_photo,QQLoginBtn;
    private RelativeLayout inputLayout,rootView;
    private User user;
    private MD5Utils md5Utils = new MD5Utils();
    private UserDataUtil userDataUtil = null;
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
        rootView = findViewById(R.id.rootView);
        inputLayout = findViewById(R.id.inputLayout);
        login_btn.setOnClickListener(this);
        QQLoginBtn.setOnClickListener(this);
        reg_btn.setOnClickListener(this);
        forget_pwd.setOnClickListener(this);
        login_profile_photo.setOnClickListener(this);
        userDataUtil = new UserDataUtil(this);
        init();
    }

    private void init() {
        initUser();
        toMain = () -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("source","0");
            startActivity(intent);
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

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(listener);
    }

    ViewTreeObserver.OnGlobalLayoutListener listener = () -> {
        Rect r = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        int heightDifference = visibleHeight - (r.bottom - r.top); // 实际高度减去可视图高度即是键盘高度
        boolean isKeyboardShowing = heightDifference > visibleHeight / 3;
        if(isKeyboardShowing){
            int h1 = (visibleHeight-inputLayout.getHeight())/2;
            if(h1<heightDifference){
                inputLayout.animate().translationY(h1-heightDifference).setDuration(0).start();
            }
        }else{
            inputLayout.animate().translationY(0).start();
        }
    };

    private void initUser() {
        user = userDataUtil.getUserDataFromSP();
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
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
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
                            jsonObject.put("login_mode",1);
                            jsonObject.put("open_id",open_id);
                            jsonObject.put("username",((JSONObject)response).getString("nickname"));
                            jsonObject.put("profile_photo",((JSONObject)response).getString("figureurl_qq"));
                            userDataUtil.getUserDataFromServer(jsonObject,true,new UserDataUtil.HttpCallbackStringListener() {
                                @Override
                                public void onFinish(User userdata) {
                                    user = userdata;
                                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                                    mHandler.sendEmptyMessage(1);
                                }

                                @Override
                                public void onError(Exception e) {

                                }
                            });
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
            jsonObject.put("username",username_et.getText());
        }catch (JSONException e){
            e.printStackTrace();
        }
        userDataUtil.getUserDataFromServer(jsonObject, false, new UserDataUtil.HttpCallbackStringListener() {
            @Override
            public void onFinish(User u) {
                if(u!=null){
                    user = u;
                    setImage(u.getProfile_photo());
                }else{
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.unload);
                    mHandler.obtainMessage(0,bitmap).sendToTarget();
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
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
                    user.setStatus(1);
                    userDataUtil.updateUserDataInLocal(user);
                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    mHandler.obtainMessage(1).sendToTarget();
                }else if(pwd.equals(user.getPassword())){
                    user.setStatus(1);
                    userDataUtil.updateUserDataInLocal(user);
                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    mHandler.sendEmptyMessage(1);
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