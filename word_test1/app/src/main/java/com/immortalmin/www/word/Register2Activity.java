package com.immortalmin.www.word;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Register2Activity extends AppCompatActivity
        implements View.OnClickListener, Register0Fragment.OnFragmentInteractionListener,
        Register1Fragment.OnFragmentInteractionListener,Register2Fragment.OnFragmentInteractionListener{

    private Button return_btn;
    private JsonRe jsonRe = new JsonRe();
    private MD5Utils md5Utils = new MD5Utils();
    private Runnable toLogin;
    private String profilephotoPath="null";
    private HashMap<String,Object> userdata=null;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentTransaction transaction = fragmentManager.beginTransaction();
    private Register0Fragment register0Fragment = new Register0Fragment();
    private Register1Fragment register1Fragment = new Register1Fragment();
    private Register2Fragment register2Fragment = new Register2Fragment();
    private int stepIndex=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        return_btn = findViewById(R.id.return_btn);


        return_btn.setOnClickListener(this);
        init();


    }

    private void init() {
        init_fragment();
    }

    /**
     * 加载所有的fragment
     */
    private void init_fragment() {
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.stepsFrameLayout, register0Fragment);
        transaction.add(R.id.stepsFrameLayout, register1Fragment);
        transaction.add(R.id.stepsFrameLayout, register2Fragment);
        transaction.hide(register1Fragment).hide(register2Fragment);
        transaction.commit();
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.return_btn:
                goPreStep();
                break;
        }
    }

    /**
     * 回到上一步
     */
    public void goPreStep(){
        if(stepIndex==0){
            finish();
            overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
        }else if(stepIndex==1){
            transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_left_in,R.anim.slide_to_right);
            transaction.hide(register1Fragment).show(register0Fragment);
            transaction.commit();
            stepIndex=0;
        }else if(stepIndex==2){
            transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_left_in,R.anim.slide_to_right);
            transaction.hide(register2Fragment).show(register1Fragment);
            transaction.commit();
            stepIndex=1;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            goPreStep();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void Register0FragmentInteraction() {
        transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_right_in,R.anim.slide_to_left);
        transaction.hide(register0Fragment).show(register1Fragment);
        transaction.commit();
        stepIndex=1;
    }
    @Override
    public void Register1FragmentInteraction(int what) {

        switch(what){
            case 0://commit 跳转到登录界面
            case 1://not binding
                //弹框警告一下
                //这里先跳转到登录界面
                finish();
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
            case 2://other ways
                transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_right_in,R.anim.slide_to_left);
                transaction.hide(register1Fragment).show(register2Fragment);
                transaction.commit();
                stepIndex=2;
                break;
        }

    }

    @Override
    public void Register2FragmentInteraction() {

    }
}
