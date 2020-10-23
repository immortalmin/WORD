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

import java.util.HashMap;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class CommitFeedbackActivity extends AppCompatActivity implements View.OnClickListener{

    Button return_btn,add_pic_btn,commit_btn;
    RadioGroup radiogroup1,radiogroup2;
    private AutoLineUtil img_group;
    private MyEditText descriptionText,contactText;
    private UserData userData = new UserData();
    private DataUtil dataUtil;
    private String ImageString = "";
    private int count=0;

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
                finish();
                overridePendingTransition(R.anim.slide_left_in,R.anim.slide_to_right);
                break;
            case R.id.add_pic_btn:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,0);
                break;
            case R.id.commit_btn:
                HashMap<String,Object> data = new HashMap<>();
                data.put("uid",userData.getUid());
                data.put("description",descriptionText.getText());
                data.put("image",ImageString);
                switch (radiogroup1.getCheckedRadioButtonId()){
                    case R.id.functionRB:
                        data.put("what","0");
                        break;
                    case R.id.feedbackRB:
                        data.put("what","1");
                        data.put("phone_model","XiaoMi6");
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
                httpGetContext.uploadFeedback(data);
            }
        }).start();

    }

    private Handler mHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 0:
//                    iv1.setImageBitmap((Bitmap)msg.obj);
                    LayoutInflater inflater = getLayoutInflater();
                    View view1 = inflater.inflate(R.layout.imglayout,null);
                    ImageView imageview =view1.findViewById(R.id.imageView);
                    Bitmap img = (Bitmap)msg.obj;
                    int img_width = img.getWidth();
                    int img_height = img.getHeight();
                    Bitmap square_img = Bitmap.createBitmap(img,0,0,Math.min(img_width,img_height),Math.min(img_width,img_height));
                    imageview.setImageBitmap(square_img);
                    Button img_del = view1.findViewById(R.id.img_del);
                    img_del.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            img_group.removeView(view1);
                            count--;
                        }
                    });
                    img_group.addView(view1,count++);
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
                    // 将图片显示到界面上
                    Bitmap bitmap = ImageUtils.getBitmapFromPath(picturePath, 80, 80);
                    //上传图片到服务器
                    ImageString = android.os.Environment.getExternalStorageDirectory()+"/temp.jpg";
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
//            Intent intent = new Intent();
//            setResult(1,intent);
            finish();
            overridePendingTransition(R.anim.slide_left_in,R.anim.slide_to_right);
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
