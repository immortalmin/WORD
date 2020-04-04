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
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{

    private Button return_btn,logout_btn;
    private EditText recite_num,recite_scope;
    private TextView nickname;
    private CircleImageView photo;
    private SignIn signIn;
    private ImageUtils imageUtils = new ImageUtils();
    private UserData userData = new UserData();
    private JsonRe jsonRe = new JsonRe();
    private UseTimeDataManager mUseTimeDataManager = new UseTimeDataManager(this);

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
        signIn = (SignIn) findViewById(R.id.signIn);
        photo.setOnClickListener(this);
        return_btn.setOnClickListener(this);
        logout_btn.setOnClickListener(this);
        init();
    }

    private void init() {
        init_user();
        mHandler.obtainMessage(1).sendToTarget();

        //获取使用时间并显示
        getusetime();
//        SharedPreferences sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
//        uid = sp.getString("uid",null);
//        recite_num.setText(String.valueOf(sp.getInt("recite_num",20)));
//        recite_scope.setText(String.valueOf(sp.getInt("recite_scope",10)));
//        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
//        nickname.setText(sp.getString("username",null));
//        profile_photo = sp.getString("profile_photo",null);
//        setImage(profile_photo);

    }

    private void init_user(){
        SharedPreferences sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
        userData.setUid(sp.getString("uid",null));
        userData.setRecite_num(sp.getInt("recite_num",20));
        userData.setRecite_scope(sp.getInt("recite_scope",10));
        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        userData.setUsername(sp.getString("username",null));
        userData.setLast_login(sp.getLong("last_login",404L));
        userData.setProfile_photo(sp.getString("profile_photo",null));
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.return_btn:
                update_setting();
                Intent intent = new Intent();
                setResult(1,intent);
                finish();
                overridePendingTransition(R.anim.slide_right_in,R.anim.slide_to_left);
                break;
            case R.id.logout_btn:
                SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                sp.edit().putString("status","0").apply();
                intent = new Intent();
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

    private void getusetime(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("uid",userData.getUid());
                }catch (JSONException e){
                    e.printStackTrace();
                }
                HttpGetContext httpGetContext = new HttpGetContext();
                String res = httpGetContext.getData("http://47.98.239.237/word/php_file2/getusetime.php",jsonObject);
                ArrayList<Integer> usetime = jsonRe.usetimeData(res);
                //加入今天的数据
                usetime.add(0,get_today_usetime());
                signIn.setSign_in_times(usetime);
            }
        }).start();
    }

    /**
     * 获取今天已使用的时间
     * @return
     */
    private int get_today_usetime(){
        int minutes = 0;
        mUseTimeDataManager = UseTimeDataManager.getInstance(SettingActivity.this);
        mUseTimeDataManager.refreshData(userData.getLast_login(),System.currentTimeMillis());
        JSONObject jsonObject = new JSONObject();
        List<PackageInfo> packageInfos = mUseTimeDataManager.getmPackageInfoListOrderByTime();
        for (int i = 0; i < packageInfos.size(); i++) {
            if ("com.immortalmin.www.word".equals(packageInfos.get(i).getmPackageName())) {
                minutes = (int)(packageInfos.get(i).getmUsedTime()/60000);
                break;
            }
        }
        return minutes;
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

                    jsonObject.put("uid",userData.getUid());
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

    private void setImage(String pic) {
        Bitmap bitmap=imageUtils.getPhotoFromStorage(pic);
        if(bitmap==null){
            Log.i("ccc","照片不存在 正从服务器下载...");
            getImage(pic);
        }else{
//            Log.i("ccc","照片存在");
            mHandler.obtainMessage(0,bitmap).sendToTarget();
        }
    }

    private void getImage(final String pic){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                Bitmap bitmap = httpGetContext.HttpclientGetImg("http://47.98.239.237/word/img/"+pic);
                imageUtils.savePhotoToStorage(bitmap,pic);
                Log.i("ccc","图片下载完成");
                mHandler.obtainMessage(0,bitmap).sendToTarget();
            }
        }).start();
    }

    private Handler mHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    photo.setImageBitmap((Bitmap)msg.obj);
                    break;
                case 1:
                    recite_num.setText(String.valueOf(userData.getRecite_num()));
                    recite_scope.setText(String.valueOf(userData.getRecite_scope()));
                    nickname.setText(userData.getUsername());
                    setImage(userData.getProfile_photo());
                    break;

            }
            return false;
        }
    });


    private void uploadPic(final String url,final String file){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                httpGetContext.uploadpic(url,file,userData.getUid());
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
                    uploadPic("http://47.98.239.237/word/php_file2/upload_picture.php",android.os.Environment.getExternalStorageDirectory()+"/temp.jpg");

                    //删除老的，添加新的
                    ImageUtils imageUtils = new ImageUtils();
                    imageUtils.deletePhotoFromStorage(userData.getProfile_photo());
                    imageUtils.savePhotoToStorage(bitmap,userData.getProfile_photo());
                    SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                    sp.edit().putString("profile_photo", userData.getProfile_photo()).apply();
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
            update_setting();
            Intent intent = new Intent();
            setResult(1,intent);
            finish();
            overridePendingTransition(R.anim.slide_right_in,R.anim.slide_to_left);
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }


}
