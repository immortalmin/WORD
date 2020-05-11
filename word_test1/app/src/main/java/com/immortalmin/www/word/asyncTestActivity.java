package com.immortalmin.www.word;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

public class asyncTestActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn1;
    private MyAsyncTask myAsyncTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_test);
        btn1 = (Button)findViewById(R.id.btn1);
        btn1.setOnClickListener(this);
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete(new MyAsyncTask.isLoadDataListener() {
            @Override
            public void loadComplete(String result) {
                Log.i("ccc","ending:"+result);
            }
        });
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn1:
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("what","0");
                    jsonObject.put("uid","4");
                    jsonObject.put("wid","100");
                }catch (JSONException e){
                    e.printStackTrace();
                }
                myAsyncTask.execute(jsonObject);
                break;
        }
    }


}
