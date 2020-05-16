package com.immortalmin.www.word;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchAdapter extends BaseAdapter {

    List<HashMap<String,Object>> mdata;
    private LayoutInflater mInflater;//布局装载器对象

    public SearchAdapter(Context context, List<HashMap<String,Object>> data) {
        this.mdata = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.searchitem,null);
        //old
//        WordView word_group = (WordView)v.findViewById(R.id.word_group);
//        TextView C_meaning = (TextView)v.findViewById(R.id.C_meaning);
//
//        word_group.setmText(mdata.get(position).get("word_group").toString());
//        word_group.setAccount((float)(Integer.valueOf(mdata.get(position).get("correct_times").toString())/5.0));
//        C_meaning.setText(mdata.get(position).get("C_meaning").toString());

        //new
        WordView word_en = (WordView)v.findViewById(R.id.word_en);
        TextView word_ch = (TextView)v.findViewById(R.id.word_ch);

        word_en.setmText(mdata.get(position).get("word_en").toString());
        word_en.setAccount(0);
        word_ch.setText(mdata.get(position).get("word_ch").toString());

        return v;
    }

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
