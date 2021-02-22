package com.immortalmin.www.word;

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

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class SynchronizeActivity extends AppCompatActivity implements View.OnClickListener{

    private Button uploadBtn,downloadBtn;
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
        progressBar = findViewById(R.id.progressBar);
        uploadBtn.setOnClickListener(this);
        downloadBtn.setOnClickListener(this);
        init();
    }

    private void init() {
        user = dataUtil.set_user();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.uploadBtn:
                uploadData();
                break;
            case R.id.downloadBtn:
                downloadData();
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
        Log.i("ccc","syncList:"+wordList.toString());
        JSONObject jsonObject = new JSONObject();
        for(int i=0;i<wordList.size();i++){
            try{
                jsonObject.put("wid",wordList.get(i).getWid());
                jsonObject.put("word_en",wordList.get(i).getWord_en());
                jsonObject.put("word_ch",wordList.get(i).getWord_ch());
                jsonObject.put("correct_times",wordList.get(i).getCorrect_times());
                jsonObject.put("error_times",wordList.get(i).getError_times());
                jsonObject.put("last_date",wordList.get(i).getLast_date());
                jsonObject.put("review_date",wordList.get(i).getReview_date());
                jsonObject.put("gid",wordList.get(i).getGid());
                jsonObject.put("dict_source",wordList.get(i).getDict_source());
                jsonObject.put("source",wordList.get(i).getSource());
                jsonObject.put("isCollect",wordList.get(i).isCollect()?1:0);
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
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
            collectDbDao.deleteData();
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
