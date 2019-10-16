package com.example.administrator.listviewadptwebjsonimg;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReciteActivity extends AppCompatActivity implements View.OnClickListener {

    ExecutorService mExecutorService = null;
    ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);
    Runnable changeColor,update_recite_data;
    Vibrator vibrator;//vibrator.vibrate(30);
    private SoundPool soundPool;
    private int sound_success,sound_fail;
    Button sel1,sel2,sel3,sel4;
    TextView wordview,finish_view,all_finish_view;
    JsonRe  jsonRe;
    Boolean flag=Boolean.FALSE;
    List<Map<String,Object>> recite_list=null;
//    int r_id,r_correct_times,r_error_times,r_pro
    int recite_num = 20;//今天要背的单词数
    int recite_scope = 10;//额外加入的单词数
    int c_times = 2;//每个单词变成今天背完需要的次数
    int prof_times = 2;//达到掌握需要的次数
    int correct_sel = 0;//正确答案的下标
    int[] select = null;//下标转换到在recite_list中的下标
    int[] finish_ind = new int[10000];//今天是否已经连续背对5次
    int finish_num = 0;//今天背完的单词数
//    String word_info_url="http://192.168.57.1/word/querybyid.php?id=";
    String word_info_url="http://47.98.239.237/word/querybyid.php?id=";
//    String recite_list_url="http://192.168.57.1/word/getrecitelist.php?mount=";
    String recite_list_url="http://47.98.239.237/word/getrecitelist.php?mount=";
    //http://192.168.57.1/word/update_recite.php?id=1&correct_times=1&error_times=1&prof_flag=1
//    String update_url="http://192.168.57.1/word/update_recite.php?";
    String update_url="http://47.98.239.237/word/update_recite.php?";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recite);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        sound_success = soundPool.load(this, R.raw.success, 1);
        sound_fail = soundPool.load(this, R.raw.fail, 1);
        sel1 = (Button)findViewById(R.id.sel1);
        sel2 = (Button)findViewById(R.id.sel2);
        sel3 = (Button)findViewById(R.id.sel3);
        sel4 = (Button)findViewById(R.id.sel4);
        wordview = (TextView)findViewById(R.id.wordview);
        finish_view = (TextView)findViewById(R.id.finish_view);
        all_finish_view = (TextView)findViewById(R.id.all_finish_view);
        mHandler.obtainMessage(1).sendToTarget();//清空内容
        jsonRe=new JsonRe();
        Arrays.fill(finish_ind,0);
        getrecitelist();
        sel1.setOnClickListener(this);
        sel2.setOnClickListener(this);
        sel3.setOnClickListener(this);
        sel4.setOnClickListener(this);
        changeColor = new Runnable(){
            public void run(){
                sel1.setBackgroundColor(Color.parseColor("#30000000"));
                sel2.setBackgroundColor(Color.parseColor("#30000000"));
                sel3.setBackgroundColor(Color.parseColor("#30000000"));
                sel4.setBackgroundColor(Color.parseColor("#30000000"));
                if(finish_num>=recite_num){
//                    Log.i("背诵完成",recite_list.toString());
                    scheduledThreadPool.schedule(update_recite_data,0, TimeUnit.MILLISECONDS);
                    Intent intent = new Intent(ReciteActivity.this,MainActivity.class);
                    startActivity(intent);
                }else{
                    recite();
                }
            }
        };
        update_recite_data = new Runnable() {
            @Override
            public void run() {
                Log.i("update_recite_data","开始更新数据库");
                HttpGetContext httpGetContext=new HttpGetContext();
                for(int i=0;i<recite_list.size();i++){
//                    Log.i("update",recite_list.get(i).toString());
                    httpGetContext.update_recite_list(update_url+"id="+recite_list.get(i).get("id")+"&correct_times="+recite_list.get(i).get("correct_times")+"&error_times="+recite_list.get(i).get("error_times")+"&prof_flag="+recite_list.get(i).get("prof_flag"));
                }
                Log.i("update_recite_data","更新数据库完成");
            }
        };
    }

    /**
     * 获取今天要背的单词列表
     */
    private void getrecitelist()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext=new HttpGetContext();
                String wordlistjson=httpGetContext.httpclientgettext(recite_list_url+String.valueOf(recite_num+recite_scope));
                recite_list=jsonRe.getReciteList(wordlistjson);
                recite();
            }
        }).start();
    }
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what==0){
                Map<String, Object> recite_info = new HashMap<String, Object>();
                recite_info = (Map<String, Object>)msg.obj;
//                String word=msg.obj.toString();
                wordview.setText(recite_info.get("wordview").toString());
                finish_view.setText(recite_info.get("finish_view").toString());
                all_finish_view.setText(recite_info.get("all_finish_view").toString());
//                Log.i("handler",recite_info.toString());
            }else if(msg.what==1){
                wordview.setText("");
                finish_view.setText("");
                all_finish_view.setText("");
                sel1.setText("");
                sel2.setText("");
                sel3.setText("");
                sel4.setText("");
            }
        }
    };

    /**
     * 随机生成选项
     */
    private void recite(){
//        Log.i("recite","刷新选项");
        int count = 0;
        int[] mark = new int[10000];
        select = new int[4];
        Arrays.fill(mark,0);
        int num;
        while(true){
            while(true){
                num = (int)(Math.random()*(recite_num+recite_scope));
                if(mark[num]==0 && finish_ind[num]==0){
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
        Map<String, Object> recite_info = new HashMap<String, Object>();
        recite_info.put("wordview",recite_list.get(select[correct_sel]).get("word_group").toString());
        recite_info.put("finish_view",recite_list.get(select[correct_sel]).get("today_correct_times").toString()+"/"+String.valueOf(c_times));
        recite_info.put("all_finish_view",String.valueOf(finish_num)+"/"+String.valueOf(recite_num));
        mHandler.obtainMessage(0,recite_info).sendToTarget();
        sel1.setText(recite_list.get(select[0]).get("C_meaning").toString());
        sel2.setText(recite_list.get(select[1]).get("C_meaning").toString());
        sel3.setText(recite_list.get(select[2]).get("C_meaning").toString());
        sel4.setText(recite_list.get(select[3]).get("C_meaning").toString());
    }

    /**
     * 选项按钮点击事件
     * @param view
     */
    public void onClick(View view){
        switch(view.getId()){
            case R.id.sel1:
                update_recite_list(0);
                if(correct_sel == 0){
                    sel1.setBackgroundColor(Color.parseColor("#6093DB70"));
                }else{
                    sel1.setBackgroundColor(Color.parseColor("#60FF2400"));
                }
                scheduledThreadPool.schedule(changeColor,500, TimeUnit.MILLISECONDS);
                break;
            case R.id.sel2:
                update_recite_list(1);
                if(correct_sel == 1){
                    sel2.setBackgroundColor(Color.parseColor("#6093DB70"));
                }else{
                    sel2.setBackgroundColor(Color.parseColor("#60FF2400"));
                }
                scheduledThreadPool.schedule(changeColor,500, TimeUnit.MILLISECONDS);
                break;
            case R.id.sel3:
                update_recite_list(2);
                if(correct_sel == 2){
                    sel3.setBackgroundColor(Color.parseColor("#6093DB70"));
                }else{
                    sel3.setBackgroundColor(Color.parseColor("#60FF2400"));
                }
                scheduledThreadPool.schedule(changeColor,500, TimeUnit.MILLISECONDS);
                break;
            case R.id.sel4:
                update_recite_list(3);
                if(correct_sel == 3){
                    sel4.setBackgroundColor(Color.parseColor("#6093DB70"));
                }else{
                    sel4.setBackgroundColor(Color.parseColor("#60FF2400"));
                }
                scheduledThreadPool.schedule(changeColor,500, TimeUnit.MILLISECONDS);
                break;
        }
    }
    public void update_recite_list(int user_sel){
        //正确选项今天已连续回答正确的次数
        int correct_to_times = Integer.parseInt(recite_list.get(select[correct_sel]).get("today_correct_times").toString());
        //正确选项背过的次数
        int correct_all_times = Integer.parseInt(recite_list.get(select[correct_sel]).get("correct_times").toString());
        //正确选项错误的次数
        int correct_error_times = Integer.parseInt(recite_list.get(select[correct_sel]).get("error_times").toString());
        //用户选择的选项连续正确的次数
        int user_to_times = Integer.parseInt(recite_list.get(select[user_sel]).get("today_correct_times").toString());
        //用户选择的选项错误的次数
        int user_error_times = Integer.parseInt(recite_list.get(select[user_sel]).get("error_times").toString());
        Map<String,Object> correct_word = recite_list.get(select[correct_sel]);
        Map<String,Object> user_word = recite_list.get(select[user_sel]);
        if(user_sel == correct_sel){//回答正确
            soundPool.play(sound_success, 1.0f, 1.0f, 0, 0, 1.0f);
            correct_word.put("today_correct_times",correct_to_times+1);
            if(correct_to_times+1 >= c_times){
                correct_word.put("correct_times",correct_all_times+1);
                finish_ind[select[correct_sel]]=1;
                finish_num++;
                //如果答对单词的次数达到掌握的程度，就进行标记
                if(correct_all_times+1>=prof_times){
                    correct_word.put("prof_flag",1);
                }
            }
            recite_list.set(select[correct_sel],correct_word);
        }else{//回答错误
            soundPool.play(sound_fail, 1.0f, 1.0f, 0, 0, 1.0f);
            correct_word.put("today_correct_times",0);
            correct_word.put("error_times",correct_error_times+1);
            user_word.put("today_correct_times",0);
            user_word.put("error_times",user_error_times+1);
            recite_list.set(select[correct_sel],correct_word);
            recite_list.set(select[user_sel],user_word);
            jump_to_example(select[correct_sel]);
        }
        Log.i("已答对的单词数",String.valueOf(finish_num));
        Log.i("该单词今天答对的次数",correct_word.get("today_correct_times").toString());
        Log.i("recite_list",recite_list.toString());
    }
    public void jump_to_example(int id){
        Log.i("跳转到例句",recite_list.get(id).get("word_group").toString());
        Intent intent = new Intent(ReciteActivity.this, ExampleActivity.class);
        intent.putExtra("id",recite_list.get(id).get("id").toString());
        startActivity(intent);
    }
}
