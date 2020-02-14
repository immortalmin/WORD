package com.example.administrator.listviewadptwebjsonimg;

import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class DataTestActivity extends AppCompatActivity implements View.OnClickListener{

    Button btn1,btn2,btn3,btn4,btn5,btn6,btn7,btn8,btn9,btn10,btn11;
    JsonRe jsonRe;
    JSONObject jsonObject;
    boolean flag=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_test);
        btn1 = (Button)findViewById(R.id.btn1);
        btn2 = (Button)findViewById(R.id.btn2);
        btn3 = (Button)findViewById(R.id.btn3);
        btn4 = (Button)findViewById(R.id.btn4);
        btn5 = (Button)findViewById(R.id.btn5);
        btn6 = (Button)findViewById(R.id.btn6);
        btn7 = (Button)findViewById(R.id.btn7);
        btn8 = (Button)findViewById(R.id.btn8);
        btn9 = (Button)findViewById(R.id.btn9);
        btn10 = (Button)findViewById(R.id.btn10);
        btn11 = (Button)findViewById(R.id.btn11);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);
        btn10.setOnClickListener(this);
        btn11.setOnClickListener(this);
        jsonRe = new JsonRe();

    }


    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn1:
                jsonObject = new JSONObject();
                try{
                    jsonObject.put("mount",3);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                getrecitelist(jsonObject);
                break;
            case R.id.btn2:
                jsonObject = new JSONObject();
                try{
                    jsonObject.put("id",22);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                getexampledata(jsonObject);
                break;
            case R.id.btn3:
                jsonObject = new JSONObject();
                try{
                    jsonObject.put("id",22);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                getworddata(jsonObject);
                break;
            case R.id.btn4:
                getallword();
                break;
            case R.id.btn5:
                jsonObject = new JSONObject();
                try{
                    jsonObject.put("word","fu");
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                getsearchlist(jsonObject);
                break;
            case R.id.btn6:
                jsonObject = new JSONObject();
                try{
                    jsonObject.put("last_date","2020_02_13");
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                getreviewlist(jsonObject);
                break;
            case R.id.btn7:
                jsonObject = new JSONObject();
                try{
                    jsonObject.put("word_group","test2");
                    jsonObject.put("C_meaning","测试2");
                    jsonObject.put("page","222");
                    JSONArray jsonArray = new JSONArray();
                    JSONObject translate = new JSONObject();
                    translate.put("word_meaning","he1");
                    translate.put("C_translate","呵1");
                    translate.put("E_sentence","hehehe1");
                    jsonArray.put(translate);
                    translate = new JSONObject();
                    translate.put("word_meaning","he2");
                    translate.put("C_translate","呵2");
                    translate.put("E_sentence","hehehe2");
                    jsonArray.put(translate);
                    jsonObject.put("translate",jsonArray);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                add_wordandexample(jsonObject);
                break;
            case R.id.btn8:
                jsonObject = new JSONObject();
                try{
                    jsonObject.put("id",27);
                    JSONArray jsonArray = new JSONArray();
                    JSONObject translate = new JSONObject();
                    translate.put("word_meaning","he5");
                    translate.put("C_translate","呵5");
                    translate.put("E_sentence","hehehe5");
                    jsonArray.put(translate);
                    translate = new JSONObject();
                    translate.put("word_meaning","he6");
                    translate.put("C_translate","呵6");
                    translate.put("E_sentence","hehehe6");
                    jsonArray.put(translate);
                    jsonObject.put("translate",jsonArray);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                add_example(jsonObject);
                break;
            case R.id.btn9:
                jsonObject = new JSONObject();
                try{
                    jsonObject.put("id",27);
                    jsonObject.put("correct_times",1);
                    jsonObject.put("error_times",1);
                    jsonObject.put("prof_flag",1);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                update_recite(jsonObject);
                break;
            case R.id.btn10:
                jsonObject = new JSONObject();
                try{
                    jsonObject.put("id",27);
                    jsonObject.put("collect",0);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                update_collect(jsonObject);
                break;
            case R.id.btn11:
                if(flag){
                    Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.star_off, null);
                    btn11.setBackground(drawable);
                }else{
                    Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.star_on, null);
                    btn11.setBackground(drawable);
                }
                flag=!flag;
                break;
        }
    }
    //***********改***********
    private void update_recite(final JSONObject jsonObject){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                httpGetContext.getData("http://47.98.239.237/word/php_file2/update_recite.php",jsonObject);
            }
        }).start();
    }
    private void update_collect(final JSONObject jsonObject){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                httpGetContext.getData("http://47.98.239.237/word/php_file2/update_collect.php",jsonObject);
            }
        }).start();
    }

    //***********增***********
    private void add_wordandexample(final JSONObject jsonObject){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                httpGetContext.getData("http://47.98.239.237/word/php_file2/addword.php",jsonObject);
            }
        }).start();
    }

    private void add_example(final JSONObject jsonObject){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                httpGetContext.getData("http://47.98.239.237/word/php_file2/addexample.php",jsonObject);
            }
        }).start();
    }


    //***********查***********
    private void getreviewlist(final JSONObject jsonObject) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                String recitejson = httpGetContext.getData("http://47.98.239.237/word/php_file2/getreviewlist.php",jsonObject);
                List<HashMap<String,Object>> reviewlist = null;
                reviewlist = jsonRe.reciteData(recitejson);
                Log.i("ccc",reviewlist.toString());
            }
        }).start();
    }

    private void getsearchlist(final JSONObject jsonObject) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                String recitejson = httpGetContext.getData("http://47.98.239.237/word/php_file2/getsearchlist.php",jsonObject);
                List<HashMap<String,Object>> searchlist = null;
                searchlist = jsonRe.allwordData(recitejson);
                Log.i("ccc",searchlist.toString());
            }
        }).start();
    }

    private void getallword(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                String recitejson = httpGetContext.httpclientgettext("http://47.98.239.237/word/php_file2/getwordlist.php");
                List<HashMap<String,Object>> wordlist = null;
                wordlist = jsonRe.allwordData(recitejson);
                Log.i("ccc",wordlist.toString());
            }
        }).start();
    }

    private void getrecitelist(final JSONObject jsonObject) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                String recitejson = httpGetContext.getData("http://47.98.239.237/word/php_file2/getrecitelist.php",jsonObject);
                List<HashMap<String,Object>> recitelist = null;
                recitelist = jsonRe.reciteData(recitejson);
                Log.i("ccc",recitelist.toString());
            }
        }).start();
    }

    private void getexampledata(final JSONObject jsonObject) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                String examplejson = httpGetContext.getData("http://47.98.239.237/word/php_file2/getexampledata.php",jsonObject);
                List<HashMap<String,Object>> examplelist = null;
                examplelist = jsonRe.exampleData(examplejson);
                Log.i("ccc",examplelist.toString());
            }
        }).start();
    }

    private void getworddata(final JSONObject jsonObject) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                String wordjson = httpGetContext.getData("http://47.98.239.237/word/php_file2/getworddata.php",jsonObject);
                HashMap<String,Object> word = null;
                word = jsonRe.wordData(wordjson);
                Log.i("ccc",word.toString());
            }
        }).start();
    }
}
