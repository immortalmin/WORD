package com.immortalmin.www.word;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.HashMap;

public class EditDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private EditDialog.OnDialogInteractionListener listener;
    private TextView edit_title,stat_tv;
    private EditText edit_et;
    private Button confirm_btn,cancel_btn,clean_btn;
    private HashMap<String,Object> data;
    private int max_length = 50;


    public EditDialog(Context context) {
        super(context);
        this.context=context;
    }

    /**
     * @param data
     *      title:       标题
     *      max_length:  最大长度
     *      is_null:     是否可以为空
     *      hint:        提示文
     *      content:     初始内容
    */
    public EditDialog(Context context, int themeResId,HashMap<String,Object> data) {
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
        edit_title = view.findViewById(R.id.edit_title);
        stat_tv = view.findViewById(R.id.stat_tv);
        edit_et = view.findViewById(R.id.edit_et);
        confirm_btn = view.findViewById(R.id.confirm_btn);
        cancel_btn = view.findViewById(R.id.cancel_btn);
        clean_btn = view.findViewById(R.id.clean_btn);

        confirm_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
        clean_btn.setOnClickListener(this);

        edit_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = (int)getTextLength(edit_et.getText().toString())+"/"+max_length;
                stat_tv.setText(text);
            }
        });


        setContentView(view);
        mHandler.obtainMessage(0).sendToTarget();
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.confirm_btn:
                if("false".equals(data.get("is_null").toString())&&edit_et.getText().toString().length()==0){
                    break;
                }
                commitData();
                break;
            case R.id.cancel_btn:
                dismiss();
                break;
            case R.id.clean_btn:
                edit_et.setText("");
                break;
        }
    }

    private void commitData() {
        data.put("content",edit_et.getText().toString());
        dismiss();
        listener.EditInteraction(data);
    }


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    max_length = Integer.valueOf(data.get("max_length").toString());
                    // 这个方法，返回空字符串，就代表匹配不成功，返回null代表匹配成功
                    edit_et.setFilters(new InputFilter[]{(source, start, end, dest, dstart, dend) -> {
                        // 获取字符个数(一个中文算2个字符)
                        if (getTextLength(dest.toString()) + getTextLength(source.toString()) > max_length) {
                            return "";
                        }
                        return null;
                    }});
                    edit_title.setText(data.get("title").toString());
                    edit_et.setHint(data.get("hint").toString());
                    edit_et.setText(data.get("content").toString());
                    edit_et.setSelection(edit_et.getText().length());
                    break;
            }
            return false;
        }
    });

    /**
     * 获取字符数量 汉字占2个，英文占一个
     */
    static double getTextLength(String text) {
        double length = 0;
        for (int i = 0; i < text.length(); i++) {
            // text.charAt(i)获取当前字符是的chart值跟具ASCII对应关系255以前的都是英文或者符号之等而中文并不在这里面所以此方法可行</span>
            if (text.charAt(i) > 255) {
                length += 2;
            } else {
                length++;
            }
        }
        return length;
    }

    public interface OnDialogInteractionListener {
        void EditInteraction(HashMap<String,Object> res);
    }

}
