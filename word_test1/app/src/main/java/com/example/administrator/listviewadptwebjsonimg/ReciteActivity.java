package com.example.administrator.listviewadptwebjsonimg;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
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
    Runnable changeColor;
    Button sel1,sel2,sel3,sel4;
    TextView wordview;
    JsonRe  jsonRe;
    Boolean flag=Boolean.FALSE;
    List<Map<String,Object>> recite_list=null;
    int recite_num = 1;//今天要背的单词数
    int recite_scope = 10;//额外加入的单词数
    int c_times = 1;//每个单词变成今天背完需要的次数
    int correct_sel = 0;//正确答案的下标
    int[] select = null;//下标转换到在recite_list中的下标
    int[] finishi_ind = new int[10000];//今天是否已经连续背对5次
    int finish_num = 0;//今天背完的单词数
    String  word_info_url="http://192.168.57.1/word/querybyid.php?id=";
    String  recite_list_url="http://192.168.57.1/word/getrecitelist.php?mount=";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recite);
        sel1 = (Button)findViewById(R.id.sel1);
        sel2 = (Button)findViewById(R.id.sel2);
        sel3 = (Button)findViewById(R.id.sel3);
        sel4 = (Button)findViewById(R.id.sel4);
        wordview = (TextView)findViewById(R.id.wordview);
        jsonRe=new JsonRe();
        Arrays.fill(finishi_ind,0);
        getrecitelist();
        sel1.setOnClickListener(this);
        sel2.setOnClickListener(this);
        sel3.setOnClickListener(this);
        sel4.setOnClickListener(this);
        changeColor = new Runnable(){
            public void run(){
                sel1.setBackgroundColor(Color.parseColor("#b5beb5"));
                sel2.setBackgroundColor(Color.parseColor("#b5beb5"));
                sel3.setBackgroundColor(Color.parseColor("#b5beb5"));
                sel4.setBackgroundColor(Color.parseColor("#b5beb5"));
                if(finish_num>=recite_num){
                    Intent intent = new Intent(ReciteActivity.this,MainActivity.class);
                    startActivity(intent);
                }else{
//                    Log.i("")
                    recite();
                }
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
//                Log.i("recite_list",recite_list.toString());
                recite();
            }
        }).start();
    }
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what==0){
                String word=msg.obj.toString();
                wordview.setText(word);
            }
        }
    };

    /**
     * 随机生成选项
     */
    private void recite(){
        Log.i("recite","刷新选项");
        int count = 0;
        int[] mark = new int[10000];
        select = new int[4];
        Arrays.fill(mark,0);
        int num;
        while(true){
            while(true){
                num = (int)(Math.random()*(recite_num+recite_scope));
                if(mark[num]==0 && finishi_ind[num]==0){
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
        mHandler.obtainMessage(0,recite_list.get(select[correct_sel]).get("word_group").toString()).sendToTarget();
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
//                    sel1.setBackgroundColor(Color.parseColor("#b5beb5"));
                    sel1.setBackgroundColor(Color.GREEN);
                }else{
                    sel1.setBackgroundColor(Color.RED);
                }
                scheduledThreadPool.schedule(changeColor,500, TimeUnit.MILLISECONDS);
                break;
            case R.id.sel2:
                update_recite_list(1);
                if(correct_sel == 1){
                    sel2.setBackgroundColor(Color.GREEN);
                }else{
                    sel2.setBackgroundColor(Color.RED);
                }
                scheduledThreadPool.schedule(changeColor,500, TimeUnit.MILLISECONDS);
                break;
            case R.id.sel3:
                update_recite_list(2);
                if(correct_sel == 2){
                    sel3.setBackgroundColor(Color.GREEN);
                }else{
                    sel3.setBackgroundColor(Color.RED);
                }
                scheduledThreadPool.schedule(changeColor,500, TimeUnit.MILLISECONDS);
                break;
            case R.id.sel4:
                update_recite_list(3);
                if(correct_sel == 3){
                    sel4.setBackgroundColor(Color.GREEN);
                }else{
                    sel4.setBackgroundColor(Color.RED);
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
        if(user_sel == correct_sel){
            correct_word.put("today_correct_times",correct_to_times+1);
            if(correct_to_times+1 >= c_times){
                correct_word.put("correct_times",correct_all_times+1);
                finishi_ind[correct_sel]=1;
                finish_num++;
                //如果答对单词的次数达到掌握的程度，就进行标记
                if(correct_all_times+1>=5){
                    correct_word.put("prof_flag",1);
                }
            }
            recite_list.set(select[correct_sel],correct_word);
        }else{
            correct_word.put("today_correct_times",0);
            correct_word.put("error_times",correct_error_times+1);
            user_word.put("today_correct_times",0);
            user_word.put("error_times",user_error_times+1);
            recite_list.set(select[correct_sel],correct_word);
            recite_list.set(select[user_sel],user_word);
        }
        Log.i("已答对的单词数",String.valueOf(finish_num));
        Log.i("该单词今天答对的次数",correct_word.get("today_correct_times").toString());
    }
}
