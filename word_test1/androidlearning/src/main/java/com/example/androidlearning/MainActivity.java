package com.example.androidlearning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv2 = findViewById(R.id.tv2);
        TextView tv4 = findViewById(R.id.tv4);
        tv2.setBackgroundColor(0x00ff00);
        DisplayMetrics dm = getScreenDisplayMetrics(this);
        tv2.setText("height:"+dm.heightPixels+",width:"+dm.widthPixels+",density:"+dm.density);
        setLayoutParamsTest(tv4);
    }

    public DisplayMetrics getScreenDisplayMetrics(Context context){
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    public void setLayoutParamsTest(View view){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,300);
        layoutParams.setMargins(10,10,10,10);
        view.setLayoutParams(layoutParams);

    }
}
