package com.immortalmin.www.word;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * 其他方式
 */
public class Register2Fragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    public Register2Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register2, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button btn0 = getActivity().findViewById(R.id.btn0);
        Button btn1 = getActivity().findViewById(R.id.btn1);
        Button btn2 = getActivity().findViewById(R.id.btn2);
        Button btn3 = getActivity().findViewById(R.id.btn3);
        Button btn4 = getActivity().findViewById(R.id.btn4);
        btn0.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn0:
                mListener.Register2FragmentInteraction(0);
                break;
            case R.id.btn1:
                mListener.Register2FragmentInteraction(1);
                break;
            case R.id.btn2:
                mListener.Register2FragmentInteraction(2);
                break;
            case R.id.btn3:
                mListener.Register2FragmentInteraction(3);
                break;
            case R.id.btn4:
                mListener.Register2FragmentInteraction(4);
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        void Register2FragmentInteraction(int what);
    }
}
