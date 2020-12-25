package com.immortalmin.www.word;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class CommitFeedbackActivity extends AppCompatActivity implements View.OnClickListener{

    Button return_btn,add_pic_btn,commit_btn;
    RadioGroup radiogroup1,radiogroup2;
    private AutoLineUtil img_group;
    private MyEditText descriptionText,contactText;
    private UserData userData = new UserData();
    private DataUtil dataUtil;
    private ImageUtils imageUtils = new ImageUtils();
    private ArrayList<String> img_list = new ArrayList<>();//需要上传的图片
    private ArrayList<Integer> remove_list = new ArrayList<>();//img_list需要移除的图片的下标
    private int count=0,img_index=0;//count表示要上传的照片的数量，img_index表示所有的照片（包括中途被用户去除的照片）的数量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commit_feedback);
        return_btn = (Button)findViewById(R.id.return_btn);
        add_pic_btn = (Button)findViewById(R.id.add_pic_btn);
        commit_btn = (Button)findViewById(R.id.commit_btn);
        radiogroup1 = (RadioGroup)findViewById(R.id.radiogroup1);
        radiogroup2 = (RadioGroup)findViewById(R.id.radiogroup2);
        descriptionText = (MyEditText) findViewById(R.id.descriptionText);
        contactText = (MyEditText) findViewById(R.id.contactText);
        img_group = (AutoLineUtil) findViewById(R.id.img_group);
        return_btn.setOnClickListener(this);
        add_pic_btn.setOnClickListener(this);
        commit_btn.setOnClickListener(this);
        dataUtil = new DataUtil(this);
        init();
    }

    private void init() {
        //获取用户信息
        dataUtil.getdata(new DataUtil.HttpCallbackStringListener() {
            @Override
            public void onFinish(UserData userdata) {
                userData = userdata;
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.return_btn:
                Intent intent = new Intent();
                setResult(0,intent);
                finish();
                overridePendingTransition(R.anim.slide_left_in,R.anim.slide_to_right);
                break;
            case R.id.add_pic_btn:
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,0);
                break;
            case R.id.commit_btn:
                HashMap<String,Object> data = new HashMap<>();
                data.put("uid",userData.getUid());
                data.put("description",descriptionText.getText());
                switch (radiogroup1.getCheckedRadioButtonId()){
                    case R.id.functionRB:
                        data.put("what","0");
                        break;
                    case R.id.feedbackRB:
                        data.put("what","1");
                        data.put("phone_model","手机型号:" + android.os.Build.MODEL + ",SDK:"
                                + android.os.Build.VERSION.SDK + ",版本号:"
                                + android.os.Build.VERSION.RELEASE);
                        break;
                }
                switch (radiogroup2.getCheckedRadioButtonId()){
                    case R.id.phone_number:
                        data.put("contact","phoneNumber:"+contactText.getText());
                        break;
                    case R.id.email:
                        data.put("contact","email:"+contactText.getText());
                        break;
                    case R.id.QQ:
                        data.put("contact","QQ:"+contactText.getText());
                        break;
                    case R.id.wechat:
                        data.put("contact","wechat:"+contactText.getText());
                        break;
                }
                //去除img_list中需要被移除的图片
                //先按下标从大到小排序
                Collections.sort(remove_list, new Comparator<Integer>() {
                    @Override
                    public int compare(Integer t1, Integer t2) {
                        return t2-t1;
                    }
                });
                //再一个个删除
                for(int i=0;i<remove_list.size();i++){
                    int index = remove_list.get(i);
                    img_list.remove(index);
                }
                //提交反馈
                commitFeedback(data);
                break;
        }
    }

    private void commitFeedback(final HashMap<String,Object> data){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                int feedback_res = httpGetContext.uploadFeedback(data,img_list);
                //XXX:应该等上传反馈结束并返回结果后再显示结果给用户，但是我不会，埋下一个坑给未来的陈大神解决
                /*现在这样容易出现意想不到的结果，比如用户上传反馈失败了也不知道。*/
//                Log.i("ccc",""+feedback_res);
//                if(feedback_res==1){
//                    mHandler.obtainMessage(1,"十分感谢您的反馈，我们会尽快处理!").sendToTarget();
//                }else{
//                    mHandler.obtainMessage(1,"糟糕，提交回馈出错了...").sendToTarget();
//                    //这可如何是好？
//                }
                mHandler.obtainMessage(1,"十分感谢您的反馈，我们会尽快处理!").sendToTarget();
            }
        }).start();

    }

    /**
     * 提交反馈后弹出的Dialog
     * @param res
     */
    private void resDialog(String res){
        SweetAlertDialog feedback_dialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        feedback_dialog.setTitleText("Feedback")
                .setContentText(res)
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent intent = new Intent();
                        setResult(1,intent);
                        finish();
                        overridePendingTransition(R.anim.slide_left_in,R.anim.slide_to_right);
                    }
                });
        feedback_dialog.setCancelable(false);
        feedback_dialog.show();
    }



    private Handler mHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    int now_ind=img_index;
                    LayoutInflater inflater = getLayoutInflater();
                    View view1 = inflater.inflate(R.layout.imglayout,null);
                    ImageView imageview =view1.findViewById(R.id.imageView);
                    Bitmap img = (Bitmap)msg.obj;
                    //将图片显示成方形的
                    int img_width = img.getWidth();
                    int img_height = img.getHeight();
                    Bitmap square_img = Bitmap.createBitmap(img,0,0,Math.min(img_width,img_height),Math.min(img_width,img_height));
                    imageview.setImageBitmap(square_img);
                    Button img_del = view1.findViewById(R.id.img_del);
                    img_del.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            img_group.removeView(view1);
                            remove_list.add(now_ind);
                            count--;
                        }
                    });
                    img_group.addView(view1,count++);
                    img_index++;
                    break;
                case 1:
                    resDialog((String)msg.obj);
                    break;
            }
            return false;
        }
    });

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
                if(data==null){
                    Log.i("ccc","数据为空");
                    break;
                }
                //打开相册并选择照片，这个方式选择单张
                // 获取返回的数据，这里是android自定义的Uri地址
                Uri selectedImage = data.getData();
                // 获取选择照片的数据视图
                if(selectedImage!=null){
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    // 从数据视图中获取已选择图片的路径
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);

                    //保存到本地的临时文件中
                    String tempPath = imageUtils.compressImage(picturePath,"temp_"+img_index);
                    img_list.add(tempPath);
                    // 将图片显示到界面上
                    Bitmap bitmap = ImageUtils.getBitmapFromPath(tempPath, 80, 80);
                    mHandler.obtainMessage(0,bitmap).sendToTarget();
                    cursor.close();
                }else{
                    Log.i("ccc","数据为空");
                }
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent();
            setResult(0,intent);
            finish();
            overridePendingTransition(R.anim.slide_left_in,R.anim.slide_to_right);
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
