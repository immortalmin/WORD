package com.immortalmin.www.word;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DataTestActivity extends AppCompatActivity implements View.OnClickListener,
        AddWordDialog.OnDialogInteractionListener,
        AddExampleDialog.OnDialogInteractionListener {

    Button btn6,btn7,btn8,btn12,btn13,btn14;
    LinearLayout linearLayout;
    JsonRe jsonRe;
    JSONObject jsonObject;
    EditText[][] word_meaning = new EditText[10][5];
    LinearLayout[][] word_layout = new LinearLayout[10][5];
    RelativeLayout[] btn_layout = new RelativeLayout[10];
    Button[] del_btn = new Button[10];
    Button[] add_btn = new Button[10];
    int index=0;
    boolean flag=true;

    //定义一个自己的dialog
    private AddWordDialog addWordDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_test);
        btn6 = (Button)findViewById(R.id.btn6);
        btn7 = (Button)findViewById(R.id.btn7);
        btn8 = (Button)findViewById(R.id.btn8);
        btn12 = (Button)findViewById(R.id.btn12);
        btn13 = (Button)findViewById(R.id.btn13);
        btn14 = (Button)findViewById(R.id.btn14);
        linearLayout = (LinearLayout)findViewById(R.id.total_lin);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn12.setOnClickListener(this);
        btn13.setOnClickListener(this);
        btn14.setOnClickListener(this);
        jsonRe = new JsonRe();



    }

    public void onClick(View view) {
        switch (view.getId()){
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
            case R.id.btn12:
                showDialog();
                break;
            case R.id.btn13:
                showExampleDialog();
                break;
            case R.id.btn14:
                del_warning();
                break;
        }
    }
    private void del_warning(){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Really?")
                .setContentText("Data will be permanently deleted.")
                .setConfirmText("OK")
                .setCancelText("No,cancel del!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Toast.makeText(DataTestActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                        sweetAlertDialog.cancel();

                    }
                })
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.cancel();
                    }
                })
                .show();
    }

    private void get_data(){
//        Log.i("ccc","get_data"+String.valueOf(index));
        for(int i=0;i<index;i++) {
            String res1,res2,res3;
            res1 = word_meaning[i][0].getText().toString();
            res2 = word_meaning[i][1].getText().toString();
            res3 = word_meaning[i][2].getText().toString();
            Log.i("ccc",res1);
            Log.i("ccc",res2);
            Log.i("ccc",res3);
        }
    }

    private void add_view(){
        TextView[] textView = new TextView[20];
        for(int i=0;i<10;i++){
            textView[i] = new TextView(this);
            textView[i].setText("测试"+String.valueOf(i));
        }
        for(int i=0;i<10;i++){
            linearLayout.addView(textView[i]);
        }
    }
    private void add_view2(){
        String[] hint = {"在例句中的意思","英文例句","中文翻译"};
        for(int i=0;i<3;i++){
            // 1.创建外围LinearLayout控件
            word_layout[index][i] = new LinearLayout(this);
            LinearLayout.LayoutParams eLayoutlayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            eLayoutlayoutParams.setMargins(((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getResources().getDisplayMetrics())),
                    ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics())),
                    ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getResources().getDisplayMetrics())), 0);
            eLayoutlayoutParams.setLayoutDirection(LinearLayout.HORIZONTAL);
            word_layout[index][i].setLayoutParams(eLayoutlayoutParams);
            word_layout[index][i].setGravity(Gravity.CENTER);
            Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.word_input, null);
            word_layout[index][i].setBackground(d);
            word_layout[index][i].setPadding(((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics())),0,
                    ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics())),0);
            word_layout[index][i].setOrientation(LinearLayout.HORIZONTAL);
            //2.word_meaning
            word_meaning[index][i] = new EditText(this);
            LinearLayout.LayoutParams word_meaning_Params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics())));
            word_meaning[index][i].setPadding(0,0,0,0);
            word_meaning[index][i].setLayoutParams(word_meaning_Params);
            word_meaning[index][i].setBackgroundColor(Color.parseColor("#00000000"));
            word_meaning[index][i].setHint(hint[i]);
            word_layout[index][i].addView(word_meaning[index][i]);
            linearLayout.addView(word_layout[index][i]);
        }
        btn_layout[index] = new RelativeLayout(this);
        LinearLayout.LayoutParams btn_layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        btn_layout[index].setLayoutParams(btn_layoutParams);

        del_btn[index] = new Button(this);
        RelativeLayout.LayoutParams del_btn_Params = new RelativeLayout.LayoutParams(
                ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics())),
                ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics())));
//        add_btn_Params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        del_btn_Params.setMargins(((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getResources().getDisplayMetrics())), 0,
                ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, getResources().getDisplayMetrics())), 0);
        del_btn_Params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        del_btn[index].setLayoutParams(del_btn_Params);
        del_btn[index].setPadding(0,0,0,0);
        del_btn[index].setBackgroundColor(Color.parseColor("#00000000"));
        del_btn[index].setTextColor(Color.parseColor("#000000"));
        del_btn[index].setTextSize(20);
        del_btn[index].setText("-");
        del_btn[index].setId(index);

        add_btn[index] = new Button(this);
        RelativeLayout.LayoutParams add_btn_Params = new RelativeLayout.LayoutParams(
                ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics())),
                ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics())));
        add_btn_Params.setMargins(((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getResources().getDisplayMetrics())), 0,
                ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics())), 0);
        add_btn_Params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        add_btn[index].setLayoutParams(add_btn_Params);
        add_btn[index].setPadding(0,0,0,0);
        add_btn[index].setBackgroundColor(Color.parseColor("#00000000"));
        add_btn[index].setTextColor(Color.parseColor("#000000"));
        add_btn[index].setTextSize(20);
        add_btn[index].setText("+");
        add_btn[index].setId(index);
        add_btn[index].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_view2();
            }
        });
        final int ind=index;
        del_btn[index].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.removeView(word_layout[ind][0]);
                linearLayout.removeView(word_layout[ind][1]);
                linearLayout.removeView(word_layout[ind][2]);
                linearLayout.removeView(btn_layout[ind]);
            }
        });
        btn_layout[index].addView(add_btn[index]);
        btn_layout[index].addView(del_btn[index]);
        linearLayout.addView(btn_layout[index]);
        index++;
    }



    private void showDialog(){
        AddWordDialog addWordDialog = new AddWordDialog(this,R.style.MyDialog);
        addWordDialog.show();
    }

    private void showExampleDialog(){
        AddExampleDialog addExampleDialog = new AddExampleDialog(this,R.style.MyDialog,1);
        addExampleDialog.show();
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

    @Override
    public void addWordInteraction(JSONObject jsonObject){
        add_wordandexample(jsonObject);
        Log.i("ccc","addWordInteraction:"+jsonObject.toString());
    }

    @Override
    public void addExampleInteraction(JSONObject jsonObject){
//        add_wordandexample(jsonObject);
        Log.i("ccc","addWordInteraction:"+jsonObject.toString());
    }
}
