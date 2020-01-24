package com.example.administrator.listviewadptwebjsonimg;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class CountDownFragment extends Fragment implements View.OnClickListener{
    private final static String TAG = "CountDownFragment";
    private OnFragmentInteractionListener mListener;
    private String word;
    private Button acquaint,vague,strange;
    private CountDownProgressBar cpb_countdown;

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
        View view = inflater.inflate(R.layout.activity_countdown,null);
        Log.d(TAG,"onCreateView");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        acquaint = (Button)getActivity().findViewById(R.id.acquaint);
        vague = (Button)getActivity().findViewById(R.id.vague);
        strange = (Button)getActivity().findViewById(R.id.strange);
        cpb_countdown = (CountDownProgressBar) getActivity().findViewById(R.id.cpb_countdown);
        acquaint.setOnClickListener(this);
        vague.setOnClickListener(this);
        strange.setOnClickListener(this);
        cpb_countdown.setOnClickListener(this);
        /**
         * 接受来自activity的数据
         */
        Bundle bundle = getArguments();
        word = bundle.getString("word");
        cpb_countdown.setWord(word);
        cpb_countdown.setCenterTextColor(Color.BLUE);
        cpb_countdown.setDuration(5000, new CountDownProgressBar.OnFinishListener() {
            @Override
            public void onFinish() {
//                Toast.makeText(getActivity(), "完成了", Toast.LENGTH_SHORT).show();
            }
        });






    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void countdownonFragmentInteraction(ArrayList<String> s);
    }

    /**
     * 选项按钮点击事件
     * @param view
     */
    public void onClick(View view){
        switch(view.getId()){
            case R.id.cpb_countdown:
                cpb_countdown.finishProgressBar();
                break;
            case R.id.acquaint:
                send_to_activity(1);
                break;
            case R.id.vague:
                send_to_activity(2);
                break;
            case R.id.strange:
                send_to_activity(3);
                break;
        }
//        send_to_activity(res);
    }

    /**
     * 向activity回送数据
     */
    private void send_to_activity(int res){
        if (mListener != null) {
            ArrayList a = new ArrayList();
            switch (res){
                case 1://认识
                    a.add("1");
                    break;
                case 2://模糊
                    a.add("2");
                    break;
                case 3://不认识
                    a.add("3");
                    break;
            }
//            a.add(s);
            mListener.countdownonFragmentInteraction(a);
        }
    }

    public void update_options(String new_word){
        word = new_word;
//        cpb_countdown.finishProgressBar();
        cpb_countdown.setWord(word);
        cpb_countdown.setCenterTextColor(Color.BLUE);
        cpb_countdown.setDuration(5000, new CountDownProgressBar.OnFinishListener() {
            @Override
            public void onFinish() {
//                Toast.makeText(getActivity(), "完成了", Toast.LENGTH_SHORT).show();
            }
        });
//        Log.i("user_selandcorrect_sel2",String.valueOf(user_sel)+"  "+String.valueOf(correct_sel));
    }

}
