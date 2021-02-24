package com.immortalmin.www.word;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class SynchronizeActivity extends AppCompatActivity implements View.OnClickListener{

    private Button uploadBtn,downloadBtn,returnBtn,onlyWifiBtn;
    private ProgressBar progressBar;
    private MyAsyncTask myAsyncTask;
    private User user;
    private DataUtil dataUtil = new DataUtil(this);
    private JsonRe jsonRe = new JsonRe();
    private CollectDbDao collectDbDao = new CollectDbDao(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchronize);
        uploadBtn = findViewById(R.id.uploadBtn);
        downloadBtn = findViewById(R.id.downloadBtn);
        returnBtn = findViewById(R.id.returnBtn);
        onlyWifiBtn = findViewById(R.id.onlyWifiBtn);
        progressBar = findViewById(R.id.progressBar);
        uploadBtn.setOnClickListener(this);
        downloadBtn.setOnClickListener(this);
        returnBtn.setOnClickListener(this);
        onlyWifiBtn.setOnClickListener(this);
        init();
    }

    private void init() {
        user = dataUtil.set_user();
    }

    /**
     * 确认上传dialog
     */
    private void uploadConfirmDialog(){
        SweetAlertDialog confirm_alert = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        confirm_alert.setTitleText("上传数据")
                .setContentText("确定将本地数据上传到云端？")
                .setConfirmText("是")
                .setCancelText("否")
                .setConfirmClickListener(sweetAlertDialog -> {
                    uploadData();
                    sweetAlertDialog.cancel();
                })
                .setCancelClickListener(SweetAlertDialog::cancel);
        confirm_alert.setCancelable(false);
        confirm_alert.show();
    }

    /**
     * 确认同步本地数据dialog
     */
    private void downloadConfirmDialog(){
        SweetAlertDialog confirm_alert = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        confirm_alert.setTitleText("同步数据")
                .setContentText("将以云端的数据为主，同步本地的数据")
                .setConfirmText("是")
                .setCancelText("否")
                .setConfirmClickListener(sweetAlertDialog -> {
                    downloadData();
                    sweetAlertDialog.cancel();
                })
                .setCancelClickListener(SweetAlertDialog::cancel);
        confirm_alert.setCancelable(false);
        confirm_alert.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.uploadBtn:
                uploadConfirmDialog();
                break;
            case R.id.downloadBtn:
                downloadConfirmDialog();
                break;
            case R.id.returnBtn:
                finish();
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
        }
    }

    /**
     * 上传数据
     * 收藏的变化
     * 背诵数据的变化
     * 单词本身数据的变化（暂时不处理）
     */
    private void uploadData() {
        ArrayList<DetailWord> wordList = collectDbDao.getSyncList();
        if(wordList.size()==0){
            Toast.makeText(this,"没有需要的同步的数据",Toast.LENGTH_SHORT).show();
            return ;
        }
        mHandler.sendEmptyMessage(0);
        progressBar.setMax(1000);
        progressBar.setProgress(0);
        new Thread(()->{
            for(int i=0;i<wordList.size();i++){
                DetailWord syncWord = wordList.get(i);
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("what",27);
                    jsonObject.put("uid",user.getUid());
                    jsonObject.put("wid",syncWord.getWid());
                    /**
                     * 如果直接为null的话，似乎无法正常存入JSONObject，所以改成"null"
                     */
//                jsonObject.put("cid",wordList.get(i).getCid());
                    if(wordList.get(i).getCid()==null) jsonObject.put("cid","null");
                    else jsonObject.put("cid",syncWord.getCid());
                    jsonObject.put("gid",syncWord.getGid());
                    jsonObject.put("correct_times",syncWord.getCorrect_times());
                    jsonObject.put("error_times",syncWord.getError_times());
                    jsonObject.put("last_date",syncWord.getLast_date());
                    jsonObject.put("review_date",syncWord.getReview_date());
                    jsonObject.put("dict_source",syncWord.getDict_source());
                    jsonObject.put("isCollect",syncWord.isCollect()?1:0);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                myAsyncTask = new MyAsyncTask();
                myAsyncTask.setLoadDataComplete((result -> {
                    try {
                        JSONObject jsonObject1 = new JSONObject(result);
                        int what = jsonObject1.getInt("what");
                        if(what==0){//在云端collect添加了新数据,需要在本地修改isSynchronized=1和cid
                            int cid = jsonObject1.getInt("cid");
                            collectDbDao.execCommonSQL("update collect set cid="+cid+",isSynchronized=1 where id="+syncWord.getHid());
                        }else if(what==1||what==3){//在云端collect删除了该条记录或无效数据，需要在本地删除这条记录
                            collectDbDao.execCommonSQL("delete from collect where id="+syncWord.getHid());
                        }else if(what==2){//在云端collect更新了背诵数据，需要在本地修改isSynchronized=1
                            collectDbDao.execCommonSQL("update collect set isSynchronized=1 where id="+syncWord.getHid());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }));
                myAsyncTask.execute(jsonObject);
                int progress = (int)(i/(float)wordList.size()*1000);
                progressBar.setProgress(progress);
            }
            mHandler.sendEmptyMessage(1);
            Looper.prepare();
            Toast.makeText(this,"同步成功",Toast.LENGTH_SHORT).show();
            Looper.loop();
        }).start();

    }

    /**
     * 下载数据
     */
    private void downloadData() {
        mHandler.sendEmptyMessage(0);
        progressBar.setMax(1000);
        progressBar.setProgress(0);
        //假进度，来弥补获取网络数据时没有进度显示的缺陷
        Thread fakeThread = new Thread(()->{
            for(int i=0;i<500;i++){
                progressBar.setProgress(i);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        fakeThread.start();
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("what",4);
            jsonObject.put("uid", user.getUid());
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            ArrayList<DetailWord> wordList = jsonRe.detailWordData(result);
            collectDbDao.deleteData();//清除本地的数据
            fakeThread.interrupt();
            int fakeProgress = progressBar.getProgress();
            new Thread(()->{
                for(int i=0;i<wordList.size();i++){
                    collectDbDao.insertData(wordList.get(i),false);
                    int progress = (int)(i/(float)wordList.size()*(1000-fakeProgress));
                    progressBar.setProgress(fakeProgress+progress);
                }
                mHandler.sendEmptyMessage(1);
                Looper.prepare();
                Toast.makeText(this,"同步成功",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }).start();
        });
        myAsyncTask.execute(jsonObject);
    }

    private Handler mHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    progressBar.setVisibility(View.INVISIBLE);
                    break;
            }
            return false;
        }
    });

}
