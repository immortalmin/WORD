package com.immortalmin.www.word;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;

public class CaptureUtil {

    /**
     * 截屏
     * @return
     */
    public Bitmap getcapture(Activity activity){
        View view = activity.getWindow().getDecorView();     // 获取DecorView
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        bitmap = Bitmap.createBitmap(bitmap, 0, 0,getScreenWidth(activity.getApplicationContext()), getScreenHeight(activity.getApplicationContext()), null, false);
        return bitmap;
    }

    //获取屏幕高度 不包含虚拟按键
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    //获取屏幕宽度
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

}
