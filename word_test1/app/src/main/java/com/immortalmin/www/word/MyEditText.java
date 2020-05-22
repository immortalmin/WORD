package com.immortalmin.www.word;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


/**
 * 参考博客：https://www.jianshu.com/p/7717cda9eb2a
 */
public class MyEditText extends android.support.v7.widget.AppCompatEditText {

    private Context context;
    private Paint mpaint = new Paint();
    private Button clear_btn;
    private Drawable clear_img;


    public MyEditText(Context context) {
        super(context);
        init(context, null);
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int xDown = (int) event.getX();
        Log.i("ccc","x:"+event.getX()+" y:"+event.getY());
        if (event.getAction() == MotionEvent.ACTION_DOWN && xDown >= (getWidth() - getCompoundPaddingRight() * 2) && xDown < getWidth()) {
            // 清除按钮的点击范围 按钮自身大小 +-padding
            setText("");
            Log.i("ccc","MyEditText clicked");
            return false;
        }
        super.onTouchEvent(event);
        return true;
    }

    private void init(Context context, AttributeSet attrs) {
        clear_img = getResources().getDrawable(R.drawable.del3);
        clear_img.setBounds(0,0,50,50);

//        clear_btn.setCompoundDrawables(null,null,clear_img,null);
//        setCompoundDrawables(null,null,clear_img,null);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //设置右内边距, 防止清除按钮和文字重叠
        setPadding(getPaddingLeft(), getPaddingTop(), 50, getPaddingBottom());
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if(text.length()>0&&isFocused()){
            setCompoundDrawables(null,null,clear_img,null);
        }else{
            setCompoundDrawables(null,null,null,null);
        }
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        Log.i("ccc",""+focused);
        if(focused&&getText().toString().length()>0){
            setCompoundDrawables(null,null,clear_img,null);
        }else{
            setCompoundDrawables(null,null,null,null);
        }
    }
}
