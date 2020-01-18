package com.example.administrator.listviewadptwebjsonimg;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CountDownFragment extends Fragment {
    private final static String TAG = "CountDownFragment";
    private View mView;
    private String res = "false";
    TextView textView1;

    /**
     * Activity绑定上Fragment时，调用该方法
     * 这个是第一次被调用的
     * @param context
     */
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Log.d(TAG,"onAttach");
    }

    /**
     * Fragment显示的内容是怎样的，就是通过下面这个方法返回回去的(view)
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_view_test,null);
        Log.d(TAG,"onCreateView");
        mView = view;
        textView1 = mView.findViewById(R.id.tv1);
        textView1.setText("YES");
//        Log.d("gettextttttt",..toString());
        res = "ture";
        return view;
    }

    public String getV(){


        return res;
    }


}
