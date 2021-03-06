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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

//XXX:如果回答正确，并且下一轮也是拼写，不要关闭键盘
public class SpellFragment extends Fragment implements View.OnClickListener{
    private final static String TAG = "SpellFragment";
    private OnFragmentInteractionListener mListener;
    private String word_en,word_ch;
    private MediaPlayerUtil mediaPlayerUtil;
    private AudioManager audioManager;//音量调整器
    private int changed_volume=0;//通过点击单词调整的音量
    private SoundPool soundPool;
    private int sound_success,sound_fail;
    private Runnable music_delay;
    private TextView cword,correct_word;//display word_ch
    private EditText eword;//spell word_en
    private Button clean_btn;
    private int WrongTimes=0;//拼写错误的次数
    private Boolean isTyping = true;//是否在等待用户输入
    private Boolean userAns = true;//用户回答是否正确
    private int duration;
    ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.activity_spell, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        cword = getActivity().findViewById(R.id.cword);
        correct_word = getActivity().findViewById(R.id.correct_word);
        eword = getActivity().findViewById(R.id.eword);
        clean_btn = getActivity().findViewById(R.id.clean_btn);
        cword.setOnClickListener(this);
        clean_btn.setOnClickListener(this);
        eword.setOnEditorActionListener(ewordEd);


        /**
         * 在拼写错误并显示答案后，键盘有输入就清除单词
         * 现在这样会先打出字，再清除，不美观
         * 暂时没有想到好一点的办法
         */
        eword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0&&isTyping){
                    mHandler.sendEmptyMessage(5);
                }else{
                    mHandler.sendEmptyMessage(6);
                }
                if(!isTyping&&!userAns){//拼写错误后用户又继续输入时
                    Message msg = new Message();
                    msg.what = 2;
                    //只保留下标前的一个字符，即刚输入的字符
                    msg.obj = s.charAt(eword.getSelectionStart()-1);
                    mHandler.sendMessage(msg);
                }
            }
        });
        //music
        audioManager =   (AudioManager) getActivity().getSystemService(AUDIO_SERVICE);
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
//        sound_success = soundPool.load(getActivity(), R.raw.success, 1);
//        sound_fail = soundPool.load(getActivity(), R.raw.fail, 1);
        sound_success = soundPool.load(getActivity(), R.raw.bubble, 1);
        sound_fail = soundPool.load(getActivity(), R.raw.drums, 1);

        /**
         * 让其播放完音频再进行后面的处理
         */
        music_delay = () -> mHandler.obtainMessage(3).sendToTarget();
    }
    public interface OnFragmentInteractionListener {
        void spellFragmentInteraction(int WrongTimes);
    }

    /**
     * 输入框回车监听事件
     * 判断答案的对错
     */
    TextView.OnEditorActionListener ewordEd = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if(isTyping){
                //点击回车，判断答案是否正确
                if (keyEvent != null && KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode() && KeyEvent.ACTION_DOWN == keyEvent.getAction()) {
                    isTyping = false;
                    resetVolume();
                    mediaPlayerUtil.start();
                    mHandler.sendEmptyMessage(6);
                    if(Comparison(eword.getText().toString(),word_en)){
                        userAns = true;
                        soundPool.play(sound_success, 1.0f, 1.0f, 0, 0, 1.0f);
                        mHandler.obtainMessage(0).sendToTarget();
                        eword.setEnabled(false);
                        scheduledThreadPool.schedule(music_delay,Math.max(duration+200,1000), TimeUnit.MILLISECONDS);
                    }else{
                        userAns = false;
                        WrongTimes++;
                        soundPool.play(sound_fail, 1.0f, 1.0f, 0, 0, 1.0f);
                        mHandler.sendEmptyMessage(1);
                    }
                    return true;
                }
            }else{
                //回答错误后用户点击了回车
                if(keyEvent != null && KeyEvent.ACTION_DOWN == keyEvent.getAction()){
                    mHandler.sendEmptyMessage(2);//重新显示题目
                    return true;
                }
            }
            return false;
        }
    };

    /**
     * 校对机制
     * 去除除英文字母以外所有的字符
     * 不分大小写
     * 希望实现 /两边的单词可以互换
     * @param s1
     * @param s2
     * @return
     */
    Boolean Comparison(String s1,String s2){
        s1 = s1.replaceAll("[^a-zA-Z]","").toLowerCase();
        s2 = s2.replaceAll("[^a-zA-Z]","").toLowerCase();
        if(s1.equals(s2)) return true;
        return false;
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 0://correct
                    eword.setText(word_en);
                    eword.setSelection(word_en.length());
                    eword.setCursorVisible(false);//隐藏光标
                    eword.setTextColor(Color.parseColor("#05f725"));
                    clean_btn.setVisibility(View.INVISIBLE);
                    break;
                case 1://wrong
                    eword.setTextColor(Color.parseColor("#ed0707"));
                    correct_word.setText(word_en);
                    correct_word.setVisibility(View.VISIBLE);
                    break;
                case 2://set c_word
                    //防止中文释义太长
                    if(word_ch.length()>=30){
                        cword.setText(word_ch.substring(0,30)+"...");
                    }else{
                        cword.setText(word_ch);
                    }
                    isTyping = true;
                    if(message.obj!=null){
                        eword.setText(message.obj.toString());
                        eword.setSelection(message.obj.toString().length());
                    }else{
                        eword.setText("");
                    }
                    eword.setTextColor(Color.parseColor("#000000"));
                    correct_word.setVisibility(View.INVISIBLE);
                    break;
                case 3://go back to recite_word_activity
                    //关闭键盘
                    InputMethodManager InputManger = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    InputManger.hideSoftInputFromWindow(eword.getWindowToken(), 0);
                    //停止播放音频
                    mediaPlayerUtil.stop();
                    //向Activity返回数据
                    mListener.spellFragmentInteraction(WrongTimes);
                    break;
                case 4://reset
                    eword.setText("");
                    eword.setTextColor(Color.parseColor("#000000"));
                    correct_word.setVisibility(View.INVISIBLE);
                case 5:
                    clean_btn.setVisibility(View.VISIBLE);
                    break;
                case 6:
                    clean_btn.setVisibility(View.INVISIBLE);
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
        switch(view.getId()){
            case R.id.cword:
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
                changed_volume++;
                mediaPlayerUtil.start();
                break;
            case R.id.clean_btn:
                eword.setText("");
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

    //String new_word
    public void update_options(HashMap<String,Object> words){
        changed_volume = 0;
        word_en = words.get("word_en").toString();
        word_ch = words.get("word_ch").toString();
        eword.setCursorVisible(true);//显示光标
        WrongTimes = 0;
        eword.setEnabled(true);
        mediaPlayerUtil.setFinishListener(() -> {
            duration = mediaPlayerUtil.getDuration();
        });
        mHandler.sendEmptyMessage(2);
        showInput(eword);
        mediaPlayerUtil.reset(word_en,false);
    }

    /**
     * 为控件自动获取焦点
     */
    private void setfocus(View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
