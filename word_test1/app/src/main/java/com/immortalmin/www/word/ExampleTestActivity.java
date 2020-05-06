package com.immortalmin.www.word;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ExampleTestActivity extends AppCompatActivity implements
        ExampleFragment.OnFragmentInteractionListener,
        KelinsiFragment.OnFragmentInteractionListener,
        UpdateExampleDialog.OnDialogInteractionListener,
        View.OnClickListener{

    private Button example_btn,kelinsi_btn;
    private ImageView backdrop;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentTransaction transaction = fragmentManager.beginTransaction();
    private ExampleFragment exampleFragment = new ExampleFragment();
    private KelinsiFragment kelinsiFragment = new KelinsiFragment();
    private HashMap<String,Object> word = null;
    private ArrayList<HashMap<String,Object>> examplelist = null;
    private UserData userData = new UserData();
    private JsonRe jsonRe = new JsonRe();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_test);
        example_btn = (Button)findViewById(R.id.example_btn);
        kelinsi_btn = (Button)findViewById(R.id.kelinsi_btn);
        backdrop = (ImageView)findViewById(R.id.backdrop);
        example_btn.setOnClickListener(this);
        kelinsi_btn.setOnClickListener(this);
        init();
    }

    private void init() {
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.framelayout,exampleFragment);
        transaction.add(R.id.framelayout,kelinsiFragment);
        transaction.hide(kelinsiFragment);
        transaction.commit();
        init_user();
        getwordlist();
    }

    private void init_user(){
        SharedPreferences sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
        userData.setUid(sp.getString("uid",null));
        userData.setRecite_num(sp.getInt("recite_num",20));
        userData.setRecite_scope(sp.getInt("recite_scope",10));
        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        userData.setUsername(sp.getString("username",null));
        userData.setPassword(sp.getString("password",null));
        userData.setProfile_photo(sp.getString("profile_photo",null));
        userData.setStatus(sp.getString("status","0"));
        userData.setLast_login(sp.getLong("last_login",946656000000L));
        userData.setEmail(sp.getString("email",null));
        userData.setTelephone(sp.getString("telephone",null));
        userData.setMotto(sp.getString("motto",null));
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.example_btn:
                transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_left_in,R.anim.slide_to_right);
                transaction.hide(kelinsiFragment).show(exampleFragment);
//                FragmentTransaction
                transaction.commit();
                mHandler.obtainMessage(0).sendToTarget();
                break;
            case R.id.kelinsi_btn:
                transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_right_in,R.anim.slide_to_left);
                transaction.hide(exampleFragment).show(kelinsiFragment);
                transaction.commit();
                mHandler.obtainMessage(1).sendToTarget();
                break;
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    example_btn.setBackgroundColor(Color.parseColor("#30000000"));
                    kelinsi_btn.setBackgroundColor(Color.parseColor("#10000000"));
                    break;
                case 1:
                    example_btn.setBackgroundColor(Color.parseColor("#10000000"));
                    kelinsi_btn.setBackgroundColor(Color.parseColor("#30000000"));
                    break;
            }
            return false;
        }
    });

    private void getwordlist() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("uid",userData.getUid());
                    jsonObject.put("wid",Integer.valueOf(10));
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpGetContext httpGetContext = new HttpGetContext();
//                String wordjson = httpGetContext.getData("http://47.98.239.237/word/php_file2/getworddata.php",jsonObject);
//                word = jsonRe.wordData(wordjson);
                String examplejson = httpGetContext.getData("http://47.98.239.237/word/php_file2/getexampledata.php",jsonObject);
                examplelist = jsonRe.exampleData(examplejson);
                Log.i("ccc",examplelist.toString());
                exampleFragment.setData(937,userData,backdrop);
//                mHandler.obtainMessage(0).sendToTarget();
            }
        }).start();

    }



    private void update_example(final JSONObject jsonObject){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                httpGetContext.getData("http://47.98.239.237/word/php_file2/update_example.php",jsonObject);
            }
        }).start();
        getwordlist();
    }

    @Override
    public void exampleFragmentInteraction(String res){

    }

    @Override
    public void kelinsiFragmentInteraction(String res){

    }

    @Override
    public void updateExampleInteraction(JSONObject jsonObject){
        update_example(jsonObject);

    }
}
