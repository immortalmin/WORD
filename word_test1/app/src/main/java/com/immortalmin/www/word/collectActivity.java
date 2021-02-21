package com.immortalmin.www.word;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

/**
 * 获取单词列表
 * */
public class collectActivity extends AppCompatActivity implements View.OnClickListener{

    private JsonRe  jsonRe = new JsonRe();
    private User user = new User();
    private MyAsyncTask myAsyncTask = null;
    private BlurImageView blurImageView = new BlurImageView();
    private ListView listView;
    private TextView all_num,finished_num;
    private RelativeLayout main_relative;
    private List<DetailWord> word_list=null;
    private List<DetailWord> collect_list=null;
    private WordListAdapter wordListAdapter = null;
    private CollectDbDao collectDbDao = new CollectDbDao(this);
    private int now_position=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        SQLiteStudioService.instance().start(this);//连接SQLiteStudio
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        listView= findViewById(R.id.ListView1);
        all_num = findViewById(R.id.all_num);
        finished_num = findViewById(R.id.finished_num);
        main_relative = findViewById(R.id.main_relative);
        finished_num.setOnClickListener(this);
        listView.setOnItemClickListener((adapterView, view, position, l) -> {
            Intent intent = new Intent(collectActivity.this, ExampleActivity.class);
            intent.putExtra("wid",collect_list.get(position).getWid());
            intent.putExtra("dict_source",collect_list.get(position).getDict_source());
            startActivityForResult(intent,1);
            overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                now_position = i;
            }
        });
        init();

    }

    private void init() {
        init_user();
//        getCollect();
        getCollectList();
//        get_amount();
        mHandler.sendEmptyMessage(1);
    }

    private void init_user(){
        SharedPreferences sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
        user.setUid(sp.getString("uid",null));
        user.setRecite_num(sp.getInt("recite_num",20));
        user.setRecite_scope(sp.getInt("recite_scope",10));
        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        user.setUsername(sp.getString("username",null));
        user.setPassword(sp.getString("password",null));
        user.setProfile_photo(sp.getString("profile_photo",null));
        user.setStatus(sp.getInt("status",0));
        user.setLast_login(sp.getLong("last_login",946656000000L));
        user.setEmail(sp.getString("email",null));
        user.setTelephone(sp.getString("telephone",null));
        user.setMotto(sp.getString("motto",null));
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.finished_num:

                break;
        }
    }

    //从2021/2/21开始停止使用
//    private void getCollect(){
//        JSONObject jsonObject = new JSONObject();
//        try{
//            jsonObject.put("what",4);
//            jsonObject.put("uid", user.getUid());
//        }catch (JSONException e){
//            e.printStackTrace();
//        }
//        myAsyncTask = new MyAsyncTask();
//        myAsyncTask.setLoadDataComplete((result)->{
//            if(collect_list==null){
//                collect_list = jsonRe.detailWordData(result);
//                wordListAdapter = new WordListAdapter(collectActivity.this,collect_list);
//                listView.setAdapter(wordListAdapter);
//            }else{
//                collect_list.clear();
//                collect_list.addAll(jsonRe.detailWordData(result));
//                wordListAdapter.notifyDataSetChanged();
//            }
////            saveAllWordsToSqlite(collect_list);
//        });
//        myAsyncTask.execute(jsonObject);
//    }

    /**
     * 获取收藏的单词列表
     */
    private void getCollectList(){
        if(collect_list==null){
            collect_list = collectDbDao.getCollectList();
            wordListAdapter = new WordListAdapter(collectActivity.this,collect_list);
            listView.setAdapter(wordListAdapter);
        }else{
            collect_list.clear();
            collect_list.addAll(collectDbDao.getCollectList());
            wordListAdapter.notifyDataSetChanged();
        }
    }


    /**
     * 手动更新本地缓存用
     * 缓存所有收藏的单词的音频
     */
    private void getAllAudio(){
        for(int i=0;i<collect_list.size();i++){
            DetailWord detailWord = collect_list.get(i);
            int finalI = i;
            new Thread(()->{
                HttpGetContext httpGetContext = new HttpGetContext();
                httpGetContext.saveMp3IntoSD(formatWord(detailWord.getWord_en()));
                double percent =(double) finalI /(double)collect_list.size()*100.0;
                Log.i("ccc",detailWord.getWord_en()+"下载完成，进度："+percent);
            }).start();
        }
    }
    public String formatWord(String word){
        //XXX:末尾的sth没有成功替换成something
        word = word.replaceAll("sb.","somebody").replaceAll("sb ","somebody ")
                .replaceAll("sth.","something").replaceAll("sth ","something ")
                .replaceAll("/"," or ");
        //处理没有英文字母、数字的字符串
        if(word.replaceAll("[^a-zA-Z0-9]","").length()==0){
            word="nothing";
        }
        return word;
    }
    //数据库测试
    public void saveAllWordsToSqlite(List<DetailWord> word){
        CollectDbDao collectDbDao = new CollectDbDao(this);
        for(int i=0;i<word.size();i++){
            collectDbDao.insertData(word.get(i));
        }
    }


    //从2021/2/21开始停止使用
//    private void get_amount() {
//        new Thread(() -> {
//            SharedPreferences sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
//            JSONObject jsonObject = new JSONObject();
//            try{
//                jsonObject.put("uid",sp.getString("uid",null));
//            }catch (JSONException e){
//                e.printStackTrace();
//            }
//            HttpGetContext httpGetContext = new HttpGetContext();
//            String recitejson = httpGetContext.getData("http://47.98.239.237/word/php_file2/get_count.php",jsonObject);
//            HashMap<String,Object> count = null;
//            count = jsonRe.getCount(recitejson);
//            mHandler.obtainMessage(1,count).sendToTarget();
//        }).start();
//
//    }



    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 0:
                    word_list = (List<DetailWord>)message.obj;
                    wordListAdapter = new WordListAdapter(collectActivity.this,word_list);
                    listView.setAdapter(wordListAdapter);
                    listView.setSelection(now_position);
                    wordListAdapter.notifyDataSetChanged();
                    break;
                case 1:
//                    HashMap<String,Object> count = (HashMap<String,Object>)message.obj;
//                    all_num.setText(count.get("sum").toString());
//                    finished_num.setText(count.get("prof_count").toString());
                    int allCount = collectDbDao.getCollectCount();
                    int finishCount = collectDbDao.getFinishCount();
                    all_num.setText(String.valueOf(allCount));
                    finished_num.setText(String.valueOf(finishCount));
                    break;
            }
            return false;
        }
    });

    /**
     * 截屏
     * @return
     */
    private Bitmap getcapture(){
        View view = getWindow().getDecorView();     // 获取DecorView
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        bitmap = Bitmap.createBitmap(bitmap, 0, 0,getScreenWidth(collectActivity.this), getScreenHeight(collectActivity.this), null, false);
        return bitmap;
    }

    //获取屏幕高度 不包含虚拟按键=
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    //获取屏幕宽度
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
        if (requestCode == 1 && resultCode == 1) {//单词数据发生改变，则更新数据
//            getCollect();
            getCollectList();
//            get_amount();
            mHandler.sendEmptyMessage(1);
        }
    }

}
