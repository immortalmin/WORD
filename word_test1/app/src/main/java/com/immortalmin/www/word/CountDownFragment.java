package com.immortalmin.www.word;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.content.Context.AUDIO_SERVICE;

public class CountDownFragment extends Fragment implements View.OnClickListener{
    private final static String TAG = "CountDownFragment";
    private OnFragmentInteractionListener mListener;
    private String word_en,word_ch,mode;
    private Button acquaint,vague,strange;
    private CountDownProgressBar cpb_countdown;
    private Boolean isCountdownfinish=false,pron_flag=true,living_flag=true;//pron_flag:是否播放音频,living_flag:按钮是否激活
    private MediaPlayerUtil mediaPlayerUtil;
    private AudioManager audioManager;//音量调整器
    private int changed_volume=0;//通过点击单词调整的音量
    private SoundPool soundPool;
    private int sound_acquaint,sound_vague,sound_unknown;
    private Runnable music_delay;
    private int user_sel;
    ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);
    private int duration,minDuration = 3000;//倒计时的时间

    /**
     * Activity绑定上Fragment时，调用该方法
     * 这个是第一次被调用的
     * @param context
     */
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mediaPlayerUtil = new MediaPlayerUtil(context);
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
        return inflater.inflate(R.layout.activity_countdown,null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        cpb_countdown = getActivity().findViewById(R.id.cpb_countdown);
        acquaint = getActivity().findViewById(R.id.acquaint);
        vague = getActivity().findViewById(R.id.vague);
        strange = getActivity().findViewById(R.id.strange);
        cpb_countdown.setOnClickListener(this);
        acquaint.setOnClickListener(this);
        vague.setOnClickListener(this);
        strange.setOnClickListener(this);
        cpb_countdown.setCenterTextColor(Color.BLACK);
        //music
        audioManager =   (AudioManager) getActivity().getSystemService(AUDIO_SERVICE);
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        sound_acquaint = soundPool.load(getActivity(), R.raw.bubble1, 1);
        sound_vague = soundPool.load(getActivity(), R.raw.bubble2, 1);
        sound_unknown = soundPool.load(getActivity(), R.raw.bubble3, 1);
        //点击按钮后延迟跳转
        music_delay = () -> mHandler.obtainMessage(0).sendToTarget();
    }

    public interface OnFragmentInteractionListener {
        void countdownonFragmentInteraction(HashMap<String,Object> res);
    }

    private void display_pro(){
        isCountdownfinish = true;
        if(pron_flag){
            mediaPlayerUtil.start();
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 0:
                    send_to_activity(user_sel);
                    break;
                case 1:
                    countdown_mode();
                    break;
            }
            return false;
        }
    });

    /**
     * 选项按钮点击事件
     * @param view
     */
    public void onClick(View view){
        //judge whether button is clicking or music is playing
        if(!living_flag){
            return ;
        }
        living_flag = false;
        switch(view.getId()){
            case R.id.cpb_countdown:
                if(isCountdownfinish){
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
                    changed_volume++;
                    mediaPlayerUtil.start();
                }else{
                    cpb_countdown.finishProgressBar();
                }
                living_flag = true;
                break;
            case R.id.acquaint:
                resetVolume();
                if(!isCountdownfinish){
                    cpb_countdown.finishProgressBar();
                }
                soundPool.play(sound_acquaint, 0.3f, 0.3f, 0, 0, 1.0f);
                user_sel = 1;
                //如果已经发过音的，只延迟1000毫秒就跳转
                //没发过音的，延迟单词音频的时间再跳转
                if(pron_flag){
                    scheduledThreadPool.schedule(music_delay,duration, TimeUnit.MILLISECONDS);
                }else{
                    scheduledThreadPool.schedule(music_delay,1000, TimeUnit.MILLISECONDS);
                }

                break;
            case R.id.vague:
                resetVolume();
                if(!isCountdownfinish){
                    pron_flag=false;
                    cpb_countdown.finishProgressBar();
                }
                soundPool.play(sound_vague, 0.3f, 0.3f, 0, 0, 1.0f);
                user_sel = 2;
                mediaPlayerUtil.start();
                scheduledThreadPool.schedule(music_delay,duration, TimeUnit.MILLISECONDS);
                break;
            case R.id.strange:
                resetVolume();
                if(!isCountdownfinish){
                    pron_flag=false;
                    cpb_countdown.finishProgressBar();
                }
                soundPool.play(sound_unknown, 0.3f, 0.3f, 0, 0, 1.0f);
                user_sel = 3;
                scheduledThreadPool.schedule(music_delay,1000, TimeUnit.MILLISECONDS);
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
        mediaPlayerUtil.stop();
        if (mListener != null) {
            HashMap<String,Object> s = new HashMap<String,Object>();
            switch (res){
                case 1://认识
                    s.put("judge",1);
                    break;
                case 2://模糊
                    s.put("judge",2);
                    break;
                case 3://不认识
                    s.put("judge",3);
                    break;
            }
            mListener.countdownonFragmentInteraction(s);
        }
    }
    //String new_word
    public void update_options(HashMap<String,Object> words){
        changed_volume = 0;
        isCountdownfinish = false;
        pron_flag=true;
        living_flag = true;
        mode = words.get("mode").toString();
        word_en = words.get("word_en").toString();
        word_ch = words.get("word_ch").toString();
        mediaPlayerUtil.setFinishListener(() -> {
            mHandler.sendEmptyMessage(1);
        });
        switch (mode){
            case "1": case "3":
                mediaPlayerUtil.reset(word_en,true);
                break;
            case "2":
                mediaPlayerUtil.reset(word_en,false);
                break;
        }
    }

    private void countdown_mode(){
        duration = mediaPlayerUtil.getDuration();
        switch (mode){
            case "1"://play music
                cpb_countdown.setDuration(Math.max(duration,minDuration),"Guess who I am",word_en,word_ch, this::display_pro);
                pron_flag = false;//因为一轮只需要发一次音，既然开始要发音，最后就不需要发音了
                break;
            case "2"://show word_ch
                cpb_countdown.setDuration(Math.max(duration,minDuration),word_ch,word_en,word_ch, this::display_pro);
                break;
            case "3"://show word_en
                cpb_countdown.setDuration(Math.max(duration,minDuration),word_en,word_en,word_ch, this::display_pro);
                pron_flag = false;
                break;
        }
    }

}
