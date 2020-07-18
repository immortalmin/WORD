package com.immortalmin.www.word;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener,
        PickerDialog.OnDialogInteractionListener{

    private Button return_btn;
    private TextView prof_tv;
    private PickerDialog pickerDialog;
    private MyAsyncTask myAsyncTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        return_btn = (Button)findViewById(R.id.return_btn);
        prof_tv = (TextView)findViewById(R.id.prof_tv);
        return_btn.setOnClickListener(this);
        prof_tv.setOnClickListener(this);
    }


    public void onClick(View view){
        switch (view.getId()){
            case R.id.return_btn:
                finish();
                overridePendingTransition(R.anim.slide_right_in,R.anim.slide_to_left);
                break;
            case R.id.prof_tv:
                List<String> list = Arrays.asList("apple","banana","orange","watermelon","芒果");
                ArrayList<Object> arrayList = new ArrayList<>();
                arrayList.addAll(list);
                pickerDialog = new PickerDialog(this,R.style.MyDialog,arrayList);
                pickerDialog.show();
//                addWordDialog = new AddWordDialog(this,R.style.MyDialog,"lalala");
//                addWordDialog.show();
                break;
        }
    }

    private void UpdateSettings(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("what",21);
//            jsonObject.put("uid",userData.getUid());
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{

        });
        myAsyncTask.execute(jsonObject);
    }

    @Override
    public void PickerInteraction(Object ret){
        Log.i("ccc",""+ret);
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
