package com.immortalmin.www.word;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    SearchView searchView1;
    ListView listView1;
    List<HashMap<String,Object>> word_list= new ArrayList<HashMap<String,Object>>();
    JsonRe jsonRe;
    String fuzzy_str;
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
            String id = word_list.get(position).get("wid").toString();
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
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            fuzzy_str = s;
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
                JSONObject jsonObject = new JSONObject();
                try{
                    SharedPreferences sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
                    jsonObject.put("uid",sp.getString("uid",null));
                    jsonObject.put("word",word);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpGetContext httpGetContext = new HttpGetContext();
                String recitejson = httpGetContext.getData("http://47.98.239.237/word/php_file2/getsearchlist.php",jsonObject);
                word_list = jsonRe.allwordData(recitejson);
                mHandler.obtainMessage(0,word_list).sendToTarget();
            }
        }).start();
    }
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what == 0){
                word_list = (List<HashMap<String,Object>>)msg.obj;
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
        startActivityForResult(intent,1);
        overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
        if (requestCode == 1 && resultCode == 2) {
            fuzzyquery(fuzzy_str);
        }
    }
}
