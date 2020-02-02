package com.example.administrator.listviewadptwebjsonimg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class WordViewTestActivity extends AppCompatActivity {

    Button btn1;
    WordView word;
    EditText editText;
    int rank;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_view_test);
        btn1 = (Button)findViewById(R.id.btn1);
        word = (WordView)findViewById(R.id.word);
        editText = (EditText)findViewById(R.id.editText);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rank = Integer.valueOf(editText.getText().toString());
                word.setRank(rank);
            }
        });
    }
}
