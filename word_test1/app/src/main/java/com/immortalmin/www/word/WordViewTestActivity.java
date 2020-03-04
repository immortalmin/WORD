package com.immortalmin.www.word;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class WordViewTestActivity extends AppCompatActivity implements View.OnClickListener{

    private ObjectAnimator objectAnimator;
    SeekBar seekBar;
    MainView mainView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_view_test);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        mainView = (MainView)findViewById(R.id.mainView);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                Log.i("ccc",String.valueOf(i));
                mainView.setSunHigh(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mainView.setOnClickListener(this);

    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.mainView:
                mHandler.obtainMessage(0).sendToTarget();
                break;
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 0:
                    objectAnimator = objectAnimator.ofFloat(mainView,"rotation",0f,360f);
                    objectAnimator.setDuration(60000);
//                    objectAnimator.setRepeatMode(ValueAnimator.RESTART);
                    objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
                    objectAnimator.setInterpolator(new LinearInterpolator());
                    objectAnimator.start();

                    break;
            }
            return false;
        }
    });
}
