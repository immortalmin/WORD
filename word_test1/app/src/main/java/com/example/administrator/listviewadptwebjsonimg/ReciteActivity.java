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

public class ReciteActivity extends AppCompatActivity implements View.OnClickListener {

    ExecutorService mExecutorService = null;
    Button sel1,sel2,sel3,sel4;
    TextView wordview;
    JsonRe  jsonRe;
    Boolean flag=Boolean.FALSE;
    List<Map<String,Object>> recite_list=null; //商家列表数据arraylist数组
    int recite_num = 20;
    int correct_num = 0;//正确答案的下标
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
        getrecitelist();
        sel1.setOnClickListener(this);
        sel2.setOnClickListener(this);
        sel3.setOnClickListener(this);
        sel4.setOnClickListener(this);

    }
    private void getrecitelist()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext=new HttpGetContext();
                String wordlistjson=httpGetContext.httpclientgettext(recite_list_url+String.valueOf(recite_num));
                recite_list=jsonRe.getReciteList(wordlistjson);
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
    private void recite(){
        int count = 0;
        int[] mark = new int[10000];
        int[] select = new int[4];
        Arrays.fill(mark,0);
        int num;
        while(true){
            while(true){
                num = (int)(Math.random()*20);
                if(mark[num]==0){
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
        correct_num = (int)(Math.random()*4);
        mHandler.obtainMessage(0,recite_list.get(select[correct_num]).get("word_group").toString()).sendToTarget();
        sel1.setText(recite_list.get(select[0]).get("C_meaning").toString());
        sel2.setText(recite_list.get(select[1]).get("C_meaning").toString());
        sel3.setText(recite_list.get(select[2]).get("C_meaning").toString());
        sel4.setText(recite_list.get(select[3]).get("C_meaning").toString());
    }
    public void onClick(View view){
        switch(view.getId()){
            case R.id.sel1:
                if(correct_num == 0){
                    Log.i("result","答对了");
//                    sel1.setBackgroundColor(Color.parseColor("#b5beb5"));
                    sel1.setBackgroundColor(Color.GREEN);
                }else{
                    Log.i("result","答错了");
                    sel1.setBackgroundColor(Color.RED);
                }
                break;
            case R.id.sel2:
                if(correct_num == 1){
                    Log.i("result","答对了");
                    sel2.setBackgroundColor(Color.GREEN);
                }else{
                    Log.i("result","答错了");
                    sel2.setBackgroundColor(Color.RED);
                }
                break;
            case R.id.sel3:
                if(correct_num == 2){
                    Log.i("result","答对了");
                    sel3.setBackgroundColor(Color.GREEN);
                }else{
                    Log.i("result","答错了");
                    sel3.setBackgroundColor(Color.RED);
                }
                break;
            case R.id.sel4:
                if(correct_num == 3){
                    Log.i("result","答对了");
                    sel4.setBackgroundColor(Color.GREEN);
                }else{
                    Log.i("result","答错了");
                    sel4.setBackgroundColor(Color.RED);
                }
                break;
        }
        /**
         * 还原选项颜色的线程
         */
//        mExecutorService.submit(new Runnable() {
//            @Override
//            public void run() {
//                SystemClock.sleep(1000);
//                sel1.setBackgroundColor(Color.parseColor("#b5beb5"));
//                sel2.setBackgroundColor(Color.parseColor("#b5beb5"));
//                sel3.setBackgroundColor(Color.parseColor("#b5beb5"));
//                sel4.setBackgroundColor(Color.parseColor("#b5beb5"));
//                recite();
//            }
//        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(1000);
                sel1.setBackgroundColor(Color.parseColor("#b5beb5"));
                sel2.setBackgroundColor(Color.parseColor("#b5beb5"));
                sel3.setBackgroundColor(Color.parseColor("#b5beb5"));
                sel4.setBackgroundColor(Color.parseColor("#b5beb5"));
                recite();
            }
        }).start();
    }
}
