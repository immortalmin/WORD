package com.immortalmin.www.word;

import android.content.Context;
import android.content.res.TypedArray;
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
    private Paint mPaint = new Paint();
    private Button clear_btn;
    private Drawable clear_img,paste_img;
    private int btn_width = 50;
    private boolean isShowPaste = false;


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
        if (event.getAction() == MotionEvent.ACTION_DOWN && xDown >= (getWidth() - btn_width*1.5) && xDown < getWidth()) {
            // 清除按钮的点击范围 按钮自身大小 +-padding
            setText("");
            return false;
        }
        super.onTouchEvent(event);
        return true;
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyEditText);
            //是否显示粘贴按钮
            isShowPaste = array.getBoolean(R.styleable.MyEditText_isShowPaste,false);
            array.recycle();
        }
        clear_img = getResources().getDrawable(R.drawable.del3);
        paste_img = getResources().getDrawable(R.drawable.paste);
        clear_img.setBounds(0,0,btn_width,btn_width);
        paste_img.setBounds(0,0,btn_width,btn_width);
        setTextColor(Color.BLACK);
        setBackground(getResources().getDrawable(R.drawable.word_input));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setAntiAlias(true);
//        mPaint.setColor(Color.GREEN);
//
//        if(isFocused()){
//            mPaint.setStrokeWidth(20);
//        }else{
//            mPaint.setStrokeWidth(10);
//        }
//        drawBorder(canvas);
    }

    private void drawBorder(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        canvas.drawRoundRect(0,0,width,height,30,30,mPaint);
//        setTextColor(Color.parseColor("#000000"));
//        canvas.drawRect(0, 0, width, height, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //设置右内边距, 防止清除按钮和文字重叠
        setPadding(20, 20, 10, 20);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if(text.length()>0&&isFocused()){
            setCompoundDrawables(null,null,clear_img,null);
        }else{
            if(isShowPaste){
                setCompoundDrawables(null,null,paste_img,null);
            }else{
                setCompoundDrawables(null,null,null,null);
            }
        }
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if(focused&&getText().toString().length()>0){
            setCompoundDrawables(null,null,clear_img,null);
        }else if(focused&&getText().toString().length()==0&&isShowPaste){
            setCompoundDrawables(null,null,paste_img,null);
        }else{
            setCompoundDrawables(null,null,null,null);
        }
    }
}
