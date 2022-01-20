package com.immortalmin.www.word;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

/**
 * 获取收藏的单词列表
 * */
public class collectActivity extends AppCompatActivity implements View.OnClickListener{

    private User user = new User();
    private UserDataUtil userDataUtil = new UserDataUtil(this);
    private ListView listView;
    private TextView all_num,finished_num;
    private List<DetailWord> word_list=null;
    private List<DetailWord> collect_list=null;
    private WordListAdapter wordListAdapter = null;
    private CollectDbDao collectDbDao = new CollectDbDao(this);
    private int now_position=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        SQLiteStudioService.instance().start(this);//连接SQLiteStudio
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        listView= findViewById(R.id.ListView1);
        all_num = findViewById(R.id.all_num);
        finished_num = findViewById(R.id.finished_num);
        finished_num.setOnClickListener(this);
        listView.setOnItemClickListener((adapterView, view, position, l) -> {
            Intent intent = new Intent(collectActivity.this, ExampleActivity.class);
            intent.putExtra("wid",collect_list.get(position).getWid());
            intent.putExtra("dict_source",collect_list.get(position).getDict_source());
            startActivityForResult(intent,1);
            overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                now_position = i;
            }
        });
        init();

    }

    private void init() {
        user = userDataUtil.getUserDataFromSP();
        getCollectList();
        mHandler.sendEmptyMessage(1);
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.finished_num:

                break;
        }
    }

    /**
     * 获取收藏的单词列表
     */
    private void getCollectList(){
        if(collect_list==null){
            collect_list = collectDbDao.getCollectList();
            wordListAdapter = new WordListAdapter(collectActivity.this,collect_list);
            listView.setAdapter(wordListAdapter);
        }else{
            collect_list.clear();
            collect_list.addAll(collectDbDao.getCollectList());
            wordListAdapter.notifyDataSetChanged();
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 0:
                    word_list = (List<DetailWord>)message.obj;
                    wordListAdapter = new WordListAdapter(collectActivity.this,word_list);
                    listView.setAdapter(wordListAdapter);
                    listView.setSelection(now_position);
                    wordListAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    int allCount = collectDbDao.getCollectCount();
                    int finishCount = collectDbDao.getFinishCount();
                    all_num.setText(String.valueOf(allCount));
                    finished_num.setText(String.valueOf(finishCount));
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + message.what);
            }
            return false;
        }
    });

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
        if (requestCode == 1 && resultCode == 1) {//单词数据发生改变，则更新数据
            getCollectList();
            mHandler.sendEmptyMessage(1);
        }
    }

}
