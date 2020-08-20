package com.immortalmin.www.word;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class AddWordDialog extends Dialog implements View.OnClickListener{

    private Context context;
    private Button commit_btn,cancel_btn,first_add_btn;
    private MyEditText word_group,C_meaning;
    private TextView tv2;
    private OnDialogInteractionListener listener;
    private MyEditText[][] word = new MyEditText[100][5];
    private RelativeLayout[] btn_layout = new RelativeLayout[10];
    private LinearLayout example_layout;
    private Button[] del_btn = new Button[10];
    private Button[] add_btn = new Button[10];
    private int index=0;
    private boolean[] del_flag = new boolean[10];
    private int sum=0;//统计例句的数量
    private boolean cancel_flag = false;
    private String word_text="";

    public AddWordDialog(Context context) {
        super(context);
        this.context=context;
    }

    public AddWordDialog(Context context, int themeResId, String word_text) {
        super(context, themeResId);
        this.context = context;
        this.word_text = word_text;
    }

    protected AddWordDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(context,R.layout.add_word,null);
        listener = (OnDialogInteractionListener) context;//绑定回调函数的监听器
        word_group = (MyEditText)view.findViewById(R.id.word_group);
        C_meaning = (MyEditText)view.findViewById(R.id.C_meaning);
        commit_btn = (Button)view.findViewById(R.id.commit_btn);
        cancel_btn = (Button)view.findViewById(R.id.cancel_btn);
        first_add_btn = (Button)view.findViewById(R.id.first_add_btn);
        example_layout = (LinearLayout) view.findViewById(R.id.example_layout);
        tv2 = (TextView)view.findViewById(R.id.tv2);

        commit_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
        first_add_btn.setOnClickListener(this);
        tv2.setOnClickListener(this);

        //XXX:因为最初是存在“新增例句”的，之后C_meaning的内容改变之后，其粘贴的内容无法同步更新，所以先用这个解决
        C_meaning.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //如果先增加了例句框，再修改C_meaning，其粘贴的内容依旧无法同步，因此对每个现有的输入框都重新设置PasteString
                for(int i=0;i<index;i++){
                    if(del_flag[i]) word[i][0].setPasteString(editable.toString());
                }
            }
        });
        setContentView(view);
        Arrays.fill(del_flag,true);
        add_view();
        //FIXME:word_group在未获取焦点的情况下，显得异常的粗
//        word_group.setText(word_text);
        Message message = new Message();
        message.what = 3;
        message.obj = word_text;
        mHandler.sendMessage(message);
        if(word_text.length()>0){
            setfocus();
        }
    }

    /**
     * 为word_group自动获取焦点
     */
    private void setfocus() {
        C_meaning.setFocusable(true);
        C_meaning.setFocusableInTouchMode(true);
        C_meaning.requestFocus();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }


    public interface OnDialogInteractionListener {
        void addWordInteraction(JSONObject jsonObject);
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
                    mHandler.sendEmptyMessageDelayed(2,500);
                }else{
                    dismiss();
                }
                break;
            case R.id.tv2:
                add_view();
                break;
            case R.id.first_add_btn:
                add_view();
                mHandler.obtainMessage(0).sendToTarget();
                break;
            default:
//                dismiss();
        }

    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    first_add_btn.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    first_add_btn.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    cancel_flag = false;
                    break;
                case 3:
                    word_group.setText(message.obj.toString());
                    break;
            }
            return false;
        }
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
                word[ind][i].setPasteString(C_meaning.getText().toString());
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
        add_btn[ind].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_view();
            }
        });

        del_btn[ind].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                del_flag[ind]=false;
                example_layout.removeView(word[ind][0]);
                example_layout.removeView(word[ind][1]);
                example_layout.removeView(word[ind][2]);
                example_layout.removeView(btn_layout[ind]);
                sum--;
                if(sum==0){
                    first_add_btn.setVisibility(View.VISIBLE);
                }
            }
        });
        btn_layout[ind].addView(add_btn[ind]);
        btn_layout[ind].addView(del_btn[ind]);
        example_layout.addView(btn_layout[ind]);
        index++;
    }

    private int conversion(int value){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }

    private boolean judge(){
        String s1,s2,s3;
        s1 = word_group.getText().toString();
        s2 = C_meaning.getText().toString();
        if(s1.length()==0||s2.length()==0){
            Toast.makeText(context,"单词 请填写完整",Toast.LENGTH_SHORT).show();
            return false;
        }
        for(int i=0;i<index;i++){
            s1 = word[i][0].getText().toString();
            s2 = word[i][1].getText().toString();
            s3 = word[i][2].getText().toString();
            if(!del_flag[i]||(s1.length()==0&&s2.length()==0&&s3.length()==0)){
                continue;
            }else if((s1.length()==0||s2.length()==0||s3.length()==0)&&(s1.length()!=0||s2.length()!=0||s3.length()!=0)){
                Toast.makeText(context,"例句 请填写完整",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void pack_data(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("word_group",word_group.getText().toString().replaceAll("\"","\\\\\\\""));
            jsonObject.put("C_meaning",C_meaning.getText().toString());
            JSONArray jsonArray = new JSONArray();
            for(int i=0;i<index;i++){
                JSONObject translate = new JSONObject();
                String wString = word[i][0].getText().toString();
                String eString = word[i][1].getText().toString().replaceAll("\"","\\\\\\\"");
                String cString = word[i][2].getText().toString();
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
        listener.addWordInteraction(jsonObject);
    }

}
