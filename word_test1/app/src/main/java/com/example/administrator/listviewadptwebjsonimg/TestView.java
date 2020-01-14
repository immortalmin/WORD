package com.example.administrator.listviewadptwebjsonimg;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import org.w3c.dom.Attr;

public class TestView extends View {

    /**
     * 在java代码里new的时候会用到
     * @param context
     */
    public TestView(Context context){
        super(context);
    }

    /**
     * 在xml布局文件中使用时自动调用
     * @param context
     * @param attrs
     */
    public TestView(Context context, @Nullable AttributeSet attrs){
        super(context,attrs);
    }

    /**
     * 不会自动调用，如果有默认style时，在第二个构造函数中调用
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public TestView(Context context,@Nullable AttributeSet attrs,int defStyleAttr){
        super(context,attrs,defStyleAttr);
    }

    /**
     * 只有在API版本>21时才会用到
     * 不会自动调用，如果有默认的style是，在第二个构造函数中调用
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
    @RequiresApi(api= Build.VERSION_CODES.LOLLIPOP)
    public TestView(Context context, @Nullable AttributeSet attrs,int defStyleAttr,int defStyleRes){
        super(context,attrs,defStyleAttr,defStyleRes);
    }
}
