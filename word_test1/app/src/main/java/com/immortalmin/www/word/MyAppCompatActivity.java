package com.immortalmin.www.word;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;


public class MyAppCompatActivity extends AppCompatActivity {

    int visibleHeight = 0;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //获取visible height
        Rect r = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        visibleHeight = r.bottom-r.top;
    }

    public int getVisibleHeight() {
        return visibleHeight;
    }
}
