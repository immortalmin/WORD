package com.immortalmin.www.word;

import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;

public class TestActivity extends MyAppCompatActivity{

    private EditText et1;
//    private int visibleHeight=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spell);
        et1 = findViewById(R.id.et1);
        init();

    }

    private void init(){
        ViewTreeObserver.OnGlobalLayoutListener listener = () -> {
            Rect r = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
            int heightDifference = visibleHeight - (r.bottom - r.top); // 实际高度减去可视图高度即是键盘高度
            boolean isKeyboardShowing = heightDifference > visibleHeight / 3;
            if(isKeyboardShowing){
                et1.animate().translationY(-heightDifference).setDuration(0).start();
            }else{
                //键盘隐藏
                et1.animate().translationY(0).start();
            }
        };
        et1.getViewTreeObserver().addOnGlobalLayoutListener(listener);
    }

}
