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
    Button btn1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_test);
        btn1 = (Button)findViewById(R.id.btn1);
//        setFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        final CountDownFragment fragment1 = new CountDownFragment();

//        tv1=getFragmentManager().findFragmentById()

        beginTransaction.replace(android.R.id.content,fragment1);
        beginTransaction.commit();
//        btn1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.i("!!!!!!!!!!!!",fragment1.getV());
//            }
//        });

//        tv1 = .findViewById(R.id.tv1);
//        tv1.setText("success");
    }

//    private void setFragment(){
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.add(R.id.f1,new CountDownFragment()).commit();
//
//    }

}
