package com.immortalmin.www.word;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchAdapter extends BaseAdapter {

    List<DetailWord> mdata;
    private LayoutInflater mInflater;//布局装载器对象
    private onItemListener mOnItemListener;

    public SearchAdapter(Context context, List<DetailWord> data) {
        this.mdata = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.searchitem,null);
        WordView word_en = v.findViewById(R.id.word_en);
        TextView word_ch = v.findViewById(R.id.word_ch);
        ImageView history_icon = v.findViewById(R.id.history_icon);
        int correct_times = mdata.get(position).getCorrect_times();
        int grasp_times = 5;
        boolean isCached = mdata.get(position).isCached();
        if(isCached) history_icon.setVisibility(View.VISIBLE);
        history_icon.setOnClickListener(view->mOnItemListener.onDeleteClick(position));
        float account = (correct_times>=grasp_times?1.0f:(float)correct_times/(float)grasp_times);
        word_en.setmText(mdata.get(position).getWord_en());
        word_en.setAccount(account);
        word_ch.setText(mdata.get(position).getWord_ch());

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

    public interface onItemListener{
        void onDeleteClick(int i);
    }

    public void setmOnItemListener(onItemListener mOnItemListener) {
        this.mOnItemListener = mOnItemListener;
    }
}
