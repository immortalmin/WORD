package com.immortalmin.www.word;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.smssdk.gui.DefaultContactViewItem;

public class FeedbackAdapter extends BaseAdapter {

    List<HashMap<String,Object>> mdata;
    private LayoutInflater mInflater,img_inflater;//布局装载器对象
    private Context context;

    /*
    * convertView优化？
    * setTag和getTag?
    *
    * */

    public FeedbackAdapter(Context context, List<HashMap<String,Object>> data) {
        this.mdata = data;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

//        View v = mInflater.inflate(R.layout.feedbackitem,null);
//        TextView username = (TextView)v.findViewById(R.id.username);
//        username.setText(mdata.get(position).get("aaa").toString());

//        WordView word_en = (WordView)v.findViewById(R.id.word_en);
//        TextView word_ch = (TextView)v.findViewById(R.id.word_ch);
//        TextView review_date = (TextView)v.findViewById(R.id.review_date);
//
//        word_en.setmText(mdata.get(position).get("word_en").toString());
//        word_en.setAccount((float)(Integer.valueOf(mdata.get(position).get("correct_times").toString())/5.0));
//        word_ch.setText(mdata.get(position).get("word_ch").toString());
//        String review_date_string = mdata.get(position).get("review_date").toString();
//        if("1970-01-01".equals(review_date_string) || "null".equals(review_date_string)) review_date.setText("");
//        else review_date.setText(review_date_string);
        View v;
        ViewHolder viewHolder;
        if(convertView==null){
            v = mInflater.inflate(R.layout.feedbackitem,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.username = v.findViewById(R.id.username);
            viewHolder.context = v.findViewById(R.id.context);
//            viewHolder.profile_photo = v.findViewById(R.id.profile_photo);
            viewHolder.img_group = v.findViewById(R.id.img_group);
            v.setTag(viewHolder);
        }else{
            v = convertView;
            viewHolder = (ViewHolder)v.getTag();
        }

        //获取控件实例，并调用set...方法使其显示出来
        viewHolder.username.setText(mdata.get(position).get("uid").toString());
        viewHolder.context.setText(mdata.get(position).get("description").toString());


        ArrayList<Object> img_list = (ArrayList<Object>)mdata.get(position).get("img_list");
        for(int i=0;i<img_list.size();i++){
            ImageView imageView = new ImageView(context);
            LinearLayout.LayoutParams word_meaning_Params = new LinearLayout.LayoutParams(conversion(80), conversion(80));
//            word_meaning_Params.setMargins(conversion(18), conversion(5), conversion(18), 0);
            imageView.setLayoutParams(word_meaning_Params);
            imageView.setImageBitmap((Bitmap)img_list.get(i));
            viewHolder.img_group.addView(imageView);
        }
//        viewHolder.profile_photo.setImageBitmap((Bitmap)img_list.get(0));
        return v;
    }

    private int conversion(int value){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
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

    class ViewHolder{
        TextView username,context;
//        ImageView profile_photo;
        AutoLineUtil img_group;
    }
}


