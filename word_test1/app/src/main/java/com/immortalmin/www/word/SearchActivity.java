package com.immortalmin.www.word;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener,
        AddWordDialog.OnDialogInteractionListener{

    private SearchView searchView;
    private ListView listView;
    private ImageView imgView;
    private Button add_word_btn,clear_btn;
    private List<DetailWord> word_list= new ArrayList<>();
    private JsonRe jsonRe= new JsonRe();
    private HttpUtil httpUtil = new HttpUtil();
    private CaptureUtil captureUtil = new CaptureUtil();
    private NetworkUtil networkUtil = null;
    private User user = new User();
    private String fuzzy_str;
    private RecordDbDao mRecordDbDao;
    private MyAsyncTask myAsyncTask;
    private SearchAdapter searchAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.listView);
        imgView = findViewById(R.id.imgView);
        add_word_btn = findViewById(R.id.add_word_btn);
//        clear_btn = findViewById(R.id.clear_btn);
        searchAdapter = new SearchAdapter(SearchActivity.this,word_list);
        listView.setAdapter(searchAdapter);
        mRecordDbDao = new RecordDbDao(SearchActivity.this);
        searchView.setOnQueryTextListener(searchListener);
        listView.setOnItemClickListener(listListener);
        searchView.onActionViewExpanded();
        add_word_btn.setOnClickListener(this);
//        clear_btn.setOnClickListener(this);
        networkUtil = new NetworkUtil(this);
        init_user();
        setCursorIcon();
    }

    ListView.OnItemClickListener listListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            String wid = word_list.get(position).getWid();
            String dict_source = word_list.get(position).getDict_source();
            jump_to_example(wid,dict_source);
            if(!word_list.get(position).isCached()) mRecordDbDao.insertData(word_list.get(position));
        }
    };

    SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            fuzzy_str = s;
            //查询历史记录
            queryHistoryRecords(s);
            if(s.length()>0){
                if(mHandler.hasMessages(1)){
                    mHandler.removeMessages(1);
                }
                Message msg = new Message();
                msg.what=1;
                msg.obj=s;
                mHandler.sendMessageDelayed(msg,400);
            }else{
                searchAdapter.notifyDataSetChanged();
            }
            return false;
        }
    };

    /**
     * 查询历史记录
     * @param s
     */
    private void queryHistoryRecords(String s){
        word_list.clear();
        word_list.addAll(mRecordDbDao.queryData(s));
        for(int i=0;i<word_list.size();i++) word_list.get(i).setCached(true);
        searchAdapter.notifyDataSetChanged();
    }


    private void init_user(){
        SharedPreferences sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
        user.setUid(sp.getString("uid",null));
        user.setRecite_num(sp.getInt("recite_num",20));
        user.setRecite_scope(sp.getInt("recite_scope",10));
        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        user.setUsername(sp.getString("username",null));
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.add_word_btn:
                showDialog();
                break;
//            case R.id.clear_btn:
//                clear_dialog();
//                break;
        }
    }

    private void clear_dialog() {
        mHandler.sendEmptyMessage(2);
        SweetAlertDialog clear_alert = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        clear_alert.setTitleText("Delete Records")
                .setContentText("Are you sure to delete all history records?")
                .setConfirmText("yes")
                .setCancelText("nooo")
                .setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.cancel();
                    mHandler.sendEmptyMessage(3);
                    mRecordDbDao.deleteData();
                    queryHistoryRecords(fuzzy_str);
                })
                .setCancelClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.cancel();
                    mHandler.sendEmptyMessage(3);
                });
        clear_alert.setCancelable(false);
        clear_alert.show();
    }

    /**
     * 模糊查询
     * @param word
     */
    private void getWordList(String word){
        if(!networkUtil.isNetworkConnected()) return ;
        word = word.replaceAll("\"","\\\"");
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("what",12);
            jsonObject.put("uid", user.getUid());
            jsonObject.put("word",word);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result -> {
            List<DetailWord> tmp_list= new ArrayList<>();
            tmp_list.clear();
            tmp_list.addAll(jsonRe.detailWordData(result));
            for(int i=0;i<tmp_list.size();i++){
                if(!word_list.contains(tmp_list.get(i))) word_list.add(tmp_list.get(i));
            }
            searchAdapter.notifyDataSetChanged();
        }));
        myAsyncTask.execute(jsonObject);
    }


    private void showDialog(){
        mHandler.obtainMessage(2).sendToTarget();
        AddWordDialog addWordDialog = new AddWordDialog(this,R.style.MyDialog,fuzzy_str);
        addWordDialog.setCancelable(false);
        addWordDialog.setOnDismissListener(dialogInterface -> mHandler.obtainMessage(3).sendToTarget());
        addWordDialog.show();
    }

    private void add_wordandexample(final JSONObject jsonObject){
        new Thread(() -> {
            HttpGetContext httpGetContext = new HttpGetContext();
            httpGetContext.getData("http://47.98.239.237/word/php_file2/addword.php",jsonObject);
            getWordList(fuzzy_str);
        }).start();
    }


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 1:
                    getWordList((String)message.obj);
                    break;
                case 2:
                    Glide.with(SearchActivity.this).load(captureUtil.getcapture(SearchActivity.this))
                            .apply(bitmapTransform(new BlurTransformation(25))).into(imgView);
                    imgView.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    imgView.setVisibility(View.INVISIBLE);
                    break;
            }
            return false;
        }
    });

    /**
     * 跳转到例句页面
     * @param wid
     */
    public void jump_to_example(String wid,String dict_source){
        Intent intent = new Intent(SearchActivity.this, ExampleActivity.class);
        intent.putExtra("wid",wid);
        intent.putExtra("dict_source",dict_source);
        startActivityForResult(intent,1);
        overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
    }

    /**
     * 设置搜索框光标和字体的颜色
     */
    private void setCursorIcon(){
        int searchPlateId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_plate", null, null);
        View searchPlate = searchView.findViewById(searchPlateId);
        if (searchPlate != null) {
            int searchTextId = searchPlate.getContext().getResources()
                    .getIdentifier("android:id/search_src_text", null, null);
            //文字颜色
            TextView searchText = searchPlate.findViewById(searchTextId);

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
    public void addWordInteraction(JSONObject jsonObject){
        SharedPreferences sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
        try{
            jsonObject.put("uid",sp.getString("uid",null));
        }catch (JSONException e){
            e.printStackTrace();
        }
        add_wordandexample(jsonObject);
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
        queryHistoryRecords(fuzzy_str);
        getWordList(fuzzy_str);
    }
}
