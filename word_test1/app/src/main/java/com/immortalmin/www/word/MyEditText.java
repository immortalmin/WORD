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
    private Canvas canvas;
    private Paint mPaint = new Paint();
    private Bitmap visible_bitmap,invisible_bitmap,clear_bitmap,paste_bitmap;
    private Drawable clear_img,paste_img,seen_img,unseen_img;
    private int btn_length = 50,btn_padding=10;
    private boolean isVisible = true;
    /**
     * 0:普通输入框，带删除按钮
     * 1:带删除按钮和粘贴按钮
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

        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyEditText);
            //输入框类型
            TextType = array.getInteger(R.styleable.MyEditText_TextType,0);
            array.recycle();
        }
        //获取图标资源
        visible_bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.seen_icon);
        invisible_bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.unseen_icon);
        clear_bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.del3);
        paste_bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.paste);
        setTextColor(Color.BLACK);
        setBackground(getResources().getDrawable(R.drawable.word_input));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
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
        Rect rect = new Rect(width-btn_length-btn_padding,height-btn_padding-btn_length,width-btn_padding,height-btn_padding);
        canvas.drawBitmap(clear_bitmap,null,rect,mPaint);
    }

    private void drawPasteButton(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        Rect rect = new Rect(width-btn_length-btn_padding,height-btn_padding-btn_length,width-btn_padding,height-btn_padding);
        canvas.drawBitmap(paste_bitmap,null,rect,mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //设置右内边距, 防止清除按钮和文字重叠
        setPadding(20, 20, 20, btn_length+btn_padding);
    }
}
