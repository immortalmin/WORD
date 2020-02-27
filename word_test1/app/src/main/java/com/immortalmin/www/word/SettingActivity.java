package com.immortalmin.www.word;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{

    private Button return_btn,logout_btn;
    private EditText recite_num,recite_scope;
    private TextView nickname;
    private CircleImageView photo;
    private String uid;
    private ImageUtils imageUtils = new ImageUtils();
    private String profile_photo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        return_btn = (Button)findViewById(R.id.return_btn);
        logout_btn = (Button)findViewById(R.id.logout_btn);
        recite_num = (EditText)findViewById(R.id.recite_num);
        recite_scope = (EditText)findViewById(R.id.recite_scope);
        nickname = (TextView) findViewById(R.id.nickname);
        photo = (CircleImageView) findViewById(R.id.photo);
        photo.setOnClickListener(this);
        return_btn.setOnClickListener(this);
        logout_btn.setOnClickListener(this);
        init();
    }

    private void init() {
        SharedPreferences sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
        uid = sp.getString("uid",null);
        recite_num.setText(String.valueOf(sp.getInt("recite_num",20)));
        recite_scope.setText(String.valueOf(sp.getInt("recite_scope",10)));
        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        nickname.setText(sp.getString("username",null));
        profile_photo = sp.getString("profile_photo",null);
        setImage(profile_photo);

    }

    private void setImage(String pic) {
        Bitmap bitmap=imageUtils.getPhotoFromStorage(pic);
        if(bitmap==null){
            Log.i("ccc","照片不存在");
            getImage(pic);
        }else{
            Log.i("ccc","照片存在");
            mHandler.obtainMessage(0,bitmap).sendToTarget();
        }
    }


    public void onClick(View view){
        switch (view.getId()){
            case R.id.return_btn:
//                Intent intent = new Intent(SettingActivity.this,MainActivity.class);
//                startActivity(intent);
                update_setting();
                finish();
                overridePendingTransition(R.anim.slide_right_in,R.anim.slide_to_left);
                break;
            case R.id.logout_btn:
                SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                sp.edit().putString("status","0").apply();
                Intent intent = new Intent();
                intent.setAction("com.immortalmin.www.MainActivity");
                sendBroadcast(intent);
                intent = new Intent(SettingActivity.this,LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                finish();
                break;
            case R.id.photo:
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,0);
                break;
        }
    }

    private void update_setting(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
                sp.edit().putInt("recite_num",Integer.valueOf(recite_num.getText().toString()))
                        .putInt("recite_scope",Integer.valueOf(recite_scope.getText().toString()))
                        .apply();
                JSONObject jsonObject = new JSONObject();
                try{

                    jsonObject.put("uid",uid);
                    jsonObject.put("recite_num",recite_num.getText().toString());
                    jsonObject.put("recite_scope",recite_scope.getText().toString());
                }catch (JSONException e){
                    e.printStackTrace();
                }
                HttpGetContext httpGetContext = new HttpGetContext();
                httpGetContext.getData("http://47.98.239.237/word/php_file2/update_setting.php",jsonObject);
            }
        }).start();
    }





    private void getImage(final String pic){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                Bitmap bitmap = httpGetContext.HttpclientGetImg("http://47.98.239.237/word/img/"+pic);
                imageUtils.savePhotoToStorage(bitmap,pic);
                mHandler.obtainMessage(0,bitmap).sendToTarget();
            }
        }).start();
    }

    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    photo.setImageBitmap((Bitmap)msg.obj);
                    break;

            }
        }
    };


    private void uploadPic(final String url,final String file){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                httpGetContext.uploadpic(url,file,uid);
            }
        }).start();

    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
//            case TAKEPHOTO:
//                if(resultCode ==RESULT_OK){
//                    draweeView.setImageURI(imageUri);
//                }
//                break;
            case 0:
                //打开相册并选择照片，这个方式选择单张
                // 获取返回的数据，这里是android自定义的Uri地址
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                // 获取选择照片的数据视图
                if(selectedImage!=null){
                    Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    // 从数据视图中获取已选择图片的路径
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    // 将图片显示到界面上
                    Bitmap bitmap = ImageUtils.getBitmapFromPath(picturePath, 80, 80);

                    File file = new File(picturePath);
                    uploadPic("http://47.98.239.237/word/php_file2/upload_picture.php",picturePath);

                    ImageUtils imageUtils = new ImageUtils();
                    imageUtils.deletePhotoFromStorage(profile_photo);
                    SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                    sp.edit().putString("profile_photo", profile_photo).apply();

                    mHandler.obtainMessage(0,bitmap).sendToTarget();
                    cursor.close();
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            update_setting();
            finish();
            overridePendingTransition(R.anim.slide_right_in,R.anim.slide_to_left);
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }


}
