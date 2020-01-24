package com.example.administrator.listviewadptwebjsonimg;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ReciteWordActivity extends AppCompatActivity
        implements View.OnClickListener,
        CountDownFragment.OnFragmentInteractionListener,
        SelectFragment.OnFragmentInteractionListener{

    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    SelectFragment selectFragment = new SelectFragment();
    CountDownFragment countDownFragment = new CountDownFragment();
    Button turn_mode;
    JsonRe  jsonRe;
    AlertDialog finish_Dialog,interrupt_Dialog;
    ProgressBar total_progress;

    private MediaPlayer mediaPlayer;
    List<Map<String,Object>> recite_list=null;//the list of word
    int recite_num = 20;//the number of word today
    int recite_scope = 10;//additional number of word
    int c_times = 2;//每个单词变成今天背完需要的次数
    int prof_times = 5;//达到掌握需要的次数
    int correct_sel = 0;//正确答案的下标
    int[] select = null;//下标转换到在recite_list中的下标
    int[] finish_ind = new int[10000];//今天是否已经连续背对5次
    int finish_num = 0;//今天背完的单词数
    int today_finish = 0;//该单词今天背完的次数
    int pre_ind = 0;//上一个单词的id
    int mode_num=0;//the number of mode
    Boolean living_flag=true;
    Boolean pron_lock = false;
    String recite_list_url="http://47.98.239.237/word/php_file/getrecitelist.php?mount=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recite_word);
        turn_mode = (Button)findViewById(R.id.turn_mode);
        turn_mode.setOnClickListener(this);
        total_progress = (ProgressBar)findViewById(R.id.total_progress);
        initialize();
//        initialize();
//        start_countdown_mode();
//        start_select_mode();
    }

    /**
     * select recite mode
     */
    private void start_recite(){
        int count = 0;
        int[] mark = new int[10000];
        select = new int[4];
        Arrays.fill(mark,0);
        int num;
        while(true){
            while(true){
                num = (int)(Math.random()*(recite_num+recite_scope));
                if(mark[num]==0 && finish_ind[num]==0 && num!=pre_ind){
                    mark[num]=1;
                    select[count]=num;
                    count++;
                    break;
                }
            }
            if(count == 4){
                break;
            }
        }
        correct_sel = (int)(Math.random()*4);
        pre_ind = select[correct_sel];
        HashMap<String, Object> recite_info = new HashMap<String, Object>();
        Log.i("数据",recite_list.get(select[correct_sel]).get("word_group").toString());
        recite_info.put("wordview",recite_list.get(select[correct_sel]).get("word_group").toString());
//        recite_info.put("finish_view",recite_list.get(select[correct_sel]).get("today_correct_times").toString()+"/"+String.valueOf(c_times));
        recite_info.put("finish_view",recite_list.get(select[correct_sel]).get("today_correct_times"));
        recite_info.put("all_finish_view",String.valueOf(finish_num)+"/"+String.valueOf(recite_num));
        recite_info.put("sel1",recite_list.get(select[0]).get("C_meaning").toString());
        recite_info.put("sel2",recite_list.get(select[1]).get("C_meaning").toString());
        recite_info.put("sel3",recite_list.get(select[2]).get("C_meaning").toString());
        recite_info.put("sel4",recite_list.get(select[3]).get("C_meaning").toString());
        recite_info.put("correct_sel",correct_sel);
        recite_info.put("c_times",String.valueOf(c_times));
        today_finish=Integer.valueOf(recite_info.get("finish_view").toString());
//        Log.i("recite_info",recite_info.toString());
//        start_select_mode(recite_info);
        switch (today_finish){
            case 0:
                start_select_mode(recite_info);
                break;
            case 1:
                start_countdown_mode(recite_list.get(select[correct_sel]).get("word_group").toString());
                break;
        }

//        living_flag = true;//button relive

//        progressBar2.post(new Runnable() {
//            @Override
//            public void run() {
//                int pro_num = today_finish*10/c_times;
//                progressBar2.setProgress(pro_num);
//            }
//        });

        //set music of word
//        mediaPlayer = new MediaPlayer();
//        String word;
//        word = recite_list.get(select[correct_sel]).get("word_group").toString();
//        initMediaPlayer(word,0);//音频初始化
//        mHandler.obtainMessage(0,recite_info).sendToTarget();
//        mediaPlayer.start();
    }

    /**
     * 获取今天要背的单词列表
     */
    private void getrecitelist() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext=new HttpGetContext();
                String wordlistjson=httpGetContext.httpclientgettext(recite_list_url+String.valueOf(recite_num+recite_scope));
                recite_list=jsonRe.getReciteList(wordlistjson);
//                Log.i("recite_list",recite_list.toString());
                start_recite();
            }
        }).start();
    }

    /**
     * start countdown mode
     */
    private void start_countdown_mode(String word){
        if(countDownFragment.isAdded()){
            transaction = fragmentManager.beginTransaction();
            transaction.hide(selectFragment).show(countDownFragment);
            transaction.commit();
            countDownFragment.update_options(word);//update data
        }else{
            Bundle sendBundle = new Bundle();
            sendBundle.putString("word",word);
            countDownFragment.setArguments(sendBundle);
            transaction = fragmentManager.beginTransaction();

            transaction.add(R.id.recite_model,countDownFragment);
            transaction.hide(selectFragment).show(countDownFragment);
            transaction.commit();
        }

//        transaction = fragmentManager.beginTransaction();
//        CountDownFragment countDownFragment = new CountDownFragment();
//        Bundle sendBundle = new Bundle();
//        sendBundle.putString("word",word);
//        countDownFragment.setArguments(sendBundle);
//        transaction.replace(R.id.recite_model,countDownFragment);
//        transaction.commit();
    }
    /**
     * start select mode
     */
    private void start_select_mode(HashMap<String, Object> words){
        if(selectFragment.isAdded()){
            transaction = fragmentManager.beginTransaction();
            transaction.hide(countDownFragment).show(selectFragment);
            transaction.commit();
            selectFragment.update_options(words);//update data
        }else{
            Bundle sendBundle = new Bundle();
            sendBundle.putString("wordview",words.get("wordview").toString());
            sendBundle.putString("sel1",words.get("sel1").toString());
            sendBundle.putString("sel2",words.get("sel2").toString());
            sendBundle.putString("sel3",words.get("sel3").toString());
            sendBundle.putString("sel4",words.get("sel4").toString());
            sendBundle.putString("correct_sel",words.get("correct_sel").toString());
            sendBundle.putString("finish_view",words.get("finish_view").toString());
            sendBundle.putString("c_times",String.valueOf(c_times));

            selectFragment.setArguments(sendBundle);
            transaction = fragmentManager.beginTransaction();

            transaction.add(R.id.recite_model,selectFragment);
            transaction.hide(countDownFragment).show(selectFragment);
            transaction.commit();
        }
    }


    /**
     * 初始化操作
     */
    public void initialize(){
        jsonRe=new JsonRe();
        finish_Dialog = new AlertDialog.Builder(this)
                .setTitle("任务完成")
                .setMessage("返回主页")
                .setIcon(R.mipmap.finish_icon)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent=new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.putParcelableArrayListExtra("id_list",(ArrayList<? extends Parcelable>) id_list);
//                        intent.setClass(ReciteActivity.this,MainActivity.class);
                        intent.setClass(ReciteWordActivity.this,spell_reciteActivity.class);
                        startActivity(intent);
                    }
                })

                .setNegativeButton("我不", new DialogInterface.OnClickListener() {//添加取消
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                      dialogInterface.dismiss();
                    }
                })
                .setNeutralButton("备用按钮", new DialogInterface.OnClickListener() {//添加普通按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(ReciteWordActivity.this, "这是普通按钮按钮", Toast.LENGTH_SHORT).show();
                    }
                })
                .create();
        interrupt_Dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确定要退出?")
                .setIcon(R.mipmap.warning_icon)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent=new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(ReciteWordActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                })

                .setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加取消
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNeutralButton("备用按钮", new DialogInterface.OnClickListener() {//添加普通按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(ReciteWordActivity.this, "这是普通按钮按钮", Toast.LENGTH_SHORT).show();
                    }
                })
                .create();
        Arrays.fill(finish_ind,0);
        getrecitelist();//get the list of word
    }

    /**
     * 选项按钮点击事件
     * @param view
     */
    public void onClick(View view){
        switch(view.getId()){
            case R.id.turn_mode:
                switch (mode_num){
                    case 0:
                        start_countdown_mode(recite_list.get(select[correct_sel]).get("word_group").toString());
                        mode_num=1;
                        break;
                    case 1:
                        start_countdown_mode(recite_list.get(select[correct_sel]).get("word_group").toString());
                        mode_num=0;
                        break;
                }

                break;

        }
    }


    /**
     * CountDownFragment的回调函数
     * ArrayList<String> s
     * @param s
     */
    @Override
    public void countdownonFragmentInteraction(ArrayList<String> s) {
        Log.i("回调了",s.toString());
        start_recite();
    }

    /**
     * SelectFragment的回调函数
     * ArrayList<String> s
     * @param res
     */
    @Override
    public void selectonFragmentInteraction(HashMap<String,Object> res) {
        Log.i("SelectFragment回调了",res.toString());
        Map<String,Object> correct_word = new HashMap<String,Object>();
        Map<String,Object> wrong_word = new HashMap<String,Object>();
        switch (res.get("judge").hashCode()){
            case 1:
                Log.i("结果是","回答正确");
                correct_word = recite_list.get(select[Integer.valueOf(res.get("correct_sel").toString())]);
//                Log.i("数据 前",recite_list.get(select[Integer.valueOf(res.get("correct_sel").toString())]).toString());
                correct_word.put("today_correct_times",Integer.valueOf(correct_word.get("today_correct_times").toString())+1);
                if(Integer.valueOf(correct_word.get("today_correct_times").toString())>=c_times){
                    finish_ind[select[correct_sel]]=1;
                    finish_num++;
                    total_progress.post(new Runnable() {
                        @Override
                        public void run() {
                            int pro_num = finish_num*100/recite_num;
                            total_progress.setProgress(pro_num);
                        }
                    });
                    correct_word.put("correct_times",Integer.valueOf(correct_word.get("correct_times").toString())+1);
                    if(Integer.valueOf(correct_word.get("correct_times").toString())>=prof_times){
                        correct_word.put("prof_flag",1);
                    }
                }
                recite_list.set(select[Integer.valueOf(res.get("correct_sel").toString())],correct_word);
//                Log.i("数据 后",recite_list.get(select[Integer.valueOf(res.get("correct_sel").toString())]).toString());
                break;
            case 2:
                Log.i("结果是","回答错误");
//                Log.i("数据 前",recite_list.get(select[Integer.valueOf(res.get("correct_sel").toString())]).toString());
//                Log.i("数据 前（user_select）",recite_list.get(select[Integer.valueOf(res.get("wrong_sel").toString())]).toString());
                correct_word = recite_list.get(select[Integer.valueOf(res.get("correct_sel").toString())]);
                correct_word.put("today_correct_times",0);
                correct_word.put("error_times",Integer.valueOf(correct_word.get("error_times").toString())+1);
                recite_list.set(select[Integer.valueOf(res.get("correct_sel").toString())],correct_word);
                wrong_word = recite_list.get(select[Integer.valueOf(res.get("wrong_sel").toString())]);
                wrong_word.put("today_correct_times",0);
                wrong_word.put("error_times",Integer.valueOf(wrong_word.get("error_times").toString())+1);
                recite_list.set(select[Integer.valueOf(res.get("wrong_sel").toString())],wrong_word);
//                Log.i("数据 后",recite_list.get(select[Integer.valueOf(res.get("correct_sel").toString())]).toString());
//                Log.i("数据 后（user_select）",recite_list.get(select[Integer.valueOf(res.get("wrong_sel").toString())]).toString());
                pron_lock=true;
                jump_to_example(select[correct_sel]);
                break;
            case 3:
                Log.i("结果是","不知道");
//                Log.i("数据 前",recite_list.get(select[Integer.valueOf(res.get("correct_sel").toString())]).toString());
                correct_word = recite_list.get(select[Integer.valueOf(res.get("correct_sel").toString())]);
                correct_word.put("today_correct_times",0);
                correct_word.put("error_times",Integer.valueOf(correct_word.get("error_times").toString())+1);
                recite_list.set(select[Integer.valueOf(res.get("correct_sel").toString())],correct_word);
//                Log.i("数据 后",recite_list.get(select[Integer.valueOf(res.get("correct_sel").toString())]).toString());
                pron_lock=true;
                jump_to_example(select[correct_sel]);
                break;
        }
//        transaction.remove(selectFragment);
//        start_countdown_mode(recite_list.get(select[correct_sel]).get("word_group").toString());
//        transaction = fragmentManager.beginTransaction();
//        transaction = fragmentManager.beginTransaction();
//        transaction.hide(selectFragment);
//        transaction.commit();
//        selectFragment.onDestroy();
        if(!pron_lock){
            start_recite();
        }
    }

    /**
     * 跳转到例句页面
     * @param id
     */
    public void jump_to_example(int id){
        Intent intent = new Intent(ReciteWordActivity.this, ExampleActivity.class);
        intent.putExtra("id",recite_list.get(id).get("id").toString());
//        startActivity(intent);
        startActivityForResult(intent,1);

    }
    /**
     * 子页面跳回
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
        if (requestCode == 1 && resultCode == 1) {
            pron_lock = false;
            start_recite();
        }
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
            interrupt_Dialog.show();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
