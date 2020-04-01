package com.immortalmin.www.word;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class SignIn extends View {

    private Paint mPaint = new Paint();
    private int[] color = {
            Color.parseColor("#50ebedf0"),
            Color.parseColor("#50c6e48b"),
            Color.parseColor("#507bc96f"),
            Color.parseColor("#50239a3b"),
            Color.parseColor("#50196127")
    };
    private int[] sign_in_times = {
            0,1,4,2,2,3,2,
            1,4,1,0,2,3,2,
            1,4,1,0,2,3,2,
            1,1,0,2,3,2,1,
            4,1,0,2,3,4,1,
            0,2,3,2,1,2,1,
            0,2,3,2,1,4,1,
            0,2,3,2,1,4,1,
            0,2,3,2,1,4,2,
            1,0,2,3,4,1,0,
            3,2,1,4,1,0,4,
            3,2,1,4,1,0,2,
            3,2,1,4,1,0,2,
            3,0,2,1,4,1,0,
            2,3,4,1,0,3,2,
            1,4,1,0,4,3,2,
            1,4,1,0,2,3,0,
            4,1,0,2,3,0,1,
            1,1,0,2,3,0,4,
            1,1,2,3,0,4,0,
            2,1,4,1,0,2,3,
            4,1,0,3,2,4,1,
            0,4,3,2,1,4,1,
            0,2,3,2,1,4,1,
            2,3,2,1,4,1,0,
            0,2,3,2,1,4,1,
            0,3,0,2,1,1,4,
            2,3,0
    };

//    private int[] sign_in_times = {0,1,4,2,2,3,2,1,4,1,0,0,2,3,4,2,3,2,4,0,
//            1,4,2,2,3,2,1,4,1,0,0,2,3,4,2,3,2,4,0,1,4,2,2,3,2,1,4,1,0,0,2,3,
//            4,2,3,2,4,0,1,4,2,2,3,2,1,4,1,0,0,2,3,4,2,3,2,4,0,1,4,2,2,3,2,1,
//            4,1,0,0,2,3,4,2,3,2,4,0,1,4,2,2,3,2,1,4,1,0,0,2,3,4,2,3,2,4,0,
//            1,4,2,2,3,2,1,4,1,0,0,2,3,4,2,3,2,4,0,1,4,2,2,3,2};

    public SignIn(Context context){
        this(context,null);
    }
    public SignIn(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }
    public SignIn(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);

    }

    @Override
    protected void onDraw(Canvas canvas){
        drawRect(canvas);

    }

    //绘制矩阵
    private void drawRect(Canvas canvas){
        int width = canvas.getWidth();
        int height = canvas.getHeight();
//        canvas.drawColor(Color.GRAY);
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.FILL);

        for(int i=0;i<sign_in_times.length;i++){
            if(i%7==0){
                canvas.save();
            }
            mPaint.setColor(color[sign_in_times[i]]);

            canvas.drawRect(10,10,40,40,mPaint);

            canvas.translate(0,35);
            if(i%7==6){
                canvas.restore();
                canvas.translate(35,0);
            }
        }




//        mPaint.setColor(color[2]);
//        canvas.drawRect(10,10,40,40,mPaint);
//        canvas.restore();

    }
}
