package com.example.administrator.listviewadptwebjsonimg;

import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * 主界面
 */
public class MainActivity extends AppCompatActivity {

    JsonRe jsonRe;
    List<Map<String,Object>> word_list=null;
    Button btn_wordlist,btn_recite,btn_test;
    WordDAO wordDAO = new WordDAO();
    private DBAdapter dbAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_wordlist = (Button)findViewById(R.id.btn_wordlist);
        btn_recite = (Button)findViewById(R.id.btn_recite);
        btn_test = (Button)findViewById(R.id.btn_test);
        btn_wordlist.setOnClickListener(wordlistClick);
        btn_recite.setOnClickListener(reciteClick);
        btn_test.setOnClickListener(colorTest);
        dbAdapter = new DBAdapter(this);
        dbAdapter.open();
        jsonRe=new JsonRe();
    }
    View.OnClickListener wordlistClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this,word1Activity.class);
            startActivity(intent);
        }
    };
    View.OnClickListener reciteClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this,ReciteActivity.class);
            startActivity(intent);
        }
    };
    View.OnClickListener colorTest = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(1000);
                    btn_test.setBackgroundColor(Color.RED);
                }
            }).start();
//            SystemClock.sleep(1000);
            btn_test.setBackgroundColor(Color.GREEN);
        }
    };

}