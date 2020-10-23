package com.immortalmin.www.word;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.immortalmin.www.word.R;

public class AutoLineUtil extends ViewGroup {

    /**
     * 子view左右间距
     */
    private int mHorizontalSpacing;
    /**
     * 子view上下行距离
     */
    private int mVerticalSpacing;

    private Context context;


    public AutoLineUtil(Context context) {
        this(context, null);
        this.context = context;
    }

    public AutoLineUtil(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoLineUtil(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs,
                    R.styleable.AutoLineUtil);

            mHorizontalSpacing = array.getDimensionPixelOffset(
                    R.styleable.AutoLineUtil_horizontalSpacing, 0);
            mVerticalSpacing = array.getDimensionPixelOffset(
                    R.styleable.AutoLineUtil_verticalSpacing, 0);
            array.recycle();

            if (mHorizontalSpacing < 0) mHorizontalSpacing = 0;
            if (mVerticalSpacing < 0) mVerticalSpacing = 0;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }


        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(
                    getAutoLinefeedWidth(width), widthMode);
        }

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    getAutoLinefeedHeight(width), heightMode);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    /**
     * 自动换行 计算需要的宽度
     *
     * @param width 可用宽度
     * @return 需要的宽度
     */
    private int getAutoLinefeedWidth(int width) {
        int totalWidth = getPaddingLeft() + getPaddingRight();

        for (int i = 0; i < getChildCount(); i++) {
            if (i > 0) totalWidth += mHorizontalSpacing;
            View child = getChildAt(i);
            int childWidth = child.getMeasuredWidth();
            totalWidth += childWidth;
            if (totalWidth >= width) {
                totalWidth = width;
                break;
            }
        }

        return totalWidth;
    }

    /**
     * 自动换行 计算需要的高度
     *
     * @param width 可用宽度
     * @return 需要的高度
     */
    private int getAutoLinefeedHeight(int width) {

        //一行最大可用宽度
        int lineWidth = width - getPaddingLeft() - getPaddingRight();
        //剩余可用宽度
        int availableLineWidth = lineWidth;
        //需要的高度
        int totalHeight = getPaddingTop() + getPaddingBottom();
        int lineChildIndex = 0;
        //本行最大高度
        int lineMaxHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            //这个child需要的宽度  如果不是第一位的 那么需要加上间距
            //这里是用来判断需不需要换行
            int needWidth = i == 0 ? childWidth : (childWidth + mHorizontalSpacing);
            //如果剩余可用宽度小于需要的长度 那么换行
            if (availableLineWidth < needWidth) {
                totalHeight = totalHeight + lineMaxHeight;
                if (i > 0) totalHeight += mVerticalSpacing;
                availableLineWidth = lineWidth;
                lineMaxHeight = 0;
                lineChildIndex = 0;
            }
            //这个child需要的宽度  如果不是第一位的 那么需要加上间距
            int realNeedWidth = lineChildIndex == 0 ? childWidth : (childWidth + mHorizontalSpacing);
            lineMaxHeight = Math.max(childHeight, lineMaxHeight);
            availableLineWidth = availableLineWidth - realNeedWidth;
            lineChildIndex++;
        }

        totalHeight = totalHeight + lineMaxHeight;
        return totalHeight;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layout();
    }


    private void layout() {

        int count = getChildCount();
        int childLeft = getPaddingLeft();
        int childTop = getPaddingTop();
        int lineWidth = getMeasuredWidth() - getPaddingRight() - getPaddingLeft();
        int availableLineWidth = lineWidth;
        int lineChildIndex = 0;
        //一行的最大高度
        int lineMaxHeight = 0;
        for (int i = 0; i < count; i++) {

            View child = getChildAt(i);

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            int needWidth = i == 0 ? childWidth : (childWidth + mHorizontalSpacing);

            if (availableLineWidth < needWidth) {
                availableLineWidth = lineWidth;
                childTop += lineMaxHeight;
                if (i > 0) childTop += mVerticalSpacing;
                lineMaxHeight = 0;
                childLeft = getPaddingLeft();
                lineChildIndex = 0;
            }

            int realNeedWidth = lineChildIndex == 0 ? childWidth : (childWidth + mHorizontalSpacing);

            lineMaxHeight = Math.max(lineMaxHeight, childHeight);
            child.layout(childLeft + realNeedWidth - childWidth, childTop, childLeft + realNeedWidth, childTop + childHeight);
            availableLineWidth -= realNeedWidth;
            childLeft += realNeedWidth;
            lineChildIndex++;
        }
    }

    public int getHorizontalSpacing() {
        return mHorizontalSpacing;
    }

    public void setHorizontalSpacing(int horizontalSpacing) {
        mHorizontalSpacing = horizontalSpacing;
    }

    public int getVerticalSpacing() {
        return mVerticalSpacing;
    }

    public void setVerticalSpacing(int verticalSpacing) {
        mVerticalSpacing = verticalSpacing;
    }
}

