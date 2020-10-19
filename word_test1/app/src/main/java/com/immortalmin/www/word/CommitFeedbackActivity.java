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
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;

import java.util.HashMap;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class CommitFeedbackActivity extends AppCompatActivity implements View.OnClickListener{

    Button return_btn,add_pic_btn,commit_btn;
    RadioGroup radiogroup1,radiogroup2;
    ImageView iv1;
    private MyEditText descriptionText,contactText;
    private UserData userData = new UserData();
    private DataUtil dataUtil;
    private String ImageString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commit_feedback);
        return_btn = (Button)findViewById(R.id.return_btn);
        add_pic_btn = (Button)findViewById(R.id.add_pic_btn);
        commit_btn = (Button)findViewById(R.id.commit_btn);
        radiogroup1 = (RadioGroup)findViewById(R.id.radiogroup1);
        radiogroup2 = (RadioGroup)findViewById(R.id.radiogroup2);
        iv1 = (ImageView) findViewById(R.id.iv1);
        descriptionText = (MyEditText) findViewById(R.id.descriptionText);
        contactText = (MyEditText) findViewById(R.id.contactText);
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
                if(!"".equals(ImageString)){
                    data.put("image",ImageString);
                }
                switch (radiogroup1.getCheckedRadioButtonId()){
                    case R.id.functionRB:

                        break;
                    case R.id.feedbackRB:
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
                //TODO:编写提交网络数据的部分，以及数据库表的设计
                Log.i("ccc",data.toString());
                break;
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    iv1.setImageBitmap((Bitmap)msg.obj);
                    break;

            }
            return false;
        }
    });

    private void commitContext(final String url, HashMap<String,Object> data){
        new Thread(new Runnable() {
            @Override
            public void run() {
//                HttpGetContext httpGetContext = new HttpGetContext();
//                httpGetContext.uploadFeedback(url,file,userData.getUid());
                Log.i("ccc",data.toString());
            }
        }).start();

    }

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
//                    commitContext("http://47.98.239.237/word/php_file2/upload_picture.php",android.os.Environment.getExternalStorageDirectory()+"/temp.jpg");
                    ImageString = android.os.Environment.getExternalStorageDirectory()+"/temp.jpg";
                    //删除老的，添加新的
//                    ImageUtils imageUtils = new ImageUtils();
//                    imageUtils.deletePhotoFromStorage(userData.getProfile_photo());
//                    imageUtils.savePhotoToStorage(bitmap,userData.getProfile_photo());
//                    SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
//                    sp.edit().putString("profile_photo", userData.getProfile_photo()).apply();
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
