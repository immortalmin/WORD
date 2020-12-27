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
        WordView word_en = (WordView)v.findViewById(R.id.word_en);
        TextView word_ch = (TextView)v.findViewById(R.id.word_ch);
        int correct_times = Integer.parseInt(mdata.get(position).get("correct_times").toString());
        int grasp_times = 5;
        float account = (correct_times>=grasp_times?1.0f:(float)correct_times/(float)grasp_times);

        word_en.setmText(mdata.get(position).get("word_en").toString());

        word_en.setAccount(account);
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
