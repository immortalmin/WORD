package com.example.administrator.listviewadptwebjsonimg;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    Button reg_btn;
    EditText username,pwd;
    TextView warn_text;
    JsonRe jsonRe;
    private HashMap<String,Object> userdata=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        reg_btn = (Button)findViewById(R.id.reg_btn);
        username = (EditText)findViewById(R.id.username);
        pwd = (EditText)findViewById(R.id.pwd);
        warn_text = (TextView) findViewById(R.id.warn_text);
        reg_btn.setOnClickListener(this);

        jsonRe = new JsonRe();

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mHandler.obtainMessage(0).sendToTarget();
                String uname = username.getText().toString();
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("username",uname);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                query_user(jsonObject);
            }
        });
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.reg_btn:
                String uname = username.getText().toString();
                String password = pwd.getText().toString();
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("username",uname);
                    jsonObject.put("pwd",password);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                register(jsonObject);
                break;
        }
    }

    private void register(final JSONObject jsonObject) {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                HttpGetContext httpGetContext = new HttpGetContext();
//                httpGetContext.getData("http://47.98.239.237/word/php_file2/register.php",jsonObject);
//                Looper.prepare();
//                Toast.makeText(RegisterActivity.this,"注册成功 即将跳转到登录界面",Toast.LENGTH_SHORT).show();
//                Looper.loop();
//                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));

                RegisterActivity.this.finish();
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
            }
        }).start();
    }

    private void query_user(final JSONObject jsonObject) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                String wordjson = httpGetContext.getData("http://47.98.239.237/word/php_file2/getuserdata.php",jsonObject);
                userdata = jsonRe.userData(wordjson);
                if(userdata.size()!=0){
                    mHandler.obtainMessage(1).sendToTarget();
                }
//                mHandler.obtainMessage(0).sendToTarget();
            }
        }).start();
    }
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    warn_text.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    warn_text.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };


}
