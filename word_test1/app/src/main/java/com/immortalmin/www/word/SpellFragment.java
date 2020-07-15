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

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.INPUT_METHOD_SERVICE;

public class SpellFragment extends Fragment implements View.OnClickListener{
    private final static String TAG = "SpellFragment";
    private OnFragmentInteractionListener mListener;
    private String word_en,word_ch,mode,user_ans;
    private CountDownProgressBar cpb_countdown;
    private MediaPlayer mediaPlayer=new MediaPlayer();
    private AudioManager audioManager;//音量调整器
    private int changed_volume=0;//通过点击单词调整的音量
    private SoundPool soundPool;
    private int sound_success,sound_fail;
    private Runnable music_delay,correct_action,wrong_action;
    private TextView cword,correct_word;//display word_ch
    private EditText eword;//spell word_en
    private Button finish_btn;
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
        finish_btn = (Button) getActivity().findViewById(R.id.finish_btn);
        cword.setOnClickListener(this);
        eword.setOnEditorActionListener(ewordEd);
        /**
         * 接受来自activity的数据
         */
//        Bundle bundle = getArguments();
//        word_en = bundle.getString("word_en");
//        word_ch = bundle.getString("word_ch");
//        once_flag = bundle.getBoolean("once_flag");
//        mHandler.obtainMessage(2).sendToTarget();
//        showInput(eword);
        //music
        audioManager =   (AudioManager) getActivity().getSystemService(AUDIO_SERVICE);
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
//        sound_success = soundPool.load(getActivity(), R.raw.success, 1);
//        sound_fail = soundPool.load(getActivity(), R.raw.fail, 1);
        sound_success = soundPool.load(getActivity(), R.raw.bubble, 1);
        sound_fail = soundPool.load(getActivity(), R.raw.drums, 1);

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

//        finish_btn.requestLayout();
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
                resetVolume();
                btn_flag = false;
                eword.setEnabled(false);
                mediaPlayer.start();
                user_ans = eword.getText().toString().replaceAll(" ","");
                String co_word = word_en.replaceAll(" ","");
                if(co_word.equals(user_ans)){
                    judge_flag = 1;
                    soundPool.play(sound_success, 1.0f, 1.0f, 0, 0, 1.0f);
                    mHandler.obtainMessage(0).sendToTarget();
                }else{
                    once_flag = false;
                    judge_flag = 2;
                    soundPool.play(sound_fail, 1.0f, 1.0f, 0, 0, 1.0f);
                    mHandler.obtainMessage(1).sendToTarget();
                }
                scheduledThreadPool.schedule(music_delay,mediaPlayer.getDuration()+200, TimeUnit.MILLISECONDS);
                return true;
            }
            return false;
        }
    };

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 0://correct
                    eword.setTextColor(Color.parseColor("#05f725"));
                    break;
                case 1://wrong
                    eword.setTextColor(Color.parseColor("#ed0707"));
                    correct_word.setText(word_en);
                    correct_word.setVisibility(View.VISIBLE);
                    break;
                case 2://set c_word
                    cword.setText(word_ch);
                    break;
                case 3://go back to recite_word_activity
                    send_to_activity(judge_flag);
                    break;
                case 4://reset
                    eword.setText("");
                    eword.setTextColor(Color.parseColor("#000000"));
                    correct_word.setVisibility(View.INVISIBLE);
            }
            return false;
        }
    });

    /**
     * 选项按钮点击事件
     * @param view
     */
    public void onClick(View view){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
        }
        switch(view.getId()){
            case R.id.cword:
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
                changed_volume++;
                mediaPlayer.start();
                break;

        }
    }

    /**
     * 重置音量
     */
    private void resetVolume(){
        int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);//得到听筒模式的当前值
        //当前音量-通过点击单词调整的音量
        //不减去用户手动调整的音量
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume-changed_volume, AudioManager.FLAG_PLAY_SOUND);
        changed_volume=0;
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
        changed_volume = 0;
        btn_flag = true;
        eword.setEnabled(true);
//        mode = words.get("mode").toString();
        word_en = words.get("word_en").toString();
        word_ch = words.get("word_ch").toString();
        once_flag = Boolean.valueOf(words.get("once_flag").toString());
        this.mediaPlayer = (MediaPlayer)words.get("media_player");
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
