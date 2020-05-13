package com.immortalmin.www.word;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class KelinsiAdapter extends BaseAdapter {

    ArrayList<HashMap<String,Object>> mdata;
    private Context context;
    private LayoutInflater mInflater;//布局装载器对象
    private TextView number,label,word_ch,explanation,gram;
    private MyListView sentences_listview;
    private KelinsiSentencesAdapter kelinsiSentencesAdapter;

    public KelinsiAdapter(Context context, ArrayList<HashMap<String,Object>> data) {
        this.context = context;
        this.mdata = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.kelinsiitem,null);
        number = (TextView)v.findViewById(R.id.number);
        label = (TextView)v.findViewById(R.id.label);
        word_ch = (TextView)v.findViewById(R.id.word_ch);
        explanation = (TextView)v.findViewById(R.id.explanation);
        gram = (TextView)v.findViewById(R.id.gram);
        sentences_listview = (MyListView)v.findViewById(R.id.sentences_listview);
        number.setText(mdata.get(position).get("number").toString());
        label.setText(mdata.get(position).get("label").toString());
        word_ch.setText(mdata.get(position).get("word_ch").toString());
        explanation.setText(mdata.get(position).get("explanation").toString());
        gram.setText(mdata.get(position).get("gram").toString());
        kelinsiSentencesAdapter = new KelinsiSentencesAdapter(context,(ArrayList<HashMap<String,Object>>) mdata.get(position).get("sentences"));
        sentences_listview.setAdapter(kelinsiSentencesAdapter);
        return v;
    }


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 0:

                    break;
            }
            return false;
        }
    });

    @Override
    //ListView需要显示的数据数量
    public int getCount() {
        return mdata.size();
    }

    @Override
    //指定的索引对应的数据项
    public Object getItem(int position) {
        return mdata.get(position);
    }

    @Override
    //指定的索引对应的数据项ID
    public long getItemId(int position) {
        return position;
    }
}
