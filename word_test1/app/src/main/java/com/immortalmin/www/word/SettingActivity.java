package com.immortalmin.www.word;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener,
        PickerDialog.OnDialogInteractionListener{

    private Button return_btn;
    private TextView prof_tv,recite_num,recite_scope;
    private LinearLayout finish_num_layout,scope_num_layout;
    private PickerDialog pickerDialog;
    private MyAsyncTask myAsyncTask;
    private String[] settingStr = {"recite_num","recite_scope"};
    private UserData userData = new UserData();
    private DataUtil dataUtil = new DataUtil(SettingActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        return_btn = (Button)findViewById(R.id.return_btn);
        prof_tv = (TextView)findViewById(R.id.prof_tv);
        recite_num = (TextView)findViewById(R.id.recite_num);
        recite_scope = (TextView)findViewById(R.id.recite_scope);
        finish_num_layout = (LinearLayout)findViewById(R.id.finish_num_layout);
        scope_num_layout = (LinearLayout)findViewById(R.id.scope_num_layout);
        return_btn.setOnClickListener(this);
        prof_tv.setOnClickListener(this);
        finish_num_layout.setOnClickListener(this);
        scope_num_layout.setOnClickListener(this);
        dataUtil.getdata(new DataUtil.HttpCallbackStringListener() {
            @Override
            public void onFinish(UserData userdata) {
                userData = userdata;
                mHandler.sendEmptyMessage(0);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }


    public void onClick(View view){
        switch (view.getId()){
            case R.id.return_btn:
                finish();
                overridePendingTransition(R.anim.slide_right_in,R.anim.slide_to_left);
                break;
            case R.id.prof_tv:
//                List<String> list = Arrays.asList("apple","banana","orange","watermelon","芒果");
//                ArrayList<Object> arrayList = new ArrayList<>();
//                arrayList.addAll(list);
//                pickerDialog = new PickerDialog(this,R.style.MyDialog,arrayList);
//                pickerDialog.show();
                break;
            case R.id.finish_num_layout:
                ArrayList<Object> arrayList = new ArrayList<>(Arrays.asList(5,10,20,30,50));
                int position = 0;
                for(int i=0;i<arrayList.size();i++){
                    if(arrayList.get(i).equals(userData.getRecite_num())){
                        position = i;
                        break;
                    }
                }
                pickerDialog = new PickerDialog(this,R.style.MyDialog,arrayList,0,position);
                pickerDialog.show();
                break;
            case R.id.scope_num_layout:
                arrayList = new ArrayList<>(Arrays.asList(5,10,15,20,25));
                position = 0;
                for(int i=0;i<arrayList.size();i++){
                    if(arrayList.get(i).equals(userData.getRecite_scope())){
                        position = i;
                        break;
                    }
                }
                pickerDialog = new PickerDialog(this,R.style.MyDialog,arrayList,1,position);
                pickerDialog.show();
                break;
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    recite_num.setText(String.valueOf(userData.getRecite_num()));
                    recite_scope.setText(String.valueOf(userData.getRecite_scope()));
                    break;
            }
            return false;
        }
    });

    private void UpdateSettings(JSONObject jsonObject){
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            dataUtil.getdata(new DataUtil.HttpCallbackStringListener() {
                @Override
                public void onFinish(UserData userdata) {
                    userData = userdata;
                    mHandler.sendEmptyMessage(0);
                }

                @Override
                public void onError(Exception e) {

                }
            });//将用户数据保存在本地，以及userData中

        });
        myAsyncTask.execute(jsonObject);
    }

    @Override
    public void PickerInteraction(JSONObject ret){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("what",21);
            jsonObject.put(settingStr[Integer.valueOf(ret.get("what").toString())],ret.get("value"));
            jsonObject.put("uid",userData.getUid());
        }catch (JSONException e){
            e.printStackTrace();
        }
        UpdateSettings(jsonObject);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            overridePendingTransition(R.anim.slide_right_in,R.anim.slide_to_left);
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
