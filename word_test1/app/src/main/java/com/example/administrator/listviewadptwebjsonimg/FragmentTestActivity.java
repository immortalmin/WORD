package com.example.administrator.listviewadptwebjsonimg;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FragmentTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_test);
//        setFragment();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
//        CountDownFragment fragment1 = new CountDownFragment();
//        beginTransaction.replace(android.R.id.content,new CountDownFragment());
//        beginTransaction.commit();

    }

//    private void setFragment(){
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.add(R.id.f1,new CountDownFragment()).commit();
//
//    }

}
