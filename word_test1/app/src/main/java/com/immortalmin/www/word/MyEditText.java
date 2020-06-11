package com.immortalmin.www.word;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Property;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.nio.channels.NonReadableChannelException;
import java.util.Locale;


/**
 * 参考博客：https://www.jianshu.com/p/7717cda9eb2a
 */
public class MyEditText extends android.support.v7.widget.AppCompatEditText {

    private Context context;
    private Paint mPaint = new Paint();
    private Bitmap visible_bitmap,invisible_bitmap,clear_bitmap,paste_bitmap;
    private int btn_length = 50,btn_padding=10;//按钮边长、按钮边距
    private int padding = 20;
    private boolean isVisible = true;//是否是可见文本
    private String pasteString = "";//粘贴文本

    private int mAnimatorProgress = 0;
    private ObjectAnimator mAnimator;
    //出现和消失动画
    private ValueAnimator show_animator;
    private ValueAnimator dismiss_animator;
    private static final int ANIMATOR_TIME = 200;//动画时间
    /**
     * 显示风格
     * 0:按钮与文本显示在同一行,单行文本
     * 1:按钮显示在文本的右上角，多行文本
     */
    private int DisplayStyle;
    /**
     * 0:普通输入框，带删除按钮
     * 1:带删除按钮和粘贴按钮  需要setPasteString
     * 2:密码输入框
     */
    private int TextType = 0;


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
        int yDown = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN && xDown >= (getWidth() - btn_length-btn_padding) && xDown < getWidth()-btn_padding) {
            if((TextType==0||TextType==2)&&getText().length()>0){
                setText("");
            }else if(TextType==1 && yDown>=0 && yDown<=btn_length+btn_padding){
                if(getText().length()>0){
                    setText("");
                }else if(TextType==1){
                    setText(pasteString);
                }
            }
            return false;
        }else if(TextType==2 && event.getAction() == MotionEvent.ACTION_DOWN && xDown >= getWidth()-btn_length*2-btn_padding*2 && xDown < getWidth()-btn_length-btn_padding*2){
            if(isVisible){
                setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
            }else{
                setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
            setSelection(getText().length());
            isVisible = !isVisible;
            return false;
        }
        super.onTouchEvent(event);
        return true;
    }

    private void init(Context context, AttributeSet attrs) {
        this.context=context;
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyEditText);
            //输入框类型
            TextType = array.getInteger(R.styleable.MyEditText_TextType,0);
            //显示风格
            DisplayStyle = array.getInteger(R.styleable.MyEditText_DisplayStyle,0);
            array.recycle();
        }
        //获取图标资源
        visible_bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.seen_icon);
        invisible_bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.unseen_icon);
        clear_bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.del3);
        paste_bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.paste);
        setTextColor(Color.BLACK);
        setBackground(getResources().getDrawable(R.drawable.word_input));
        if(DisplayStyle==0){
            setSingleLine();
        }
        show_animator = ValueAnimator.ofFloat(1f, 0f).setDuration(ANIMATOR_TIME);
        dismiss_animator = ValueAnimator.ofFloat(0f, 1f).setDuration(ANIMATOR_TIME);
    }
    private static final Property<MyEditText, Integer> BORDER_PROGRESS
            = new Property<MyEditText, Integer>(Integer.class, "borderProgress") {
        @Override
        public Integer get(MyEditText myEditText) {
            return myEditText.getBorderProgress();
        }

        @Override
        public void set(MyEditText myEditText, Integer value) {
            myEditText.setBorderProgress(value);
        }
    };

    protected void setBorderProgress(int borderProgress) {
        mAnimatorProgress = borderProgress;
        postInvalidate();
    }

    protected int getBorderProgress() {
        return mAnimatorProgress;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (TextType){
            case 0:
                if(isFocused()&&getText().length()>0){
                    drawClearButton(canvas);
                }
                break;
            case 1:
                if(isFocused()){
                    if(getText().length()>0){
                        drawClearButton(canvas);
//                        drawClearButton(1,canvas);
                    }else{
                        drawPasteButton(canvas);
                    }
                }
                break;
            case 2:
                if(isFocused()){
                    if(getText().length()>0){
                        drawVisibleButton(canvas);
                        drawClearButton(canvas);
                    }
                }
                break;
        }
    }


    private void drawVisibleButton(Canvas canvas) {
        Rect rect = new Rect(getWidth()+getScrollX()-btn_length*2-btn_padding*2,(getHeight()-btn_length)/2,getWidth()+getScrollX()-btn_length-btn_padding*2,getHeight()-(getHeight()-btn_length)/2);
        if(!isVisible){
            canvas.drawBitmap(visible_bitmap,null,rect,mPaint);
        }else{
            canvas.drawBitmap(invisible_bitmap,null,rect,mPaint);
        }
    }

    private void drawClearButton(Canvas canvas) {
        Rect rect;
        if(DisplayStyle==0){
            rect = new Rect(getWidth()+getScrollX()-btn_length-btn_padding,(getHeight()-btn_length)/2,getWidth()+getScrollX()-btn_padding,getHeight()-(getHeight()-btn_length)/2);
        }else{
            rect = new Rect(getWidth()+getScrollX()-btn_length-btn_padding,btn_padding,getWidth()+getScrollX()-btn_padding,btn_padding+btn_length);
        }
        canvas.drawBitmap(clear_bitmap,null,rect,mPaint);
    }

    private void drawClearButton(float scale, Canvas canvas) {
        //按钮间隔
        int visible_res_padding = 10;
        //按钮宽度
        int visible_res_width = 50;
        int right = (int) (getWidth() + getScrollX() - visible_res_padding - visible_res_width * (1f - scale) / 2f);
        int left = (int) (getWidth() + getScrollX() - visible_res_padding - visible_res_width * (scale + (1f - scale) / 2f));
        int top = (int) ((getHeight() - visible_res_width * scale) / 2);
        int bottom = (int) (top + visible_res_width * scale);
        Rect rect = new Rect(left, top, right, bottom);
        canvas.drawBitmap(clear_bitmap, null, rect, mPaint);
    }


    private void drawPasteButton(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        Rect rect = new Rect(width-btn_length-btn_padding,btn_padding,width-btn_padding,btn_length+btn_padding);
        canvas.drawBitmap(paste_bitmap,null,rect,mPaint);
    }



    public void setPasteString(String pasteString) {
        this.pasteString = pasteString;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //设置右内边距, 防止清除按钮和文字重叠
        if(DisplayStyle==0){
            if(TextType==0){
                setPadding(20, 20, 10+btn_padding+btn_length, 20);
            }else if(TextType==2){
                setPadding(20, 20, 10+2*(btn_padding+btn_length), 20);
            }
        }else{
            setPadding(20, btn_length+btn_padding, 20,20 );
        }
    }
}
