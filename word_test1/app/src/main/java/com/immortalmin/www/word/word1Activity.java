package com.immortalmin.www.word;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * 获取单词列表
 * */
public class word1Activity extends AppCompatActivity implements View.OnClickListener{

    JsonRe  jsonRe;
    private BlurImageView blurImageView = new BlurImageView();
    ListView listView;
    private ImageView imgview;
    TextView all_num,finished_num;
    private RelativeLayout main_relative;
    List<HashMap<String,Object>> word_list=null;
    private WordListAdapter wordListAdapter = null;
    private int now_position=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word1);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        listView=(ListView)findViewById(R.id.ListView1);
        all_num = (TextView)findViewById(R.id.all_num);
        finished_num = (TextView)findViewById(R.id.finished_num);
        main_relative = (RelativeLayout)findViewById(R.id.main_relative);
        imgview = (ImageView)findViewById(R.id.imgview);
        finished_num.setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(word1Activity.this, ExampleActivity.class);
                String id = word_list.get(position).get("wid").toString();
                intent.putExtra("id",id);
                startActivityForResult(intent,1);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
            }
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
        getwordlist();
        get_amount();
        jsonRe=new JsonRe();

    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.finished_num:
                Log.i("ccc","finished_num");
                mHandler.obtainMessage(2).sendToTarget();
                break;
        }
    }


    private void getwordlist()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("uid",sp.getString("uid",null));
                }catch (JSONException e){
                    e.printStackTrace();
                }
                HttpGetContext httpGetContext = new HttpGetContext();
                String recitejson = httpGetContext.getData("http://47.98.239.237/word/php_file2/getwordlist.php",jsonObject);
                List<HashMap<String,Object>> wlist =jsonRe.allwordData(recitejson);
                mHandler.obtainMessage(0,wlist).sendToTarget();
            }
        }).start();

    }
    private void get_amount()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("uid",sp.getString("uid",null));
                }catch (JSONException e){
                    e.printStackTrace();
                }
                HttpGetContext httpGetContext = new HttpGetContext();
                String recitejson = httpGetContext.getData("http://47.98.239.237/word/php_file2/get_count.php",jsonObject);
                HashMap<String,Object> count = null;
                count = jsonRe.getcount(recitejson);
                mHandler.obtainMessage(1,count).sendToTarget();
            }
        }).start();

    }
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 0:
                    word_list = (List<HashMap<String,Object>>)message.obj;
                    wordListAdapter = new WordListAdapter(word1Activity.this,word_list);
                    listView.setAdapter(wordListAdapter);
                    listView.setSelection(now_position);
                    wordListAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    HashMap<String,Object> count = (HashMap<String,Object>)message.obj;
                    all_num.setText(count.get("sum").toString());
                    finished_num.setText(count.get("prof_count").toString());
                    break;
                case 2:
                    wordListAdapter.notifyDataSetChanged();
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
        bitmap = Bitmap.createBitmap(bitmap, 0, 0,getScreenWidth(word1Activity.this), getScreenHeight(word1Activity.this), null, false);
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
        getwordlist();
        get_amount();
//        mHandler.obtainMessage(4).sendToTarget();
//        if (requestCode == 1 && resultCode == 2) {
//            getwordlist();
//            get_amount();
//        }
    }

}
