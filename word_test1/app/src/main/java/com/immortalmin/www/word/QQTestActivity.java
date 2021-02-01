package com.immortalmin.www.word;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;


public class QQTestActivity extends AppCompatActivity {

    private Tencent mTencent;
    private UserInfo mUserInfo;
    private IUiListener iUiListener;
    private Button loginBtn,logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qqtest);
        loginBtn = findViewById(R.id.loginBtn);
        logoutBtn = findViewById(R.id.logoutBtn);


        loginBtn.setOnClickListener(v -> {
            loginForQQ();
        });

        logoutBtn.setOnClickListener(v -> {
            if(!mTencent.isSessionValid()){
                Log.i("ccc","未登录，无需登出");
            }else{
                mTencent.logout(this);
                Log.i("ccc","登出成功");
            }
        });


        mTencent = Tencent.createInstance("101933564", this.getApplicationContext());
        mTencent.setOpenId("4DF7D2E09A35A27A5E4E30AD28E86187");
        mTencent.setAccessToken("F36C9E5E871791A25719460950ED3216","7776000");
    }



    private void loginForQQ() {
        if (!mTencent.isSessionValid()) {
            Log.i("ccc","未登录，现在登录");
            iUiListener = new BaseUiListener() {
                @Override
                protected void doComplete(JSONObject values) {
//                    Log.i("ccc","doComplete:"+values.toString());
                    Log.i("ccc","登录成功");
                }
            };
            int res = mTencent.login(this, "all", iUiListener);
            Log.i("ccc","res:"+res);
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
            Log.i("ccc","数据1："+response.toString());
            Toast.makeText(QQTestActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
            JSONObject obj = (JSONObject) response;
            try {
                String openID = obj.getString("openid");
                String accessToken = obj.getString("access_token");
                String expires = obj.getString("expires_in");
                mTencent.setOpenId(openID);
                mTencent.setAccessToken(accessToken,expires);
                QQToken qqToken = mTencent.getQQToken();
                mUserInfo = new UserInfo(getApplicationContext(),qqToken);
                mUserInfo.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object response) {
                        doComplete((JSONObject)response);
//                        Log.i("ccc","数据2："+response.toString());
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

        /**
         * 处理返回的数据，将JSONObject转成其他数据格式
         *
         * @param values
         */
        protected void doComplete(JSONObject values) {
        }

        @Override
        public void onError(UiError e) {
            Toast.makeText(QQTestActivity.this, e.toString(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(QQTestActivity.this, "cancel", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWarning(int i) {
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.REQUEST_LOGIN){
            Tencent.onActivityResultData(requestCode,resultCode,data,iUiListener);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}


