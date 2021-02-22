package com.immortalmin.www.word;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
    private DetailWord word;
    private ArrayList<OtherSentence> examplelist = null;
    private MediaPlayerUtil mediaPlayerUtil = new MediaPlayerUtil(this);
    private RecordDbDao mRecordDbDao;
    private CollectDbDao collectDbDao = new CollectDbDao(this);
    private User user = new User();
    private JsonRe jsonRe = new JsonRe();
    private MyAsyncTask myAsyncTask;
    private CaptureUtil captureUtil = new CaptureUtil();
    private int mode=0;
    private int fragment_mode=0;//0:example  1:kelinsi
    private String current_word="error",wid = "100",dict_source="0";
    private boolean first_coming = true;
    private int collect_flag = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        example_btn = findViewById(R.id.example_btn);
        kelinsi_btn = findViewById(R.id.kelinsi_btn);
        edit_btn = findViewById(R.id.edit_btn);
        word_del_btn = findViewById(R.id.word_del_btn);
        word_edit_btn = findViewById(R.id.word_edit_btn);
        ban_icon = findViewById(R.id.ban_icon);
        collect = findViewById(R.id.collect);
        return_btn = findViewById(R.id.return_btn);
        backdrop = findViewById(R.id.backdrop);
        word_en = findViewById(R.id.word_en);
        word_ch = findViewById(R.id.word_ch);
        source = findViewById(R.id.source);
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
        mRecordDbDao = new RecordDbDao(ExampleActivity.this);
        first_coming = true;
        init_user();
        if(collectDbDao.hasData(wid,dict_source)){
            word = collectDbDao.getSingleWordByWidAndSource(wid,dict_source);
            mHandler.sendEmptyMessage(4);
        }else{
            getWordDataFromServer();
        }
        if("0".equals(dict_source)){//有例句，没有柯林斯
            fragment_mode=0;
            transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.framelayout,exampleFragment);
            transaction.commit();
            exampleFragment.setData(Integer.valueOf(wid), user,backdrop,dict_source);//设置例句fragment的数据
            mHandler.obtainMessage(8,0).sendToTarget();
        }else{//有柯林斯，可能有例句
            fragment_mode=1;
            transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.framelayout,exampleFragment);
            transaction.add(R.id.framelayout,kelinsiFragment);
            transaction.hide(exampleFragment);
            transaction.commit();
            exampleFragment.setData(Integer.valueOf(wid), user,backdrop,dict_source);//设置例句fragment的数据
            kelinsiFragment.setWid(Integer.valueOf(wid));
            mHandler.obtainMessage(8,1).sendToTarget();
        }
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
                mediaPlayerUtil.start();
                break;
            case R.id.word_del_btn:
                del_warning();
                break;
            case R.id.word_edit_btn:
                updateWordDialog(word);
                break;
            case R.id.return_btn:
                mediaPlayerUtil.stop();
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
                    if(user.getUsername().equals(word.getSource())){
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
                    word_en.setmText(word.getWord_en());
                    word_en.setAccount(0);
                    word_ch.setText(word.getWord_ch());
                    //暂时不显示
//                    source.setText(word.get("source").toString());
                    if(word.isCollect()){//是收藏的单词
                        word_en.setAccount((float)(word.getCorrect_times()/5.0));
                        collect_flag=1;
                        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.star_on, null);
                        collect.setBackground(drawable);
                    }
                    current_word = word.getWord_en();
                    if(first_coming){
                        mediaPlayerUtil.reset(current_word,true);
                        first_coming = false;
                    }else{
                        mediaPlayerUtil.reset(current_word,false);
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

    private void updateWordDialog(DetailWord data){
        mHandler.obtainMessage(5).sendToTarget();
        UpdateWordDialog updateWordDialog = new UpdateWordDialog(this,R.style.MyDialog,data);
        updateWordDialog.show();
        updateWordDialog.setCancelable(false);
        updateWordDialog.setOnDismissListener(dialogInterface -> mHandler.obtainMessage(6).sendToTarget());
    }

    private void addExampleDialog(){
        mHandler.obtainMessage(5).sendToTarget();
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("wid",wid);
            jsonObject.put("uid", user.getUid());
            jsonObject.put("C_meaning",word_ch.getText().toString());
        }catch (JSONException e){
            e.printStackTrace();
        }
        AddExampleDialog addExampleDialog = new AddExampleDialog(this,R.style.MyDialog,jsonObject);
        addExampleDialog.show();
        addExampleDialog.setCancelable(false);
        addExampleDialog.setOnDismissListener(dialogInterface -> mHandler.obtainMessage(6).sendToTarget());
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
                .setConfirmClickListener(sweetAlertDialog -> {
                    deleteWord();
                    sweetAlertDialog.cancel();
                })
                .showCancelButton(true)
                .setCancelClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.cancel();
                    mHandler.obtainMessage(6).sendToTarget();
                });
        del_alert.setCancelable(false);
        del_alert.show();
    }

    private void deleteWord(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("wid",word.getWid());
            jsonObject.put("what",3);
            //从历史记录中删除该条记录
            mRecordDbDao.deleteSingleData(jsonObject.getString("wid"),dict_source);
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            finish();
            overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
            Toast.makeText(ExampleActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
        });
        myAsyncTask.execute(jsonObject);
    }

    /**
     * @param sel 0:取消收藏；1:添加收藏
     */
//    private void updateCollect(int sel){
//        JSONObject jsonObject = new JSONObject();
//        try{
//            jsonObject.put("what",17);
//            jsonObject.put("collect",sel);
//            if(sel==0){
//                jsonObject.put("cid",word.getCid());
//            }else{
//                jsonObject.put("uid", user.getUid());
//                jsonObject.put("wid",word.getWid());
//                jsonObject.put("dict_source",dict_source);
//            }
//        }catch (JSONException e){
//            e.printStackTrace();
//        }
//        myAsyncTask = new MyAsyncTask();
//        myAsyncTask.setLoadDataComplete((result)->{
//            if(sel==1){
//                word = jsonRe.wordData(result);
//                Toast.makeText(this,"已收藏",Toast.LENGTH_SHORT).show();
//            }else{
//                Toast.makeText(this,"已取消收藏",Toast.LENGTH_SHORT).show();
//            }
//            collect.setClickable(true);
//        });
//        myAsyncTask.execute(jsonObject);
//    }
    /**
     * 修改收藏
     * @param sel 0:取消收藏；1:添加收藏
     */
    private void updateCollect(int sel){
        if(collectDbDao.hasData(word.getWid(),word.getDict_source())){
            collectDbDao.updateCollectByWidAndSource(word.getWid(),word.getDict_source(),sel);
        }else{//不存在本地数据库，说明还未收藏，所以直接插入数据
            collectDbDao.insertData(word,true);
        }
        if(sel==0){
            Toast.makeText(this,"已取消收藏",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"已收藏",Toast.LENGTH_SHORT).show();
        }
        collect.setClickable(true);
    }

    /**
     * 从服务器获取单词数据
     */
    public void getWordDataFromServer(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("what",6);
            jsonObject.put("uid", user.getUid());
            jsonObject.put("wid",Integer.valueOf(wid));
            jsonObject.put("dict_source",dict_source);
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            word = jsonRe.wordData(result);
            mHandler.sendEmptyMessage(4);
        });
        myAsyncTask.execute(jsonObject);
    }

    public void updateWord(JSONObject jsonObject){
        try{
            jsonObject.put("uid", user.getUid());
            jsonObject.put("what",24);
            //更新本地的数据库（历史记录）
            mRecordDbDao.updateData(jsonObject.get("wid").toString(),dict_source,jsonObject.get("word_group").toString(),jsonObject.get("C_meaning").toString());
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            word = jsonRe.wordData(result);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            mediaPlayerUtil.stop();
            finish();
            overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
