package com.example.administrator.listviewadptwebjsonimg;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ViewTestActivity extends AppCompatActivity {

    private CountDownProgressBar cpb_countdown;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_test);
        Button acquaint = findViewById(R.id.acquaint);
        Button vague = findViewById(R.id.vague);
        Button strange = findViewById(R.id.strange);
        cpb_countdown = (CountDownProgressBar) findViewById(R.id.cpb_countdown);
        cpb_countdown.setWord("accuse of");
        cpb_countdown.setCenterTextColor(Color.WHITE);
        cpb_countdown.setDuration(5000, new CountDownProgressBar.OnFinishListener() {
            @Override
            public void onFinish() {
                Toast.makeText(ViewTestActivity.this, "完成了", Toast.LENGTH_SHORT).show();
            }
        });
        cpb_countdown.setOnClickListener(count_down_Click);
    }
    View.OnClickListener count_down_Click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            cpb_countdown.
//            cpb_countdown.setVisibility(View.GONE);
            cpb_countdown.finishProgressBar();
        }
    };
}
