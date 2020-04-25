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

public class SelectFragment extends Fragment implements View.OnClickListener{
    private final static String TAG = "SelectFragment";
    private OnFragmentInteractionListener mListener;
    private MediaPlayer mediaPlayer = new MediaPlayer();
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
        sound_success = soundPool.load(getActivity(), R.raw.success, 1);
        sound_fail = soundPool.load(getActivity(), R.raw.fail, 1);
        resetColor = new Runnable(){
            public void run(){
                mHandler.obtainMessage(1).sendToTarget();
//                progressBar.setProgress(finish_num/recite_num);
//                progressBar.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        int pro_num = finish_num*100/recite_num;
//                        progressBar.setProgress(pro_num);
//                    }
//                });
//
//                if(finish_num>=recite_num){
//                    //设置按钮不可用
//                    sel1.setClickable(false);
//                    sel2.setClickable(false);
//                    sel3.setClickable(false);
//                    sel4.setClickable(false);
//                    update_sql_data();
//                }else if(!pron_lock){
//                    recite();
//                }
            }
        };
        /**
         * 接收来自activity的数据(first time)
         */
//        Bundle bundle = getArguments();
//        word_list.put("wordview",bundle.getString("wordview"));
//        word_list.put("sel1",bundle.getString("sel1"));
//        word_list.put("sel2",bundle.getString("sel2"));
//        word_list.put("sel3",bundle.getString("sel3"));
//        word_list.put("sel4",bundle.getString("sel4"));
//        word_list.put("today_correct_times",bundle.getString("today_correct_times"));
//        word_list.put("c_times",bundle.getString("c_times"));
//        correct_sel = Integer.valueOf(bundle.getString("correct_sel"));
//
//        mediaPlayer = new MediaPlayer();
//        initMediaPlayer(bundle.getString("wordview"),0);//音频初始化
//        mediaPlayer.start();
//
//        mHandler.obtainMessage(0).sendToTarget();


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
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0);
                }
                mediaPlayer.start();
                break;
        }
    }

    public void judge_ring(){
        if(user_sel==correct_sel){
            word_times_pro.post(new Runnable() {
                @Override
                public void run() {
                    int pro_num = (Integer.valueOf(word_list.get("today_correct_times").toString())+1)*10/Integer.valueOf(word_list.get("c_times").toString());
                    word_times_pro.setProgress(pro_num);
                }
            });
            soundPool.play(sound_success, 0.3f, 0.3f, 0, 0, 1.0f);
        }else{
            soundPool.play(sound_fail, 0.3f, 0.3f, 0, 0, 1.0f);
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

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 0:
                    wordview.setText(word_list.get("wordview").toString());
                    sel1.setText(word_list.get("sel1").toString());
                    sel2.setText(word_list.get("sel2").toString());
                    sel3.setText(word_list.get("sel3").toString());
                    sel4.setText(word_list.get("sel4").toString());
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
        living_flag = true;//激活按钮
        correct_sel = Integer.valueOf(words.get("correct_sel").toString());
        this.word_list = words;
        this.mediaPlayer = (MediaPlayer)words.get("media_player");
        mediaPlayer.start();
        mHandler.obtainMessage(0).sendToTarget();
    }

}
