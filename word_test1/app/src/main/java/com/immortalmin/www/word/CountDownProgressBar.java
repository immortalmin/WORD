package com.immortalmin.www.word;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import static android.animation.ValueAnimator.REVERSE;


//XXX:有时文字到达一定长度会出现抖动
public class CountDownProgressBar extends View {
    private Paint.FontMetrics fontMetrics;
    /**
     * 进度条最大值
     */
    private int maxValue = 200;

    /**
     * 单词
     */
    private String first_word = "None",second_word = "None",third_word="无";

    /**
     * 当前进度值
     */
    private int currentValue ;

    /**
     * 每次扫过的角度，用来设置进度条圆弧所对应的圆心角，alphaAngle=(currentValue/maxValue)*360
     */
    private float alphaAngle;

    /**
     * 底部圆弧的颜色，默认为Color.LTGRAY
     */
    private int firstColor;

    /**
     * 进度条圆弧块的颜色
     */
    private int secondColor;
    /**
     * 中间文字颜色(默认蓝色)
     */
    private int centerTextColor = Color.BLUE;
    /**
     * 中间文字的字体大小(默认40dp)
     */
    private int centerTextSize,maxSize;//70

    /**
     * 圆环的宽度
     */
    private int circleWidth;

    /**
     * 画圆弧的画笔
     */
    private Paint circlePaint;

    /**
     * 画文字的画笔
     */
    private Paint textPaint;
    /**
     * 是否使用渐变色
     */
    private boolean isShowGradient = false;

    /**
     * 渐变圆周颜色数组
     */
    private int[] colorArray = new int[]{Color.parseColor("#2773FF"),
            Color.parseColor("#27C0D2"), Color.parseColor("#40C66E")};
    private int duration;
    private OnFinishListener listener;
    private ValueAnimator animator;
    private TypedArray ta;

    public CountDownProgressBar(Context context) {
        this(context, null);
    }


    public CountDownProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public CountDownProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CountDownProgressBar,
                defStyleAttr, 0);
        int n = ta.getIndexCount();

        for (int i = 0; i < n; i++) {
            int attr = ta.getIndex(i);
            switch (attr) {
                case R.styleable.CountDownProgressBar_countDown_firstColor:
                    firstColor = ta.getColor(attr, Color.parseColor("#50ea0a30")); //
                    break;
                case R.styleable.CountDownProgressBar_countDown_secondColor:
                    secondColor = ta.getColor(attr, Color.parseColor("#50ea0a30")); //
                    break;
                case R.styleable.CountDownProgressBar_countDown_centerTextSize:
                    centerTextSize = ta.getDimensionPixelSize(attr, (int) dip2px(40)); // 默认中间文字字体大小为40dp
                    break;
                case R.styleable.CountDownProgressBar_countDown_circleWidth:
                    circleWidth = ta.getDimensionPixelSize(attr, (int) dip2px(6f)); // 默认圆弧宽度为6dp
                    break;
                case R.styleable.CountDownProgressBar_countDown_centerTextColor:
                    centerTextColor = ta.getColor(attr, Color.BLUE); // 默认中间文字颜色为蓝色
                    break;
                case R.styleable.CountDownProgressBar_countDown_isShowGradient:
                    isShowGradient = ta.getBoolean(attr, false); // 默认不适用渐变色
                    break;
                default:
                    break;
            }
        }
        ta.recycle();

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true); // 抗锯齿
        circlePaint.setDither(true); // 防抖动
        circlePaint.setStrokeWidth(circleWidth);//画笔宽度

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setDither(true);

        maxSize = DisplayUtil.dip2px(context,20);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 分别获取期望的宽度和高度，并取其中较小的尺寸作为该控件的宽和高,并且不超过屏幕宽高
        int widthPixels = this.getResources().getDisplayMetrics().widthPixels;//获取屏幕宽
        int heightPixels = this.getResources().getDisplayMetrics().heightPixels;//获取屏幕高
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int hedight = MeasureSpec.getSize(heightMeasureSpec);
        int minWidth = Math.min(widthPixels, width);
        int minHedight = Math.min(heightPixels, hedight);
        setMeasuredDimension(Math.min(minWidth, minHedight), Math.min(minWidth, minHedight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int center = this.getWidth() / 2;
        int radius = center - circleWidth / 2;
        drawCircle(canvas, center, radius); // 绘制进度圆弧
        drawText(canvas, center);
    }

    /**
     * 绘制进度圆弧
     *
     * @param canvas 画布对象
     * @param center 圆心的x和y坐标
     * @param radius 圆的半径
     */
    private void drawCircle(Canvas canvas, int center, int radius) {
        circlePaint.setShader(null); // 清除上一次的shader
        circlePaint.setColor(firstColor); // 设置底部圆环的颜色，这里使用第一种颜色
        circlePaint.setStyle(Paint.Style.STROKE); // 设置绘制的圆为空心
        canvas.drawCircle(center, center, radius, circlePaint); // 画底部的空心圆
        RectF oval = new RectF(center - radius, center - radius, center + radius, center + radius); // 圆的外接正方形
        if (isShowGradient) {
            // 绘制颜色渐变圆环
            // shader类是Android在图形变换中非常重要的一个类。Shader在三维软件中我们称之为着色器，其作用是来给图像着色。
            LinearGradient linearGradient = new LinearGradient(circleWidth, circleWidth, getMeasuredWidth()
                    - circleWidth, getMeasuredHeight() - circleWidth, colorArray, null, Shader.TileMode.MIRROR);
            circlePaint.setShader(linearGradient);
        }
        circlePaint.setShadowLayer(10, 10, 10, Color.TRANSPARENT);
        circlePaint.setColor(secondColor); // 设置圆弧的颜色
        circlePaint.setStrokeCap(Paint.Cap.ROUND); // 把每段圆弧改成圆角的

        alphaAngle = currentValue * 360.0f / maxValue * 1.0f; // 计算每次画圆弧时扫过的角度，这里计算要注意分母要转为float类型，否则alphaAngle永远为0
        canvas.drawArc(oval, -90, alphaAngle, false, circlePaint);
    }

    /**
     * 绘制文字
     *
     * @param canvas 画布对象
     * @param center 圆心的x和y坐标
     */
    private void drawText(Canvas canvas, int center) {
        String word;
        //这里也要设置文字的大小是因为...，大概是因为后面获取fontMetrics需要吧。
        textPaint.setTextSize(centerTextSize);
        if (maxValue == currentValue) {//显示结果
            //因为有两行，所以选择其中长的那一个作为文字大小的自适应的依据
            //比较时second_word要除以2是因为英文字母占的宽度大约只有汉字的一半
            word = (second_word.length()/2>third_word.length()?second_word:third_word);
            textPaint.setTextSize(getRightSize(word));
        } else {//显示结果前
            word = first_word;
            textPaint.setTextSize(getRightSize(word));
        }

        textPaint.setTextAlign(Paint.Align.CENTER); // 设置文字居中，文字的x坐标要注意
        textPaint.setColor(centerTextColor); // 设置文字颜色

        FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        int baseline = center + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom; // 计算文字的基线,方法见http://blog.csdn.net/harvic880925/article/details/50423762
        // 绘制表示进度的文字
        if (maxValue == currentValue){
            //word_en
            textPaint.setTextSize(getRightSize(second_word));
            canvas.drawText(second_word, center, baseline-getTextHeight(second_word)*0.8f, textPaint);
            //word_ch
            textPaint.setTextSize(getRightSize(third_word));
            canvas.drawText(third_word,center,baseline+getTextHeight(third_word)*0.8f,textPaint);
        }else{
            canvas.drawText(first_word, center, baseline, textPaint);
        }
    }

    /**
     * 根据文字的长度，设置文字的大小\
     */
    private int getRightSize(String text){
        Rect rect = new Rect();
        textPaint.getTextBounds(text,0,text.length(),rect);
        fontMetrics = textPaint.getFontMetrics();
        float width = rect.width();
        float height = fontMetrics.bottom-fontMetrics.top;
        float canvasWidth = (float)getWidth()*0.9f;
        float canvasHeight = (float)getHeight()*0.9f;
        //当前文字的大小/文字的宽度=更改后文字的大小/组件的宽度
        centerTextSize = Math.min(Math.min((int)Math.floor(canvasWidth*(float)centerTextSize/width),(int)Math.floor(canvasHeight*(float)centerTextSize/height)),maxSize);
        return centerTextSize;
    }

    /**
     * 获取文字的宽度
     */
    private float getTextHeight(String text){
        Rect rect = new Rect();
        TextPaint textPaint1 = new TextPaint();
        textPaint1.setTextSize(centerTextSize);
        textPaint1.getTextBounds(text,0,text.length(),rect);
        return rect.height();
    }

    /**
     * 设置圆环的宽度
     */
    public void setCircleWidth(int width) {
        this.circleWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, getResources()
                .getDisplayMetrics());
        circlePaint.setStrokeWidth(circleWidth);
        invalidate();
    }

    /**
     * 设置圆环的底色，默认为亮灰色LTGRAY
     */
    public void setFirstColor(int color) {
        this.firstColor = color;
        circlePaint.setColor(firstColor);
        invalidate();
    }

    /**
     * 设置进度条的颜色，默认为蓝色<br>
     */
    public void setSecondColor(int color) {
        this.secondColor = color;
        circlePaint.setColor(secondColor);
        invalidate();
    }

    /**
     * 设置进度条渐变色颜色数组
     *
     * @param colors 颜色数组，类型为int[]
     */
    public void setColorArray(int[] colors) {
        this.colorArray = colors;
        invalidate();
    }


    /**
     * 按进度显示百分比，可选择是否启用数字动画
     * 将过长的中文释义进行修剪
     * @param duration 动画时长
     */
    public void setDuration(int duration,String first_word,String second_word,String third_word, OnFinishListener listener) {
        this.listener = listener;
        this.duration = duration + 1000;
        this.first_word = (first_word.length()>=25&&isNeedCut(first_word)?first_word.substring(0,25)+"...":first_word);
        this.second_word = (second_word.length()>=25&&isNeedCut(second_word)?second_word.substring(0,25):second_word);
        this.third_word = (third_word.length()>=25?third_word.substring(0,25)+"...":third_word);
        setSecondColor(Color.RED);
        if (animator != null) {
            animator.cancel();
        } else {
            animator = ValueAnimator.ofInt(0, maxValue);
            animator.addUpdateListener(animation -> {
                currentValue = (int) animation.getAnimatedValue();
                invalidate();
                if (maxValue == currentValue && CountDownProgressBar.this.listener != null) {
                    CountDownProgressBar.this.listener.onFinish();
                }
            });
        }
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(duration);
        animator.start();
    }

    /**
     * 判断字符串是word_en还是word_ch
     * 目前只是简单地判断第一个字符是不是英文字母
     */
    private boolean isNeedCut(String s){
        char c = s.charAt(0);
        return (c < 'a' || c > 'z') && (c < 'A' || c > 'Z');
    }

    public interface OnFinishListener {
        void onFinish();
    }

    public void setOnFinishListener(OnFinishListener listener) {
        this.listener = listener;
    }

    public static int px2dip(int pxValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    public static float dip2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (dipValue * scale + 0.5f);
    }

    public void setFirst_word(String first_word) {
        this.first_word = first_word;
    }

    public void setSecond_word(String second_word) {
        this.second_word = second_word;
    }

    public void setThird_word(String third_word) {
        this.third_word = third_word;
    }


    /**
     * an early closure
     */
    public void finishProgressBar() {
        animator.end();
    }

    public void setCenterTextColor(int centerTextColor) {
        this.centerTextColor = centerTextColor;
    }


}