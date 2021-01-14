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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.content.Context.AUDIO_SERVICE;

public class SelectFragment extends Fragment implements View.OnClickListener{
    private final static String TAG = "SelectFragment";
    private OnFragmentInteractionListener mListener;
    private MediaPlayerUtil mediaPlayerUtil;
    private AudioManager audioManager;//音量调整器
    private int changed_volume=0;//通过点击单词调整的音量
    private SoundPool soundPool;
    private int sound_success,sound_fail;
    Runnable resetColor;
//    private String word,res;
    private Button sel1,sel2,sel3,sel4,sel5;
    private TextView wordview;
    private ProgressBar word_times_pro;
    private HashMap<String, Object> word_list = new HashMap<String, Object>();
    Boolean living_flag=true;
    Boolean pron_lock = false;
    ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);
    private int correct_sel = 0;//正确答案的下标
    private int user_sel;
    private float word_volume = 10f;//音频音量

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
        View view = inflater.inflate(R.layout.activity_select,null);
        Log.d(TAG,"onCreateView");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        sel1 = (Button)getActivity().findViewById(R.id.sel1);
        sel2 = (Button)getActivity().findViewById(R.id.sel2);
        sel3 = (Button)getActivity().findViewById(R.id.sel3);
        sel4 = (Button)getActivity().findViewById(R.id.sel4);
        sel5 = (Button)getActivity().findViewById(R.id.sel5);
        wordview = (TextView) getActivity().findViewById(R.id.wordview);
        word_times_pro = (ProgressBar) getActivity().findViewById(R.id.word_times_pro);
        sel1.setOnClickListener(this);
        sel2.setOnClickListener(this);
        sel3.setOnClickListener(this);
        sel4.setOnClickListener(this);
        sel5.setOnClickListener(this);
        wordview.setOnClickListener(this);
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        audioManager =   (AudioManager) getActivity().getSystemService(AUDIO_SERVICE);
//        sound_success = soundPool.load(getActivity(), R.raw.success, 1);
//        sound_fail = soundPool.load(getActivity(), R.raw.fail, 1);
        sound_success = soundPool.load(getActivity(), R.raw.bubble, 1);
        sound_fail = soundPool.load(getActivity(), R.raw.drums, 1);
        resetColor = new Runnable(){
            public void run(){
                mHandler.obtainMessage(1).sendToTarget();
            }
        };
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void selectonFragmentInteraction(HashMap<String,Object> res);
    }

    /**
     * 选项按钮点击事件
     * @param view
     */
    public void onClick(View view){
        if(!living_flag){
            return ;
        }
        switch(view.getId()){
            case R.id.sel1:
                living_flag = false;
                user_sel=0;
                judge_ring();
                if(correct_sel == 0){
                    sel1.setBackgroundResource(R.drawable.rounded_corners_green);
                }else{
                    sel1.setBackgroundResource(R.drawable.rounded_corners_red);
                    correct_shine();
                }
                scheduledThreadPool.schedule(resetColor,500, TimeUnit.MILLISECONDS);
                break;
            case R.id.sel2:
                living_flag = false;
                user_sel=1;
                judge_ring();
                if(correct_sel == 1){
                    sel2.setBackgroundResource(R.drawable.rounded_corners_green);
                }else{
                    sel2.setBackgroundResource(R.drawable.rounded_corners_red);
                    correct_shine();
                }
                scheduledThreadPool.schedule(resetColor,500, TimeUnit.MILLISECONDS);
                break;
            case R.id.sel3:
                living_flag = false;
                user_sel=2;
                judge_ring();
                if(correct_sel == 2){
                    sel3.setBackgroundResource(R.drawable.rounded_corners_green);
                }else{
                    sel3.setBackgroundResource(R.drawable.rounded_corners_red);
                    correct_shine();
                }
                scheduledThreadPool.schedule(resetColor,500, TimeUnit.MILLISECONDS);
                break;
            case R.id.sel4:
                living_flag = false;
                user_sel=3;
                judge_ring();
                if(correct_sel == 3){
                    sel4.setBackgroundResource(R.drawable.rounded_corners_green);
                }else{
                    sel4.setBackgroundResource(R.drawable.rounded_corners_red);
                    correct_shine();
                }
                scheduledThreadPool.schedule(resetColor,500, TimeUnit.MILLISECONDS);
                break;
            case R.id.sel5:
                living_flag = false;
                user_sel=-1;
                judge_ring();
                correct_shine();
                scheduledThreadPool.schedule(resetColor,500, TimeUnit.MILLISECONDS);
                break;
            case R.id.wordview:
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
                changed_volume++;
                mediaPlayerUtil.start();
                break;
        }
    }

    public void judge_ring(){
        resetVolume();
        if(user_sel==correct_sel){
            word_times_pro.post(new Runnable() {
                @Override
                public void run() {
                    int pro_num = (Integer.valueOf(word_list.get("today_correct_times").toString())+1)*10/Integer.valueOf(word_list.get("c_times").toString());
                    word_times_pro.setProgress(pro_num);
                }
            });
            soundPool.play(sound_success, 1.0f, 1.0f, 0, 0, 1.0f);
        }else{
            soundPool.play(sound_fail, 1.0f, 1.0f, 0, 0, 1.0f);
        }

    }

    /**
     * 正确选项发绿
     */
    public void correct_shine(){
//        today_finish --;
        if(correct_sel == 0){
            sel1.setBackgroundResource(R.drawable.rounded_corners_green);
        }else if (correct_sel == 1){
            sel2.setBackgroundResource(R.drawable.rounded_corners_green);
        }else if (correct_sel == 2){
            sel3.setBackgroundResource(R.drawable.rounded_corners_green);
        }else{
            sel4.setBackgroundResource(R.drawable.rounded_corners_green);
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

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 0:
                    String wordviewString = word_list.get("wordview").toString();
                    String sel1String = word_list.get("sel1").toString();
                    String sel2String = word_list.get("sel2").toString();
                    String sel3String = word_list.get("sel3").toString();
                    String sel4String = word_list.get("sel4").toString();
                    wordview.setText(wordviewString.length()>=30?wordviewString.substring(0,25)+"...":wordviewString);
                    sel1.setText(sel1String.length()>=30?sel1String.substring(0,25)+"...":sel1String);
                    sel2.setText(sel2String.length()>=30?sel2String.substring(0,25)+"...":sel2String);
                    sel3.setText(sel3String.length()>=30?sel3String.substring(0,25)+"...":sel3String);
                    sel4.setText(sel4String.length()>=30?sel4String.substring(0,25)+"...":sel4String);
                    word_times_pro.post(new Runnable() {
                        @Override
                        public void run() {
                            int pro_num = Integer.valueOf(word_list.get("today_correct_times").toString())*10/Integer.valueOf(word_list.get("c_times").toString());
                            word_times_pro.setProgress(pro_num);
                        }
                    });
                    break;
                case 1:
                    sel1.setBackgroundResource(R.drawable.rounded_corners_gray);
                    sel2.setBackgroundResource(R.drawable.rounded_corners_gray);
                    sel3.setBackgroundResource(R.drawable.rounded_corners_gray);
                    sel4.setBackgroundResource(R.drawable.rounded_corners_gray);
                    send_to_activity(user_sel);
            }
            return false;
        }
    });

    /**
     * 向activity回送数据
     */
    private void send_to_activity(int ans){
        mediaPlayerUtil.stop();
        if (mListener != null) {
            HashMap<String,Object> res = new HashMap<String,Object>();
            if(ans==-1){//select unknown
                res.put("judge",3);
                res.put("correct_sel",correct_sel);
            }else if(ans==correct_sel){//correct
                res.put("judge",1);
                res.put("correct_sel",correct_sel);
            }else{//wrong
                res.put("judge",2);
                res.put("correct_sel",correct_sel);
                res.put("wrong_sel",ans);
            }
            mListener.selectonFragmentInteraction(res);

        }
    }
    public void update_options(HashMap<String,Object> words){
        changed_volume = 0;
        living_flag = true;//激活按钮
        correct_sel = Integer.valueOf(words.get("correct_sel").toString());
        this.word_list = words;
        mediaPlayerUtil.reset(words.get("wordview").toString(),true);
        mHandler.obtainMessage(0).sendToTarget();
    }
}
