package com.example.administrator.listviewadptwebjsonimg;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 点击单词后进入单词例句
 * 跳转到这个界面需要传id的值
 */
public class ExampleActivity extends AppCompatActivity implements View.OnClickListener,
        AddExampleDialog.OnDialogInteractionListener{

    TextView word_meaning,E_sentence,C_translate,non_example,page,C_meaning,example;
    WordView word_group;
    ListView example_list;
    Button btn1,collect,del_btn;
    JsonRe  jsonRe;
    ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);
    private MediaPlayer mediaPlayer;
    private JSONObject jsonObject;
    List<Map<String,Object>> word_list=null;
    HashMap<String,Object> word = null;
    List<HashMap<String,Object>> examplelist = null;
//    String  url="http://192.168.57.1/word/querybyid.php?id=";
    String  url="http://47.98.239.237/word/php_file/querybyid.php?id=";
    String id = "1";
    String current_word="error";
    boolean first_coming = true;
    int del_id = 1;
    int collect_flag = 0,request_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_example);
        word_meaning = (TextView)findViewById(R.id.word_meaning);
        E_sentence = (TextView)findViewById(R.id.E_sentence);
        C_translate = (TextView)findViewById(R.id.C_translate);
        non_example = (TextView)findViewById(R.id.non_example);
        page = (TextView)findViewById(R.id.page);
        example = (TextView)findViewById(R.id.example);
        word_group = (WordView) findViewById(R.id.word_group);
        C_meaning = (TextView)findViewById(R.id.C_meaning);
        example_list = (ListView)findViewById(R.id.example_list);
        btn1 = (Button)findViewById(R.id.btn1);
        del_btn = (Button)findViewById(R.id.del_btn);
        collect = (Button)findViewById(R.id.collect);
        btn1.setOnClickListener(this);
        del_btn.setOnClickListener(this);
        collect.setOnClickListener(this);
        word_group.setOnClickListener(this);
        example.setOnClickListener(this);
        mHandler.obtainMessage(1).sendToTarget();
        jsonRe=new JsonRe();
        first_coming = true;
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        /**
         * release mediaPlayer at the end of the playing
         */
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
            }
        });
        getwordlist();

    }
    /**
     * 音频播放
     */
    private void initMediaPlayer(String word) {
        try {
            //modify type to change pronunciation between US and UK
            mediaPlayer.setDataSource("http://dict.youdao.com/dictvoice?type=1&audio="+ URLEncoder.encode(word));
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                Intent intent = new Intent(ExampleActivity.this, ReciteActivity.class);
                setResult(1,intent);
                finish();
                break;
            case R.id.word_group:
                mediaPlayer.start();
                break;
            case R.id.collect:
                if(collect_flag==1){
                    Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.star_off, null);
                    collect.setBackground(drawable);
                    collect_flag=0;
                }else{
                    Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.star_on, null);
                    collect.setBackground(drawable);
                    collect_flag=1;
                }
                update_collect();
                break;
            case R.id.example:
                showExampleDialog();
                break;
            case R.id.del_btn:
                del_id=1;
                del_warning();
                break;
        }
    }

    private void delete_word(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("id",word.get("wid"));
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                httpGetContext.getData("http://47.98.239.237/word/php_file2/delete_word.php",jsonObject);
            }
        }).start();
    }

    private void update_collect(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("id",word.get("wid"));
                    jsonObject.put("collect",collect_flag);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                httpGetContext.getData("http://47.98.239.237/word/php_file2/update_collect.php",jsonObject);
            }
        }).start();
    }


    private void getwordlist() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("id",Integer.valueOf(id));
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpGetContext httpGetContext = new HttpGetContext();
                String wordjson = httpGetContext.getData("http://47.98.239.237/word/php_file2/getworddata.php",jsonObject);
                word = jsonRe.wordData(wordjson);
                String examplejson = httpGetContext.getData("http://47.98.239.237/word/php_file2/getexampledata.php",jsonObject);
                examplelist = jsonRe.exampleData(examplejson);
                mHandler.obtainMessage(0).sendToTarget();
            }
        }).start();

    }
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what == 0){
                page.setText("页码："+word.get("page").toString());
                word_group.setmText(word.get("word_group").toString());
                word_group.setAccount((float)(Integer.valueOf(word.get("correct_times").toString())/5.0));
                C_meaning.setText(word.get("C_meaning").toString());
                //set music of word
                current_word = word.get("word_group").toString();
                collect_flag = Integer.valueOf(word.get("collect").toString());
                if(first_coming){
                    mediaPlayer = new MediaPlayer();
                    initMediaPlayer(current_word);//音频初始化
                    mediaPlayer.start();
                    first_coming = false;
                }


                if(collect_flag==1){
                    Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.star_on, null);
                    collect.setBackground(drawable);
                }else{
                    Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.star_off, null);
                    collect.setBackground(drawable);
                }

                if(examplelist.size() == 0){
                    non_example.setVisibility(View.VISIBLE);
                    example_list.setVisibility(View.GONE);
                }
                ExampleAdapter exampleAdapter = new ExampleAdapter(ExampleActivity.this,examplelist);
                example_list.setAdapter(exampleAdapter);
                exampleAdapter.setOnItemDeleteClickListener(new ExampleAdapter.onItemDeleteListener() {
                    @Override
                    public void onDeleteClick(int i) {
                        jsonObject = new JSONObject();
                        try{
                            jsonObject.put("id",examplelist.get(i).get("eid").toString());
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        del_id=2;
                        del_warning();
                    }
                });
//                SimpleAdapter adapter = new SimpleAdapter(ExampleActivity.this,
//                        examplelist,R.layout.exampleitem,new String[]{
//                        "word_meaning","E_sentence","C_translate"},
//                        new int[]{R.id.word_meaning,R.id.E_sentence,R.id.C_translate});
//                example_list.setAdapter(adapter);
            }else if (msg.what==1){
                page.setText("");
                word_group.setmText("");
                C_meaning.setText("");
            }

        }
    };


    private void showExampleDialog(){
        AddExampleDialog addExampleDialog = new AddExampleDialog(this,R.style.MyDialog,Integer.valueOf(id));
        addExampleDialog.show();
    }

    /**
     * 删除警告
     */

    private void del_warning(){
        new SweetAlertDialog(ExampleActivity.this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Really?")
            .setContentText("Data will be permanently deleted.")
            .setConfirmText("OK")
            .setCancelText("No,cancel del!")
            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    if(del_id==1){
                        delete_word();
                        Intent intent = new Intent();
                        setResult(2,intent);
                        finish();
                        overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                    }else{
                        delete_example(jsonObject);
                    }
                    Toast.makeText(ExampleActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                    sweetAlertDialog.cancel();

                }
            })
            .showCancelButton(true)
            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.cancel();
                }
            })
            .show();
    }


    private void delete_example(final JSONObject jsonObject){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                httpGetContext.getData("http://47.98.239.237/word/php_file2/delete_example.php",jsonObject);
            }
        }).start();
        getwordlist();
    }

    private void add_example(final JSONObject jsonObject){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                httpGetContext.getData("http://47.98.239.237/word/php_file2/addexample.php",jsonObject);
            }
        }).start();
        getwordlist();
    }

    @Override
    public void addExampleInteraction(JSONObject jsonObject){
        add_example(jsonObject);
        Log.i("ccc","addWordInteraction:"+jsonObject.toString());
    }

    /**
     * 回车键事件
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
//            Intent intent = new Intent(ExampleActivity.this, ReciteActivity.class);
            Intent intent = new Intent();
            setResult(1,intent);
            finish();
            overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
