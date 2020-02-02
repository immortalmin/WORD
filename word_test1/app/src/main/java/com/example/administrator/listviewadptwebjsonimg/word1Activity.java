package com.example.administrator.listviewadptwebjsonimg;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取单词列表
 * */
public class word1Activity extends AppCompatActivity {

    JsonRe  jsonRe;
    ListView listView;
    TextView all_num,finished_num;
    List<Map<String,Object>> word_list=null; //商家列表数据arraylist数组
//    String  url="http://192.168.57.1/word/db3-conn.php"; //获取商家基本信息的API
    String url="http://47.98.239.237/word/php_file/getall.php"; //获取商家基本信息的API
    String getwordinfo_url = "http://47.98.239.237/word/php_file/getallinfo.php";
    List<Map<String, Object>> word_info = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word1);
        listView=(ListView)findViewById(R.id.ListView1);
        all_num = (TextView)findViewById(R.id.all_num);
        finished_num = (TextView)findViewById(R.id.finished_num);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(word1Activity.this, ExampleActivity.class);
                String id = String.valueOf(position+1);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });
        getwordlist();
        get_amount();
        jsonRe=new JsonRe();

    }
    //获取服务器端商家数据
    private void getwordlist()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext=new HttpGetContext();
//                String wordlistjson=httpGetContext.httpclientgettext(url);
//                word_list=jsonRe.getWordList(wordlistjson);
                String wordinfo=httpGetContext.httpclientgettext(getwordinfo_url);
                word_info = new ArrayList<Map<String, Object>>();
                word_info=jsonRe.get_wordinfo(wordinfo);
                mHandler.obtainMessage(0,word_info).sendToTarget();
            }
        }).start();
    }
    private void get_amount()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext=new HttpGetContext();
                String amount = httpGetContext.httpclientgettext("http://47.98.239.237/word/php_file/total_amount.php");
                String total_amount = jsonRe.get_amount(amount);
                amount = httpGetContext.httpclientgettext("http://47.98.239.237/word/php_file/prof_amount.php");
                String finished_amount = jsonRe.get_amount(amount);
                List<String> amount_list = new ArrayList<String>();
                amount_list.add(total_amount);
                amount_list.add(finished_amount);
                mHandler.obtainMessage(1,amount_list).sendToTarget();
            }
        }).start();
    }
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what == 0){
                word_list = (List<Map<String,Object>>)msg.obj;
                listView.setAdapter(new WordListAdapter(word1Activity.this,word_list));
            }else if(msg.what == 1){
                List<String> amount = (ArrayList<String>)msg.obj;
                all_num.setText(amount.get(0));
                finished_num.setText(amount.get(1));
            }

        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

}
