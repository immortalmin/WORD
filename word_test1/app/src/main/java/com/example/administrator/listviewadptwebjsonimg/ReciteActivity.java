package com.example.administrator.listviewadptwebjsonimg;

import android.os.Handler;
import android.os.Message;
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

public class ReciteActivity extends AppCompatActivity {

    Button sel1,sel2,sel3,sel4;
    TextView wordview;
    JsonRe  jsonRe;
    Boolean flag=Boolean.FALSE;
    List<Map<String,Object>> recite_list=null; //商家列表数据arraylist数组
    String  word_info_url="http://192.168.57.1/word/querybyid.php?id=";
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

    }
    private void getrecitelist()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext=new HttpGetContext();
                String wordlistjson=httpGetContext.httpclientgettext("http://192.168.57.1/word/test.php");
                recite_list=jsonRe.getReciteList(wordlistjson);
                start_recite();
//                mHandler.obtainMessage(0,recite_list).sendToTarget();
            }
        }).start();
    }
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what==0){
                String word=msg.obj.toString();
                wordview.setText(word);
                Log.i("wordview",word);
            }
//            tv1.setText(recite_list.toString());
        }
    };
    private void start_recite(){
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
        int correct_num = (int)(Math.random()*4);
        mHandler.obtainMessage(0,recite_list.get(select[correct_num]).get("word_group").toString()).sendToTarget();
        sel1.setText(recite_list.get(select[0]).get("C_meaning").toString());
        sel2.setText(recite_list.get(select[1]).get("C_meaning").toString());
        sel3.setText(recite_list.get(select[2]).get("C_meaning").toString());
        sel4.setText(recite_list.get(select[3]).get("C_meaning").toString());
    }
}
