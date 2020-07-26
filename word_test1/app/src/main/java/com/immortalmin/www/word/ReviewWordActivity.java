package com.immortalmin.www.word;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class ReviewWordActivity extends AppCompatActivity
        implements View.OnClickListener,
        SpellFragment.OnFragmentInteractionListener,
        CountDownFragment.OnFragmentInteractionListener {

    private ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentTransaction transaction = fragmentManager.beginTransaction();
    private CountDownFragment countDownFragment = new CountDownFragment();
    private SpellFragment spellFragment = new SpellFragment();
    private Button turn_mode,ret_btn;
    private ImageView imgview;
    private TextView total_times, word_times;
    private JsonRe jsonRe = new JsonRe();
    private MyAsyncTask myAsyncTask;
    private CaptureUtil captureUtil = new CaptureUtil();
    private UserData userData = new UserData();
    private ProgressBar total_progress;
    private SweetAlertDialog finishDialog,interruptDialog,inadequateDialog;
    private HashMap<String,Object> setting = new HashMap<>();
    private Map<String, Object> update_word = null;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private List<HashMap<String, Object>> review_list = null;//the list of word
    private int review_num = 1;//the number of word today
    private int c_times = 1;//每个单词变成今天背完需要的次数
    private int[] finish_ind = new int[10000];//今天是否已经连续背对5次
    private int finish_num = 0;//今天背完的单词数
    private int today_finish = 0;//该单词今天背完的次数
    private int current_ind = -1;
    private int mode = 0;
    private Boolean pron_lock = false;
    private HashMap<String, Object> now_words = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_word);
        total_times = (TextView) findViewById(R.id.total_times);
        word_times = (TextView) findViewById(R.id.word_times);
        turn_mode = (Button) findViewById(R.id.turn_mode);
        ret_btn = (Button) findViewById(R.id.ret_btn);
        imgview = (ImageView)findViewById(R.id.imgview);
        turn_mode.setOnClickListener(this);
        ret_btn.setOnClickListener(this);
        total_progress = (ProgressBar) findViewById(R.id.total_progress);
        initialize();
    }

    /**
     * 先回忆，选择模糊或者不认识的留到最后再过一遍
     */
    private void startCountdownReview(){
        current_ind++;
        while(finish_ind[current_ind]==1){
            current_ind++;
            if(current_ind==review_list.size()) current_ind=0;
        }
        today_finish = Integer.valueOf(review_list.get(current_ind).get("today_correct_times").toString());
        mHandler.obtainMessage(0).sendToTarget();
        //初始化单词音频
        resetMediaPlayer(review_list.get(current_ind).get("word_en").toString());
        hideInput();
        int countdown_mode = (int) (Math.random() * 3) + 1;
        now_words = new HashMap<>();
        now_words.put("mode", countdown_mode);
        now_words.put("word_en", review_list.get(current_ind).get("word_en").toString());
        now_words.put("word_ch", review_list.get(current_ind).get("word_ch").toString());
        now_words.put("media_player",mediaPlayer);
        start_countdown_mode(now_words);
    }

    /**
     * 后拼写，拼错的最后再拼一次
     */
    private void startSpellReview(){
        current_ind++;
        while(finish_ind[current_ind]==1){
            current_ind++;
            if(current_ind==review_list.size()) current_ind=0;
        }
        today_finish = Integer.valueOf(review_list.get(current_ind).get("today_correct_times").toString());
        mHandler.obtainMessage(0).sendToTarget();
        //初始化单词音频
        resetMediaPlayer(review_list.get(current_ind).get("word_en").toString());
        now_words = new HashMap<>();
        now_words.put("once_flag", true);
        now_words.put("word_en", review_list.get(current_ind).get("word_en").toString());
        now_words.put("word_ch", review_list.get(current_ind).get("word_ch").toString());
        now_words.put("media_player",mediaPlayer);
        start_spell_mode(now_words);
    }

    private void getReviewList(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("what",11);
            jsonObject.put("uid",4);
            jsonObject.put("review_date","2020-7-20");
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            review_list =jsonRe.reciteData(result);
            review_num = review_list.size();
            startCountdownReview();
        });
        myAsyncTask.execute(jsonObject);
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

    /**
     * start countdown mode
     */
    private void start_countdown_mode(HashMap<String, Object> words) {
        transaction = fragmentManager.beginTransaction();
        transaction.hide(spellFragment).show(countDownFragment);
        transaction.commit();
        countDownFragment.update_options(words);//update data
    }


    /**
     * start spell mode
     */
    private void start_spell_mode(HashMap<String, Object> words) {
        transaction = fragmentManager.beginTransaction();
        transaction.hide(countDownFragment).show(spellFragment);
        transaction.commit();
        spellFragment.update_options(words);//update data
    }

    /**
     * 初始化操作
     */
    public void initialize() {
        init_user();
        init_fragment();
        dialog_init();
        setting.put("uid",userData.getUid());
        Arrays.fill(finish_ind, 0);
        mHandler.obtainMessage(0).sendToTarget();
        getReviewList();
    }

    /**
     * 加载所有的fragment
     */
    private void init_fragment() {
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.recite_model, countDownFragment);
        transaction.add(R.id.recite_model, spellFragment);
        transaction.hide(countDownFragment).hide(spellFragment);
        transaction.commit();
    }

    private void dialog_init(){
        /**
         * finish recite
         */
        finishDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Good job!")
                .setContentText("return to main page.")
                .setConfirmText("fine")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(ReviewWordActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                    }
                });

        finishDialog.setCancelable(false);

        /**
         * shortage of words
         */
        inadequateDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("shortage of words")
                .setContentText("you finished all")
                .setConfirmText("return to main")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(ReviewWordActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                    }
                });
        inadequateDialog.setCancelable(false);
    }

    /**
     * 中途退出
     */
    private void interruptDialog(){
        mHandler.obtainMessage(1).sendToTarget();
        SweetAlertDialog interrup_alert = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        interrup_alert.setTitleText("Are you sure?")
                .setContentText("Won't be able to recover this file!")
                .setConfirmText("fine")
                .setCancelText("nooo")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(ReviewWordActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.cancel();
                        mHandler.obtainMessage(2).sendToTarget();
                    }
                });
        interrup_alert.setCancelable(false);
        interrup_alert.show();
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

    /**
     * 返回主页
     */
    private void return_main(){
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Good job!")
                .setContentText("return to main page.")
                .setConfirmText("fine")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(ReviewWordActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .show();
    }

    /**
     * 中途退出
     */
    private void interrupt(){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Won't be able to recover this file!")
                .setConfirmText("fine")
                .setCancelText("nooo")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(ReviewWordActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                    }
                })
                .show();
    }

    /**
     * 选项按钮点击事件
     *
     * @param view
     */
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.turn_mode:

                break;
            case R.id.ret_btn:
                interruptDialog();
//                interrupt();
                break;
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    total_times.setText(String.valueOf(finish_num) + "/" + String.valueOf(review_num));
                    word_times.setText(String.valueOf(today_finish) + "/" + String.valueOf(c_times));
                    break;
                case 1:
                    Glide.with(ReviewWordActivity.this).load(captureUtil.getcapture(ReviewWordActivity.this))
                            .apply(bitmapTransform(new BlurTransformation(25))).into(imgview);
                    imgview.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    imgview.setVisibility(View.INVISIBLE);
                    break;
            }
            return false;
        }
    });

    /**
     * CountDownFragment的回调函数
     * HashMap<String,Object> res
     *
     * @param res
     */
    @Override
    public void countdownonFragmentInteraction(HashMap<String, Object> res) {
        HashMap<String, Object> now_word = new HashMap<String, Object>();
        now_word = review_list.get(current_ind);
        int to_co_times = Integer.valueOf(now_word.get("today_correct_times").toString());
        int er_times = Integer.valueOf(now_word.get("error_times").toString());
        int co_times = Integer.valueOf(now_word.get("correct_times").toString());
        switch (Integer.valueOf(res.get("judge").toString())) {
            case 1://acquaint
                now_word.put("today_correct_times", to_co_times + 1);
                if (to_co_times + 1 >= c_times) {
                    finish_ind[current_ind] = 1;
                    finish_num++;
                    total_progress.post(new Runnable() {
                        @Override
                        public void run() {
                            int pro_num = finish_num * 100 / review_num;
                            total_progress.setProgress(pro_num);
                        }
                    });
                    now_word.put("correct_times", co_times + 1);
//                    if (co_times + 1 >= prof_times) {
//                        now_word.put("prof_flag", 1);
//                    }
                    review_list.set(current_ind, now_word);
                    update_sql_data(current_ind,1);
                } else {
                    review_list.set(current_ind, now_word);
                }
                break;
            case 2://vague
                now_word.put("today_correct_times", 0);
                review_list.set(current_ind, now_word);
                pron_lock = true;
                jump_to_example(current_ind);
                break;
            case 3://unknown
                now_word.put("today_correct_times", 0);
                now_word.put("error_times", er_times + 1);
                review_list.set(current_ind, now_word);
                pron_lock = true;
                jump_to_example(current_ind);
                break;
        }
        mHandler.obtainMessage(0).sendToTarget();
        if (!pron_lock) {
            if(finish_num>=review_list.size()){
                Arrays.fill(finish_ind, 0);
                mode = 1;
                current_ind = -1;
                finish_num=0;
                startSpellReview();
            }else{
                startCountdownReview();
            }
        }
    }

    /**
     * spellfragment的回调函数
     *
     * @param res
     */
    @Override
    public void spellFragmentInteraction(HashMap<String, Object> res) {
        HashMap<String, Object> correct_word = new HashMap<String, Object>();
        correct_word = review_list.get(current_ind);
        int er_times = Integer.valueOf(correct_word.get("error_times").toString());
        int co_times = Integer.valueOf(correct_word.get("correct_times").toString());
        if ("1".equals(res.get("judge").toString())) {
            if (Boolean.valueOf(res.get("once_flag").toString())) {//一次就过
                finish_ind[current_ind] = 1;
                finish_num++;
                total_progress.post(new Runnable() {
                    @Override
                    public void run() {
                        int pro_num = finish_num * 100 / review_num;
                        total_progress.setProgress(pro_num);
                    }
                });
                correct_word.put("correct_times", co_times + 1);
//                if (co_times + 1 >= prof_times) {
//                    correct_word.put("prof_flag", 1);
//                }
                review_list.set(current_ind, correct_word);
                update_sql_data(current_ind,1);
            } else {//不是一次就过，下回重新拼写
                correct_word.put("error_times", er_times + 1);
                correct_word.put("today_correct_times", 0);
                review_list.set(current_ind, correct_word);
            }
            mHandler.obtainMessage(0).sendToTarget();
            if (finish_num >= review_num) {
                update_all();
            }else{
                startSpellReview();
//                start_recite();
            }
        } else {//回答错误，重新拼写
            correct_word.put("error_times", er_times + 1);
            review_list.set(current_ind, correct_word);
            now_words.put("once_flag", false);
            start_spell_mode(now_words);
        }
    }

    /**
     *
     * @param i
     * @param what 0：不更新日期  1：更新日期
     */
    public void update_sql_data(int i,int what) {
        update_word  = new HashMap<>();
        UpdateServer updateServer = new UpdateServer();
        updateServer.sendMap(review_list.get(i),what);
        scheduledThreadPool.schedule(updateServer, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * update rest of word list
     */
    private void update_all(){
        for(int i=0;i<review_num;i++){
            if(finish_ind[i]==0){
                update_sql_data(i,0);
            }
        }
        mHandler.obtainMessage(1).sendToTarget();
        finishDialog.show();
//        return_main();
    }

    /**
     * 跳转到例句页面
     *
     * @param id
     */
    public void jump_to_example(int id) {
        Intent intent = new Intent(ReviewWordActivity.this, ExampleTestActivity.class);
        intent.putExtra("wid", review_list.get(id).get("wid").toString());
        intent.putExtra("dict_source", review_list.get(id).get("dict_source").toString());
        startActivityForResult(intent, 1);

    }

    /**
     * 子页面跳回
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
        if (requestCode == 1) {
            pron_lock = false;
            if(mode==0) startCountdownReview();
            else startSpellReview();
        }
    }

    /**
     * 回车键事件
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            interruptDialog();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 隐藏键盘
     */
    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}


