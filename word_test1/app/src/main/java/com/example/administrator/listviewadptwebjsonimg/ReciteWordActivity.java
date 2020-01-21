package com.example.administrator.listviewadptwebjsonimg;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ReciteWordActivity extends AppCompatActivity implements View.OnClickListener,CountDownFragment.OnFragmentInteractionListener{

    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    CountDownFragment countDownFragment = new CountDownFragment();
    Button turn_model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recite_word);
        turn_model = (Button)findViewById(R.id.turn_model);
        turn_model.setOnClickListener(this);
        initialize();
        start_countdown();
    }

    private void start_countdown(){

        Bundle sendBundle = new Bundle();
        sendBundle.putString("word","accuse of");
        countDownFragment.setArguments(sendBundle);
        transaction.commit();
    }


    /**
     * 初始化操作
     */
    public void initialize(){
        transaction.add(R.id.recite_model,countDownFragment);
//        transaction.show(countDownFragment);
//        transaction.commit();
    }

    /**
     * 选项按钮点击事件
     * @param view
     */
    public void onClick(View view){
        switch(view.getId()){
            case R.id.turn_model:

                break;

        }
    }


    /**
     * CountDownFragment的回调函数
     * @param s
     */
    @Override
    public void onFragmentInteraction(ArrayList<String> s) {
        Log.i("回调了",s.toString());
    }

}
