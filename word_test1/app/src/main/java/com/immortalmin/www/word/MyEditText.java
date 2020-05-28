package com.immortalmin.www.word;

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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.nio.channels.NonReadableChannelException;


/**
 * 参考博客：https://www.jianshu.com/p/7717cda9eb2a
 */
public class MyEditText extends android.support.v7.widget.AppCompatEditText {

    private Context context;
    private Paint mPaint = new Paint();
    private Bitmap visible_bitmap,invisible_bitmap,clear_bitmap,paste_bitmap;
    private int btn_length = 50,btn_padding=10;//按钮边长、按钮边距
    private boolean isVisible = true;//是否是可见文本
    private String pasteString = "";//粘贴文本
    /**
     * 显示风格
     * 0:按钮与文本显示在同一行,单行文本
     * 1:按钮显示在文本的右下角，多行文本
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
        if (event.getAction() == MotionEvent.ACTION_DOWN && xDown >= (getWidth() - btn_length-btn_padding) && xDown < getWidth()-btn_padding) {
            if(getText().length()>0){
                setText("");
                Log.i("ccc","delete");
            }else if(TextType==1){
                setText(pasteString);
                Log.i("ccc","paste");
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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setTextSize(getTextSize());
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
        int width = getWidth();
        int height = getHeight();
        Rect rect = new Rect(width-btn_length*2-btn_padding*2,height-btn_padding-btn_length,width-btn_length-btn_padding*2,height-btn_padding);
        if(!isVisible){
            canvas.drawBitmap(visible_bitmap,null,rect,mPaint);
        }else{
            canvas.drawBitmap(invisible_bitmap,null,rect,mPaint);
        }
    }

    private void drawClearButton(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int textWidth = getTextWidth(getText().toString(),mPaint);
        Log.i("ccc","width:"+width+" textWidth:"+textWidth);
        Rect rect;
        if(DisplayStyle==0&&textWidth>width-20-20-btn_length){//文本较长
            Log.i("ccc","文本较长");
            width = 20+textWidth+btn_length+20;
            rect = new Rect(width-btn_length-btn_padding,height-btn_padding-btn_length,width-btn_padding,height-btn_padding);
        }else{
            rect = new Rect(width-btn_length-btn_padding,height-btn_padding-btn_length,width-btn_padding,height-btn_padding);
        }

        canvas.drawBitmap(clear_bitmap,null,rect,mPaint);
    }

    public int dp2px(Context context, float dpValue){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dpValue,context.getResources().getDisplayMetrics());
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private int getTextWidth(String text, Paint paint) {
        Rect rect = new Rect(); // 文字所在区域的矩形
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.width();
    }

    private void drawPasteButton(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        Rect rect = new Rect(width-btn_length-btn_padding,height-btn_padding-btn_length,width-btn_padding,height-btn_padding);
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
            setPadding(20, 20, 10+btn_padding+btn_length, 20);
        }else{
            setPadding(20, 20, 20, btn_length+btn_padding);
        }

    }
}
