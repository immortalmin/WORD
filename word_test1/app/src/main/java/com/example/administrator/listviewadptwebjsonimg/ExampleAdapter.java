package com.example.administrator.listviewadptwebjsonimg;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExampleAdapter extends BaseAdapter {

    List<HashMap<String,Object>> mdata;
    private LayoutInflater mInflater;//布局装载器对象
    private onItemListener mOnItemListener;
    private int mode=0;//0 view,1 edit
    private boolean flag = false;
    private View[] views = new View[100];

    public ExampleAdapter(Context context, List<HashMap<String,Object>> data,int mode) {
        this.mdata = data;
        this.mode = mode;
//        total_num=0;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.exampleitem,null);
        views[position]=v;
        TextView word_meaning = (TextView)v.findViewById(R.id.word_meaning);
        TextView E_sentence = (TextView)v.findViewById(R.id.E_sentence);
        TextView C_translate = (TextView)v.findViewById(R.id.C_translate);
        Button del_btn = (Button)v.findViewById(R.id.del_btn);
        Button edit_btn = (Button)v.findViewById(R.id.edit_btn);
        if(mode==0){
            del_btn.setVisibility(View.INVISIBLE);
            edit_btn.setVisibility(View.INVISIBLE);
        }
        word_meaning.setText(mdata.get(position).get("word_meaning").toString());
        E_sentence.setText(mdata.get(position).get("E_sentence").toString());
        C_translate.setText(mdata.get(position).get("C_translate").toString());
        del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemListener.onDeleteClick(position);
            }
        });
        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemListener.onEditClick(position);
            }
        });
        return v;
    }

    public void setVisible(){
        mode=1;
        Log.i("ccc",String.valueOf(views.length));
        for(int i=0;i<mdata.size();i++){
            Button del_btn = views[i].findViewById(R.id.del_btn);
            Button edit_btn = views[i].findViewById(R.id.edit_btn);
            del_btn.setVisibility(View.VISIBLE);
            edit_btn.setVisibility(View.VISIBLE);
        }
    }

    public void setinVisible(){
        mode = 0;
        Log.i("ccc",String.valueOf(views.length));
        for(int i=0;i<mdata.size();i++){
            Button del_btn = views[i].findViewById(R.id.del_btn);
            Button edit_btn = views[i].findViewById(R.id.edit_btn);
            del_btn.setVisibility(View.INVISIBLE);
            edit_btn.setVisibility(View.INVISIBLE);
        }
    }

    public interface onItemListener {
        void onDeleteClick(int i);
        void onEditClick(int i);
    }

    public void setOnItemClickListener(onItemListener mOnItemListener) {
        this.mOnItemListener = mOnItemListener;
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
