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

import java.util.Objects;


/**
 * 绑定
 */
public class Register1Fragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    public Register1Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register1, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button getCodeBtn = Objects.requireNonNull(getActivity()).findViewById(R.id.getCodeBtn);
        Button commitBtn = Objects.requireNonNull(getActivity()).findViewById(R.id.commitBtn);
        Button notBindingBtn = Objects.requireNonNull(getActivity()).findViewById(R.id.notBindingBtn);
        Button otherWaysBtn = Objects.requireNonNull(getActivity()).findViewById(R.id.otherWaysBtn);

        getCodeBtn.setOnClickListener(this);
        commitBtn.setOnClickListener(this);
        notBindingBtn.setOnClickListener(this);
        otherWaysBtn.setOnClickListener(this);

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
            case R.id.getCodeBtn:

                break;
            case R.id.commitBtn:
                mListener.Register1FragmentInteraction(0);
                break;
            case R.id.notBindingBtn:
                mListener.Register1FragmentInteraction(1);
                break;
            case R.id.otherWaysBtn:
                mListener.Register1FragmentInteraction(2);
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        void Register1FragmentInteraction(int what);
    }
}
