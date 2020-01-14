package com.example.administrator.listviewadptwebjsonimg;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

public class MyTextView extends View {

    private int mTextSize = 15;
    private int mTextColor = Color.BLACK;
    private String mText; //文本
    Paint mPaint;

    public MyTextView(Context context) {
        this(context,null);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    //构造函数里拿到自定义属性 初始化画笔
    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.CountDownProgressBar);
        mText = a.getString(R.styleable.CountDownProgressBar_lctyText);
        mTextColor = a.getColor(R.styleable.CountDownProgressBar_lctyColor,mTextColor);
        mTextSize = a.getDimensionPixelSize(R.styleable.CountDownProgressBar_lctySize,sp2px(mTextSize));
        a.recycle();
        mPaint = new Paint();
        //抗锯齿
        mPaint.setAntiAlias(true);
        //文字大小
        mPaint.setTextSize(mTextSize);
        //文字颜色
        mPaint.setColor(mTextColor);
    }

    //sp单位转为px
    private int sp2px(int size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,size,getResources().getDisplayMetrics());
    }

    //onMeasure方法测绘 设置宽高
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //得到测量模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //得到宽高的值
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);

        //判断测量模式
        //如果mode是EXACTLY（match_parent 100dp） 不用管 直接用户是设置多少就是多少
        //如果mode是AT_MOST(wrap_content) 要判断设置宽高
        if (widthMode == MeasureSpec.AT_MOST){
            //用画笔测量文字的宽高 由字体大小和宽高决定
            Rect bunds = new Rect();
            //得到文字的矩形区域
            mPaint.getTextBounds(mText,0,mText.length(),bunds);
            width = getPaddingRight()+ getPaddingLeft() + bunds.width(); //左右间距加上文本宽度
        }

        if (heightMode == MeasureSpec.AT_MOST){
            //用画笔测量文字的宽高 由字体大小和宽高决定
            Rect bunds = new Rect();
            //得到文字的矩形区域
            mPaint.getTextBounds(mText,0,mText.length(),bunds);
            height = getPaddingBottom()+getPaddingTop()+bunds.height(); //上下间距加上文本高度
        }
        //设置宽高
        setMeasuredDimension(width,height);
    }

    //onDraw()方法进行文字绘制
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //public void drawText(@NonNull String text, float x, float y, @NonNull Paint paint)
        /*  The text to be drawn 要绘制的文字
         *   The x-coordinate of the origin of the text being drawn 绘制的起始点
         *    The y-coordinate of the baseline of the text being drawn 绘制的基线
         *    The paint used for the text (e.g. color, size, style) 画笔
         *    dy是整个文字的高度的一般和基线之间的增量
         */
        Paint.FontMetricsInt metricsInt =  mPaint.getFontMetricsInt();
        int dy = (metricsInt.bottom - metricsInt.top)/2 - metricsInt.bottom;
        int baseline = getHeight()/2 + dy;
        canvas.drawText(mText,getPaddingLeft(),baseline,mPaint);
    }
}