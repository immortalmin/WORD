package com.example.administrator.listviewadptwebjsonimg;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

/**
 * 获取单词列表
 * */
public class word1Activity extends AppCompatActivity {

    JsonRe  jsonRe;
    ListView listView;
    List<Map<String,Object>> word_list=null; //商家列表数据arraylist数组
//    String  url="http://192.168.57.1/word/db3-conn.php"; //获取商家基本信息的API
    String  url="http://47.97.116.200/word/db3-conn.php"; //获取商家基本信息的API
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word1);
        listView=(ListView)findViewById(R.id.ListView1);
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
        jsonRe=new JsonRe();

    }
    //获取服务器端商家数据
    private void getwordlist()
    {

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext=new HttpGetContext();
                String wordlistjson=httpGetContext.httpclientgettext(url);
                word_list=jsonRe.getWordList(wordlistjson);
                mHandler.obtainMessage(0,word_list).sendToTarget();
            }
        }).start();
    }
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            word_list = (List<Map<String,Object>>)msg.obj;
            String translate = word_list.get(1).toString();
            SimpleAdapter adapter = new SimpleAdapter(word1Activity.this,
                    word_list,R.layout.worditem,new String[]{
                    "word_group","C_meaning"},
                    new int[]{R.id.word_group,R.id.C_meaning});
            listView.setAdapter(adapter);
        }
    };
}
