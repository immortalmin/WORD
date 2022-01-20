package com.immortalmin.www.word;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import cn.pedant.SweetAlert.SweetAlertDialog;
import jp.wasabeef.glide.transformations.BlurTransformation;
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class ReciteWordActivity extends AppCompatActivity
        implements View.OnClickListener,
        SpellFragment.OnFragmentInteractionListener,
        CountDownFragment.OnFragmentInteractionListener,
        SelectFragment.OnFragmentInteractionListener {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentTransaction transaction = fragmentManager.beginTransaction();
    private SelectFragment selectFragment = new SelectFragment();
    private CountDownFragment countDownFragment = new CountDownFragment();
    private SpellFragment spellFragment = new SpellFragment();
    private Button turn_mode,ret_btn;
    private ImageView imgview;
    private TextView total_times, word_times;
    private CaptureUtil captureUtil = new CaptureUtil();
    private UserDataUtil userDataUtil = new UserDataUtil(this);
    private CollectDbDao collectDbDao = new CollectDbDao(this);
    private User user = new User();
    private ProgressBar total_progress;
    private SweetAlertDialog finishDialog,inadequateDialog;
    private HashMap<String,Object> setting = new HashMap<>();
    private ArrayList<DetailWord> recite_list = null;//the list of word
    private int recite_num = 1;//the number of word today
    private int recite_scope = 5;//additional number of word
    private int c_times = 3;//每个单词变成今天背完需要的次数
    private int prof_times = 5;//达到掌握需要的次数
    private int correct_sel = 0;//正确答案的下标
    private int correct_ind = 0;//the index of correct word in recite_list of correct word
    private int[] select = null;//下标转换到在recite_list中的下标
    private int[] finish_ind = new int[10000];//今天是否已经连续背对5次
    private int finish_num = 0;//今天背完的单词数
    private int today_finish = 0;//该单词今天背完的次数
    private int pre_ind = 0;//上一个单词的下标，用来防止连续两次背到同样的单词
    private int next_ind = 0;//下一个单词的下标，用来决定拼写模式后是否隐藏软键盘
    private Boolean pron_lock = false;
    private HashMap<String, Object> recite_info = new HashMap<>();
    private HashMap<String, Object> now_words = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recite_word);
        SQLiteStudioService.instance().start(this);//连接SQLiteStudio
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

    /**
     * 初始化操作
     */
    public void initialize() {
        user = userDataUtil.getUserDataFromSP();
        init_fragment();
        dialog_init();
        setting.put("uid", user.getUid());
        recite_num = user.getRecite_num();
        recite_scope = user.getRecite_scope();
        Arrays.fill(finish_ind, 0);
        mHandler.obtainMessage(0).sendToTarget();
        getReciteWordFromLocal();
    }

    private int get_next_ind(){
        int ind;
        do {
            ind = (int) (Math.random() * (recite_num + recite_scope));
        } while (finish_ind[ind] != 0 || ind == correct_ind);
        return ind;
    }

    /**
     * start a new recite round
     */
    private void start_recite() {
        pre_ind = correct_ind;
        correct_ind = next_ind;
        next_ind = get_next_ind();
        today_finish = recite_list.get(correct_ind).getToday_correct_times();
        mHandler.sendEmptyMessage(0);
        switch (today_finish) {
            case 0://select
                hideInput();
                select_option();
                recite_info.put("wordview", recite_list.get(select[correct_sel]).getWord_en());
                recite_info.put("today_correct_times", recite_list.get(select[correct_sel]).getToday_correct_times());
                recite_info.put("sel1", recite_list.get(select[0]).getWord_ch());
                recite_info.put("sel2", recite_list.get(select[1]).getWord_ch());
                recite_info.put("sel3", recite_list.get(select[2]).getWord_ch());
                recite_info.put("sel4", recite_list.get(select[3]).getWord_ch());
                recite_info.put("correct_sel", correct_sel);
                recite_info.put("c_times", String.valueOf(c_times));
                start_select_mode(recite_info);
                break;
            case 1://countdown
                hideInput();
                int countdown_mode = (int) (Math.random() * 3) + 1;
                now_words = new HashMap<>();
                now_words.put("mode", countdown_mode);
                now_words.put("word_en", recite_list.get(correct_ind).getWord_en());
                now_words.put("word_ch", recite_list.get(correct_ind).getWord_ch());
                start_countdown_mode(now_words);
                break;
            case 2://spell
                now_words = new HashMap<>();
                now_words.put("once_flag", true);
                now_words.put("word_en", recite_list.get(correct_ind).getWord_en());
                now_words.put("word_ch", recite_list.get(correct_ind).getWord_ch());
                start_spell_mode(now_words,!(recite_list.get(correct_ind).getToday_correct_times()==2));
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
        do {
            do {
                num = (int) (Math.random() * (recite_num + recite_scope));
                if (mark[num] == 0 && finish_ind[num] == 0 && num != pre_ind) {
                    mark[num] = 1;
                    select[count] = num;
                    count++;
                    break;
                }
            } while (true);
        } while (count != 4);
        correct_sel = (int) (Math.random() * 4);
        select[correct_sel] = correct_ind;

    }

    /**
     * 从本地获取背诵的单词列表
     */
    private void getReciteWordFromLocal(){
        recite_list = collectDbDao.getReciteData(recite_num+recite_scope);
        if(recite_list.size()<recite_num+recite_scope){
            Looper.prepare();
            mHandler.obtainMessage(1).sendToTarget();
            inadequateDialog.show();
            Looper.loop();
        }else{
            next_ind = get_next_ind();
            //XXX:我也不知道为啥不能直接start_recite()
            mHandler.sendEmptyMessage(3);//start_recite();
        }
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
    private void start_spell_mode(HashMap<String, Object> words,boolean isHide) {
        transaction = fragmentManager.beginTransaction();
        transaction.hide(countDownFragment).hide(selectFragment).show(spellFragment);
        transaction.commit();
        spellFragment.update_options(words,isHide);//update data
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
        finishDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Good job!")
                .setContentText("返回主界面")
                .setConfirmText("好")
                .setConfirmClickListener(sweetAlertDialog -> {
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(ReciteWordActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                });
        finishDialog.setCancelable(false);

        inadequateDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("单词数不够")
                .setContentText("需要背诵的单词太少了")
                .setConfirmText("返回主界面")
                .setConfirmClickListener(sweetAlertDialog -> {
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(ReciteWordActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
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
                .setContentText("退出后数据将无法恢复")
                .setConfirmText("退出")
                .setCancelText("继续")
                .setConfirmClickListener(sweetAlertDialog -> {
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(ReciteWordActivity.this, MainActivity.class);
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
                    String totalTimesString = finish_num + "/" + recite_num,wordTimesString = today_finish + "/" + c_times;
                    total_times.setText(totalTimesString);
                    word_times.setText(wordTimesString);
                    break;
                case 1:
                    Glide.with(ReciteWordActivity.this).load(captureUtil.getcapture(ReciteWordActivity.this))
                            .apply(bitmapTransform(new BlurTransformation(25))).into(imgview);
                    imgview.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    imgview.setVisibility(View.INVISIBLE);
                    break;
                case 3:
                    start_recite();
                    break;
            }
            return false;
        }
    });

    @Override
    public void countdownonFragmentInteraction(HashMap<String, Object> res) {
        DetailWord now_word = recite_list.get(correct_ind);
        int to_co_times =now_word.getToday_correct_times();
        int er_times = now_word.getError_times();
        int co_times = now_word.getCorrect_times();
        switch (Integer.valueOf(res.get("judge").toString())) {
            case 1://acquaint
                now_word.setToday_correct_times(to_co_times + 1);
                recite_list.set(correct_ind, now_word);
                break;
            case 2://vague
                now_word.setToday_correct_times(0);
                recite_list.set(correct_ind, now_word);
                break;
            case 3://unknown
                now_word.setToday_correct_times(0);
                now_word.setError_times(er_times + 1);
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

    @Override
    public void selectonFragmentInteraction(HashMap<String, Object> res) {
        DetailWord correct_word;
        DetailWord wrong_word;
        switch (res.get("judge").hashCode()) {
            case 1://回答正确
                correct_word = recite_list.get(correct_ind);
                correct_word.setToday_correct_times(correct_word.getToday_correct_times() + 1);
//                if (correct_word.getToday_correct_times() >= c_times) {
//                    finish_ind[correct_ind] = 1;
//                    finish_num++;
//                    total_progress.post(() -> {
//                        int pro_num = finish_num * 100 / recite_num;
//                        total_progress.setProgress(pro_num);
//                    });
//                    correct_word.setCorrect_times(correct_word.getCorrect_times() + 1);
//                    recite_list.set(correct_ind, correct_word);
//                    update_sql_data(correct_ind,1);
//                } else {
//                    recite_list.set(correct_ind, correct_word);
//                }
                recite_list.set(correct_ind, correct_word);
                break;
            case 2://回答错误
                correct_word = recite_list.get(correct_ind);
                correct_word.setToday_correct_times(0);
                correct_word.setError_times(correct_word.getError_times() + 1);
                recite_list.set(correct_ind, correct_word);
                wrong_word = recite_list.get(select[Integer.valueOf(res.get("wrong_sel").toString())]);
                wrong_word.setToday_correct_times(0);
                wrong_word.setError_times(wrong_word.getError_times() + 1);
                recite_list.set(select[Integer.valueOf(res.get("wrong_sel").toString())], wrong_word);
                pron_lock = true;
                jump_to_example(correct_ind);
                break;
            case 3://不认识
                correct_word = recite_list.get(correct_ind);
                correct_word.setToday_correct_times(0);
                correct_word.setError_times(correct_word.getError_times() + 1);
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

    /**
     * spellfragment的回调函数
     * @param WrongTimes 错误的次数
     */
    @Override
    public void spellFragmentInteraction(int WrongTimes) {
        DetailWord correct_word = recite_list.get(correct_ind);
        int er_times = correct_word.getError_times();
        int co_times = correct_word.getCorrect_times();
        if (WrongTimes == 0) {//一次就过
            finish_ind[correct_ind] = 1;
            finish_num++;
            total_progress.post(() -> {
                int pro_num = finish_num * 100 / recite_num;
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
            recite_list.set(correct_ind, correct_word);
            updateSingleLocalData(recite_list.get(correct_ind));
        } else {//不是一次就过，下回重新拼写
            correct_word.setError_times(er_times + WrongTimes);
            correct_word.setToday_correct_times(0);
            recite_list.set(correct_ind, correct_word);
        }
        mHandler.obtainMessage(0).sendToTarget();
        if (finish_num >= recite_num) {
            updateRestLocalData();
        }else{
            start_recite();
        }
    }

    /**
     * 更新一条数据（本地）
     */
    private void updateSingleLocalData(DetailWord word){
        collectDbDao.updateData(word);
    }

    /**
     * 更新剩余的数据
     */
    private void updateRestLocalData(){
        for(int i=0;i<recite_list.size();i++){
            if(finish_ind[i]==0){
                collectDbDao.updateData(recite_list.get(i));
            }
        }
        mHandler.obtainMessage(1).sendToTarget();
        finishDialog.show();
    }

    /**
     * 跳转到例句页面
     * @param id 单词id
     */
    public void jump_to_example(int id) {
        Intent intent = new Intent(ReciteWordActivity.this, ExampleActivity.class);
        intent.putExtra("wid", recite_list.get(id).getWid());
        intent.putExtra("dict_source", recite_list.get(id).getDict_source());
        startActivityForResult(intent, 1);
    }

    //FIXME:如果是从单词详情界面返回，并且下一轮是拼写模式，键盘不会自动弹出
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
        if (requestCode == 1) {
            pron_lock = false;
            start_recite();
        }
    }

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
