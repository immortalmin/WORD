package com.example.administrator.listviewadptwebjsonimg;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AddWordDialog extends Dialog implements View.OnClickListener{

    private Context context;
    private Button commit_btn,cancel_btn,add_btn;
    private EditText word_group,C_meaning,page,word_meaning,E_sentence,C_translate;
    private OnDialogInteractionListener listener;
    public AddWordDialog(Context context) {
        super(context);
        this.context=context;
    }

    public AddWordDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context=context;
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
        word_group = (EditText)view.findViewById(R.id.word_group);
        C_meaning = (EditText)view.findViewById(R.id.C_meaning);
        page = (EditText)view.findViewById(R.id.page);
        word_meaning = (EditText)view.findViewById(R.id.word_meaning);
        E_sentence = (EditText)view.findViewById(R.id.E_sentence);
        C_translate = (EditText)view.findViewById(R.id.C_translate);
        commit_btn = (Button)view.findViewById(R.id.commit_btn);
        cancel_btn = (Button)view.findViewById(R.id.cancel_btn);
        add_btn = (Button)view.findViewById(R.id.add_btn);
        commit_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
        add_btn.setOnClickListener(this);
        setContentView(view);
    }

    public interface OnDialogInteractionListener {
        // TODO: Update argument type and name
        void addWordInteraction(JSONObject jsonObject);
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.commit_btn:
                pack_data();
                break;
            case R.id. cancel_btn:
                dismiss();
                break;
            case R.id. add_btn:

                break;
        }
        dismiss();
    }

    private void pack_data(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("word_group",word_group.getText().toString());
            jsonObject.put("C_meaning",C_meaning.getText().toString());
            jsonObject.put("page",page.getText().toString());
            JSONArray jsonArray = new JSONArray();
            JSONObject translate = new JSONObject();
            translate.put("word_meaning",word_meaning.getText().toString());
            translate.put("E_sentence",E_sentence.getText().toString());
            translate.put("C_translate",C_translate.getText().toString());
            jsonArray.put(translate);
            jsonObject.put("translate",jsonArray);
        }catch (JSONException e){
            e.printStackTrace();
        }
        listener.addWordInteraction(jsonObject);
    }

}
