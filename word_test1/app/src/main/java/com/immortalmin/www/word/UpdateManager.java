package com.immortalmin.www.word;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UpdateManager {
    private Context mContext;
    private boolean intercept = false;
    private boolean isPause = false;
    private String savePath = Environment.getExternalStorageDirectory()+"/WORD/version_update/";
    private String saveFileName = savePath + "beiyuedanci.apk";
    // 下载线程
    private Thread downLoadThread;
    private float progress;// 当前进度
    TextView text;

    private NetworkUtil networkUtil = null;
    private MyAsyncTask myAsyncTask = null;
    private JsonRe jsonRe = new JsonRe();
    private HashMap<String,String> versionData = null;
    private SweetAlertDialog download_dialog = null;
    private User user = null;
    private UserDataUtil userDataUtil = null;

    // 进度条与通知UI刷新的handler和msg常量
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;

    UpdateManager(Context context) {
        mContext = context;
        init();
    }

    private void init() {
        networkUtil = new NetworkUtil(mContext);
        userDataUtil = new UserDataUtil(mContext);
        user = userDataUtil.getUserDataFromSP();
    }

    /**
     * 检查是否是最新的版本
     */
    void checkUpdateInfo(boolean isAutoCheck) {
        if(!networkUtil.isNetworkConnected()) return ;
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("what",28);
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result->{
            versionData = jsonRe.versionData(result);
            if(isAutoCheck){
                //如果是自动检查更新，需要将用户选择忽略的版本号也考虑进去。另外如果没有新版本，不显示Toast
                if(getVersionCode(mContext)!=Integer.parseInt(versionData.get("version_code"))&&user.getIgnore_version()!=Integer.parseInt(versionData.get("version_code"))){
                    showUpdateDialog(Integer.parseInt(versionData.get("version_code")));
                }
            }else{
                //如果是用户自己手动检查更新，进行版本号比较时，忽略用户之前选择忽略的版本号。另外如果没有新版本，将显示Toast
                if(getVersionCode(mContext)!=Integer.parseInt(versionData.get("version_code"))){
                    showUpdateDialog(Integer.parseInt(versionData.get("version_code")));
                }else{
                    Toast.makeText(mContext, "已是最新版本", Toast.LENGTH_SHORT).show();
                }
            }
        }));
        myAsyncTask.execute(jsonObject);
    }


    /**
     * 获取当前本地apk的版本
     */
    private static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
                versionCode = mContext.getPackageManager().
                getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }


    private void showUpdateDialog(int current_version_code){
        SweetAlertDialog updateDialog = new SweetAlertDialog(mContext,SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Update")
                .setContentText("有新版本可以更新")
                .setConfirmText("更新")
                .setConfirmClickListener(sweetAlertDialog -> {
                    showDownloadDialog();
                    sweetAlertDialog.cancel();
                })
                .setCancelText("下次再说")
                .setCancelClickListener(sweetAlertDialog -> {
                    user.setIgnore_version(current_version_code);
                    userDataUtil.updateUserDataInServer(user,true);
//                    updateIgnoreVersion(current_version_code);
                    sweetAlertDialog.cancel();
                });
        updateDialog.setCancelable(false);
        updateDialog.show();
    }

    private void showDownloadDialog(){

        download_dialog = new SweetAlertDialog(mContext,SweetAlertDialog.PROGRESS_TYPE);
        download_dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        download_dialog.getProgressHelper().setRimColor(Color.parseColor("#bbc0c5"));
        download_dialog.setContentText("正在下载安装包...");
        download_dialog.setNeutralText("暂停");
        //FIXME:暂停下载的功能还没实现，待完成自定义dialog或学会wait()和notify()怎么用了再来搞
//        download_dialog.setNeutralClickListener(sweetAlertDialog -> {
//            if(!isPause){
//                try {
//                    downLoadThread.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                sweetAlertDialog.setConfirmText("继续");
//            }else{
//
//                sweetAlertDialog.setConfirmText("暂停");
//            }
//            isPause = !isPause;
//        });
        download_dialog.setCancelText("取消");
        download_dialog.setCancelClickListener(sweetAlertDialog -> {
            intercept = true;
            download_dialog.cancel();
        });
        download_dialog.setCancelable(false);
        download_dialog.show();
        downloadApk();
    }

//    private void updateIgnoreVersion(int current_version_code){
//        JSONObject jsonObject = new JSONObject();
//        try{
//            jsonObject.put("what",23);
//            jsonObject.put("ignore_version",current_version_code);
//            jsonObject.put("uid",user.getUid());
//        }catch (JSONException e){
//            e.printStackTrace();
//        }
//        myAsyncTask = new MyAsyncTask();
//        myAsyncTask.setLoadDataComplete((result->{
//            Log.i("ccc","更新ignore_version完成");
//            SharedPreferences sp = mContext.getSharedPreferences("login", Context.MODE_PRIVATE);
//            sp.edit().putInt("ignore_version", current_version_code)
//                    .apply();
//        }));
//        myAsyncTask.execute(jsonObject);
//    }

    /**
     * 从服务器下载APK安装包
     */
    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    private Runnable mdownApkRunnable = new Runnable() {

        @Override
        public void run() {
            URL url;
            try {
                url = new URL(versionData.get("update_url"));
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream ins = conn.getInputStream();
                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdir();
                }
                File apkFile = new File(saveFileName);
                FileOutputStream fos = new FileOutputStream(apkFile);
                int count = 0;
                byte[] buf = new byte[1024];
                while (!intercept) {
                    int numread = ins.read(buf);
                    count += numread;
                    progress = ((float) count / length);
                    // 下载进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(3);
                        mHandler.sendEmptyMessageDelayed(DOWN_OVER,1000);
                        break;
                    }
                    fos.write(buf, 0, numread);
                }
                if(intercept) apkFile.delete();
                fos.close();
                ins.close();
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 安装APK内容
     */
    private void installAPK() {
        File apkFile = new File(saveFileName);
        if (!apkFile.exists()) {
            Log.i("ccc","文件不存在:");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(getUriForFile(mContext,apkFile),"application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        mContext.startActivity(intent);
    }
    private static Uri getUriForFile(Context context, File file) {
        if (context == null || file == null) {
            throw new NullPointerException();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context.getApplicationContext(), "com.immortalmin.www.word.provider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    download_dialog.getProgressHelper().setInstantProgress(progress);
                    break;

                case DOWN_OVER:
                    download_dialog.cancel();
                    installAPK();
                    break;

                case 3:
                    download_dialog.setContentText("下载完成，即将开始安装");
//                    download_dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    break;
                default:
                    break;
            }
            return false;
        }
    });
}
