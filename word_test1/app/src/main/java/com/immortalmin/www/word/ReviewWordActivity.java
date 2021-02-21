package com.immortalmin.www.word;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import jp.wasabeef.glide.transformations.BlurTransformation;
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

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
    private CollectDbDao collectDbDao = new CollectDbDao(this);
    private Button turn_mode,ret_btn;
    private ImageView imgview;
    private TextView total_times, word_times;
    private JsonRe jsonRe = new JsonRe();
    private MyAsyncTask myAsyncTask;
    private CaptureUtil captureUtil = new CaptureUtil();
    private User user = new User();
    private ProgressBar total_progress;
    private SweetAlertDialog interruptDialog,inadequateDialog;
    private HashMap<String,Object> setting = new HashMap<>();
    private ArrayList<DetailWord> review_list;//the list of word
    private int review_num = 1;//the number of word today
    private int c_times = 2;//每个单词变成今天背完需要的次数
    private int[] finish_ind = new int[10000];//该单词是否完成今天的背诵
    private int finish_num = 0;//今天背完的单词数
    private int today_finish = 0;//该单词今天背完的次数
    private int current_ind = -1;
    private Boolean pron_lock = false;
    private HashMap<String, Object> now_words = null;
    private static int group_num = 20;//每组复习的单词数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SQLiteStudioService.instance().start(this);//连接SQLiteStudio
        setContentView(R.layout.activity_review_word);
        total_times = findViewById(R.id.total_times);
        word_times = findViewById(R.id.word_times);
        turn_mode = findViewById(R.id.turn_mode);
        ret_btn = findViewById(R.id.ret_btn);
        imgview = findViewById(R.id.imgview);
        turn_mode.setOnClickListener(this);
        ret_btn.setOnClickListener(this);
        total_progress = findViewById(R.id.total_progress);
        initialize();
    }

    //从2021/2/21开始停止使用
//    /**
//     * 获取单词复习列表
//     */
//    private void getReviewList(){
//        JSONObject jsonObject = new JSONObject();
//        try{
//            jsonObject.put("what",11);
//            jsonObject.put("uid",4);
//            //获取当前时间
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
//            jsonObject.put("review_date",simpleDateFormat.format(new Date(System.currentTimeMillis())));
//        }catch (JSONException e){
//            e.printStackTrace();
//        }
//        myAsyncTask = new MyAsyncTask();
//        myAsyncTask.setLoadDataComplete((result)->{
//            review_list =jsonRe.detailWordData(result);
//            review_num = Math.min(review_list.size(),group_num);
//            if(review_num>0){
//                startReview();
//            }else{
//                mHandler.obtainMessage(1).sendToTarget();
//                inadequateDialog.show();
//            }
//
//        });
//        myAsyncTask.execute(jsonObject);
//    }

    /**
     * 从本地获取复习的单词列表
     */
    private void getReviewListFromLocal(){
        review_list = collectDbDao.getReviewData();
        review_num = Math.min(review_list.size(),group_num);
        if(review_num>0){
            mHandler.sendEmptyMessage(3);//startReview();
        }else{
            mHandler.obtainMessage(1).sendToTarget();
            inadequateDialog.show();
        }
    }

    /**
     * 开始背诵
     * 按顺序背诵，背错的之后重背
     */
    private void startReview(){
        current_ind++;
        if(current_ind==review_num) current_ind=0;
        while(finish_ind[current_ind]==1){
            current_ind++;
            if(current_ind==review_num) current_ind=0;
        }
        today_finish = review_list.get(current_ind).getToday_correct_times();
        mHandler.obtainMessage(0).sendToTarget();
//        hideInput();
        switch(today_finish){
            case 0:
                int countdown_mode = (int) (Math.random() * 3) + 1;
                now_words = new HashMap<>();
                now_words.put("mode", countdown_mode);
                now_words.put("word_en", review_list.get(current_ind).getWord_en());
                now_words.put("word_ch", review_list.get(current_ind).getWord_ch());
                start_countdown_mode(now_words);
                break;
            case 1:
                now_words = new HashMap<>();
                now_words.put("once_flag", true);
                now_words.put("word_en", review_list.get(current_ind).getWord_en());
                now_words.put("word_ch", review_list.get(current_ind).getWord_ch());
                start_spell_mode(now_words);
                break;
        }
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
        setting.put("uid", user.getUid());
        Arrays.fill(finish_ind, 0);
        mHandler.obtainMessage(0).sendToTarget();
//        getReviewList();
        getReviewListFromLocal();
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
        inadequateDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("shortage of words")
                .setContentText("you finished all")
                .setConfirmText("return to main")
                .setConfirmClickListener(sweetAlertDialog -> {
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(ReviewWordActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                });
        inadequateDialog.setCancelable(false);
    }

    /**
     * finish a group of recite
     */
    private void finishAGroupDialog(){
        mHandler.obtainMessage(1).sendToTarget();
        SweetAlertDialog finish_alert = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Good job!")
                .setContentText("There are "+(review_list.size()-review_num)+" words left unreviewed")
                .setConfirmText("continue")
                .setConfirmClickListener(sweetAlertDialog -> {
                    current_ind = -1;
                    Arrays.fill(finish_ind, 0);
                    finish_num = 0;
                    total_progress.post(() -> total_progress.setProgress(0));
                    for(int i=0;i<review_num;i++) review_list.remove(0);
                    review_num = Math.min(review_list.size(),group_num);
                    startReview();
                    mHandler.obtainMessage(2).sendToTarget();
                    sweetAlertDialog.cancel();
                })
                .setCancelText("later")
                .setCancelClickListener(sweetAlertDialog -> {
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(ReviewWordActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                });
        finish_alert.setCancelable(false);
        finish_alert.show();
    }

    /**
     * finish all words
     */
    private void finishDialog(){
        mHandler.obtainMessage(1).sendToTarget();
        SweetAlertDialog finish_alert = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Good job!")
                .setContentText("Return to main page.")
                .setConfirmText("fine")
                .setConfirmClickListener(sweetAlertDialog -> {
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(ReviewWordActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                });

        finish_alert.setCancelable(false);
        finish_alert.show();
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
                .setConfirmClickListener(sweetAlertDialog -> {
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(ReviewWordActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                })
                .setCancelClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.cancel();
                    mHandler.obtainMessage(2).sendToTarget();
                });
        interrup_alert.setCancelable(false);
        interrup_alert.show();
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
                break;
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    total_times.setText(finish_num + "/" + review_num);
                    word_times.setText(today_finish + "/" + c_times);
                    break;
                case 1:
                    Glide.with(ReviewWordActivity.this).load(captureUtil.getcapture(ReviewWordActivity.this))
                            .apply(bitmapTransform(new BlurTransformation(25))).into(imgview);
                    imgview.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    imgview.setVisibility(View.INVISIBLE);
                    break;
                case 3:
                    startReview();
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
        DetailWord now_word = review_list.get(current_ind);
        int to_co_times = now_word.getToday_correct_times();
        int er_times = now_word.getError_times();
        switch (Integer.valueOf(res.get("judge").toString())) {
            case 1://acquaint
                now_word.setToday_correct_times(to_co_times + 1);
                review_list.set(current_ind, now_word);
                break;
            case 2://vague
                now_word.setToday_correct_times(0);
                review_list.set(current_ind, now_word);
                break;
            case 3://unknown
                now_word.setToday_correct_times(0);
                now_word.setError_times(er_times + 1);
                review_list.set(current_ind, now_word);
                pron_lock = true;
                jump_to_example(current_ind);
                break;
        }
        mHandler.obtainMessage(0).sendToTarget();
        if (!pron_lock) {
            startReview();
        }
    }

    /**
     * spellfragment的回调函数
     *
     * @param WrongTimes
     */
    @Override
    public void spellFragmentInteraction(int WrongTimes) {
        DetailWord correct_word = review_list.get(current_ind);
        int er_times = correct_word.getError_times();
        int co_times = correct_word.getCorrect_times();
        if (WrongTimes == 0) {//一次就过
            finish_ind[current_ind] = 1;
            finish_num++;
            total_progress.post(() -> {
                int pro_num = finish_num * 100 / review_num;
                total_progress.setProgress(pro_num);
            });
            correct_word.setCorrect_times(co_times + 1);
            //设置下次复习的时间
            correct_word.setLast_date(DateTransUtils.getDateAfterToday(0));
            if(co_times>=5){
                correct_word.setReview_date("1970-01-01");
            }else{
                int[] durations = {1,2,4,7,15};
                correct_word.setReview_date(DateTransUtils.getDateAfterToday(durations[co_times]));
            }
            review_list.set(current_ind, correct_word);
            updateSingleLocalData(correct_word);
        } else {//不是一次就过，下回重新拼写
            correct_word.setError_times(er_times + WrongTimes);
            correct_word.setToday_correct_times(0);
            review_list.set(current_ind, correct_word);
        }
        mHandler.obtainMessage(0).sendToTarget();
        if (finish_num >= review_num) {
            if(review_num == review_list.size()) finishDialog();
            else finishAGroupDialog();
        }else{
            startReview();
        }
    }

    //从2021/2/21开始停止使用
//    /**
//     * @param i
//     * @param what 0：不更新日期  1：更新日期
//     */
//    public void update_sql_data(int i,int what) {
//        UpdateServer updateServer = new UpdateServer();
//        updateServer.sendMap(review_list.get(i),what);
//        scheduledThreadPool.schedule(updateServer, 0, TimeUnit.MILLISECONDS);
//    }

    /**
     * 更新一条数据（本地）
     */
    private void updateSingleLocalData(DetailWord word){
        collectDbDao.updateData(word);
    }

    /**
     * 跳转到例句页面
     *
     * @param id
     */
    public void jump_to_example(int id) {
        Intent intent = new Intent(ReviewWordActivity.this, ExampleActivity.class);
        intent.putExtra("wid", review_list.get(id).getWid());
        intent.putExtra("dict_source", review_list.get(id).getDict_source());
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
            startReview();
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


