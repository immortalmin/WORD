package com.immortalmin.www.word;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class SpellFragment extends Fragment implements View.OnClickListener{
    private final static String TAG = "SpellFragment";
    private OnFragmentInteractionListener mListener;
    private String word_group,C_meaning,mode,user_ans;
    private CountDownProgressBar cpb_countdown;
    private MediaPlayer mediaPlayer;
    private SoundPool soundPool;
    private int sound_success,sound_fail;
    private Runnable music_delay,correct_action,wrong_action;
    private TextView cword,correct_word;//display C_meaning
    private EditText eword;//spell word_group
    private Boolean btn_flag = true;//avoid double click
    private Boolean once_flag=true;
    private int judge_flag=1;
    ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);

    /**
     * Activity绑定上Fragment时，调用该方法
     * 这个是第一次被调用的
     * @param context
     */
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Log.d(TAG,"onAttach");
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Fragment显示的内容是怎样的，就是通过下面这个方法返回回去的(view)
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_spell,null);
        Log.d(TAG,"onCreateView");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        cword = (TextView)getActivity().findViewById(R.id.cword);
        correct_word = (TextView)getActivity().findViewById(R.id.correct_word);
        eword = (EditText) getActivity().findViewById(R.id.eword);
        cword.setOnClickListener(this);
        eword.setOnEditorActionListener(ewordEd);
        /**
         * 接受来自activity的数据
         */
        Bundle bundle = getArguments();
//        mode = bundle.getString("mode");
        word_group = bundle.getString("word_group");
        C_meaning = bundle.getString("C_meaning");
        once_flag = bundle.getBoolean("once_flag");
        mHandler.obtainMessage(2).sendToTarget();
        showInput(eword);
        //music
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        sound_success = soundPool.load(getActivity(), R.raw.success, 1);
        sound_fail = soundPool.load(getActivity(), R.raw.fail, 1);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
            }
        });

        music_delay = new Runnable() {
            @Override
            public void run() {
                mHandler.obtainMessage(3).sendToTarget();
            }
        };
        /**
         * 答案正确执行的操作
         */
        correct_action = new Runnable() {
            @Override
            public void run() {
                mHandler.obtainMessage(0).sendToTarget();
            }
        };
        /**
         * 答案错误执行的操作
         */
        wrong_action = new Runnable() {
            @Override
            public void run() {
                mHandler.obtainMessage(1).sendToTarget();
            }
        };
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void spellFragmentInteraction(HashMap<String, Object> res);
    }

    /**
     * 输入框回车监听事件
     * 判断答案的对错
     */
    TextView.OnEditorActionListener ewordEd = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (btn_flag && keyEvent != null && KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode() && KeyEvent.ACTION_DOWN == keyEvent.getAction()) {
                btn_flag = false;
                eword.setEnabled(false);
                mediaPlayer = new MediaPlayer();
                initMediaPlayer(word_group,0);//音频初始化
                mediaPlayer.start();
                user_ans = eword.getText().toString().replaceAll(" ","");
                String co_word = word_group.replaceAll(" ","");
                if(co_word.equals(user_ans)){
                    judge_flag = 1;
                    soundPool.play(sound_success, 0.3f, 0.3f, 0, 0, 1.0f);
                    mHandler.obtainMessage(0).sendToTarget();
                }else{
                    once_flag = false;
                    judge_flag = 2;
                    soundPool.play(sound_fail, 0.3f, 0.3f, 0, 0, 1.0f);
                    mHandler.obtainMessage(1).sendToTarget();
                }
                scheduledThreadPool.schedule(music_delay,1500, TimeUnit.MILLISECONDS);
                return true;
            }
            return false;
        }
    };

    /**
     * 音频播放
     * @param word
     * @param what
     */
    private void initMediaPlayer(String word,int what) {
        try {
            if(what == 0){
                //modify type to change pronunciation between US and UK
                mediaPlayer.setDataSource("http://dict.youdao.com/dictvoice?type=1&audio="+ URLEncoder.encode(word));
            }else if(what == 1){
                mediaPlayer.setDataSource(word);
            }
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0://correct
                    eword.setTextColor(Color.parseColor("#05f725"));
                    break;
                case 1://wrong
                    eword.setTextColor(Color.parseColor("#ed0707"));
                    correct_word.setText(word_group);
                    correct_word.setVisibility(View.VISIBLE);
                    break;
                case 2://set c_word
                    cword.setText(C_meaning);
                    break;
                case 3://go back to recite_word_activity
                    send_to_activity(judge_flag);
                    break;
                case 4://reset
                    eword.setText("");
                    eword.setTextColor(Color.parseColor("#000000"));
                    correct_word.setVisibility(View.INVISIBLE);
            }
        }
    };

    /**
     * 选项按钮点击事件
     * @param view
     */
    public void onClick(View view){
        switch(view.getId()){
            case R.id.cword:
                if(!mediaPlayer.isPlaying()){
                    mediaPlayer = new MediaPlayer();
                    initMediaPlayer(word_group,0);//音频初始化
                    mediaPlayer.start();
                }
                break;

        }
    }

    /**
     * 向activity回送数据
     */
    public void send_to_activity(int res){
        if (mListener != null) {
            mHandler.obtainMessage(4).sendToTarget();
            HashMap<String,Object> s = new HashMap<String,Object>();
            switch (res){
                case 1://correct
                    s.put("judge",1);
                    break;
                case 2://wrong
                    s.put("judge",2);
                    break;
            }
            s.put("once_flag",once_flag);
            mListener.spellFragmentInteraction(s);
        }
    }
    //String new_word
    public void update_options(HashMap<String,Object> words){
        btn_flag = true;
        eword.setEnabled(true);
//        mode = words.get("mode").toString();
        word_group = words.get("word_group").toString();
        C_meaning = words.get("C_meaning").toString();
        once_flag = Boolean.valueOf(words.get("once_flag").toString());
//        Log.i("once_flag",once_flag.toString());
        mHandler.obtainMessage(2).sendToTarget();
        showInput(eword);
    }

    /**
     * 显示键盘
     *
     * @param et 输入焦点
     */
    public void showInput(final EditText et) {
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

}