package com.example.administrator.listviewadptwebjsonimg;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.renderscript.Sampler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class spell_reciteActivity extends AppCompatActivity {

    ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);
    Runnable correct_action,worry_action,spell_update;
    private Context context;
    private SoundPool soundPool;
    private int sound_success,sound_fail;
    TextView cword,numInfo1,numInfo2;
    EditText eword;
    JsonRe  jsonRe;
    List<Map<String,Object>> spell_list=null;
    Map<String,Object> update_word = new HashMap<String, Object>();
    int spell_num = 20;//今天要背的单词数
    int finish_num = 0;//今天背完的单词数
    int word_index = -1;//当前单词的下标
    Boolean once_flag = true;//是否第一次就拼写正确
    int[] finish_ind = new int[1000];//用于标记是否该单词是否还需要背
//    String spell_list_url="http://192.168.57.1/word/getrecitelist.php?mount=";
    String spell_list_url="http://47.98.239.237/word/getrecitelist.php?mount=";
//    String update_url="http://192.168.57.1/word/update_recite.php?";//http://192.168.57.1/word/update_recite.php?id=1&correct_times=1&error_times=1&prof_flag=1
    String update_url="http://47.98.239.237/word/update_recite.php?";//http://192.168.57.1/word/update_recite.php?id=1&correct_times=1&error_times=1&prof_flag=1
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spell_recite);
        context = this;
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        sound_success = soundPool.load(this, R.raw.success, 1);
        sound_fail = soundPool.load(this, R.raw.fail, 1);
        cword = (TextView)findViewById(R.id.cword);
        numInfo1 = (TextView)findViewById(R.id.numInfo1);
        numInfo2 = (TextView)findViewById(R.id.numInfo2);
        eword = (EditText) findViewById(R.id.eword);
        mHandler.obtainMessage(2).sendToTarget();//清空内容
        Arrays.fill(finish_ind,0);
        eword.setOnEditorActionListener(ewordEd);
        jsonRe=new JsonRe();
        getspelllist();
        /**
         * 答案正确执行的操作
         */
        correct_action = new Runnable() {
            @Override
            public void run() {
                eword.setText("");
                if(finish_num == spell_num){
                    update_recite_date();
                    Intent intent = new Intent(spell_reciteActivity.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    start_spell();
                }
            }
        };
        /**
         * 答案错误执行的操作
         */
        worry_action = new Runnable() {
            @Override
            public void run() {
                jump_to_example(spell_list.get(word_index).get("id").toString());
                eword.setText("");
            }
        };
        /**
         * 更新数据库的线程
         */
        spell_update = new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext=new HttpGetContext();
                String id = update_word.get("id").toString();
                String correct_times = update_word.get("correct_times").toString();
                String error_times = update_word.get("error_times").toString();
                String prof_flag = update_word.get("prof_flag").toString();
                httpGetContext.httpclientgettext(update_url+"id="+id+"&correct_times="+correct_times+"&error_times="+error_times+"&prof_flag="+prof_flag);
            }
        };
    }

    /**
     * 输入框回车监听事件
     * 判断答案的对错
     */
    TextView.OnEditorActionListener ewordEd = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if ((keyEvent != null && KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode() && KeyEvent.ACTION_DOWN == keyEvent.getAction())) {
                String ans = eword.getText().toString();
                String correct_ans = spell_list.get(word_index).get("word_group").toString();
                Map<String,Object> word = spell_list.get(word_index);
                int correct_times = Integer.valueOf(word.get("correct_times").toString());
                int error_times = Integer.valueOf(word.get("error_times").toString());
                if(ans.equals(correct_ans)){
                    soundPool.play(sound_success, 1.0f, 1.0f, 0, 0, 1.0f);
                    if(once_flag){
                        finish_ind[word_index]=1;
                        finish_num++;
                        word.put("correct_times",correct_times+1);
                    }
                    once_flag = true;
                    showMyToast(Toast.makeText(context,"回答正确",Toast.LENGTH_LONG),500);
                    scheduledThreadPool.schedule(correct_action,500, TimeUnit.MILLISECONDS);
                }else{
                    soundPool.play(sound_fail, 1.0f, 1.0f, 0, 0, 1.0f);
                    once_flag = false;
                    word.put("error_times",error_times+1);
                    showMyToast(Toast.makeText(context,"回答错误",Toast.LENGTH_LONG),300);
                    scheduledThreadPool.schedule(worry_action,700, TimeUnit.MILLISECONDS);
                }
                spell_list.set(word_index,word);
                return true;
            }
            return false;
        }
    };
    /**
     * 自定义Toast时间方法
     * */
    public void showMyToast(final Toast toast, final int cnt) {
        final Timer timer =new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        },0,3000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, cnt );
    }
    /**
     * 获取今天要背的单词列表
     */
    private void getspelllist()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext=new HttpGetContext();
                String wordlistjson=httpGetContext.httpclientgettext(spell_list_url+String.valueOf(spell_num));
                spell_list=jsonRe.getReciteList(wordlistjson);
                start_spell();
            }
        }).start();
    }

    /**
     * 开始拼写
     */
    public void start_spell(){
        word_index++;
        while(true){
            if(word_index == spell_num){
                word_index = 0;
            }
            if(finish_ind[word_index]==0){
                break;
            }
            word_index++;

        }
        String c_word = spell_list.get(word_index).get("C_meaning").toString();
        mHandler.obtainMessage(0,c_word).sendToTarget();
    }
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what==0){
                numInfo1.setText(String.valueOf(word_index+1)+"/"+String.valueOf(spell_num));
                numInfo2.setText(String.valueOf(finish_num)+"/"+String.valueOf(spell_num));
                cword.setText(msg.obj.toString());
            }else if(msg.what==1){
                Toast.makeText(context,msg.obj.toString(),Toast.LENGTH_LONG).show();
            }else if(msg.what==2){//清空所有内容
                numInfo1.setText("");
                numInfo2.setText("");
                cword.setText("");
            }
        }
    };

    /**
     * 跳转到例句
     * @param id
     */
    public void jump_to_example(String id){
        Intent intent = new Intent(spell_reciteActivity.this, ExampleActivity.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    /**
     * 更新数据库
     */
    public void update_recite_date(){
        for(int i=0;i<spell_list.size();i++){
            update_word.put("id",spell_list.get(i).get("id").toString());
            update_word.put("correct_times",spell_list.get(i).get("correct_times").toString());
            update_word.put("error_times",spell_list.get(i).get("error_times").toString());
            update_word.put("prof_flag",spell_list.get(i).get("prof_flag").toString());
            scheduledThreadPool.schedule(spell_update,0, TimeUnit.MILLISECONDS);
        }
        Log.i("update","更新数据库完成");
    }
}
