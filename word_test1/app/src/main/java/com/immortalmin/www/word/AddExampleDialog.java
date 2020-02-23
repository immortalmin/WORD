package com.immortalmin.www.word;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
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

import java.util.Arrays;

public class AddExampleDialog extends Dialog implements View.OnClickListener{

    private Context context;
    private Button commit_btn,cancel_btn;
    private EditText word_group,C_meaning,page;
    private TextView tv2;
    private OnDialogInteractionListener listener;
    private EditText[][] word = new EditText[100][5];
    private LinearLayout[][] word_layout = new LinearLayout[100][5];
    private RelativeLayout[] btn_layout = new RelativeLayout[100];
    private LinearLayout example_layout;
    private Button[] del_btn = new Button[100];
    private Button[] add_btn = new Button[100];
    private int id;
    private int index=0;
    private boolean[] del_flag = new boolean[100];
    public AddExampleDialog(Context context) {
        super(context);
        this.context=context;
    }

    public AddExampleDialog(Context context, int themeResId,int id) {
        super(context, themeResId);
        this.context=context;
        this.id = id;
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
        example_layout = (LinearLayout) view.findViewById(R.id.example_layout);
        tv2 = (TextView)view.findViewById(R.id.tv2);
        commit_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
        tv2.setOnClickListener(this);
        setContentView(view);
        Arrays.fill(del_flag,true);
        add_view();
    }

    public interface OnDialogInteractionListener {
        // TODO: Update argument type and name
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
                dismiss();
                break;
            case R.id.tv2:
                add_view();
                break;
            default:
                dismiss();
        }

    }

    private void add_view(){
        String[] hint = {"在例句中的意思","英文例句","中文翻译"};
        for(int i=0;i<3;i++){
            // 1.创建外围LinearLayout控件
            word_layout[index][i] = new LinearLayout(context);
            LinearLayout.LayoutParams eLayoutlayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            eLayoutlayoutParams.setMargins(((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, context.getResources().getDisplayMetrics())),
                    ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources().getDisplayMetrics())),
                    ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, context.getResources().getDisplayMetrics())), 0);
            eLayoutlayoutParams.setLayoutDirection(LinearLayout.HORIZONTAL);
            word_layout[index][i].setLayoutParams(eLayoutlayoutParams);
            word_layout[index][i].setGravity(Gravity.CENTER);
            Drawable d = ResourcesCompat.getDrawable(context.getResources(), R.drawable.word_input, null);
            word_layout[index][i].setBackground(d);
            word_layout[index][i].setPadding(((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics())),0,
                    ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics())),0);
            word_layout[index][i].setOrientation(LinearLayout.HORIZONTAL);
            //2.word_meaning
            word[index][i] = new EditText(context);
            LinearLayout.LayoutParams word_meaning_Params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics())));
            word[index][i].setPadding(0,0,0,0);
            word[index][i].setLayoutParams(word_meaning_Params);
            word[index][i].setBackgroundColor(Color.parseColor("#00000000"));
            word[index][i].setHint(hint[i]);
            word_layout[index][i].addView(word[index][i]);
            example_layout.addView(word_layout[index][i]);
        }
        btn_layout[index] = new RelativeLayout(context);
        LinearLayout.LayoutParams btn_layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        btn_layout[index].setLayoutParams(btn_layoutParams);

        del_btn[index] = new Button(context);
        RelativeLayout.LayoutParams del_btn_Params = new RelativeLayout.LayoutParams(
                ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, context.getResources().getDisplayMetrics())),
                ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, context.getResources().getDisplayMetrics())));
//        add_btn_Params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        del_btn_Params.setMargins(((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, context.getResources().getDisplayMetrics())), 0,
                ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, context.getResources().getDisplayMetrics())), 0);
        del_btn_Params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        del_btn[index].setLayoutParams(del_btn_Params);
        del_btn[index].setPadding(0,0,0,0);
        del_btn[index].setBackgroundColor(Color.parseColor("#00000000"));
        del_btn[index].setTextColor(Color.parseColor("#FFFFFF"));
        del_btn[index].setTextSize(20);
        del_btn[index].setText("-");
        del_btn[index].setId(index);

        add_btn[index] = new Button(context);
        RelativeLayout.LayoutParams add_btn_Params = new RelativeLayout.LayoutParams(
                ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, context.getResources().getDisplayMetrics())),
                ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, context.getResources().getDisplayMetrics())));
        add_btn_Params.setMargins(((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, context.getResources().getDisplayMetrics())), 0,
                ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics())), 0);
        add_btn_Params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        add_btn[index].setLayoutParams(add_btn_Params);
        add_btn[index].setPadding(0,0,0,0);
        add_btn[index].setBackgroundColor(Color.parseColor("#00000000"));
        add_btn[index].setTextColor(Color.parseColor("#FFFFFF"));
        add_btn[index].setTextSize(20);
        add_btn[index].setText("+");
        add_btn[index].setId(index);
        add_btn[index].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_view();
            }
        });
        final int ind=index;
        del_btn[index].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                del_flag[ind]=false;
                example_layout.removeView(word_layout[ind][0]);
                example_layout.removeView(word_layout[ind][1]);
                example_layout.removeView(word_layout[ind][2]);
                example_layout.removeView(btn_layout[ind]);
            }
        });
        btn_layout[index].addView(add_btn[index]);
        btn_layout[index].addView(del_btn[index]);
        example_layout.addView(btn_layout[index]);
        index++;
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
//            jsonObject.put("word_group",word_group.getText().toString());
//            jsonObject.put("C_meaning",C_meaning.getText().toString());
//            jsonObject.put("page",page.getText().toString());
            jsonObject.put("id",id);
            JSONArray jsonArray = new JSONArray();
            for(int i=0;i<index;i++){
                JSONObject translate = new JSONObject();
                String wString = word[i][0].getText().toString();
                String eString = word[i][1].getText().toString();
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
        listener.addExampleInteraction(jsonObject);
    }

}
