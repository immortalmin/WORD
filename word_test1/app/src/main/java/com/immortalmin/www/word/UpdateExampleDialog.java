package com.immortalmin.www.word;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

//XXX:word_en本该设置为有粘贴按钮的输入框，但是因为没办法得到需要的文本内容，暂时搁置
public class UpdateExampleDialog extends Dialog implements View.OnClickListener{

    private Context context;
    private OtherSentence data;
    private Button commit_btn,cancel_btn;
    private MyEditText word_en,E_sentence,C_translate;
    private OnDialogInteractionListener listener;
    private boolean cancel_flag=false;
    public UpdateExampleDialog(Context context) {
        super(context);
        this.context=context;
    }

    public UpdateExampleDialog(Context context, int themeResId, OtherSentence data) {
        super(context, themeResId);
        this.context=context;
        this.data = data;
    }

    protected UpdateExampleDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(context,R.layout.update_example,null);
        listener = (OnDialogInteractionListener) context;//绑定回调函数的监听器
        word_en = view.findViewById(R.id.word_en);
        E_sentence = view.findViewById(R.id.E_sentence);
        C_translate = view.findViewById(R.id.C_translate);
        commit_btn = view.findViewById(R.id.commit_btn);
        cancel_btn = view.findViewById(R.id.cancel_btn);
        commit_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);

        mHandler.obtainMessage(0).sendToTarget();
        setContentView(view);
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 0:
                    word_en.setText(data.getWord_meaning());
                    E_sentence.setText(data.getSentence_en());
                    C_translate.setText(data.getSentence_ch());
                    break;
                case 1:
                    cancel_flag=false;
                    break;
            }
            return false;
        }
    });


    public interface OnDialogInteractionListener {
        void updateExampleInteraction(JSONObject jsonObject);
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
                    mHandler.sendEmptyMessageDelayed(1,500);
                }else{
                    dismiss();
                }
                break;
        }

    }


    private boolean judge(){
        String s1,s2,s3;
        s1 = word_en.getText().toString();
        s2 = E_sentence.getText().toString();
        s3 = C_translate.getText().toString();
        if(s1.length()==0||s2.length()==0||s3.length()==0){
            Toast.makeText(context,"请填写完整",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void pack_data(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("eid",data.getEid());
            jsonObject.put("word_meaning",word_en.getText().toString().replaceAll("\"","\\\\\\\""));
            jsonObject.put("E_sentence",E_sentence.getText().toString().replaceAll("\"","\\\\\\\""));
            jsonObject.put("C_translate",C_translate.getText().toString().replaceAll("\"","\\\\\\\""));
        }catch (JSONException e){
            e.printStackTrace();
        }
        Toast.makeText(context,"修改成功",Toast.LENGTH_SHORT).show();
        dismiss();
        listener.updateExampleInteraction(jsonObject);
    }

}
