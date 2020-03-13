package com.immortalmin.www.word;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExampleAdapter extends BaseAdapter {

    ArrayList<HashMap<String,Object>> mdata;
    private LayoutInflater mInflater;//布局装载器对象
    private onItemListener mOnItemListener;
    private int mode = 0;//0 view,1 edit
    private TextView word_meaning,E_sentence,C_translate,source;
    private Button del_btn,edit_btn;
    private String username;

    public ExampleAdapter(Context context, ArrayList<HashMap<String,Object>> data,int mode,String username) {
        this.mdata = data;
        this.mode = mode;
        this.username = username;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.exampleitem,null);
        word_meaning = (TextView)v.findViewById(R.id.word_meaning);
        E_sentence = (TextView)v.findViewById(R.id.E_sentence);
        C_translate = (TextView)v.findViewById(R.id.C_translate);
        source = (TextView)v.findViewById(R.id.source);
        del_btn = (Button)v.findViewById(R.id.example_del_btn);
        edit_btn = (Button)v.findViewById(R.id.example_edit_btn);
        if(mode==1&&mdata.get(position).get("source").toString().equals(username)){
            del_btn.setVisibility(View.VISIBLE);
            edit_btn.setVisibility(View.VISIBLE);
        }else{
            del_btn.setVisibility(View.INVISIBLE);
            edit_btn.setVisibility(View.INVISIBLE);
        }
        word_meaning.setText(mdata.get(position).get("word_meaning").toString());
        E_sentence.setText(mdata.get(position).get("E_sentence").toString());
        C_translate.setText(mdata.get(position).get("C_translate").toString());
        source.setText("——由"+mdata.get(position).get("source").toString()+"添加");
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

    /**
     * 1:visible
     * 0:invisible
     * @param mode
     */
    public void setMode(int mode) {
        this.mode = mode;
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
