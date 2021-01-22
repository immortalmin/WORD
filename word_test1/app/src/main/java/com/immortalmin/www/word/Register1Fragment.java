package com.immortalmin.www.word;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/**
 * 绑定
 */
public class Register1Fragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    private Context context;
    private MyAsyncTask myAsyncTask;
    private User user;
    private JsonRe jsonRe = new JsonRe();
    private MyEditText telephone_tv,code_tv;

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
        telephone_tv = Objects.requireNonNull(getActivity()).findViewById(R.id.telephone_tv);
        code_tv = Objects.requireNonNull(getActivity()).findViewById(R.id.code_tv);

        getCodeBtn.setOnClickListener(this);
        commitBtn.setOnClickListener(this);
        notBindingBtn.setOnClickListener(this);
        otherWaysBtn.setOnClickListener(this);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
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
                if(!isChinaPhoneLegal(telephone_tv.getText().toString())){
                    Toast.makeText(context,"请输入正确的手机号",Toast.LENGTH_SHORT).show();
                }else{
                    inspectTel();
                }
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

    public static boolean isChinaPhoneLegal(String str) throws PatternSyntaxException {
        String regExp = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(147,145))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }


    /**
     * 检查手机号码是否已经被注册
     */
    private void inspectTel() {
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("telephone",telephone_tv.getText().toString());
            jsonObject.put("what",14);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            user = jsonRe.userData(result);
            if(user!=null){
                Toast.makeText(context,"该手机号已被绑定",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context,"验证码已发送",Toast.LENGTH_SHORT).show();
            }
        });
        myAsyncTask.execute(jsonObject);
    }

    public interface OnFragmentInteractionListener {
        void Register1FragmentInteraction(int what);
    }
}
