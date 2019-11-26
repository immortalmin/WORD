package com.example.administrator.listviewadptwebjsonimg;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    SearchView searchView1;
    ListView listView1;
    String url = "http://47.98.239.237/word/php_file/fuzzyquerybyword.php?word=";
    String[] data = { "Apple", "Banana", "Orange", "Watermelon",
            "Pear", "Grape", "Pineapple", "Strawberry", "Cherry", "Mango" };
    List<Map<String,Object>> word_list=null;
    JsonRe jsonRe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchView1 = (SearchView)findViewById(R.id.searchview1);
        listView1 = (ListView)findViewById(R.id.ListView1);
        jsonRe = new JsonRe();
        searchView1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.i("SearchView111","点击了按钮");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.i("SearchView111","搜索内容发生改变");
                Log.i("SearchView111",s);
                fuzzyquery(s);
                return false;
            }
        });
    }
    private void fuzzyquery(final String word)
    {

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext=new HttpGetContext();
                String wordinfo=httpGetContext.httpclientgettext(url+word);
                word_list=jsonRe.get_wordinfo(wordinfo);
                mHandler.obtainMessage(0,word_list).sendToTarget();
            }
        }).start();
    }
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what == 0){
                word_list = (List<Map<String,Object>>)msg.obj;
                SimpleAdapter adapter = new SimpleAdapter(SearchActivity.this,
                        word_list,R.layout.searchitem,new String[]{
                        "word_group"},
                        new int[]{R.id.word});
                listView1.setAdapter(adapter);
            }

        }
    };
}
