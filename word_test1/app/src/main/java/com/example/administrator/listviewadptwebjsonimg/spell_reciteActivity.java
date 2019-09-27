package com.example.administrator.listviewadptwebjsonimg;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
    Runnable judge,clean_eword;
    private Context context;
    TextView cword,numInfo1,numInfo2;
    EditText eword;
    JsonRe  jsonRe;
    List<Map<String,Object>> spell_list=null;
    //    int r_id,r_correct_times,r_error_times,r_pro
    int spell_num = 20;//今天要背的单词数
    int finish_num = 0;//今天背完的单词数
    int word_index = -1;//当前单词的下标
    int[] finish_ind = new int[1000];//用于标记是否该单词是否还需要背
    String word_info_url="http://192.168.57.1/word/querybyid.php?id=";
    String spell_list_url="http://192.168.57.1/word/getrecitelist.php?mount=";
    String update_url="http://192.168.57.1/word/update_recite.php?";//http://192.168.57.1/word/update_recite.php?id=1&correct_times=1&error_times=1&prof_flag=1
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spell_recite);
        context = this;
        cword = (TextView)findViewById(R.id.cword);
        numInfo1 = (TextView)findViewById(R.id.numInfo1);
        numInfo2 = (TextView)findViewById(R.id.numInfo2);
        eword = (EditText) findViewById(R.id.eword);
        Arrays.fill(finish_ind,0);
        eword.setOnEditorActionListener(ewordEd);
        jsonRe=new JsonRe();
        getspelllist();
        judge = new Runnable() {
            @Override
            public void run() {
                finish_ind[word_index]=1;
                eword.setText("");
                start_spell();
            }
        };
        clean_eword = new Runnable() {
            @Override
            public void run() {
                eword.setText("");
            }
        };
    }
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
                    finish_num++;
                    word.put("correct_times",correct_times+1);
                    showMyToast(Toast.makeText(context,"回答正确",Toast.LENGTH_LONG),500);
                    scheduledThreadPool.schedule(judge,500, TimeUnit.MILLISECONDS);
                }else{
                    word.put("error_times",error_times+1);
                    showMyToast(Toast.makeText(context,"回答错误",Toast.LENGTH_LONG),300);
                    scheduledThreadPool.schedule(clean_eword,700, TimeUnit.MILLISECONDS);
                }
                spell_list.set(word_index,word);
//                Log.i("spell_list",spell_list.toString());
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
//                Log.i("spell_list",spell_list.toString());
            }
        }).start();
    }
    public void start_spell(){
        word_index++;
        while(true){
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
            }
        }
    };
}
