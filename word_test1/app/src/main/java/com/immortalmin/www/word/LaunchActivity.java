package com.immortalmin.www.word;

import android.Manifest;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class LaunchActivity extends AppCompatActivity implements ImgTipDialog.OnDialogInteractionListener{

    private Handler handler = new Handler();
    private Tencent tencent;
    private SyncUtil syncUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        init();
    }

    private void init() {

        syncData();//将用户的背诵数据上传服务器

        //检查权限
        handler.postDelayed(() -> {
            if(judgePermission()){
                jumpToNext();
            }
        },2000);
    }

    /**
     * 向服务器同步数据
     */
    private void syncData() {
        SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        int login_mode = sp.getInt("login_mode",-1);
        if(login_mode!=-1){
            syncUtil = new SyncUtil(this);
            syncUtil.setFinishListener(new SyncUtil.FinishListener() {
                @Override
                public void finish() {
                    //XXX:处理完数据再进入主界面
//                    Log.i("ccc","上传完成");
                }

                @Override
                public void fail() {
                    Log.i("ccc","无网络");
                }
            });
            syncUtil.syncExecutor(3,true,false,true,false,true,false);
        }
    }

    private void jumpToNext() {
        SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        int login_mode = sp.getInt("login_mode",-1);
        switch (login_mode){
            case -1://首次进入APP
                //直接跳转到登录界面
                mHandler.sendEmptyMessage(0);
                break;
            case 0://传统的账号密码
                int status = sp.getInt("status",0);
                if(status==0){//登出状态
                    //跳转到登录界面
                    mHandler.sendEmptyMessage(0);
                }else{//登入状态
                    //跳转到主界面
                    mHandler.sendEmptyMessage(1);
                }
                break;
            case 1://QQ
                tencent = Tencent.createInstance("101933564", this.getApplicationContext());
                String openid = sp.getString("open_id",null);
                String access_token = sp.getString("access_token",null);
                String expires_in = sp.getString("expires_in",null);
                if(openid!=null){
                    tencent.setOpenId(openid);
                    tencent.setAccessToken(access_token,expires_in);
                    if (tencent.isSessionValid()){//登入状态
                        //跳转到主界面
                        mHandler.sendEmptyMessage(1);
                    }else{//登出状态：已过期，需要重新授权
                        //跳转到登录界面
                        mHandler.sendEmptyMessage(0);
                    }
                }else{
                    //跳转到登录界面
                    mHandler.sendEmptyMessage(0);
                }
                break;
        }
    }

    private Handler mHandler = new Handler(message -> {
        switch (message.what){
            case 0:
                startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                finish();
                break;
            case 1:
                Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                intent.putExtra("source","1");
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                finish();
                break;
        }
        return false;
    });

    /**
     * 获取手机权限（6.0之后要动态获取权限）
     * 权限还是到要用的时候再向用户申请比较好
     */
    protected Boolean judgePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝

            //有权查看使用情况
//            if(isNoOption()&&!isNoSwitch()){
//                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tip1);
//                show_img_tip_dialog(bitmap);
//                return false;
//            }

            // sd卡权限
            String[] SdCardPermission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (ContextCompat.checkSelfPermission(this, SdCardPermission[0]) != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, SdCardPermission, 100);
                return false;
            }

            //手机状态权限
//            String[] readPhoneStatePermission = {Manifest.permission.READ_PHONE_STATE};
//            if (ContextCompat.checkSelfPermission(this, readPhoneStatePermission[0]) != PackageManager.PERMISSION_GRANTED) {
//                // 如果没有授予该权限，就去提示用户请求
//                ActivityCompat.requestPermissions(this, readPhoneStatePermission, 200);
//                return false;
//            }

            //定位权限
//            String[] locationPermission = {Manifest.permission.ACCESS_FINE_LOCATION};
//            if (ContextCompat.checkSelfPermission(this, locationPermission[0]) != PackageManager.PERMISSION_GRANTED) {
//                // 如果没有授予该权限，就去提示用户请求
//                ActivityCompat.requestPermissions(this, locationPermission, 300);
//                return false;
//            }


//            String[] ACCESS_COARSE_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION};
//            if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION[0]) != PackageManager.PERMISSION_GRANTED) {
//                // 如果没有授予该权限，就去提示用户请求
//                ActivityCompat.requestPermissions(this, ACCESS_COARSE_LOCATION, 400);
//                return false;
//            }


            String[] READ_EXTERNAL_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE};
            if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE[0]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, READ_EXTERNAL_STORAGE, 500);
                return false;
            }

            String[] WRITE_EXTERNAL_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE[0]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, WRITE_EXTERNAL_STORAGE, 600);
                return false;
            }

        }else{
            //doSdCardResult();
        }
        //LocationClient.reStart();
        return true;
    }

    private void show_img_tip_dialog(Bitmap img){
        ImgTipDialog imgTipDialog = new ImgTipDialog(this,R.style.MyDialog,img);
        imgTipDialog.setCancelable(false);
        imgTipDialog.setOnDismissListener(dialogInterface -> {

        });
        imgTipDialog.show();
    }

    @Override
    public void ImgTipInteraction(int res){
        switch (res){
            case 0:
                finish();
                break;
            case 1:
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                this.startActivityForResult(intent,1);
                break;
        }
    }

    private boolean isNoOption() {
        PackageManager packageManager = getApplicationContext()
                .getPackageManager();
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private boolean isNoSwitch() {
        long ts = System.currentTimeMillis();
        UsageStatsManager usageStatsManager = (UsageStatsManager) getApplicationContext()
                .getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST, 0, ts);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(judgePermission()){
            jumpToNext();
        }
    }

    /**
     * 子页面跳回
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(judgePermission()){
            jumpToNext();
        }
    }
}