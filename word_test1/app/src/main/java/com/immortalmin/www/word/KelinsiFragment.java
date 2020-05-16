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
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.signature.ObjectKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class KelinsiFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    private MyAsyncTask myAsyncTask;
    private JsonRe jsonRe = new JsonRe();
    private HashMap<String,Object> kelinsi_word = null;
    private TextView word_en,star;
    private MyListView items_listview;
    private KelinsiAdapter kelinsiAdapter;
    private int wid=0;

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
//        word_en = getActivity().findViewById(R.id.word_en);
//        star = getActivity().findViewById(R.id.star);
        items_listview = getActivity().findViewById(R.id.items_listview);

        getKelinsiData();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void kelinsiFragmentInteraction(String res);
    }

    public void setWid(int wid){
        this.wid = wid;
    }

    //mHandler.obtainMessage(0).sendToTarget();
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
//                    word_en.setText(kelinsi_word.get("word_en").toString());
//                    star.setText(kelinsi_word.get("star").toString());
                    kelinsiAdapter = new KelinsiAdapter(getActivity(),(ArrayList<HashMap<String,Object>>) kelinsi_word.get("items"));
                    items_listview.setAdapter(kelinsiAdapter);
                    break;
            }
            return false;
        }
    });

    private void getKelinsiData(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("what",9);
//            jsonObject.put("wid",30595);
            jsonObject.put("wid",wid);
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            kelinsi_word = jsonRe.kelinsiwordData(result);
            mHandler.obtainMessage(0).sendToTarget();
//            Log.i("ccc",kelinsi_word.toString());
        });
        myAsyncTask.execute(jsonObject);
    }
}
