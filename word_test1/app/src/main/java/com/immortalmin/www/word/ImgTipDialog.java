package com.immortalmin.www.word;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

public class ImgTipDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private ImgTipDialog.OnDialogInteractionListener listener;
    private Button confirm_btn,cancel_btn;
    private ImageView imageView;
    private Bitmap img;


    public ImgTipDialog(Context context) {
        super(context);
        this.context=context;
    }

    public ImgTipDialog(Context context, int themeResId, Bitmap img) {
        super(context, themeResId);
        this.context = context;
        this.img = img;
    }

    protected ImgTipDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(context,R.layout.img_tip_dialog,null);
        listener = (ImgTipDialog.OnDialogInteractionListener) context;//绑定回调函数的监听器
        imageView = view.findViewById(R.id.imageview);
        confirm_btn = view.findViewById(R.id.confirm_btn);
        cancel_btn = view.findViewById(R.id.cancel_btn);

        confirm_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);

        setContentView(view);
        mHandler.obtainMessage(0).sendToTarget();
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
                dismiss();
                listener.ImgTipInteraction(1);
                break;
            case R.id.cancel_btn:
                dismiss();
                listener.ImgTipInteraction(0);
                break;
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    imageView.setImageBitmap(img);
                    break;
            }
            return false;
        }
    });



    public interface OnDialogInteractionListener {
        void ImgTipInteraction(int res);
    }

}
