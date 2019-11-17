package com.example.administrator.listviewadptwebjsonimg;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * 点击单词后进入单词例句
 * 跳转到这个界面需要传id的值
 */
public class ExampleActivity extends AppCompatActivity {

    TextView word_meaning,E_sentence,C_translate,non_example,page,word_group,C_meaning;
    ListView example_list;
    JsonRe  jsonRe;
    List<Map<String,Object>> word_list=null;
//    String  url="http://192.168.57.1/word/querybyid.php?id=";
    String  url="http://47.98.239.237/word/querybyid.php?id=";
    String id = "1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_example);
        word_meaning = (TextView)findViewById(R.id.word_meaning);
        E_sentence = (TextView)findViewById(R.id.E_sentence);
        C_translate = (TextView)findViewById(R.id.C_translate);
        non_example = (TextView)findViewById(R.id.non_example);
        page = (TextView)findViewById(R.id.page);
        word_group = (TextView)findViewById(R.id.word_group);
        C_meaning = (TextView)findViewById(R.id.C_meaning);
        example_list = (ListView)findViewById(R.id.example_list);
        mHandler.obtainMessage(1).sendToTarget();
        jsonRe=new JsonRe();
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
//        Log.i("Example__id",String.valueOf(id));
        getwordlist();
    }
    private void getwordlist()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext=new HttpGetContext();
                String wordlistjson=httpGetContext.httpclientgettext(url+id);
                word_list=jsonRe.getWordList(wordlistjson);
                mHandler.obtainMessage(0,word_list).sendToTarget();
            }
        }).start();
    }
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what == 0){
                word_list = (List<Map<String,Object>>)msg.obj;
                Map<String,Object> word = word_list.get(0);
//            Log.i("word",word.toString());
                List<Map<String,Object>> translates = (List<Map<String,Object>>)word.get("translate");
                page.setText("页码："+word.get("page").toString());
                word_group.setText(word.get("word_group").toString());
                C_meaning.setText(word.get("C_meaning").toString());
                if(translates.size() == 0){
                    Log.i("translates","为空");
                    non_example.setVisibility(View.VISIBLE);
                    example_list.setVisibility(View.GONE);
                }
                SimpleAdapter adapter = new SimpleAdapter(ExampleActivity.this,
                        translates,R.layout.exampleitem,new String[]{
                        "word_meaning","E_sentence","C_translate"},
                        new int[]{R.id.word_meaning,R.id.E_sentence,R.id.C_translate});
                example_list.setAdapter(adapter);
            }else if (msg.what==1){
                page.setText("");
                word_group.setText("");
                C_meaning.setText("");
            }

        }
    };
}
