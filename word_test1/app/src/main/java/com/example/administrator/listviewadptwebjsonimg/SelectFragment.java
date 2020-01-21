package com.example.administrator.listviewadptwebjsonimg;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SelectFragment extends Fragment implements View.OnClickListener{
    private final static String TAG = "SelectFragment";
    private OnFragmentInteractionListener mListener;
//    private String word,res;
    private Button sel1,sel2,sel3,sel4,sel5;
    private TextView wordview;
    private ProgressBar word_times_pro;

    /**
     * Activity绑定上Fragment时，调用该方法
     * 这个是第一次被调用的
     * @param context
     */
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Log.d(TAG,"onAttach");
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
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
        View view = inflater.inflate(R.layout.activity_select,null);
        Log.d(TAG,"onCreateView");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        sel1 = (Button)getActivity().findViewById(R.id.sel1);
        sel2 = (Button)getActivity().findViewById(R.id.sel2);
        sel3 = (Button)getActivity().findViewById(R.id.sel3);
        sel4 = (Button)getActivity().findViewById(R.id.sel4);
        sel5 = (Button)getActivity().findViewById(R.id.sel5);
        wordview = (TextView) getActivity().findViewById(R.id.wordview);
        word_times_pro = (ProgressBar) getActivity().findViewById(R.id.word_times_pro);
        sel1.setOnClickListener(this);
        sel2.setOnClickListener(this);
        sel3.setOnClickListener(this);
        sel4.setOnClickListener(this);
        sel5.setOnClickListener(this);
        wordview.setOnClickListener(this);
        /**
         * 接受来自activity的数据
         */
//        Bundle bundle = getArguments();
//        word = bundle.getString("word");
//        cpb_countdown.setWord(word);
//        cpb_countdown.setCenterTextColor(Color.BLUE);
//        cpb_countdown.setDuration(5000, new CountDownProgressBar.OnFinishListener() {
//            @Override
//            public void onFinish() {
////                Toast.makeText(getActivity(), "完成了", Toast.LENGTH_SHORT).show();
//            }
//        });






    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(ArrayList<String> s);
    }

    /**
     * 选项按钮点击事件
     * @param view
     */
    public void onClick(View view){
        switch(view.getId()){
            case R.id.sel1:
                send_to_activity("sel1");
                break;
            case R.id.sel2:
                send_to_activity("sel2");
                break;
            case R.id.sel3:
                send_to_activity("sel3");
                break;
            case R.id.sel4:
                send_to_activity("sel4");
                break;
            case R.id.sel5:
                send_to_activity("sel5");
                break;
            case R.id.wordview:
                send_to_activity("wordview");
                break;
        }
//        send_to_activity(res);
    }

    /**
     * 向activity回送数据
     */
    private void send_to_activity(String s){
        if (mListener != null) {
            ArrayList a = new ArrayList();
            a.add(s);
            mListener.onFragmentInteraction(a);
        }
    }

}
