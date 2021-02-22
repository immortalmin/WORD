package com.immortalmin.www.word;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class AddExampleDialog extends Dialog implements View.OnClickListener{

    private Context context;
    private Button commit_btn,cancel_btn,first_add_btn;
    private TextView tv2;
    private OnDialogInteractionListener listener;
    private MyEditText[][] word = new MyEditText[100][5];
    private RelativeLayout[][] word_layout = new RelativeLayout[100][5];
    private RelativeLayout[] btn_layout = new RelativeLayout[100];
    private LinearLayout example_layout;
    private Button[] del_btn = new Button[100];
    private Button[] add_btn = new Button[100];
    private Button[][] operate_btn = new Button[10][5];
    private int id;
    private int index=0;
    private boolean[] del_flag = new boolean[100];
    private String uid="1",wid ="1",C_meaning = "null";
    private boolean cancel_flag=false;
    private int sum=0;

    public AddExampleDialog(Context context) {
        super(context);
        this.context=context;
    }

    public AddExampleDialog(Context context, int themeResId,JSONObject jsonObject) {
        super(context, themeResId);
        this.context=context;
        try{
            this.wid = jsonObject.getString("wid");
            this.uid = jsonObject.getString("uid");
            this.C_meaning = jsonObject.getString("C_meaning");
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    protected AddExampleDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(context,R.layout.add_example,null);
        listener = (OnDialogInteractionListener) context;//绑定回调函数的监听器
        commit_btn = (Button)view.findViewById(R.id.commit_btn);
        cancel_btn = (Button)view.findViewById(R.id.cancel_btn);
        first_add_btn = (Button)view.findViewById(R.id.first_add_btn);
        example_layout = (LinearLayout) view.findViewById(R.id.example_layout);
        tv2 = (TextView)view.findViewById(R.id.tv2);
        first_add_btn.setOnClickListener(this);
        commit_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
        setContentView(view);
        Arrays.fill(del_flag,true);
        add_view();

    }

    public interface OnDialogInteractionListener {
        void addExampleInteraction(JSONObject jsonObject);
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.commit_btn:
                if(judge()){
                    pack_data();
                }
                break;
            case R.id. cancel_btn:
                if(!cancel_flag){
                    cancel_flag = true;
                    mHandler.sendEmptyMessageDelayed(0,500);
                }else{
                    dismiss();
                }
                break;
            case R.id.first_add_btn:
                add_view();
                first_add_btn.setVisibility(View.INVISIBLE);
                break;
        }

    }
    private Handler mHandler = new Handler(message -> {
        switch (message.what){
            case 0:
                cancel_flag=false;
                break;
        }
        return false;
    });


    private void add_view(){
        sum++;
        String[] hint = {"在例句中的意思","英文例句","中文翻译"};
        final int ind=index;
        for(int i=0;i<3;i++){
            word[ind][i] = new MyEditText(context);
            LinearLayout.LayoutParams word_meaning_Params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            word_meaning_Params.setMargins(conversion(18), conversion(5), conversion(18), 0);
            word[ind][i].setLayoutParams(word_meaning_Params);
            word[ind][i].setHint(hint[i]);
            if(i==0){
                word[ind][i].setTextType(1);
                word[ind][i].setPasteString(C_meaning);
            }else{
                word[ind][i].setTextType(0);
            }
            word[ind][i].setDisplayStyle(1);
            word[ind][i].setMaxLines(3);
            word[ind][i].setMinHeight(conversion(30));
            example_layout.addView(word[ind][i]);
        }
        btn_layout[ind] = new RelativeLayout(context);
        LinearLayout.LayoutParams btn_layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        btn_layout[ind].setLayoutParams(btn_layoutParams);

        del_btn[ind] = new Button(context);
        RelativeLayout.LayoutParams del_btn_Params = new RelativeLayout.LayoutParams(
                conversion(25), conversion(25));
        del_btn_Params.setMargins(conversion(20), 0, conversion(18), 0);
        del_btn_Params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        del_btn[ind].setLayoutParams(del_btn_Params);
        del_btn[ind].setPadding(0,0,0,0);
        del_btn[ind].setBackgroundColor(Color.parseColor("#00000000"));
        del_btn[ind].setTextColor(Color.parseColor("#FFFFFF"));
        del_btn[ind].setTextSize(20);
        del_btn[ind].setText("-");
        del_btn[ind].setId(ind);

        add_btn[ind] = new Button(context);
        RelativeLayout.LayoutParams add_btn_Params = new RelativeLayout.LayoutParams(conversion(25), conversion(25));
        add_btn_Params.setMargins(conversion(18), 0, conversion(20), 0);
        add_btn_Params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        add_btn[ind].setLayoutParams(add_btn_Params);
        add_btn[ind].setPadding(0,0,0,0);
        add_btn[ind].setBackgroundColor(Color.parseColor("#00000000"));
        add_btn[ind].setTextColor(Color.parseColor("#FFFFFF"));
        add_btn[ind].setTextSize(20);
        add_btn[ind].setText("+");
        add_btn[ind].setId(ind);
        add_btn[ind].setOnClickListener(view -> add_view());

        del_btn[ind].setOnClickListener(view -> {
            del_flag[ind]=false;
            example_layout.removeView(word[ind][0]);
            example_layout.removeView(word[ind][1]);
            example_layout.removeView(word[ind][2]);
            example_layout.removeView(btn_layout[ind]);
            sum--;
            if(sum==0){
                first_add_btn.setVisibility(View.VISIBLE);
            }
        });
        btn_layout[ind].addView(add_btn[ind]);
        btn_layout[ind].addView(del_btn[ind]);
        example_layout.addView(btn_layout[ind]);
        index++;
        if(sum==1){
            setfocus(word[0][0]);
        }
    }

    /**
     * 为控件自动获取焦点
     */
    private void setfocus(View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private int conversion(int value){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }

    private boolean judge(){
        boolean flag=false;
        String s1,s2,s3;
        for(int i=0;i<index;i++){
            s1 = word[i][0].getText().toString();
            s2 = word[i][1].getText().toString();
            s3 = word[i][2].getText().toString();
            if(!del_flag[i]||(s1.length()==0&&s2.length()==0&&s3.length()==0)){
                continue;
            }else if((s1.length()==0||s2.length()==0||s3.length()==0)&&(s1.length()!=0||s2.length()!=0||s3.length()!=0)){
                Toast.makeText(context,"请填写完整",Toast.LENGTH_SHORT).show();
                return false;
            }
            flag=true;
        }
        if(!flag){
            Toast.makeText(context,"一个例句都没有？",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void pack_data(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("wid",wid);
            jsonObject.put("uid",uid);
            JSONArray jsonArray = new JSONArray();
            for(int i=0;i<index;i++){
                JSONObject translate = new JSONObject();
                String wString = word[i][0].getText().toString().replaceAll("\"","\\\\\\\"");
                String eString = word[i][1].getText().toString().replaceAll("\"","\\\\\\\"");
                String cString = word[i][2].getText().toString().replaceAll("\"","\\\\\\\"");
                if(del_flag[i] && wString.length()!=0 && eString.length()!=0 && cString.length()!=0){
                    translate.put("word_meaning",wString+'\n');
                    translate.put("E_sentence",eString+'\n');
                    translate.put("C_translate",cString+'\n');
                    jsonArray.put(translate);
                }
            }
            jsonObject.put("translate",jsonArray);
        }catch (JSONException e){
            e.printStackTrace();
        }
        Toast.makeText(context,"添加成功",Toast.LENGTH_SHORT).show();
        dismiss();
        listener.addExampleInteraction(jsonObject);
    }

}
