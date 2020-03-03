package com.immortalmin.www.word;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.util.HashMap;

/**
 * 注意设置wrap_content后，暂时无法重新设置view的大小，只能适应最开始的文本大小
 * 所以建议 如果会用到setmText(),宽度就设置成match_parent
 */
public class WordView extends View {
    private String mText;//需要绘制的文字
    private int mTextFirstColor,mTextSecondColor;//文本的颜色
    private int mTextSize;//文本的大小
    private int maxTextSize=80;//默认最大字号
    private float account;
    private Rect mBound;
    private Paint mPaint;
    private Paint.FontMetrics fontMetrics;
    private int canvasWidth;//画布宽度
    private int canvasHeight;//画布高度

    public WordView(Context context){
        this(context,null);
    }
    public WordView(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }
    public WordView(Context context,AttributeSet attrs,int defStyleAttr){
        super(context,attrs,defStyleAttr);
        //获取自定义属性
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,R.styleable.WordView,defStyleAttr,0);
        mText = a.getString(R.styleable.WordView_mText);
        mTextFirstColor = a.getColor(R.styleable.WordView_mTextFirstColor,Color.GREEN);
        mTextSecondColor = a.getColor(R.styleable.WordView_mTextSecondColor,Color.WHITE);
        mTextSize = a.getDimensionPixelSize(R.styleable.WordView_mTextSize,100);
        account = a.getFloat(R.styleable.WordView_account,(float)0.5);
        a.recycle();

        mPaint = new Paint();
        mPaint.setTextSize(mTextSize);
        mBound = new Rect();
        mPaint.getTextBounds(mText,0,mText.length(),mBound);
        fontMetrics=mPaint.getFontMetrics();
    }

    @Override
    protected void onDraw(Canvas canvas){
        drawMyText(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);   //获取宽的模式
        int heightMode = MeasureSpec.getMode(heightMeasureSpec); //获取高的模式
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);   //获取宽的尺寸
        int heightSize = MeasureSpec.getSize(heightMeasureSpec); //获取高的尺寸
        int width;
        int height ;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = (int)(mBound.width()+(float)(fontMetrics.bottom*0.5));
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = (int)(mBound.height()+(float)(fontMetrics.bottom*1.5));
        }
        setMeasuredDimension(width, height);
    }

    private void drawMyText(Canvas canvas){
        canvasHeight = getHeight();
        canvasWidth = getWidth();
//        mTextSize = getRightSize();
        int width = canvas.getWidth();
        //字母顶部的坐标
        float TextTop = (float)(fontMetrics.ascent*0.87-fontMetrics.top+fontMetrics.descent*0.13);
        //字母高度
        float MyTextHigh=(float)((fontMetrics.descent-fontMetrics.ascent)*0.87);

//        mTextSize = getRightSize();

        //红色的部分
        mPaint.setColor(mTextFirstColor);
        mPaint.setTextSize(mTextSize);

        mPaint.setTextSize(Math.min(getRightSize(mText),maxTextSize));

        canvas.drawText(mText,0,-fontMetrics.top,mPaint);

        //白色的部分
        mPaint.setColor(mTextSecondColor);
        canvas.save();
        canvas.clipRect(0,0,width,(float)(TextTop+MyTextHigh*(1.0-account)));
        canvas.drawText(mText,0,-fontMetrics.top,mPaint);
        canvas.restore();
    }

    private int getRightSize(String text){
        Rect rect = new Rect();
        mPaint.getTextBounds(text,0,text.length(),rect);
        fontMetrics = mPaint.getFontMetrics();
        float width = rect.width();
        float height = fontMetrics.bottom-fontMetrics.top;
        float canvasWidth = (float)getWidth()*0.9f;
        float canvasHeight = (float)getHeight()*0.9f;
        return Math.min((int)Math.floor(canvasWidth*(float)mTextSize/width),(int)Math.floor(canvasHeight*(float)mTextSize/height));
    }

    private void drawLine(Canvas canvas){
        canvas.save();
        mPaint.setColor(Color.RED);
        fontMetrics=mPaint.getFontMetrics();
//        canvas.drawLine(0,mBound.height(),mBound.width(),mBound.height(),mPaint);
        //横线
        canvas.drawLine(0,mBound.height()+(float)(fontMetrics.bottom*1.5),mBound.width()+(float)(fontMetrics.bottom*0.5),mBound.height()+(float)(fontMetrics.bottom*1.5),mPaint);
        //竖线
        canvas.drawLine(mBound.width()+(float)(fontMetrics.bottom*0.5),0,mBound.width()+(float)(fontMetrics.bottom*0.5),mBound.height()+(float)(fontMetrics.bottom*1.5),mPaint);
        canvas.restore();
    }

    public void setRank(int rank) {

    }

    public void setmText(String mText) {
        this.mText = mText;
        mBound = new Rect();
        mPaint.getTextBounds(mText,0,mText.length(),mBound);
        invalidate();
    }

    public void setAccount(float account) {
        this.account = account;
        invalidate();
    }


    //实际绘制时，需要使用像素进行绘制，此处提供sp 转 px的方法
    private int sp2px(float spValue) {
        final float scale = WordView.this.getResources().getDisplayMetrics().density;
        return (int) (spValue * scale + 0.5f);
    }
}
