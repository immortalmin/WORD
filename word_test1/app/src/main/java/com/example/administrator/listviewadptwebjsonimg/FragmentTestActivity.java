package com.example.administrator.listviewadptwebjsonimg;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FragmentTestActivity extends AppCompatActivity {

    TextView tv1;
    Button btn1,btn2;
    CountDownFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_test);
        btn1 = (Button)findViewById(R.id.btn1);
        btn2 = (Button)findViewById(R.id.btn2);
//        setFragment();

        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        final CountDownFragment fragment1 = new CountDownFragment();
        //test
//        Bundle sendBundle = new Bundle();
//        sendBundle.putString("csk","from FragmentTestActivity.java");
//        fragment1.setArguments(sendBundle);
        //

//        transaction.commit();



//        transaction.add(android.R.id.content,fragment1);
//        beginTransaction.replace(android.R.id.content,fragment1);
//        transaction.commit();
//        transaction.hide(fragment);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                if(!fragment1.isAdded()){
                    transaction.add(R.id.f2,fragment1);
                }else{
                    transaction.show(fragment1);
                }
                transaction.commit();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
//                transaction.add(R.id.f2,fragment1);
                transaction.hide(fragment1);
                transaction.commit();
            }
        });

    }

//    private void setFragment(){
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.add(R.id.f1,new CountDownFragment()).commit();
//
//    }

}
