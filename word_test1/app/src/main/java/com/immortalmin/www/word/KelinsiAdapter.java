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

    ArrayList<KelinsiItem> mdata;
    private Context context;
    private LayoutInflater mInflater;//布局装载器对象
    private TextView number,label,word_ch,explanation,gram;
    private MyListView sentences_listview;
    private KelinsiSentencesAdapter kelinsiSentencesAdapter;

    public KelinsiAdapter(Context context, ArrayList<KelinsiItem> data) {
        this.context = context;
        this.mdata = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.kelinsiitem,null);
        number = v.findViewById(R.id.number);
        label = v.findViewById(R.id.label);
        word_ch = v.findViewById(R.id.word_ch);
        explanation = v.findViewById(R.id.explanation);
        gram = v.findViewById(R.id.gram);
        sentences_listview = v.findViewById(R.id.sentences_listview);
        number.setText(mdata.get(position).getNumber());
        label.setText(mdata.get(position).getLabel());
        word_ch.setText(mdata.get(position).getWord_ch());
        explanation.setText(mdata.get(position).getExplanation());
        gram.setText(mdata.get(position).getGram());
        kelinsiSentencesAdapter = new KelinsiSentencesAdapter(context,mdata.get(position).getSentences());
        sentences_listview.setAdapter(kelinsiSentencesAdapter);
        return v;
    }


//    private Handler mHandler = new Handler(message -> {
//        switch (message.what){
//            case 0:
//
//                break;
//        }
//        return false;
//    });

    @Override
    public int getCount() {
        return mdata.size();
    }

    @Override
    public Object getItem(int position) {
        return mdata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
