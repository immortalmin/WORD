package com.immortalmin.www.word;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class MyGestureListener implements GestureDetector.OnGestureListener {
    private MyRightLeftListener listener;
    // 垂直方向移动的距离，绝对值
    private float distanceY;
    // 水平方向移动的距离，绝对值
    private float distanceX;
    // 移动距离大于下面的值时，才触发滑动屏幕的监听
    private float distance = 100;

    public MyGestureListener(MyRightLeftListener listener) {
        this.listener = listener;
    }

    /**
     * 一旦触摸屏按下，就马上产生onDown事件
     */
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    /**
     * 点击了触摸屏，但是没有移动和弹起的动作onShowPress和onDown的区别在于 onDown是，
     * 一旦触摸屏按下，就马上产生onDown事件，但是onShowPress是onDown事件产生后，
     * 一段时间内，如果没有移动鼠标和弹起事件，就认为是onShowPress事件。
     */
    @Override
    public void onShowPress(MotionEvent e) {
    }

    /**
     * 轻击触摸屏后，弹起。如果这个过程中产onLongPress、onScroll和onFling事件， 就不会 产生onSingleTapUp事件。
     */
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    /**
     * 当手在屏幕上滑动过程中触发，参数跟onFling一样（注意两者的区别）
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        return false;
    }

    /**
     * 长按屏幕时触发
     */
    @Override
    public void onLongPress(MotionEvent e) {
    }

    /**
     * 当手在屏幕上滑动但手离开屏幕时触发
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        // 按下时的x坐标
        float startX = e1.getX();
        // 按下时的y坐标
        float startY = e1.getY();
        // 抬起时的x坐标
        float endtX = e2.getX();
        // 抬起时的y坐标
        float endtY = e2.getY();

        // 水平方向移动的距离，绝对值
        distanceX = Math.abs(endtX - startX);
        // 垂直方向移动的距离，绝对值
        distanceY = Math.abs(endtY - startY);
        // 首先判断是水平方向移动还是垂直方向移动

        if (distanceX>300&&distanceY>500) {

            listener.onSlide();
        }

        if (distanceX > distanceY) {
            // 说明水平反向移动的距离远，就认定为水平移动
            if (distanceX > distance) {
                // 水平方向移动距离大局默认距离是才触发滑动监听事件
                if (startX > endtX) {
                    // 手指从右往左滑动
                    listener.onLeft();
                } else {
                    // 手指从左往右滑动
                    listener.onRight();
                }
            }
        } else if (distanceX < distanceY) {
            // 说明垂直反向移动的距离远，就认定为垂直移动
            if (distanceY > distance) {
                // 垂直方向移动距离大局默认距离是才触发滑动监听事件
                if (startY > endtY) {
                    // 手指从下往上滑动
                    listener.onUp();
                } else {
                    // 手指从上往下滑动
                    listener.onDown();
                }
            }
        } else {
            // 水平方向和垂直方向移动距离相同时，默认为水平方向的移动
            if (distanceX > distance) {
                // 水平方向移动距离大局默认距离是才触发滑动监听事件
                if (startX > endtX) {
                    // 手指从右往左滑动
                    listener.onLeft();
                } else {
                    // 手指从左往右滑动
                    listener.onRight();
                }
            }
        }
        return false;
    }
}