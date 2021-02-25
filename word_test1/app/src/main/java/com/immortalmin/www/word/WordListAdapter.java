package com.immortalmin.www.word;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordListAdapter extends BaseAdapter {

    List<DetailWord> mdata;
    private LayoutInflater mInflater;//布局装载器对象

    public WordListAdapter(Context context, List<DetailWord> data) {
        this.mdata = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.worditem,null);

        WordView word_en = v.findViewById(R.id.word_en);
        TextView word_ch = v.findViewById(R.id.word_ch);
        TextView review_date = v.findViewById(R.id.review_date);

        word_en.setmText(mdata.get(position).getWord_en());
        word_en.setAccount((float)(mdata.get(position).getCorrect_times()/5.0));
        word_ch.setText(mdata.get(position).getWord_ch());
        String review_date_string = mdata.get(position).getReview_date();
        if("1970-01-01".equals(review_date_string) || "null".equals(review_date_string)) review_date.setText("");
        else if("2000-01-01".equals(review_date_string)) review_date.setText("new");
        else review_date.setText(review_date_string);
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
