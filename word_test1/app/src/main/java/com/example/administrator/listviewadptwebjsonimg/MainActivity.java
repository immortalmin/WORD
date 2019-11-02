package com.example.administrator.listviewadptwebjsonimg;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主界面
 */
public class MainActivity extends AppCompatActivity {

    JsonRe jsonRe;
    private Context context;
    List<Map<String,Object>> word_list=null;
    Button btn_wordlist,btn_recite,btn_test,btn_spell;
    WordDAO wordDAO = new WordDAO();
    private SoundPool soundPool;
    private int sound_success,sound_fail;
    private DBAdapter dbAdapter;
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
        btn_wordlist.setOnClickListener(wordlistClick);
        btn_recite.setOnClickListener(reciteClick);
        btn_test.setOnClickListener(Test);
        btn_spell.setOnClickListener(spellClick);
        dbAdapter = new DBAdapter(this);
        dbAdapter.open();
        jsonRe=new JsonRe();
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        sound_success = soundPool.load(this, R.raw.success, 1);
        sound_fail = soundPool.load(this, R.raw.fail, 1);

    }
    View.OnClickListener wordlistClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this,word1Activity.class);
            startActivity(intent);
        }
    };
    View.OnClickListener spellClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this,spell_reciteActivity.class);
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
    View.OnClickListener Test = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this,TestActivity.class);
            startActivity(intent);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (flag && (keyCode == KeyEvent.KEYCODE_BACK)) {
            Toast.makeText(this, "按下了back键   onKeyDown()", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

}
