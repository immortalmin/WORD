package com.immortalmin.www.word;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * 点击单词后进入单词例句
 * 跳转到这个界面需要传id的值
 */
public class ExampleTestActivity extends AppCompatActivity implements
        ExampleFragment.OnFragmentInteractionListener,
        KelinsiFragment.OnFragmentInteractionListener,
        UpdateExampleDialog.OnDialogInteractionListener,
        UpdateWordDialog.OnDialogInteractionListener,
        View.OnClickListener{

    private Button example_btn,kelinsi_btn,edit_btn,word_del_btn,word_edit_btn,ban_icon,collect;
    private ImageView backdrop;
    private WordView word_group;
    private TextView C_meaning,source;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentTransaction transaction = fragmentManager.beginTransaction();
    private ExampleFragment exampleFragment = new ExampleFragment();
    private KelinsiFragment kelinsiFragment = new KelinsiFragment();
    private HashMap<String,Object> word = null;
    private ArrayList<HashMap<String,Object>> examplelist = null;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private UserData userData = new UserData();
    private JsonRe jsonRe = new JsonRe();
    private CaptureUtil captureUtil = new CaptureUtil();
    private int mode=0;
    private String current_word="error",wid = "100";
    private boolean first_coming = true;
    private int collect_flag = 0, del_id = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_test);
        example_btn = (Button)findViewById(R.id.example_btn);
        kelinsi_btn = (Button)findViewById(R.id.kelinsi_btn);
        edit_btn = (Button)findViewById(R.id.edit_btn);
        word_del_btn = (Button)findViewById(R.id.word_del_btn);
        word_edit_btn = (Button)findViewById(R.id.word_edit_btn);
        ban_icon = (Button)findViewById(R.id.ban_icon);
        collect = (Button)findViewById(R.id.collect);
        backdrop = (ImageView)findViewById(R.id.backdrop);
        word_group = (WordView)findViewById(R.id.word_group);
        C_meaning = (TextView) findViewById(R.id.C_meaning);
        source = (TextView) findViewById(R.id.source);
        example_btn.setOnClickListener(this);
        kelinsi_btn.setOnClickListener(this);
        edit_btn.setOnClickListener(this);
        word_del_btn.setOnClickListener(this);
        word_edit_btn.setOnClickListener(this);
        collect.setOnClickListener(this);
        word_group.setOnClickListener(this);
        Intent intent = getIntent();
        wid = intent.getStringExtra("wid");
        init();
    }

    private void init() {
        first_coming = true;
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.framelayout,exampleFragment);
        transaction.add(R.id.framelayout,kelinsiFragment);
        transaction.hide(kelinsiFragment);
        transaction.commit();
        init_user();
        getwordlist();
        exampleFragment.setData(Integer.valueOf(wid),userData,backdrop);//设置例句fragment的数据
    }

    private void init_user(){
        SharedPreferences sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
        userData.setUid(sp.getString("uid",null));
        userData.setRecite_num(sp.getInt("recite_num",20));
        userData.setRecite_scope(sp.getInt("recite_scope",10));
        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        userData.setUsername(sp.getString("username",null));
        userData.setPassword(sp.getString("password",null));
        userData.setProfile_photo(sp.getString("profile_photo",null));
        userData.setStatus(sp.getString("status","0"));
        userData.setLast_login(sp.getLong("last_login",946656000000L));
        userData.setEmail(sp.getString("email",null));
        userData.setTelephone(sp.getString("telephone",null));
        userData.setMotto(sp.getString("motto",null));
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.example_btn:
                transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_left_in,R.anim.slide_to_right);
                transaction.hide(kelinsiFragment).show(exampleFragment);
//                FragmentTransaction
                transaction.commit();
                mHandler.obtainMessage(0).sendToTarget();
                break;
            case R.id.kelinsi_btn:
                transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_right_in,R.anim.slide_to_left);
                transaction.hide(exampleFragment).show(kelinsiFragment);
                transaction.commit();
                mHandler.obtainMessage(1).sendToTarget();
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
            case R.id.word_group:
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0);
                }
                mediaPlayer.start();
                break;
            case R.id.word_del_btn:
//                del_id=1;
                del_warning();
                break;
            case R.id.word_edit_btn:
                updateWordDialog(word);
                break;
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    example_btn.setBackgroundColor(Color.parseColor("#30000000"));
                    kelinsi_btn.setBackgroundColor(Color.parseColor("#10000000"));
                    break;
                case 1:
                    example_btn.setBackgroundColor(Color.parseColor("#10000000"));
                    kelinsi_btn.setBackgroundColor(Color.parseColor("#30000000"));
                    break;
                case 2:
                    if(userData.getUsername().equals(word.get("source").toString())){
                        word_del_btn.setVisibility(View.VISIBLE);
                        word_edit_btn.setVisibility(View.VISIBLE);
                    }else{
                        ban_icon.setVisibility(View.VISIBLE);
                    }
                    collect.setVisibility(View.INVISIBLE);
                    exampleFragment.change_mode(1);
                    edit_btn.setBackground(getResources().getDrawable(R.drawable.view1));
                    break;
                case 3:
                    word_del_btn.setVisibility(View.INVISIBLE);
                    word_edit_btn.setVisibility(View.INVISIBLE);
                    ban_icon.setVisibility(View.INVISIBLE);
                    collect.setVisibility(View.VISIBLE);
                    exampleFragment.change_mode(0);
                    edit_btn.setBackground(getResources().getDrawable(R.drawable.edit1));
                    break;
                case 4:
                    source.setText("来源："+word.get("source").toString());
                    word_group.setmText(word.get("word_group").toString());
                    word_group.setAccount((float)(Integer.valueOf(word.get("correct_times").toString())/5.0));
                    C_meaning.setText(word.get("C_meaning").toString());
                    //set music of word
                    current_word = word.get("word_group").toString();
                    collect_flag = Integer.valueOf(word.get("collect").toString());
                    resetMediaPlayer(current_word);
                    if(first_coming){
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
                    break;
                case 5:
                    Glide.with(ExampleTestActivity.this).load(captureUtil.getcapture(ExampleTestActivity.this))
                            .apply(bitmapTransform(new BlurTransformation(25))).into(backdrop);
                    backdrop.setVisibility(View.VISIBLE);
                    break;
                case 6:
                    backdrop.setVisibility(View.INVISIBLE);
                    break;
                case 7:
                    JSONObject jsonObject = (JSONObject)message.obj;
                    try{
                        word_group.setmText(jsonObject.getString("word_group"));
                        C_meaning.setText(jsonObject.getString("C_meaning"));
                        source.setText(jsonObject.getString("source"));
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    break;
            }
            return false;
        }
    });

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

    /**
     * 删除警告
     */
    private void del_warning(){
        mHandler.obtainMessage(5).sendToTarget();
        SweetAlertDialog del_alert = new SweetAlertDialog(ExampleTestActivity.this,SweetAlertDialog.WARNING_TYPE);
        del_alert.setTitleText("Really?")
                .setContentText("Data will be permanently deleted.")
                .setConfirmText("OK")
                .setCancelText("No,cancel del!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        delete_word();
                        Intent intent = new Intent();
                        setResult(2,intent);
                        finish();
                        overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                        Toast.makeText(ExampleTestActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
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
//                String examplejson = httpGetContext.getData("http://47.98.239.237/word/php_file2/getexampledata.php",jsonObject);
                mHandler.obtainMessage(4).sendToTarget();
            }
        }).start();
//        Log.i("ccc",word.toString());
    }

    private void update_word(final JSONObject jsonObject){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                httpGetContext.getData("http://47.98.239.237/word/php_file2/update_word.php",jsonObject);
            }
        }).start();

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

    private Boolean resetMediaPlayer(String word){
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        try{
            mediaPlayer.setDataSource("http://dict.youdao.com/dictvoice?type=1&audio="+ URLEncoder.encode(word));
            mediaPlayer.prepare();
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void exampleFragmentInteraction(String res){

    }

    @Override
    public void kelinsiFragmentInteraction(String res){

    }

    @Override
    public void updateExampleInteraction(JSONObject jsonObject){
        update_example(jsonObject);

    }

    @Override
    public void updateWordInteraction(JSONObject jsonObject){
        try{
            jsonObject.put("uid",userData.getUid());

            word.put("word_group",jsonObject.getString("word_group"));
            word.put("C_meaning",jsonObject.getString("C_meaning"));
        }catch (JSONException e){
            e.printStackTrace();
        }
        update_word(jsonObject);
        mHandler.obtainMessage(4,jsonObject).sendToTarget();


//        getwordlist();
        /**
         * 音频修改
         */
//        try{
//            current_word = jsonObject.getString("word_group");
//        }catch (JSONException e){
//            e.printStackTrace();
//        }
//        resetMediaPlayer(current_word);//音频初始化
        //有时数据库同步太慢了，只能先直接把用户改过后的数据拿来显示
//        mHandler.obtainMessage(7,jsonObject).sendToTarget();
//        getwordlist();
    }
}