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

public class UpdateExampleDialog extends Dialog implements View.OnClickListener{

    private Context context;
    private HashMap<String,Object>data = null;
    private Button commit_btn,cancel_btn;
    private EditText word_meaning,E_sentence,C_translate;
    private OnDialogInteractionListener listener;
    private boolean cancel_flag=false;
    public UpdateExampleDialog(Context context) {
        super(context);
        this.context=context;
    }

    public UpdateExampleDialog(Context context, int themeResId, HashMap<String,Object>data) {
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
//        Log.i("ccc",context.getClass().getName());
        listener = (OnDialogInteractionListener) context;//绑定回调函数的监听器
//        Log.i("ccc","after context");
        word_meaning = (EditText)view.findViewById(R.id.word_meaning);
        E_sentence = (EditText)view.findViewById(R.id.E_sentence);
        C_translate = (EditText)view.findViewById(R.id.C_translate);
        commit_btn = (Button)view.findViewById(R.id.commit_btn);
        cancel_btn = (Button)view.findViewById(R.id.cancel_btn);

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
                    word_meaning.setText(data.get("word_en").toString());
                    E_sentence.setText(data.get("E_sentence").toString());
                    C_translate.setText(data.get("C_translate").toString());
                    break;
                case 1:
                    cancel_flag=false;
                    break;
            }
            return false;
        }
    });


    public interface OnDialogInteractionListener {
        // TODO: Update argument type and name
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

            default:
//                dismiss();
        }

    }



    private boolean judge(){
        String s1,s2,s3;
        s1 = word_meaning.getText().toString();
        s2 = E_sentence.getText().toString();
        s3 = C_translate.getText().toString();
        if(s1.length()==0||s2.length()==0||s3.length()==0){
            Toast.makeText(context,"请填写完整",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * pack date
     */
    private void pack_data(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("eid",data.get("eid").toString());
            jsonObject.put("word_meaning",word_meaning.getText().toString());
            jsonObject.put("E_sentence",E_sentence.getText().toString().replaceAll("\"","\\\\\\\""));
            jsonObject.put("C_translate",C_translate.getText().toString());
        }catch (JSONException e){
            e.printStackTrace();
        }
        Toast.makeText(context,"修改成功",Toast.LENGTH_SHORT).show();
        dismiss();
        listener.updateExampleInteraction(jsonObject);
    }

}
