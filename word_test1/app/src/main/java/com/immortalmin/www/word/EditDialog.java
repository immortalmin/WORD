package com.immortalmin.www.word;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class EditDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private EditDialog.OnDialogInteractionListener listener;
    private TextView edit_title;
    private EditText edit_et;
    private Button confirm_btn,cancel_btn;
    private JSONObject data;


    public EditDialog(Context context) {
        super(context);
        this.context=context;
    }

    public EditDialog(Context context, int themeResId,JSONObject data) {
        super(context, themeResId);
        this.context = context;
        this.data = data;
    }

    protected EditDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(context,R.layout.edit_dialog,null);
        listener = (EditDialog.OnDialogInteractionListener) context;//绑定回调函数的监听器
        edit_title = (TextView)view.findViewById(R.id.edit_title);
        edit_et = (EditText)view.findViewById(R.id.edit_et);
        confirm_btn = (Button)view.findViewById(R.id.confirm_btn);
        cancel_btn = (Button)view.findViewById(R.id.cancel_btn);

        confirm_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
        setContentView(view);
        mHandler.obtainMessage(0).sendToTarget();
//        word_group.setText(word_text);
//        if(word_text.length()>0){
//            setfocus();
//        }
    }

    /**
     * 为word_group自动获取焦点
     */
    private void setfocus() {
//        C_meaning.setFocusable(true);
//        C_meaning.setFocusableInTouchMode(true);
//        C_meaning.requestFocus();
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.confirm_btn:
                judge();
                break;
            case R.id.cancel_btn:
                dismiss();
                break;
        }
    }

    private void judge() {
        listener.EditInteraction(edit_et.getText().toString());
    }


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    try{
                        edit_title.setText(data.getString("title"));
                        edit_et.setText(data.getString("content"));
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    break;
            }
            return false;
        }
    });

    public interface OnDialogInteractionListener {
        // TODO: Update argument type and name
        void EditInteraction(String res);
    }

}
