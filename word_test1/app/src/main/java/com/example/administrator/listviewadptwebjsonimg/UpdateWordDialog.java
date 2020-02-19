package com.example.administrator.listviewadptwebjsonimg;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import java.util.HashMap;

public class UpdateWordDialog extends Dialog implements View.OnClickListener{

    private Context context;
    private HashMap<String,Object>data = null;
    private Button commit_btn,cancel_btn;
    private EditText word_group,C_meaning,page;
    private OnDialogInteractionListener listener;
    public UpdateWordDialog(Context context) {
        super(context);
        this.context=context;
    }

    public UpdateWordDialog(Context context, int themeResId, HashMap<String,Object>data) {
        super(context, themeResId);
        this.context=context;
        this.data = data;
    }

    protected UpdateWordDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(context,R.layout.update_word,null);
        listener = (OnDialogInteractionListener) context;//绑定回调函数的监听器
        word_group = (EditText)view.findViewById(R.id.word_group);
        C_meaning = (EditText)view.findViewById(R.id.C_meaning);
        page = (EditText)view.findViewById(R.id.page);
        commit_btn = (Button)view.findViewById(R.id.commit_btn);
        cancel_btn = (Button)view.findViewById(R.id.cancel_btn);

        commit_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
        mHandler.obtainMessage(0).sendToTarget();
        setContentView(view);
    }

    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what == 0){
                word_group.setText(data.get("word_group").toString());
                C_meaning.setText(data.get("C_meaning").toString());
                page.setText(data.get("page").toString());
            }
        }
    };


    public interface OnDialogInteractionListener {
        // TODO: Update argument type and name
        void updateWordInteraction(JSONObject jsonObject);
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

            default:
                dismiss();
        }

    }



    private boolean judge(){
        String s1,s2;
        s1 = word_group.getText().toString();
        s2 = C_meaning.getText().toString();
        if(s1.length()==0||s2.length()==0){
            Toast.makeText(context,"请填写完整",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void pack_data(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("id",data.get("wid").toString());
            jsonObject.put("word_group",word_group.getText().toString());
            jsonObject.put("C_meaning",C_meaning.getText().toString());
            jsonObject.put("page",page.getText().toString());
        }catch (JSONException e){
            e.printStackTrace();
        }
        Toast.makeText(context,"修改成功",Toast.LENGTH_SHORT).show();
        dismiss();
        listener.updateWordInteraction(jsonObject);
    }

}
