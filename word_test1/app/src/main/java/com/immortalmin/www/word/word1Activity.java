package com.immortalmin.www.word;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取单词列表
 * */
public class word1Activity extends AppCompatActivity implements View.OnClickListener,
        AddWordDialog.OnDialogInteractionListener {

    JsonRe  jsonRe;
    ListView listView;
    TextView all_num,finished_num;
    Button add_btn;
    List<Map<String,Object>> word_list=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word1);
        listView=(ListView)findViewById(R.id.ListView1);
        all_num = (TextView)findViewById(R.id.all_num);
        finished_num = (TextView)findViewById(R.id.finished_num);
        add_btn = (Button)findViewById(R.id.add_btn);
        add_btn.setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(word1Activity.this, ExampleActivity.class);
                String id = word_list.get(position).get("wid").toString();
                intent.putExtra("id",id);
                startActivityForResult(intent,1);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
            }
        });
        getwordlist();
        get_amount();
        jsonRe=new JsonRe();

    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_btn:
                showDialog();
                break;
        }
    }

    private void getwordlist()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                String recitejson = httpGetContext.httpclientgettext("http://47.98.239.237/word/php_file2/getwordlist.php");
                List<HashMap<String,Object>> wordlist = null;
                wordlist = jsonRe.allwordData(recitejson);
                mHandler.obtainMessage(0,wordlist).sendToTarget();
            }
        }).start();
    }
    private void get_amount()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                String recitejson = httpGetContext.httpclientgettext("http://47.98.239.237/word/php_file2/get_count.php");
                HashMap<String,Object> count = null;
                count = jsonRe.getcount(recitejson);
                mHandler.obtainMessage(1,count).sendToTarget();
            }
        }).start();
    }
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what == 0){
                word_list = (List<Map<String,Object>>)msg.obj;
                listView.setAdapter(new WordListAdapter(word1Activity.this,word_list));
            }else if(msg.what == 1){
                HashMap<String,Object> count = (HashMap<String,Object>)msg.obj;
                all_num.setText(count.get("sum").toString());
                finished_num.setText(count.get("prof_count").toString());
            }

        }
    };

    private void showDialog(){
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        AddWordDialog addWordDialog = new AddWordDialog(this,R.style.MyDialog);
        addWordDialog.show();
        addWordDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
        });
    }

    private void add_wordandexample(final JSONObject jsonObject){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                httpGetContext.getData("http://47.98.239.237/word/php_file2/addword.php",jsonObject);
            }
        }).start();
    }


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

    @Override
    public void addWordInteraction(JSONObject jsonObject){
        add_wordandexample(jsonObject);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
        if (requestCode == 1 && resultCode == 2) {
            getwordlist();
            get_amount();
        }
    }

}
