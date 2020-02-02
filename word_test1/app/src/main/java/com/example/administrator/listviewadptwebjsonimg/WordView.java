package com.example.administrator.listviewadptwebjsonimg;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.HashMap;

public class WordView extends View {
    private String mText;//需要绘制的文字
    private int mTextColor;//文本的颜色
    private int mTextSize;//文本的大小
    //掌握程度
    private int[] Rank = new int[]{
            //Color.BLACK,//黑，代表是新词
            Color.parseColor("#e1e1e1"),//灰
            Color.parseColor("#03a89e"),//绿
            Color.parseColor("#0000ff"),//紫
            Color.parseColor("#aa00b1"),//紫
            Color.parseColor("#ff8c00"),//橙黄
            Color.parseColor("#ff0000"),//红

    };
    /**
     * 绘制时控制文本绘制的范围
     */
    private Rect mBound;
    private Paint mPaint;

    public WordView(Context context){
        this(context,null);
    }
    public WordView(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }
    public WordView(Context context,AttributeSet attrs,int defStyleAttr){
        super(context,attrs,defStyleAttr);
        /*//初始化
        mText = "help sb. to do sth.";
        mTextColor = Color.BLACK;
        mTextSize = 100;*/

        //获取自定义属性
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,R.styleable.WordView,defStyleAttr,0);
        mText = a.getString(R.styleable.WordView_mText);
        mTextColor = a.getColor(R.styleable.WordView_mTextColor,Color.BLACK);
//        mTextSize = a.getDimension(R.styleable.WordView_mTextSize,100);
        mTextSize = a.getDimensionPixelSize(R.styleable.WordView_mTextSize,100);
        a.recycle();


        mPaint = new Paint();
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);
        //获得绘制文本的宽和高
        mBound = new Rect();
        mPaint.getTextBounds(mText,0,mText.length(),mBound);
    }

    @Override
    protected void onDraw(Canvas canvas){

        Paint.FontMetrics fontMetrics=mPaint.getFontMetrics();
        Log.i("ccc","top"+String.valueOf(fontMetrics.top));
        Log.i("ccc","ascent"+String.valueOf(fontMetrics.ascent ));
        Log.i("ccc","descent"+String.valueOf(fontMetrics.descent));
        Log.i("ccc","bottom"+String.valueOf(fontMetrics.bottom));
        //绘制文字
        canvas.drawText(mText,getWidth()/2-mBound.width()/2,getHeight()/2+mBound.height()/2,mPaint);
//        canvas.drawText(mText,getWidth()/2-mBound.width()/2,0,mPaint);
//        canvas.drawText(mText,0,0,mPaint);
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
            //如果match_parent或者具体的值，直接赋值
            width = widthSize;
        } else {
            //如果是wrap_content，我们要得到控件需要多大的尺寸
            float textWidth = mBound.width();   //文本的宽度
            //控件的宽度就是文本的宽度加上两边的内边距。内边距就是padding值，在构造方法执行完就被赋值
            width = (int) (getPaddingLeft() + textWidth + getPaddingRight()+10);
//            Log.v("openxu", "文本的宽度:"+textWidth + "控件的宽度："+width);
        }
        //高度跟宽度处理方式一样
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            float textHeight = mBound.height();
            height = (int) (getTop() + textHeight + getBottom());
//            height = (int) (getPaddingTop() + textHeight + getPaddingBottom());
//            Log.v("openxu", "文本的高度:"+textHeight + "控件的高度："+height);
        }
        //保存测量宽度和测量高度
        setMeasuredDimension(width, height);
    }

    public void setRank(int rank) {
        if(rank>5){
            rank=5;
        }
        mPaint.setColor(Rank[rank]);
        invalidate();
    }

    public void setmText(String mText) {
        this.mText = mText;
        mBound = new Rect();
        mPaint.getTextBounds(mText,0,mText.length(),mBound);
        invalidate();
    }
}
