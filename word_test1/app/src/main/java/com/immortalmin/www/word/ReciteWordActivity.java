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

public class ReciteWordActivity extends AppCompatActivity
        implements View.OnClickListener,
        SpellFragment.OnFragmentInteractionListener,
        CountDownFragment.OnFragmentInteractionListener,
        SelectFragment.OnFragmentInteractionListener {

    private ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentTransaction transaction = fragmentManager.beginTransaction();
    private SelectFragment selectFragment = new SelectFragment();
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
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private List<HashMap<String, Object>> recite_list = null;//the list of word
    private int recite_num = 1;//the number of word today
    private int recite_scope = 5;//additional number of word
    private int c_times = 3;//每个单词变成今天背完需要的次数
    private int prof_times = 5;//达到掌握需要的次数
    private int correct_sel = 0;//正确答案的下标
    private int correct_ind;//the index of correct word in recite_list of correct word
    private int[] select = null;//下标转换到在recite_list中的下标
    private int[] finish_ind = new int[10000];//今天是否已经连续背对5次
    private int finish_num = 0;//今天背完的单词数
    private int today_finish = 0;//该单词今天背完的次数
    private int pre_ind = 0;//上一个单词的id
    private Boolean pron_lock = false;
    private HashMap<String, Object> recite_info = new HashMap<String, Object>();
    private HashMap<String, Object> now_words = null;
    private static final String TAG = "ReciteWordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recite_word);
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
     * start a new recite round
     */
    private void start_recite() {
        while (true) {
            correct_ind = (int) (Math.random() * (recite_num + recite_scope));
            if (finish_ind[correct_ind] == 0 && correct_ind != pre_ind) {
                break;
            }
        }
        today_finish = Integer.valueOf(recite_list.get(correct_ind).get("today_correct_times").toString());
        mHandler.obtainMessage(0).sendToTarget();
        pre_ind = correct_ind;
        //初始化单词音频
        resetMediaPlayer(recite_list.get(correct_ind).get("word_en").toString());
        switch (today_finish) {//according to today_finish
            case 0://select
                hideInput();
                select_option();
                recite_info.put("wordview", recite_list.get(select[correct_sel]).get("word_en").toString());
                recite_info.put("today_correct_times", recite_list.get(select[correct_sel]).get("today_correct_times"));
                recite_info.put("sel1", recite_list.get(select[0]).get("word_ch").toString());
                recite_info.put("sel2", recite_list.get(select[1]).get("word_ch").toString());
                recite_info.put("sel3", recite_list.get(select[2]).get("word_ch").toString());
                recite_info.put("sel4", recite_list.get(select[3]).get("word_ch").toString());
                recite_info.put("correct_sel", correct_sel);
                recite_info.put("c_times", String.valueOf(c_times));
                recite_info.put("media_player",mediaPlayer);
                start_select_mode(recite_info);
                break;
            case 1://countdown
                hideInput();
                int countdown_mode = (int) (Math.random() * 3) + 1;
                now_words = new HashMap<>();
                now_words.put("mode", countdown_mode);
                now_words.put("word_en", recite_list.get(correct_ind).get("word_en").toString());
                now_words.put("word_ch", recite_list.get(correct_ind).get("word_ch").toString());
                now_words.put("media_player",mediaPlayer);
                start_countdown_mode(now_words);
                break;
            case 2://spell
                now_words = new HashMap<>();
                now_words.put("once_flag", true);
                now_words.put("word_en", recite_list.get(correct_ind).get("word_en").toString());
                now_words.put("word_ch", recite_list.get(correct_ind).get("word_ch").toString());
                now_words.put("media_player",mediaPlayer);
                start_spell_mode(now_words);
                break;
        }
    }

    /**
     * 选择模式的四个选项
     */
    private void select_option() {
        int count = 0;
        int[] mark = new int[10000];
        select = new int[4];
        Arrays.fill(mark, 0);
        mark[correct_ind] = 1;//把正确选项标记为已选择
        int num;
        while (true) {
            while (true) {
                num = (int) (Math.random() * (recite_num + recite_scope));
                if (mark[num] == 0 && finish_ind[num] == 0 && num != pre_ind) {
                    mark[num] = 1;
                    select[count] = num;
                    count++;
                    break;
                }
            }
            if (count == 4) {
                break;
            }
        }
        correct_sel = (int) (Math.random() * 4);
        select[correct_sel] = correct_ind;

    }

    private void getRecite(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("what",10);
            jsonObject.put("uid",userData.getUid());
            jsonObject.put("mount",recite_num+recite_scope);
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            recite_list =jsonRe.reciteData(result);
            if(recite_list.size()<recite_num+recite_scope){
                //主线程不允许再创建第二个Looper（暂时不懂），之后别的地方再调用myAsyncTask.setLoadDataComplete时，不需要加Looper.prepare()和Looper.loop()
                Looper.prepare();
                mHandler.obtainMessage(1).sendToTarget();
                inadequateDialog.show();
                Looper.loop();
            }else{
                start_recite();
            }
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
            //获取单词音频时，要把单词转换成小写的，不然会获取不到，导致页面卡住
            mediaPlayer.setDataSource("http://dict.youdao.com/dictvoice?type=1&audio="+ URLEncoder.encode(word.toLowerCase()));
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
        transaction.hide(selectFragment).hide(spellFragment).show(countDownFragment);
        transaction.commit();
        countDownFragment.update_options(words);//update data
    }

    /**
     * start select mode
     */
    private void start_select_mode(HashMap<String, Object> words) {
        transaction = fragmentManager.beginTransaction();
        transaction.hide(countDownFragment).hide(spellFragment).show(selectFragment);
        transaction.commit();
        selectFragment.update_options(words);//update data
    }

    /**
     * start spell mode
     */
    private void start_spell_mode(HashMap<String, Object> words) {
        transaction = fragmentManager.beginTransaction();
        transaction.hide(countDownFragment).hide(selectFragment).show(spellFragment);
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
        recite_num = userData.getRecite_num();
        recite_scope = userData.getRecite_scope();
        Arrays.fill(finish_ind, 0);
        mHandler.obtainMessage(0).sendToTarget();
//        getrecitelist();//get the list of word
        getRecite();
    }

    /**
     * 加载所有的fragment
     */
    private void init_fragment() {
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.recite_model, countDownFragment);
        transaction.add(R.id.recite_model, selectFragment);
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
                        intent.setClass(ReciteWordActivity.this, MainActivity.class);
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
                        intent.setClass(ReciteWordActivity.this, MainActivity.class);
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
                        intent.setClass(ReciteWordActivity.this, MainActivity.class);
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
                        intent.setClass(ReciteWordActivity.this, MainActivity.class);
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
                        intent.setClass(ReciteWordActivity.this, MainActivity.class);
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
                    total_times.setText(String.valueOf(finish_num) + "/" + String.valueOf(recite_num));
                    word_times.setText(String.valueOf(today_finish) + "/" + String.valueOf(c_times));
                    break;
                case 1:
                    Glide.with(ReciteWordActivity.this).load(captureUtil.getcapture(ReciteWordActivity.this))
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
        now_word = recite_list.get(correct_ind);
        int to_co_times = Integer.valueOf(now_word.get("today_correct_times").toString());
        int er_times = Integer.valueOf(now_word.get("error_times").toString());
        int co_times = Integer.valueOf(now_word.get("correct_times").toString());
        switch (Integer.valueOf(res.get("judge").toString())) {
            case 1://acquaint
                now_word.put("today_correct_times", to_co_times + 1);
                recite_list.set(correct_ind, now_word);
                break;
            case 2://vague
                now_word.put("today_correct_times", 0);
                recite_list.set(correct_ind, now_word);
                break;
            case 3://unknown
                now_word.put("today_correct_times", 0);
                now_word.put("error_times", er_times + 1);
                recite_list.set(correct_ind, now_word);
                pron_lock = true;
                jump_to_example(correct_ind);
                break;
        }
        mHandler.obtainMessage(0).sendToTarget();
        if (!pron_lock) {
            start_recite();
        }
    }

    /**
     * SelectFragment的回调函数
     * ArrayList<String> s
     *
     * @param res
     */
    @Override
    public void selectonFragmentInteraction(HashMap<String, Object> res) {
        HashMap<String, Object> correct_word = new HashMap<String, Object>();
        HashMap<String, Object> wrong_word = new HashMap<String, Object>();
        switch (res.get("judge").hashCode()) {
            case 1:
                correct_word = recite_list.get(correct_ind);
                correct_word.put("today_correct_times", Integer.valueOf(correct_word.get("today_correct_times").toString()) + 1);
                if (Integer.valueOf(correct_word.get("today_correct_times").toString()) >= c_times) {
                    finish_ind[correct_ind] = 1;
                    finish_num++;
                    total_progress.post(new Runnable() {
                        @Override
                        public void run() {
                            int pro_num = finish_num * 100 / recite_num;
                            total_progress.setProgress(pro_num);
                        }
                    });
                    correct_word.put("correct_times", Integer.valueOf(correct_word.get("correct_times").toString()) + 1);
//                    if (Integer.valueOf(correct_word.get("correct_times").toString()) >= prof_times) {
//                        correct_word.put("prof_flag", 1);
//                    }
                    recite_list.set(correct_ind, correct_word);
                    update_sql_data(correct_ind,1);
                } else {
                    recite_list.set(correct_ind, correct_word);
                }
                break;
            case 2:
                correct_word = recite_list.get(correct_ind);
                correct_word.put("today_correct_times", 0);
                correct_word.put("error_times", Integer.valueOf(correct_word.get("error_times").toString()) + 1);
                recite_list.set(correct_ind, correct_word);
                wrong_word = recite_list.get(select[Integer.valueOf(res.get("wrong_sel").toString())]);
                wrong_word.put("today_correct_times", 0);
                wrong_word.put("error_times", Integer.valueOf(wrong_word.get("error_times").toString()) + 1);
                recite_list.set(select[Integer.valueOf(res.get("wrong_sel").toString())], wrong_word);
                pron_lock = true;
                jump_to_example(correct_ind);
                break;
            case 3:
                correct_word = recite_list.get(correct_ind);
                correct_word.put("today_correct_times", 0);
                correct_word.put("error_times", Integer.valueOf(correct_word.get("error_times").toString()) + 1);
                recite_list.set(correct_ind, correct_word);
                pron_lock = true;
                jump_to_example(correct_ind);
                break;
        }
        mHandler.obtainMessage(0).sendToTarget();
        if (!pron_lock) {
            start_recite();
        }
    }

//    /**
//     * spellfragment的回调函数
//     *
//     * @param WrongTimes
//     */
//    @Override
//    public void spellFragmentInteraction(int WrongTimes) {
//        HashMap<String, Object> correct_word = new HashMap<String, Object>();
//        correct_word = recite_list.get(correct_ind);
//        int er_times = Integer.valueOf(correct_word.get("error_times").toString());
//        int co_times = Integer.valueOf(correct_word.get("correct_times").toString());
//        if ("1".equals(res.get("judge").toString())) {
//            if (Boolean.valueOf(res.get("once_flag").toString())) {//一次就过
//                finish_ind[correct_ind] = 1;
//                finish_num++;
//                total_progress.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        int pro_num = finish_num * 100 / recite_num;
//                        total_progress.setProgress(pro_num);
//                    }
//                });
//                correct_word.put("correct_times", co_times + 1);
////                if (co_times + 1 >= prof_times) {
////                    correct_word.put("prof_flag", 1);
////                }
//                recite_list.set(correct_ind, correct_word);
//                update_sql_data(correct_ind,1);
//            } else {//不是一次就过，下回重新拼写
//                correct_word.put("error_times", er_times + 1);
//                correct_word.put("today_correct_times", 0);
//                recite_list.set(correct_ind, correct_word);
//            }
//            mHandler.obtainMessage(0).sendToTarget();
//            if (finish_num >= recite_num) {
//                update_all();
//            }else{
//                start_recite();
//            }
//        } else {//回答错误，重新拼写
//            correct_word.put("error_times", er_times + 1);
//            recite_list.set(correct_ind, correct_word);
//            now_words.put("once_flag", false);
//            start_spell_mode(now_words);
//        }
//    }

    /**
     * spellfragment的回调函数
     *
     * @param WrongTimes
     */
    @Override
    public void spellFragmentInteraction(int WrongTimes) {
        HashMap<String, Object> correct_word = new HashMap<String, Object>();
        correct_word = recite_list.get(correct_ind);
        int er_times = Integer.valueOf(correct_word.get("error_times").toString());
        int co_times = Integer.valueOf(correct_word.get("correct_times").toString());
        if (WrongTimes == 0) {//一次就过
            finish_ind[correct_ind] = 1;
            finish_num++;
            total_progress.post(new Runnable() {
                @Override
                public void run() {
                    int pro_num = finish_num * 100 / recite_num;
                    total_progress.setProgress(pro_num);
                }
            });
            correct_word.put("correct_times", co_times + 1);
            recite_list.set(correct_ind, correct_word);
            update_sql_data(correct_ind,1);
        } else {//不是一次就过，下回重新拼写
            correct_word.put("error_times", er_times + WrongTimes);
            correct_word.put("today_correct_times", 0);
            recite_list.set(correct_ind, correct_word);
        }
        mHandler.obtainMessage(0).sendToTarget();
        if (finish_num >= recite_num) {
            update_all();
        }else{
            start_recite();
        }
    }

    /**
     * 更新数据库
     * 传入词组在recite_list中的下标
     * @param i
     */
    public void update_sql_data(int i,int what) {
        UpdateServer updateServer = new UpdateServer();
        updateServer.sendMap(recite_list.get(i),what);
        scheduledThreadPool.schedule(updateServer, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * update rest of word list
     */
    private void update_all(){
        for(int i=0;i<recite_num+recite_scope;i++){
            if(finish_ind[i]==0){
                update_sql_data(i,0);
            }
        }
        mHandler.obtainMessage(1).sendToTarget();
        finishDialog.show();
    }

    /**
     * 跳转到例句页面
     *
     * @param id
     */
    public void jump_to_example(int id) {
        Intent intent = new Intent(ReciteWordActivity.this, ExampleActivity.class);
        intent.putExtra("wid", recite_list.get(id).get("wid").toString());
        intent.putExtra("dict_source", recite_list.get(id).get("dict_source").toString());
        startActivityForResult(intent, 1);
    }

    //FIXME:如果是从单词详情界面返回，并且下一轮是拼写模式，键盘不会自动弹出
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
            start_recite();
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
