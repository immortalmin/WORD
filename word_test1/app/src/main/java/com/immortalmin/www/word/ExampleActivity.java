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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
 * 跳转到这个界面需要传入wid与dict_source
 */
public class ExampleActivity extends AppCompatActivity implements
        ExampleFragment.OnFragmentInteractionListener,
        KelinsiFragment.OnFragmentInteractionListener,
        UpdateExampleDialog.OnDialogInteractionListener,
        UpdateWordDialog.OnDialogInteractionListener,
        AddExampleDialog.OnDialogInteractionListener,
        View.OnClickListener{

    private Button example_btn,kelinsi_btn,edit_btn,word_del_btn,word_edit_btn,ban_icon,collect,return_btn;
    private ImageView backdrop;
    private WordView word_en;
    private TextView word_ch,source;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentTransaction transaction = fragmentManager.beginTransaction();
    private ExampleFragment exampleFragment = new ExampleFragment();
    private KelinsiFragment kelinsiFragment = new KelinsiFragment();
    private HashMap<String,Object> word = null;
    private ArrayList<HashMap<String,Object>> examplelist = null;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private UserData userData = new UserData();
    private JsonRe jsonRe = new JsonRe();
    private MyAsyncTask myAsyncTask;
    private CaptureUtil captureUtil = new CaptureUtil();
    private int mode=0;
    private int fragment_mode=0;//0:example  1:kelinsi
    private String current_word="error",wid = "100",dict_source="0";
    private boolean first_coming = true;
    private int collect_flag = 0, del_id = 1;
    private boolean isChanged = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        example_btn = (Button)findViewById(R.id.example_btn);
        kelinsi_btn = (Button)findViewById(R.id.kelinsi_btn);
        edit_btn = (Button)findViewById(R.id.edit_btn);
        word_del_btn = (Button)findViewById(R.id.word_del_btn);
        word_edit_btn = (Button)findViewById(R.id.word_edit_btn);
        ban_icon = (Button)findViewById(R.id.ban_icon);
        collect = (Button)findViewById(R.id.collect);
        return_btn = (Button)findViewById(R.id.return_btn);
        backdrop = (ImageView)findViewById(R.id.backdrop);
        word_en = (WordView)findViewById(R.id.word_en);
        word_ch = (TextView) findViewById(R.id.word_ch);
        source = (TextView) findViewById(R.id.source);
        example_btn.setOnClickListener(this);
        kelinsi_btn.setOnClickListener(this);
        edit_btn.setOnClickListener(this);
        word_del_btn.setOnClickListener(this);
        word_edit_btn.setOnClickListener(this);
        return_btn.setOnClickListener(this);
        collect.setOnClickListener(this);
        word_en.setOnClickListener(this);
        Intent intent = getIntent();
        wid = intent.getStringExtra("wid");
        dict_source = intent.getStringExtra("dict_source");
        init();
    }

    private void init() {
        first_coming = true;
        init_user();
        getWordData();
        if("0".equals(dict_source)){//有例句，没有柯林斯
            fragment_mode=0;
            transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.framelayout,exampleFragment);
            transaction.commit();
            exampleFragment.setData(Integer.valueOf(wid),userData,backdrop,dict_source);//设置例句fragment的数据
            mHandler.obtainMessage(8,0).sendToTarget();
        }else{//有柯林斯，可能有例句
            fragment_mode=1;
            transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.framelayout,exampleFragment);
            transaction.add(R.id.framelayout,kelinsiFragment);
            transaction.hide(exampleFragment);
            transaction.commit();
            exampleFragment.setData(Integer.valueOf(wid),userData,backdrop,dict_source);//设置例句fragment的数据
            kelinsiFragment.setWid(Integer.valueOf(wid));
            mHandler.obtainMessage(8,1).sendToTarget();
        }
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
                if(fragment_mode==0){
                    if(mode==1){
                        addExampleDialog();
                    }
                }else{
                    transaction = fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.slide_left_in,R.anim.slide_to_right);
                    transaction.hide(kelinsiFragment).show(exampleFragment);
                    transaction.commit();
                    mHandler.obtainMessage(0).sendToTarget();
                    fragment_mode=0;
                }
                break;
            case R.id.kelinsi_btn:
                if(fragment_mode==1) return;
                transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_right_in,R.anim.slide_to_left);
                transaction.hide(exampleFragment).show(kelinsiFragment);
                transaction.commit();
                mHandler.obtainMessage(1).sendToTarget();
                fragment_mode=1;
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
                collect.setClickable(false);
                if(collect_flag==1){
                    Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.star_off, null);
                    collect.setBackground(drawable);
                    collect_flag=0;
                    updateCollect(0);
                }else{
                    Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.star_on, null);
                    collect.setBackground(drawable);
                    collect_flag=1;
                    updateCollect(1);
                }
                break;
            case R.id.word_en:
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0);
                }
                mediaPlayer.start();
                break;
            case R.id.word_del_btn:
                del_warning();
                break;
            case R.id.word_edit_btn:
                updateWordDialog(word);
                break;
            case R.id.return_btn:
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0);
                }
                Intent intent = new Intent();
                if(isChanged){
                    setResult(1,intent);
                }else{
                    setResult(2,intent);
                }
                finish();
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
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
                    example_btn.setText("例句+");
                    break;
                case 3:
                    word_del_btn.setVisibility(View.INVISIBLE);
                    word_edit_btn.setVisibility(View.INVISIBLE);
                    ban_icon.setVisibility(View.INVISIBLE);
                    collect.setVisibility(View.VISIBLE);
                    exampleFragment.change_mode(0);
                    edit_btn.setBackground(getResources().getDrawable(R.drawable.edit1));
                    example_btn.setText("例句");
                    break;
                case 4:
                    //new
                    word_en.setmText(word.get("word_en").toString());
                    word_en.setAccount(0);
                    word_ch.setText(word.get("word_ch").toString());
                    //暂时不显示
//                    source.setText(word.get("source").toString());
                    if(!"null".equals(word.get("cid"))){//是收藏的单词
                        word_en.setAccount((float)(Integer.valueOf(word.get("correct_times").toString())/5.0));
                        collect_flag=1;
                        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.star_on, null);
                        collect.setBackground(drawable);
                    }
                    //set music of word
                    current_word = word.get("word_en").toString();
                    resetMediaPlayer(current_word);
                    if(first_coming){
                        mediaPlayer.start();
                        first_coming = false;
                    }
                    break;
                case 5:
                    Glide.with(ExampleActivity.this).load(captureUtil.getcapture(ExampleActivity.this))
                            .apply(bitmapTransform(new BlurTransformation(25))).into(backdrop);
                    backdrop.setVisibility(View.VISIBLE);
                    break;
                case 6:
                    backdrop.setVisibility(View.INVISIBLE);
                    break;
                case 7:
                    JSONObject jsonObject = (JSONObject)message.obj;
                    try{
                        word_en.setmText(jsonObject.getString("word_group"));
                        word_ch.setText(jsonObject.getString("C_meaning"));
                        source.setText(jsonObject.getString("source"));
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    break;
                case 8:
                    switch (message.obj.toString()){
                        case "0":
                            kelinsi_btn.setClickable(false);
                            kelinsi_btn.setVisibility(View.INVISIBLE);
                            break;
                        case "1":
                            example_btn.setBackgroundColor(Color.parseColor("#10000000"));
                            kelinsi_btn.setBackgroundColor(Color.parseColor("#30000000"));
                            break;
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

    private void addExampleDialog(){
        mHandler.obtainMessage(5).sendToTarget();
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("wid",wid);
            jsonObject.put("uid",userData.getUid());
            jsonObject.put("C_meaning",word_ch.getText().toString());
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
                        deleteWord();
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


    private void deleteWord(){
        isChanged = true;
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("wid",word.get("wid"));
            jsonObject.put("what",3);
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            Intent intent = new Intent();
            setResult(2,intent);
            finish();
            overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
            Toast.makeText(ExampleActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
        });
        myAsyncTask.execute(jsonObject);
    }


    /**
     * 0:取消收藏；1:添加收藏
     * @param sel
     */
    private void updateCollect(int sel){
        isChanged = true;
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("what",17);
            jsonObject.put("collect",sel);
            if(sel==0){
                jsonObject.put("cid",word.get("cid"));
            }else{
                jsonObject.put("uid",userData.getUid());
                jsonObject.put("wid",word.get("wid"));
                jsonObject.put("dict_source",dict_source);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            if(sel==1){
                word = jsonRe.wordData2(result);
                Toast.makeText(this,"已收藏",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"已取消收藏",Toast.LENGTH_SHORT).show();
            }
            collect.setClickable(true);
        });
        myAsyncTask.execute(jsonObject);
    }

    public void getWordData(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("what",6);
            jsonObject.put("uid",userData.getUid());
            jsonObject.put("wid",Integer.valueOf(wid));
            jsonObject.put("dict_source",dict_source);
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
//            word = jsonRe.wordData(result);
            word = jsonRe.wordData2(result);
            mHandler.obtainMessage(4).sendToTarget();
        });
        myAsyncTask.execute(jsonObject);
    }

    public void updateWord(JSONObject jsonObject){
        isChanged = true;
        try{
            jsonObject.put("uid",userData.getUid());
            jsonObject.put("what",24);
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            word = jsonRe.wordData2(result);
            mHandler.obtainMessage(4).sendToTarget();
        });
        myAsyncTask.execute(jsonObject);
    }

    private void updateExample(JSONObject jsonObject){
        try{
            jsonObject.put("what",18);
            jsonObject.put("wid",wid);
            jsonObject.put("dict_source",dict_source);
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            examplelist = jsonRe.exampleData(result);
//            Log.i("ccc",examplelist.toString());
            exampleFragment.setExamplelist(examplelist,false);
        });
        myAsyncTask.execute(jsonObject);
    }

    private void addExample(JSONObject jsonObject){
        try{
            jsonObject.put("what",0);
            jsonObject.put("dict_source",dict_source);
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            examplelist = jsonRe.exampleData(result);
            exampleFragment.setExamplelist(examplelist,true);
        });
        myAsyncTask.execute(jsonObject);
    }

    private Boolean resetMediaPlayer(String word){
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        try{
            word = word.replaceAll("sb.","somebody").replaceAll("sth.","something").replaceAll("/"," or ");
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
        updateExample(jsonObject);
    }

    @Override
    public void addExampleInteraction(JSONObject jsonObject){
        addExample(jsonObject);
    }

    @Override
    public void updateWordInteraction(JSONObject jsonObject){
        updateWord(jsonObject);
    }

    /**
     * 显示键盘
     *
     * @param et 输入焦点
     */
    public void showInput(final EditText et) {
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
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
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
            }
            Intent intent = new Intent();
            if(isChanged){
                setResult(1,intent);
            }else{
                setResult(2,intent);
            }
            finish();
            overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
