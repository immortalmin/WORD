package com.immortalmin.www.word;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
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
import de.hdodenhof.circleimageview.CircleImageView;

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
        View v;
        ViewHolder viewHolder;
        if(convertView==null){
            v = mInflater.inflate(R.layout.feedbackitem,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.username = v.findViewById(R.id.username);
            viewHolder.context = v.findViewById(R.id.context);
            viewHolder.add_time = v.findViewById(R.id.add_time);
            viewHolder.progress_tv = v.findViewById(R.id.progress_tv);
            viewHolder.profile_photo = v.findViewById(R.id.profile_photo);
            viewHolder.img_group = v.findViewById(R.id.img_group);
            viewHolder.icon_iv = v.findViewById(R.id.icon_iv);
            v.setTag(viewHolder);
        }else{
            v = convertView;
            viewHolder = (ViewHolder)v.getTag();
            viewHolder.img_group.removeAllViews();
        }

        //获取控件实例，并调用set...方法使其显示出来
        viewHolder.username.setText(mdata.get(position).get("username").toString());
        viewHolder.context.setText(mdata.get(position).get("description").toString());
        viewHolder.add_time.setText(mdata.get(position).get("add_time").toString());
        if("0".equals(mdata.get(position).get("what").toString())){
            viewHolder.icon_iv.setImageResource(R.drawable.bug_icon);
        }else{
            viewHolder.icon_iv.setImageResource(R.drawable.suggestion_icon);
        }
        viewHolder.progress_tv.setText(mdata.get(position).get("progress").toString());
        switch (mdata.get(position).get("progress").toString()){
            case "待处理":
                viewHolder.progress_tv.setTextColor(Color.parseColor("#F79C15"));
                break;
            case "已采纳":
                viewHolder.progress_tv.setTextColor(Color.parseColor("#2FE9D8"));
                break;
            case "未采纳":

                break;
            case "实现中":
                viewHolder.progress_tv.setTextColor(Color.parseColor("#2FE9D8"));
                break;
            case "已实现":

                break;
            case "修复中":
                viewHolder.progress_tv.setTextColor(Color.parseColor("#2FE9D8"));
                break;
            case "已修复":

                break;
        }
        getImage("http://47.98.239.237/word/img/profile/",mdata.get(position).get("profile_photo").toString(),viewHolder.profile_photo);
        String img_path = mdata.get(position).get("img_path").toString();
        if(!"null".equals(img_path)){
            //XXX:加载图片的代码写得有点土，似乎应该写成工具类？线程？
            String[] img_paths = mdata.get(position).get("img_path").toString().split("#");
            for(int i=0;i<img_paths.length;i++){
                ImageView imageView = new ImageView(context);
                LinearLayout.LayoutParams imageView_Params = new LinearLayout.LayoutParams(conversion(80), conversion(80));
                imageView.setLayoutParams(imageView_Params);
                getImage("http://47.98.239.237/word/img/feedback/",img_paths[i],imageView);
                viewHolder.img_group.addView(imageView);
            }
            //给img_group设置高度
            AutoLineUtil.LayoutParams autoLineParams = viewHolder.img_group.getLayoutParams();
            autoLineParams.height = conversion(80);
            viewHolder.img_group.setLayoutParams(autoLineParams);
        }else{
            AutoLineUtil.LayoutParams autoLineParams = viewHolder.img_group.getLayoutParams();
            autoLineParams.height = conversion(0);
            viewHolder.img_group.setLayoutParams(autoLineParams);
        }
        return v;
    }

    //url:"http://47.98.239.237/word/img/feedback/"
    private void getImage(String url,final String pic,final ImageView imageView){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                Bitmap bitmap = httpGetContext.HttpclientGetImg(url+pic,1);
                HashMap<String,Object> img_data = new HashMap<>();
                img_data.put("imageView",imageView);
                img_data.put("bitmap",bitmap);
                mHandler.obtainMessage(0,img_data).sendToTarget();
            }
        }).start();
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 0:
                    HashMap<String,Object> img_data = (HashMap<String,Object>)message.obj;
                    ImageView imageView = (ImageView)img_data.get("imageView");
                    Bitmap bitmap = (Bitmap)img_data.get("bitmap");
                    imageView.setImageBitmap(bitmap);
                    break;

            }
            return false;
        }
    });

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
        TextView username,context,add_time,progress_tv;
        CircleImageView profile_photo;
        AutoLineUtil img_group;
        ImageView icon_iv;
    }
}


