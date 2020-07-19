package com.immortalmin.www.word;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PickerDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private PickerDialog.OnDialogInteractionListener listener;
    private WheelPicker wheelpicker;
    private Button confirm_btn,cancel_btn;
    private ArrayList<Object> data;
    private int what;//编号

    public PickerDialog(Context context,ArrayList<Object> data,int what) {
        super(context);
        this.context = context;
        this.data = data;
        this.what = what;
    }

    public PickerDialog(Context context, int themeResId,ArrayList<Object> data,int what) {
        super(context, themeResId);
        this.context = context;
        this.data = data;
        this.what = what;
    }

    protected PickerDialog(Context context, boolean cancelable, OnCancelListener cancelListener,ArrayList<Object> data,int what) {
        super(context, cancelable, cancelListener);
        this.context = context;
        this.data = data;
        this.what = what;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        View view = View.inflate(context,R.layout.wheelpicker,null);
        listener = (PickerDialog.OnDialogInteractionListener) context;//绑定回调函数的监听器
        wheelpicker = (WheelPicker)view.findViewById(R.id.wheelpicker);
        confirm_btn = (Button)view.findViewById(R.id.confirm_btn);
        cancel_btn = (Button)view.findViewById(R.id.cancel_btn);
        confirm_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
        wheelpicker.setDataList(data);
        setContentView(view);
    }


    public interface OnDialogInteractionListener {
        // TODO: Update argument type and name
        void PickerInteraction(JSONObject ret);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.confirm_btn:
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("what",what);
                    jsonObject.put("value",data.get(wheelpicker.getCurrentPosition()));
                }catch (JSONException e){
                    e.printStackTrace();
                }
                listener.PickerInteraction(jsonObject);
                dismiss();
                break;
            case R.id.cancel_btn:
                dismiss();
                break;
        }
    }
}
