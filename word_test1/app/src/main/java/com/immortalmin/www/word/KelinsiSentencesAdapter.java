package com.immortalmin.www.word;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class KelinsiSentencesAdapter extends BaseAdapter {

    ArrayList<HashMap<String,Object>> mdata;
    private LayoutInflater mInflater;//布局装载器对象
    private TextView sentence_en,sentence_ch;

    public KelinsiSentencesAdapter(Context context, ArrayList<HashMap<String,Object>> data) {
        this.mdata = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.kelinsisentenceitem,null);
        sentence_en = (TextView)v.findViewById(R.id.sentence_en);
        sentence_ch = (TextView)v.findViewById(R.id.sentence_ch);
        sentence_en.setText(mdata.get(position).get("sentence_en").toString());
        sentence_ch.setText(mdata.get(position).get("sentence_ch").toString());
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
