package com.immortalmin.www.word;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

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
    private CircleImageView profile_photo;
    private ImageUtils imageUtils = new ImageUtils();
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
        profile_photo = (CircleImageView) findViewById(R.id.profile_photo);
        btn_wordlist.setOnClickListener(this);
        btn_recite.setOnClickListener(this);
        btn_test.setOnClickListener(this);
        btn_spell.setOnClickListener(this);
//        search1.setOnClickListener(this);
        search_bar.setOnClickListener(this);
        profile_photo.setOnClickListener(this);

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

        //广播关闭
        CloseActivityReceiver closeReceiver = new CloseActivityReceiver();
        IntentFilter intentFilter = new IntentFilter("com.immortalmin.www.MainActivity");
        registerReceiver(closeReceiver, intentFilter);

        init();
    }

    private void init() {
        SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        setImage(sp.getString("profile_photo",null));
    }
    private void setImage(String pic) {
        Bitmap bitmap=imageUtils.getPhotoFromStorage(pic);
        if(bitmap==null){
            Log.i("ccc","照片不存在 正从服务器下载...");
            getImage(pic);
        }else{
            Log.i("ccc","照片存在");
            mHandler.obtainMessage(0,bitmap).sendToTarget();
        }
    }

    private void getImage(final String pic){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                Bitmap bitmap = httpGetContext.HttpclientGetImg("http://47.98.239.237/word/img/"+pic);
                imageUtils.savePhotoToStorage(bitmap,pic);
                mHandler.obtainMessage(0,bitmap).sendToTarget();
            }
        }).start();
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 0:
                    profile_photo.setImageBitmap((Bitmap)message.obj);
                    break;
            }
            return false;
        }
    });

    /**
     * 实现Activity的广播接收
     * @author LCry
     */
    public class CloseActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            MainActivity.this.finish();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_wordlist:
                intent = new Intent(MainActivity.this,word1Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
            case R.id.btn_spell:

                break;
            case R.id.btn_recite:
                intent = new Intent(MainActivity.this,ReciteWordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
            case R.id.btn_test:
                intent = new Intent(MainActivity.this,EnterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
            case R.id.search_bar:
                intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
            case R.id.profile_photo:
                intent = new Intent(MainActivity.this,SettingActivity.class);
                startActivityForResult(intent,1);
                overridePendingTransition(R.anim.slide_left_in,R.anim.slide_to_right);
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            init();
        }
    }

}
