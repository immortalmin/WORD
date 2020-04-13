package com.immortalmin.www.word;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * 点击单词后进入单词例句
 * 跳转到这个界面需要传id的值
 */
public class ExampleActivity extends AppCompatActivity implements View.OnClickListener,
        AddExampleDialog.OnDialogInteractionListener,
        UpdateWordDialog.OnDialogInteractionListener,
        UpdateExampleDialog.OnDialogInteractionListener{

    private UserData userData = new UserData();
    TextView word_meaning,E_sentence,C_translate,non_example,source,C_meaning,example;
    WordView word_group;
    ListView example_list;
    Button btn1,collect,word_del_btn,word_edit_btn,edit_btn,ban_icon;
    JsonRe  jsonRe;
    private ImageView backdrop;
    private ExampleAdapter exampleAdapter;
    private MediaPlayer mediaPlayer;
    private JSONObject jsonObject;
    HashMap<String,Object> word = null;
    ArrayList<HashMap<String,Object>> examplelist = null;
    String wid = "1";
    String current_word="error";
    private String TAG = "ccc";
    private boolean first_coming = true;
    int mode=0;//0 view,1 edit
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
        source = (TextView)findViewById(R.id.source);
        example = (TextView)findViewById(R.id.example);
        word_group = (WordView) findViewById(R.id.word_group);
        C_meaning = (TextView)findViewById(R.id.C_meaning);
        example_list = (ListView)findViewById(R.id.example_list);
        btn1 = (Button)findViewById(R.id.btn1);
        word_del_btn = (Button)findViewById(R.id.word_del_btn);
        word_edit_btn = (Button)findViewById(R.id.word_edit_btn);
        edit_btn = (Button)findViewById(R.id.edit_btn);
        collect = (Button)findViewById(R.id.collect);
        ban_icon = (Button)findViewById(R.id.ban_icon);
        backdrop = (ImageView)findViewById(R.id.backdrop);
        btn1.setOnClickListener(this);
        word_del_btn.setOnClickListener(this);
        word_edit_btn.setOnClickListener(this);
        edit_btn.setOnClickListener(this);
        collect.setOnClickListener(this);
        word_group.setOnClickListener(this);
        example.setOnClickListener(this);
        mHandler.obtainMessage(1).sendToTarget();
        jsonRe=new JsonRe();
        first_coming = true;
        Intent intent = getIntent();
        wid = intent.getStringExtra("id");
        init_user();
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

    private void init_user(){
        SharedPreferences sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
        userData.setUid(sp.getString("uid",null));
        userData.setRecite_num(sp.getInt("recite_num",20));
        userData.setRecite_scope(sp.getInt("recite_scope",10));
        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        userData.setUsername(sp.getString("username",null));
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
                Intent intent = new Intent(ExampleActivity.this, ReciteWordActivity.class);
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
                addExampleDialog();
                break;
            case R.id.word_del_btn:
                del_id=1;
                del_warning();
                break;
            case R.id.word_edit_btn:
                updateWordDialog(word);
                break;
            case R.id.edit_btn:
                if(mode==0){
                    mHandler.obtainMessage(2).sendToTarget();
                    mode=1;
                }else{
                    mHandler.obtainMessage(3).sendToTarget();
                    mode=0;
                }
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
                    jsonObject.put("uid",userData.getUid());
                    jsonObject.put("wid",word.get("wid"));
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
                    jsonObject.put("uid",userData.getUid());
                    jsonObject.put("wid",Integer.valueOf(wid));
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
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 0:
                    source.setText("来源："+word.get("source").toString());
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
                        example_list.setVisibility(View.INVISIBLE);
                    }else{
                        non_example.setVisibility(View.INVISIBLE);
                        example_list.setVisibility(View.VISIBLE);
                        exampleAdapter = new ExampleAdapter(ExampleActivity.this,examplelist,mode,userData.getUsername());
                        example_list.setAdapter(exampleAdapter);
                        exampleAdapter.setOnItemClickListener(new ExampleAdapter.onItemListener() {
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
                            @Override
                            public void onEditClick(int i) {
                                updateExampleDialog(examplelist.get(i));
                            }
                        });
                    }
                    break;
                case 1:
                    source.setText("");
                    word_group.setmText("");
                    C_meaning.setText("");
                    break;
                case 2:
                    if(userData.getUsername().equals(word.get("source").toString())){
                        word_del_btn.setVisibility(View.VISIBLE);
                        word_edit_btn.setVisibility(View.VISIBLE);
                    }else{
                        ban_icon.setVisibility(View.VISIBLE);
                    }
                    collect.setVisibility(View.INVISIBLE);
                    if(examplelist.size()>0){
                        exampleAdapter.setMode(1);
                        exampleAdapter.notifyDataSetChanged();
                    }
                    edit_btn.setBackground(getResources().getDrawable(R.drawable.view1));
                    break;
                case 3:
                    word_del_btn.setVisibility(View.INVISIBLE);
                    word_edit_btn.setVisibility(View.INVISIBLE);
                    ban_icon.setVisibility(View.INVISIBLE);
                    collect.setVisibility(View.VISIBLE);
                    if(examplelist.size()>0){
                        exampleAdapter.setMode(0);
                        exampleAdapter.notifyDataSetChanged();
                    }
                    edit_btn.setBackground(getResources().getDrawable(R.drawable.edit1));
                    break;
                case 4:
                    JSONObject jsonObject = (JSONObject)message.obj;
                    try{
                        word_group.setmText(jsonObject.getString("word_group"));
                        C_meaning.setText(jsonObject.getString("C_meaning"));
                        source.setText(jsonObject.getString("source"));
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    break;
                case 5:
                    Glide.with(ExampleActivity.this).load(getcapture())
                            .apply(bitmapTransform(new BlurTransformation(25))).into(backdrop);
                    backdrop.setVisibility(View.VISIBLE);
                    break;
                case 6:
                    backdrop.setVisibility(View.INVISIBLE);
                    break;
                case 7:

                    break;
            }
            return false;
        }
    });


    private void addExampleDialog(){
        mHandler.obtainMessage(5).sendToTarget();
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("wid",wid);
            jsonObject.put("uid",userData.getUid());
            jsonObject.put("C_meaning",C_meaning.getText().toString());
        }catch (JSONException e){
            e.printStackTrace();
        }
        AddExampleDialog addExampleDialog = new AddExampleDialog(this,R.style.MyDialog,jsonObject);
        addExampleDialog.show();
        addExampleDialog.setCancelable(false);
        addExampleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mHandler.obtainMessage(6).sendToTarget();
            }
        });
    }

    private void updateWordDialog(HashMap<String,Object> data){
        mHandler.obtainMessage(5).sendToTarget();
        UpdateWordDialog updateWordDialog = new UpdateWordDialog(this,R.style.MyDialog,data);
        updateWordDialog.show();
        updateWordDialog.setCancelable(false);
        updateWordDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mHandler.obtainMessage(6).sendToTarget();
            }
        });
    }

    private void updateExampleDialog(HashMap<String,Object> data){
        mHandler.obtainMessage(5).sendToTarget();
        UpdateExampleDialog updateExampleDialog = new UpdateExampleDialog(this,R.style.MyDialog,data);
        updateExampleDialog.show();
        updateExampleDialog.setCancelable(false);
        updateExampleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mHandler.obtainMessage(6).sendToTarget();
            }
        });
    }

    /**
     * 删除警告
     */
    private void del_warning(){
        mHandler.obtainMessage(5).sendToTarget();
        SweetAlertDialog del_alert = new SweetAlertDialog(ExampleActivity.this,SweetAlertDialog.WARNING_TYPE);
        del_alert.setTitleText("Really?")
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
                        mHandler.obtainMessage(6).sendToTarget();
                    }
                });
        del_alert.setCancelable(false);
        del_alert.show();
    }

    private void delete_example(final JSONObject jsonObject){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                httpGetContext.getData("http://47.98.239.237/word/php_file2/delete_example.php",jsonObject);
            }
        }).start();
        mHandler.obtainMessage(6).sendToTarget();
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
    private void update_word(final JSONObject jsonObject){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                httpGetContext.getData("http://47.98.239.237/word/php_file2/update_word.php",jsonObject);
            }
        }).start();
//        getwordlist();
    }
    private void update_example(final JSONObject jsonObject){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                httpGetContext.getData("http://47.98.239.237/word/php_file2/update_example.php",jsonObject);
            }
        }).start();
        getwordlist();
    }

    /**
     * 截屏
     * @return
     */
    private Bitmap getcapture(){
        View view = getWindow().getDecorView();     // 获取DecorView
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        bitmap = Bitmap.createBitmap(bitmap, 0, 0,getScreenWidth(ExampleActivity.this), getScreenHeight(ExampleActivity.this), null, false);
        return bitmap;
    }

    //获取屏幕高度 不包含虚拟按键
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
    public void addExampleInteraction(JSONObject jsonObject){
        add_example(jsonObject);
    }

    @Override
    public void updateWordInteraction(JSONObject jsonObject){
        try{
            jsonObject.put("uid",userData.getUid());
        }catch (JSONException e){
            e.printStackTrace();
        }
        update_word(jsonObject);

        /**
         * 音频修改
         */
        try{
            current_word = jsonObject.getString("word_group");
        }catch (JSONException e){
            e.printStackTrace();
        }
        mediaPlayer = new MediaPlayer();
        initMediaPlayer(current_word);//音频初始化
        //有时数据库同步太慢了，只能先直接把用户改过后的数据拿来显示
        mHandler.obtainMessage(4,jsonObject).sendToTarget();
    }

    @Override
    public void updateExampleInteraction(JSONObject jsonObject){
        update_example(jsonObject);

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
