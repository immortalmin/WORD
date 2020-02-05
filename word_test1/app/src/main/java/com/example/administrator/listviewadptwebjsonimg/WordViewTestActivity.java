package com.example.administrator.listviewadptwebjsonimg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

public class WordViewTestActivity extends AppCompatActivity {

    WordView word;
    SeekBar seekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_view_test);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        word = (WordView)findViewById(R.id.word);
        word.setmText("dfoashfbgslkj");
        word.setAccount((float)0.5);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                word.setAccount((float)(i/100.0));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
