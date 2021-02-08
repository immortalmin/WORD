package com.immortalmin.www.word;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

public class ExampleAdapter extends BaseAdapter {

    private ArrayList<OtherSentence> mdata;
    private LayoutInflater mInflater;//布局装载器对象
    private onItemListener mOnItemListener;
    private int mode = 0;//0 view,1 edit
    private TextView word_meaning,E_sentence,C_translate,source;
    private Button del_btn,edit_btn;
    private String username;

    public ExampleAdapter(Context context, ArrayList<OtherSentence> data,int mode,String username) {
        this.mdata = data;
        this.mode = mode;
        this.username = username;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.exampleitem,null);
        word_meaning = v.findViewById(R.id.word_meaning);
        E_sentence = v.findViewById(R.id.E_sentence);
        C_translate = v.findViewById(R.id.C_translate);
        source = v.findViewById(R.id.source);
        del_btn = v.findViewById(R.id.example_del_btn);
        edit_btn = v.findViewById(R.id.example_edit_btn);

        try{
            if(mode==1&&mdata.get(position).getSource().equals(username)){
                del_btn.setVisibility(View.VISIBLE);
                edit_btn.setVisibility(View.VISIBLE);
            }else{
                del_btn.setVisibility(View.INVISIBLE);
                edit_btn.setVisibility(View.INVISIBLE);
            }
        }catch (NullPointerException e){
            Log.i("ccc",mdata.toString());
        }
        word_meaning.setText(mdata.get(position).getWord_meaning());
        E_sentence.setText(mdata.get(position).getSentence_en());
        C_translate.setText(mdata.get(position).getSentence_ch());
        source.setText("——由"+mdata.get(position).getSource()+"添加");
        del_btn.setOnClickListener(view -> mOnItemListener.onDeleteClick(position));
        edit_btn.setOnClickListener(view -> mOnItemListener.onEditClick(position));
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

//    private Handler mHandler = new Handler(message -> {
//        switch (message.what){
//            case 0:
//
//                break;
//        }
//        return false;
//    });

    public interface onItemListener {
        void onDeleteClick(int i);
        void onEditClick(int i);
    }

    void setOnItemClickListener(onItemListener mOnItemListener) {
        this.mOnItemListener = mOnItemListener;
    }

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
