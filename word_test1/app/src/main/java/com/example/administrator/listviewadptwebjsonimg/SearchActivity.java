package com.example.administrator.listviewadptwebjsonimg;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    SearchView searchView1;
    ListView listView1;
    String url = "http://47.98.239.237/word/php_file/fuzzyquerybyword.php?word=";
    List<Map<String,Object>> word_list= new ArrayList<Map<String,Object>>();
//    Map<Integer,Object> IdtoId = new HashMap<Integer, Object>();
    JsonRe jsonRe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchView1 = (SearchView)findViewById(R.id.searchview1);
        listView1 = (ListView)findViewById(R.id.ListView1);
        jsonRe = new JsonRe();
        searchView1.setOnQueryTextListener(searchlistener1);
        listView1.setOnItemClickListener(listlistener1);
        searchView1.onActionViewExpanded();
        setCursorIcon();
    }

    /**
     * listView1的点击事件
     */
    ListView.OnItemClickListener listlistener1 = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            String id = word_list.get(position).get("id").toString();
            jump_to_example(id);
        }
    };
//    SearchView.OnClickListener searchClick = new SearchView()
    /**
     * 搜索框searchView1监听事件
     */
    SearchView.OnQueryTextListener searchlistener1 = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            Log.i("SearchView111","点击了按钮");
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
//            Log.i("SearchView111","搜索内容发生改变");
//            Log.i("SearchView111",s);
            fuzzyquery(s);
            return false;
        }
    };

    /**
     * 模糊查询的线程
     * @param word
     */
    private void fuzzyquery(final String word)
    {

        new Thread(new Runnable() {
            @Override
            public void run() {
                word_list.clear();
                HttpGetContext httpGetContext=new HttpGetContext();
                String wordinfo=httpGetContext.httpclientgettext(url+URLEncoder.encode(word));
                word_list=jsonRe.get_wordinfo(wordinfo);
                mHandler.obtainMessage(0,word_list).sendToTarget();
            }
        }).start();
    }
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what == 0){
                word_list = (List<Map<String,Object>>)msg.obj;
//                SimpleAdapter adapter = new SimpleAdapter(SearchActivity.this,
//                        word_list,R.layout.searchitem,new String[]{
//                        "word_group","C_meaning"},
//                        new int[]{R.id.word,R.id.C_meaning});
//                listView1.setAdapter(adapter);
                listView1.setAdapter(new SearchAdapter(SearchActivity.this,word_list));
            }

        }
    };

    /**
     * 跳转到例句页面
     * @param id
     */
    public void jump_to_example(String id){
        Intent intent = new Intent(SearchActivity.this, ExampleActivity.class);
        intent.putExtra("id",id);
        startActivity(intent);

    }

    /**
     * 设置搜索框光标和字体的颜色
     */
    private void setCursorIcon(){
        int searchPlateId = searchView1.getContext().getResources()
                .getIdentifier("android:id/search_plate", null, null);
        View searchPlate = searchView1.findViewById(searchPlateId);
        if (searchPlate != null) {
            int searchTextId = searchPlate.getContext().getResources()
                    .getIdentifier("android:id/search_src_text", null, null);
            //文字颜色
            TextView searchText = (TextView) searchPlate.findViewById(searchTextId);
//            if (searchText != null) {
//                searchText.setTextColor(Color.WHITE);
//                searchText.setHintTextColor(Color.WHITE);
//            }

            //光标颜色
            try {
                Field mCursorDrawableRes=TextView.class.getDeclaredField("mCursorDrawableRes");
                mCursorDrawableRes.setAccessible(true);
                mCursorDrawableRes.set(searchText, R.drawable.cursor);
            } catch (Exception e){

            }
        }
    }
    //

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.i("ccc","点击了返回按钮");
//            Intent intent = new Intent(SearchActivity.this,MainActivity.class);
//            setResult(1,intent);
            finish();
            overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
