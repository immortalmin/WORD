package com.example.administrator.listviewadptwebjsonimg;

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

import java.util.ArrayList;

public class CountDownFragment extends Fragment {
    private final static String TAG = "CountDownFragment";
    private View mView;
    private String res = "false";
    TextView tv1;
    Button acquaint;
    private OnFragmentInteractionListener mListener;

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
        View view = inflater.inflate(R.layout.activity_view_test,null);

        Log.d(TAG,"onCreateView");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        tv1 = (TextView)getActivity().findViewById(R.id.tv1);
        acquaint = (Button)getActivity().findViewById(R.id.acquaint);
//        Bundle b = getArguments();
//        String res = b.getString("csk");
//        acquaint.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.i("acquaintClickListener","success");
//            }
//        });
//        tv1.setText(res);
        if (mListener != null) {
            Log.i("mListener!!","okokok");
            ArrayList a = new ArrayList();
            a.add(1);
            a.add(2);
            a.add(3);
            mListener.onFragmentInteraction(a);
        }
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(ArrayList<Integer> s);
    }


}
