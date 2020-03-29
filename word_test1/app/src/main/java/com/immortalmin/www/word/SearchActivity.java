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
import java.util.Observable;
import java.util.Observer;

import javax.security.auth.Subject;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView1;
    private ListView listView1;
    private List<HashMap<String,Object>> word_list= new ArrayList<HashMap<String,Object>>();
    private JsonRe jsonRe= new JsonRe();
    private UserData userData = new UserData();
    private String fuzzy_str;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchView1 = (SearchView)findViewById(R.id.searchview1);
        listView1 = (ListView)findViewById(R.id.ListView1);
        searchView1.setOnQueryTextListener(searchlistener1);
        listView1.setOnItemClickListener(listlistener1);
        searchView1.onActionViewExpanded();
        init_user();
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
            if(mHandler.hasMessages(1)){
                mHandler.removeMessages(1);
            }
            Message msg = new Message();
            msg.what=1;
            msg.obj=s;
            mHandler.sendMessageDelayed(msg,200);
            return false;
        }
    };


    private void init_user(){
        SharedPreferences sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
        userData.setUid(sp.getString("uid",null));
        userData.setRecite_num(sp.getInt("recite_num",20));
        userData.setRecite_scope(sp.getInt("recite_scope",10));
        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        userData.setUsername(sp.getString("username",null));
    }

    /**
     * 模糊查询的线程
     * @param word
     */
    private void fuzzyquery(final String word) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                word_list.clear();
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("uid",userData.getUid());
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

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 0:
                    word_list = (List<HashMap<String,Object>>)message.obj;
                    listView1.setAdapter(new SearchAdapter(SearchActivity.this,word_list));
                    break;
                case 1:
                    fuzzyquery((String)message.obj);
                    break;
            }
            return false;
        }
    });

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

            //光标颜色
            try {
                Field mCursorDrawableRes=TextView.class.getDeclaredField("mCursorDrawableRes");
                mCursorDrawableRes.setAccessible(true);
                mCursorDrawableRes.set(searchText, R.drawable.cursor);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
        if (requestCode == 1 && resultCode == 2) {
            fuzzyquery(fuzzy_str);
        }
    }
}
