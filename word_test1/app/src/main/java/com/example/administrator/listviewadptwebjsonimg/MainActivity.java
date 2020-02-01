package com.example.administrator.listviewadptwebjsonimg;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主界面
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    JsonRe jsonRe;
    private Context context;
    List<Map<String,Object>> word_list=null;
    Button btn_wordlist,btn_recite,btn_test,btn_spell,search1;
    EditText editText;
    SearchView search_bar;
    WordDAO wordDAO = new WordDAO();
    private SoundPool soundPool;
    private int sound_success,sound_fail;
    private DBAdapter dbAdapter;
    Intent intent;
    Boolean flag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        btn_wordlist = (Button)findViewById(R.id.btn_wordlist);
        btn_recite = (Button)findViewById(R.id.btn_recite);
        btn_spell = (Button)findViewById(R.id.btn_spell);
        btn_test = (Button)findViewById(R.id.btn_test);
        search_bar = (SearchView) findViewById(R.id.search_bar);
        btn_wordlist.setOnClickListener(this);
        btn_recite.setOnClickListener(this);
        btn_test.setOnClickListener(this);
        btn_spell.setOnClickListener(this);
//        search1.setOnClickListener(this);
        search_bar.setOnClickListener(this);

        search_bar.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("ccc","clicked");
                intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
            }
        });
        dbAdapter = new DBAdapter(this);
        dbAdapter.open();
        jsonRe=new JsonRe();
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        sound_success = soundPool.load(this, R.raw.success, 1);
        sound_fail = soundPool.load(this, R.raw.fail, 1);
//        search1.setBackgroundColor(Color.TRANSPARENT); //背景透明
//        search1.getBackground().setAlpha(150); //int 在0-255之间, 设置半透明

    }

    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.search1:
//                intent = new Intent(MainActivity.this,SearchActivity.class);
//                startActivity(intent);
//                break;
            case R.id.btn_wordlist:
                intent = new Intent(MainActivity.this,word1Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
            case R.id.btn_spell:
                intent = new Intent(MainActivity.this,spell_reciteActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_recite:
                intent = new Intent(MainActivity.this,ReciteWordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
            case R.id.btn_test:
                search_bar.clearFocus();
//                intent = new Intent(MainActivity.this,TestActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
            case R.id.search_bar:
                intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (flag && (keyCode == KeyEvent.KEYCODE_BACK)) {
            Toast.makeText(this, "按下了back键   onKeyDown()", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.i("ccc","返回了");
//        if (requestCode == 1 && resultCode == 1) {
//            Log.i("ccc","返回了2");
//        }
//
//    }
}
