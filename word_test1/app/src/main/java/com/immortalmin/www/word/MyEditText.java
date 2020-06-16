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
import android.util.Property;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.w3c.dom.Text;

import java.nio.channels.NonReadableChannelException;
import java.util.Locale;


/**
 * 参考博客：https://www.jianshu.com/p/7717cda9eb2a
 * 未实现动画特效
 * 选中框样式
 */
public class MyEditText extends android.support.v7.widget.AppCompatEditText {

    private Context context;
    private Paint mPaint = new Paint();
    private Bitmap visible_bitmap,invisible_bitmap,clear_bitmap,paste_bitmap;
    private int btn_length = 50,btn_padding=10;//按钮边长、按钮边距
    private int padding = 20;
    private int hindTextSize = 40;
    private Paint.FontMetrics fontMetrics;
    private boolean isVisible = true;//是否是可见文本
    private boolean isFold = true;//是否折叠
    private String pasteString = "";//粘贴文本
    private String hindString = "";//提示文本
    private OnVisibleActionListener mVisible = null;

    /**
     * 显示风格
     * 0:按钮与文本显示在同一行,单行文本
     * 1:按钮显示在文本的右上角，多行文本
     */
    private int DisplayStyle=0;

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
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(DisplayStyle==0){//单行情况下
                if(TextType==0){
                    if(xDown >= (getWidth() - btn_length-btn_padding) && xDown < getWidth()-btn_padding){
                        setText("");
                    }
                }else if(TextType==1){
                    if(xDown >= (getWidth() - btn_length-btn_padding) && xDown < getWidth()-btn_padding){
                        if(getText().length()==0){
                            setText(pasteString);
                        }else{
                            setText("");
                        }
                    }
                }else{
                    if(xDown >= (getWidth() - btn_length-btn_padding) && xDown < getWidth()-btn_padding){
                        setText("");
                    }else if(xDown >= getWidth()-btn_length*2-btn_padding*2 && xDown < getWidth()-btn_length-btn_padding*2){
                        if(isVisible){
                            setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                        }else{
                            setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        }
                        setSelection(getText().length());
                        isVisible = !isVisible;
                        mVisible.OnVisible();
                    }
                }
            }else{//多行情况下
                if(TextType==0){
                    if(xDown >= (getWidth() - btn_length-btn_padding) && xDown < getWidth()-btn_padding&& yDown>=0 && yDown<=btn_length+btn_padding){
                        setText("");
                    }
                }else if(TextType==1){
                    if(xDown >= (getWidth() - btn_length-btn_padding) && xDown < getWidth()-btn_padding&& yDown>=0 && yDown<=btn_length+btn_padding){
                        if(getText().length()==0){
                            setText(pasteString);
                        }else{
                            setText("");
                        }
                    }
                }
            }
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
        if(getHint()!=null){
            hindString = getHint().toString();
        }
        if(DisplayStyle==0){
            setSingleLine();
        }else{
            setSingleLine(false);
        }
        if(TextType==2){
            isVisible=false;
            setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
        }

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
        if("".equals(hindString)){
            hindString = getHint().toString();
        }
        if(DisplayStyle==1){
            if(!isFold){
                drawText(canvas);
            }else if(!"".equals(hindString)){
                setHint(hindString);
            }

        }

    }

    //可见/不可见按钮
    private void drawVisibleButton(Canvas canvas) {
        Rect rect = new Rect(getWidth()+getScrollX()-btn_length*2-btn_padding*2,(getHeight()-btn_length)/2,getWidth()+getScrollX()-btn_length-btn_padding*2,getHeight()-(getHeight()-btn_length)/2);
        if(!isVisible){
            canvas.drawBitmap(visible_bitmap,null,rect,mPaint);
        }else{
            canvas.drawBitmap(invisible_bitmap,null,rect,mPaint);
        }
    }

    //清除按钮
    private void drawClearButton(Canvas canvas) {
        Rect rect;
        if(DisplayStyle==0){
            rect = new Rect(getWidth()+getScrollX()-btn_length-btn_padding,getScrollY()+(getHeight()-btn_length)/2,getWidth()+getScrollX()-btn_padding,getScrollY()+getHeight()-(getHeight()-btn_length)/2);
        }else{
            rect = new Rect(getWidth()+getScrollX()-btn_length-btn_padding,getScrollY()+btn_padding,getWidth()+getScrollX()-btn_padding,getScrollY()+btn_padding+btn_length);
        }
        canvas.drawBitmap(clear_bitmap,null,rect,mPaint);
    }

    //复制按钮
    private void drawPasteButton(Canvas canvas) {
        Rect rect = new Rect(getWidth()+getScrollX()-btn_length-btn_padding,getScrollY()+btn_padding,getWidth()+getScrollX()-btn_padding,getScrollY()+btn_length+btn_padding);
        canvas.drawBitmap(paste_bitmap,null,rect,mPaint);
    }

    //绘制文字
    private void drawText(Canvas canvas){
        mPaint.setTextSize(hindTextSize);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#b1b1b1"));
        fontMetrics = mPaint.getFontMetrics();
        canvas.drawText(hindString,10,getScrollY()+(int)(fontMetrics.descent-fontMetrics.ascent),mPaint);
        setHint("");
    }


    public void setDisplayStyle(int displayStyle) {
        DisplayStyle = displayStyle;
        if(DisplayStyle==0){
            setSingleLine();
        }else{
            setSingleLine(false);
        }
    }

    public void setTextType(int textType) {
        this.TextType = textType;
        if(TextType==2){
            isVisible=false;
            setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
        }
    }

    public void setPasteString(String pasteString) {
        this.pasteString = pasteString;
        invalidate();
    }

    public void setOnVisibleActionListener(OnVisibleActionListener visible){
        mVisible = visible;
    }


    public interface OnVisibleActionListener{
        public void OnVisible();
    }


    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        changeFold(!focused);
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
        }else if(isFold){
            setPadding(20, 20, 20,20 );
        }
    }

    //多行文本下，折叠或者展开
    private void changeFold(boolean foldFlag){
        isFold = foldFlag;
        if(foldFlag){
            setPadding(20, 20, 20,20 );
        }else{
            setPadding(20, btn_length+btn_padding, 20,20 );
        }
        invalidate();
    }
}
