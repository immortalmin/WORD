package com.immortalmin.www.word;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PickerDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private PickerDialog.OnDialogInteractionListener listener;
    private WheelPicker wheelpicker;

    public PickerDialog(Context context) {
        super(context);
        this.context = context;
    }

    public PickerDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    protected PickerDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        View view = View.inflate(context,R.layout.wheelpicker,null);
        listener = (PickerDialog.OnDialogInteractionListener) context;//绑定回调函数的监听器
        wheelpicker = (WheelPicker)view.findViewById(R.id.wheelpicker);
//        word_group = (EditText)view.findViewById(R.id.word_group);

        init();
    }

    private void init() {
        List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
        ArrayList<Object> data = new ArrayList<>();
        data.addAll(list);
        wheelpicker.setDataList(data);
    }

    public interface OnDialogInteractionListener {
        // TODO: Update argument type and name
        void PickerInteraction(JSONObject jsonObject);
    }

    public void onClick(View view){
        switch (view.getId()){

        }
    }
}
