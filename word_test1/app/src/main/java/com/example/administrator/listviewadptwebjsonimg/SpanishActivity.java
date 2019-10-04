package com.example.administrator.listviewadptwebjsonimg;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SpanishActivity extends AppCompatActivity {
    ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);
    Runnable judge,clean_eword;
    private Context context;
    TextView tv1,tv2,tv3;
    EditText et1;
    JsonRe  jsonRe;
    List<Map<String,Object>> spell_list=null;
    Map<String,Object> map1 = null;
    Map<String,Object> map2 = null;
//    Map<String,Object> to_mhander = null;
    //    int r_id,r_correct_times,r_error_times,r_pro
    int spell_num = 20;//今天要背的单词数
    int finish_num = 0;//今天背完的单词数
    int word_index = -1;//当前单词的下标
    int[] finish_ind = new int[1000];//用于标记是否该单词是否还需要背
    String correct_answer;
    String word_info_url="http://192.168.57.1/word/getjson.php";
    List<String> shitai = Arrays.asList("命令式-否定时","命令式-肯定时","复合条件式","简单条件式","虚拟式-将来完成时","虚拟式-将来时","虚拟式-现在完成时","虚拟式-现在时","虚拟式-过去完成时A","虚拟式-过去完成时B","虚拟式-过去未完成时A","虚拟式-过去未完成时B","陈述式-先过去时","陈述式-将来完成时","陈述式-将来时","陈述式-现在完成时","陈述式-现在时","陈述式-简单过去时","陈述式-过去完成时","陈述式-过去未完成时");
    List<String> shitaitwo = Arrays.asList("yo","tú","él","nos.","vos.","ellos.");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spanish);
        context = this;
        tv1 = (TextView)findViewById(R.id.tv1);
        tv2 = (TextView)findViewById(R.id.tv2);
        tv3 = (TextView)findViewById(R.id.tv3);
        et1 = (EditText) findViewById(R.id.et1);
        Arrays.fill(finish_ind,0);
        et1.setOnEditorActionListener(et1Ed);
        jsonRe=new JsonRe();
        getSpanishlist();
        judge = new Runnable() {
            @Override
            public void run() {
                et1.setText("");
                start_spell();
            }
        };
        clean_eword = new Runnable() {
            @Override
            public void run() {
                et1.setText("");
            }
        };
    }
    private void getSpanishlist(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext=new HttpGetContext();
                String wordlistjson=httpGetContext.httpclientgettext("http://192.168.57.1/word/getjson.php");
                spell_list = jsonRe.getSpanish(wordlistjson);
                start_spell();
            }
        }).start();
    }
    TextView.OnEditorActionListener et1Ed = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if ((keyEvent != null && KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode() && KeyEvent.ACTION_DOWN == keyEvent.getAction())) {
                String ans = et1.getText().toString();
                if(ans.equals(correct_answer)){
                    showmyToast(Toast.makeText(context,"回答正确",Toast.LENGTH_LONG),500);
                    scheduledThreadPool.schedule(judge,500, TimeUnit.MILLISECONDS);
                }else{
                    showmyToast(Toast.makeText(context,"回答错误",Toast.LENGTH_LONG),500);
                    scheduledThreadPool.schedule(clean_eword,700, TimeUnit.MILLISECONDS);
                }
                return true;
            }
            return false;
        }
    };
    public void showmyToast(final Toast toast, final int cnt) {
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

    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what==0){
                Map<String,Object> handle_map = (Map<String,Object>)msg.obj;
                tv1.setText(handle_map.get("tv1").toString());
                tv2.setText(handle_map.get("tv2").toString());
                tv3.setText(handle_map.get("tv3").toString());
            }else if(msg.what==1){

            }
        }
    };
    public void start_spell(){
        Log.i("start_spell","!!!!");
        int one = 0;
        int two = (int)(Math.random()*(20));
        int three = (int)(Math.random()*(6));
        map1 = (Map<String,Object>)spell_list.get(one).get("va_b");
        map2 = (Map<String,Object>)map1.get(shitai.get(two));
        correct_answer = map2.get(shitaitwo.get(three)).toString();
        Map<String,Object> to_mhander = new HashMap<String, Object>();
        to_mhander.put("tv1",spell_list.get(one).get("word").toString());
        to_mhander.put("tv2",shitai.get(two));
        to_mhander.put("tv3",shitaitwo.get(three));
        Log.i("correct_answer",correct_answer);
        mHandler.obtainMessage(0,to_mhander).sendToTarget();
    }
}
