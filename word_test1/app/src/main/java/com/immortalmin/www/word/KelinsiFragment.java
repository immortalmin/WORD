package com.immortalmin.www.word;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.signature.ObjectKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

//FIXME:切换到横屏的时候会闪退，现在暂时的解决办法是禁止切换到横屏
//参考博客：https://blog.csdn.net/Naide_S/article/details/80841265
//          https://www.cnblogs.com/YZFHKMS-X/p/11864496.html
public class KelinsiFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private MyAsyncTask myAsyncTask;
    private JsonRe jsonRe = new JsonRe();
    private KelinsiWord kelinsiWord = null;
    private MyListView items_listview;
    private ImageView no_network;
    private KelinsiAdapter kelinsiAdapter;
    private int wid=0;
    private boolean network = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kelinsi, container, false);
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        items_listview = getActivity().findViewById(R.id.items_listview);
        no_network = getActivity().findViewById(R.id.no_network_KeLinSi);
        if(network){
            getKelinsiData();
        }else{
            mHandler.sendEmptyMessage(1);
        }

    }

    public interface OnFragmentInteractionListener {
        void kelinsiFragmentInteraction(String res);
    }

    public void setData(int wid,boolean network){
        this.wid = wid;
        this.network = network;
    }

    //mHandler.obtainMessage(0).sendToTarget();
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
//                    word_en.setText(kelinsi_word.get("word_en").toString());
//                    star.setText(kelinsi_word.get("star").toString());
                    kelinsiAdapter = new KelinsiAdapter(getActivity(),kelinsiWord.getItems());
                    items_listview.setAdapter(kelinsiAdapter);
                    break;
                case 1:
                    no_network.setVisibility(View.VISIBLE);
                    items_listview.setVisibility(View.INVISIBLE);
                    break;
            }
            return false;
        }
    });

    private void getKelinsiData(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("what",9);
            jsonObject.put("wid",wid);
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            kelinsiWord = jsonRe.kelinsiWordData(result);
            mHandler.obtainMessage(0).sendToTarget();
        });
        myAsyncTask.execute(jsonObject);
    }
}
