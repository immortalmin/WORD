package com.immortalmin.www.word;

public interface MyRightLeftListener {
    /**
     * 手指从左往右滑动
     */
    void onRight();

    /**
     * 手指从右往左滑动
     */
    void onLeft();

    /**
     * 手指从下往上滑动
     */
    void onUp();

    /**
     * 手指从上往下滑动
     */
    void onDown();

    /**
     * 斜画屏幕
     * */
    void onSlide();
}
