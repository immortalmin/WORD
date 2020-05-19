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

public class CountDownFragment extends Fragment implements View.OnClickListener{
    private final static String TAG = "CountDownFragment";
    private OnFragmentInteractionListener mListener;
    private String word_en,word_ch,mode;
    private Button acquaint,vague,strange;
    private CountDownProgressBar cpb_countdown;
    private Boolean isCountdownfinish=false,pron_flag=true,living_flag=true;//pron_flag:是否播放音频,living_flag:按钮是否激活
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private SoundPool soundPool;
    private int sound_acquaint,sound_vague,sound_unknown;
    private Runnable music_delay;
    private int user_sel;
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
        View view = inflater.inflate(R.layout.activity_countdown,null);
        Log.d(TAG,"onCreateView");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        cpb_countdown = (CountDownProgressBar) getActivity().findViewById(R.id.cpb_countdown);
        acquaint = (Button)getActivity().findViewById(R.id.acquaint);
        vague = (Button)getActivity().findViewById(R.id.vague);
        strange = (Button)getActivity().findViewById(R.id.strange);
        cpb_countdown.setOnClickListener(this);
        acquaint.setOnClickListener(this);
        vague.setOnClickListener(this);
        strange.setOnClickListener(this);
        cpb_countdown.setCenterTextColor(Color.BLACK);
        //music
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        sound_acquaint = soundPool.load(getActivity(), R.raw.bubble1, 1);
        sound_vague = soundPool.load(getActivity(), R.raw.bubble2, 1);
        sound_unknown = soundPool.load(getActivity(), R.raw.bubble3, 1);
        music_delay = new Runnable() {
            @Override
            public void run() {
                mHandler.obtainMessage(0).sendToTarget();
            }
        };
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void countdownonFragmentInteraction(HashMap<String,Object> res);
    }

    private void display_pro(){
        isCountdownfinish = true;
        if(pron_flag){
            mediaPlayer.start();
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 0:
                    send_to_activity(user_sel);
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
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
        }
        living_flag = false;
        switch(view.getId()){
            case R.id.cpb_countdown:
                if(isCountdownfinish){
                    mediaPlayer.start();
                }else{
                    cpb_countdown.finishProgressBar();
                }
                living_flag = true;
                break;
            case R.id.acquaint:
                if(!isCountdownfinish){
                    cpb_countdown.finishProgressBar();
                }
                soundPool.play(sound_acquaint, 0.3f, 0.3f, 0, 0, 1.0f);
                user_sel = 1;
                scheduledThreadPool.schedule(music_delay,1000, TimeUnit.MILLISECONDS);
                break;
            case R.id.vague:
                if(!isCountdownfinish){
                    pron_flag=false;
                    cpb_countdown.finishProgressBar();
                }
                soundPool.play(sound_vague, 0.3f, 0.3f, 0, 0, 1.0f);
                user_sel = 2;
                scheduledThreadPool.schedule(music_delay,1000, TimeUnit.MILLISECONDS);
                break;
            case R.id.strange:
                if(!isCountdownfinish){
                    pron_flag=false;
                    cpb_countdown.finishProgressBar();
                }
                soundPool.play(sound_unknown, 0.3f, 0.3f, 0, 0, 1.0f);
                user_sel = 3;
                scheduledThreadPool.schedule(music_delay,1000, TimeUnit.MILLISECONDS);
                break;
        }
//        send_to_activity(res);
    }

    /**
     * 向activity回送数据
     */
    public void send_to_activity(int res){
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
//            a.add(s);
            mListener.countdownonFragmentInteraction(s);
        }
    }
    //String new_word
    public void update_options(HashMap<String,Object> words){
        isCountdownfinish = false;
        pron_flag=true;
        living_flag = true;
        mode = words.get("mode").toString();
        word_en = words.get("word_en").toString();
        word_ch = words.get("word_ch").toString();
        this.mediaPlayer = (MediaPlayer)words.get("media_player");
        countdown_mode();
    }

    private void countdown_mode(){
        switch (mode){
            case "1"://play music
                cpb_countdown.setDuration(3000,"Guess who I am",word_en, new CountDownProgressBar.OnFinishListener() {
                    @Override
                    public void onFinish() {
                        display_pro();
                    }
                });
                mediaPlayer.start();
                break;
            case "2"://show word_ch
                cpb_countdown.setDuration(3000,word_ch,word_en, new CountDownProgressBar.OnFinishListener() {
                    @Override
                    public void onFinish() {
                        display_pro();
                    }
                });
                break;
            case "3"://show word_en
                cpb_countdown.setDuration(3000,word_en,word_ch, new CountDownProgressBar.OnFinishListener() {
                    @Override
                    public void onFinish() {
                        display_pro();
                    }
                });
                mediaPlayer.start();
                break;
        }
    }

}
