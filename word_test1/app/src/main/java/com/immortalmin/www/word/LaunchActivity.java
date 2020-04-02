package com.immortalmin.www.word;

import android.Manifest;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class LaunchActivity extends AppCompatActivity {

    private HashMap<String,Object> userdata=null;
    private HashMap<String,Object> userSetting=null;
    JsonRe jsonRe;
    private int permission_num=0;
    private UseTimeDataManager mUseTimeDataManager = new UseTimeDataManager(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        init();
        //后台处理耗时任务
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //耗时任务，比如加载网络数据
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //跳转至 MainActivity
//                        Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
//                        startActivity(intent);
//                        //结束当前的 Activity
//                        LaunchActivity.this.finish();
//                    }
//                });
//            }
//        }).start();




    }
    private void jump_activity(){
        Handler handler = new Handler();
        SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        String status = sp.getString("status",null);
        if("1".equals(status)){
            getuserdata();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(LaunchActivity.this, MainActivity.class));
                    overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                    LaunchActivity.this.finish();
                }
            }, 2000);
        }else{
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
                    overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                    LaunchActivity.this.finish();
                }
            }, 2000);
        }
    }


    private void init() {
        jsonRe = new JsonRe();
        judgePermission();

        test();


        inspect_usetime();
    }

    private void test() {
        long test_time = 1585740654*1000;
        SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        sp.edit().putLong("lastday",test_time).apply();
    }

    private void inspect_usetime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
        //获取当前时间
        long now_time_stamp = System.currentTimeMillis();
        Date date = new Date(now_time_stamp);
        String nowday = simpleDateFormat.format(date);

        //获取上一次记录的时间
        SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        long last_time_stamp = sp.getLong("lastday",1585740654);
        Date last_date = new Date(last_time_stamp);
        String lastday = simpleDateFormat.format(last_date);
        if(!nowday.equals(lastday)){
            Log.i("ccc","不是同一天");
            sp.edit().putLong("lastday",now_time_stamp).apply();
            mUseTimeDataManager = UseTimeDataManager.getInstance(LaunchActivity.this);
            mUseTimeDataManager.refreshData(last_time_stamp,now_time_stamp);
            long use_time=0;
            JSONObject jsonObject = new JSONObject();
            List<PackageInfo> packageInfos = mUseTimeDataManager.getmPackageInfoListOrderByTime();
            for (int i = 0; i < packageInfos.size(); i++) {
                if ("com.immortalmin.www.word".equals(packageInfos.get(i).getmPackageName())) {
                    try {
                        jsonObject.put("count",packageInfos.get(i).getmUsedCount());
                        jsonObject.put("name",packageInfos.get(i).getmPackageName());
                        jsonObject.put("time",packageInfos.get(i).getmUsedTime());
                        jsonObject.put("appname",packageInfos.get(i).getmAppName());
                        use_time = packageInfos.get(i).getmUsedTime();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }

            }
            Log.i("ccc",jsonObject.toString());
            Log.i("ccc",lastday+"  +  " +use_time);
        }else{
            Log.i("ccc","是同一天");
        }




//        SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
//        sp.edit().putString("username", userdata.get("username").toString())
//                .putString("password", userdata.get("pwd").toString())
//                .putString("profile_photo", userdata.get("profile_photo").toString())
//                .putString("status","1")
//                .apply();
    }

    public String getJsonObjectStr() {
        String jsonAppdeTails = "";
        try {
            List<PackageInfo> packageInfos = mUseTimeDataManager.getmPackageInfoListOrderByTime();
            JSONObject jsonObject2 = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < packageInfos.size(); i++) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    if("com.immortalmin.www.word".equals(packageInfos.get(i).getmPackageName())){

                    }
                    jsonArray.put(i, jsonObject.accumulate("count", packageInfos.get(i).getmUsedCount()));
                    jsonArray.put(i, jsonObject.accumulate("name", packageInfos.get(i).getmPackageName()));
                    jsonArray.put(i, jsonObject.accumulate("time", packageInfos.get(i).getmUsedTime()));
                    jsonArray.put(i, jsonObject.accumulate("appname", packageInfos.get(i).getmAppName()));
                } catch (JSONException e) {
                    e.printStackTrace();
                    return "";
                }

            }
            jsonObject2.put("details", jsonArray);
            jsonAppdeTails = jsonObject2.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
        return jsonAppdeTails;
    }

    private void getuserdata() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                try {
                    SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                    jsonObject.put("username",sp.getString("username",null));
                }catch (JSONException e){
                    e.printStackTrace();
                }
                HttpGetContext httpGetContext = new HttpGetContext();
                String wordjson = httpGetContext.getData("http://47.98.239.237/word/php_file2/getuserdata.php",jsonObject);
                userdata = jsonRe.userData(wordjson);
                get_setting();
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

    //6.0之后要动态获取权限，重要！！！
    protected void judgePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝

            // sd卡权限
            String[] SdCardPermission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (ContextCompat.checkSelfPermission(this, SdCardPermission[0]) != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                permission_num=permission_num+1;
                ActivityCompat.requestPermissions(this, SdCardPermission, 100);
            }

            //手机状态权限
            String[] readPhoneStatePermission = {Manifest.permission.READ_PHONE_STATE};
            if (ContextCompat.checkSelfPermission(this, readPhoneStatePermission[0]) != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                permission_num=permission_num+1;
                ActivityCompat.requestPermissions(this, readPhoneStatePermission, 200);

            }

            //定位权限
            String[] locationPermission = {Manifest.permission.ACCESS_FINE_LOCATION};
            if (ContextCompat.checkSelfPermission(this, locationPermission[0]) != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                permission_num=permission_num+1;
                ActivityCompat.requestPermissions(this, locationPermission, 300);

            }

            String[] ACCESS_COARSE_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION};
            if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION[0]) != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                permission_num=permission_num+1;
                ActivityCompat.requestPermissions(this, ACCESS_COARSE_LOCATION, 400);

            }


            String[] READ_EXTERNAL_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE};
            if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE[0]) != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                permission_num=permission_num+1;
                ActivityCompat.requestPermissions(this, READ_EXTERNAL_STORAGE, 500);

            }

            String[] WRITE_EXTERNAL_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE[0]) != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                permission_num=permission_num+1;
                ActivityCompat.requestPermissions(this, WRITE_EXTERNAL_STORAGE, 600);

            }

            //有权查看使用情况
            if(isNoOption()&&!isNoSwitch()){
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                this.startActivity(intent);
            }


        }else{
            //doSdCardResult();
        }
        //LocationClient.reStart();


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
        permission_num=permission_num-1;
        if(permission_num==0){
            jump_activity();
        }
    }
}
