package com.example.administrator.listviewadptwebjsonimg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class spell_reciteActivity extends AppCompatActivity {

    ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);
    TextView cword;
    EditText eword;
    JsonRe  jsonRe;
    List<Map<String,Object>> spell_list=null;
    //    int r_id,r_correct_times,r_error_times,r_pro
    int spell_num = 20;//今天要背的单词数
    int finish_num = 0;//今天背完的单词数
    String word_info_url="http://192.168.57.1/word/querybyid.php?id=";
    String spell_list_url="http://192.168.57.1/word/getrecitelist.php?mount=";
    String update_url="http://192.168.57.1/word/update_recite.php?";//http://192.168.57.1/word/update_recite.php?id=1&correct_times=1&error_times=1&prof_flag=1
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spell_recite);
        cword = (TextView)findViewById(R.id.cword);
        eword = (EditText) findViewById(R.id.eword);
        jsonRe=new JsonRe();
        getspelllist();
    }
    /**
     * 获取今天要背的单词列表
     */
    private void getspelllist()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext=new HttpGetContext();
                String wordlistjson=httpGetContext.httpclientgettext(spell_list_url+String.valueOf(spell_num));
                spell_list=jsonRe.getReciteList(wordlistjson);
                Log.i("spell_list",spell_list.toString());
            }
        }).start();
    }
    public void start_spell(){
        int index=0;
        while(true){

        }
    }
}
