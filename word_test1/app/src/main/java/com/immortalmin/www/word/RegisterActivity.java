package com.immortalmin.www.word;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private Button reg_btn,return_btn;
    private EditText username,pwd,confirm_pwd;
    private TextView user_warn,pwd_warn,confirm_warn;
    private CircleImageView profile_photo;
    private JsonRe jsonRe;
    private Runnable toLogin;
    private String profilephotoPath=null;
    private HashMap<String,Object> userdata=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        reg_btn = (Button)findViewById(R.id.reg_btn);
        return_btn = (Button)findViewById(R.id.return_btn);
        username = (EditText)findViewById(R.id.username);
        pwd = (EditText)findViewById(R.id.pwd);
        confirm_pwd = (EditText)findViewById(R.id.confirm_pwd);
        user_warn = (TextView) findViewById(R.id.user_warn);
        pwd_warn = (TextView) findViewById(R.id.pwd_warn);
        confirm_warn = (TextView) findViewById(R.id.confirm_warn);
        profile_photo = (CircleImageView) findViewById(R.id.profile_photo);
        reg_btn.setOnClickListener(this);
        return_btn.setOnClickListener(this);
        profile_photo.setOnClickListener(this);

        jsonRe = new JsonRe();
        init();


    }

    private void init() {
        /**
         * 延迟跳转（等toast结束后跳转）
         */
        toLogin = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.putExtra("username",username.getText().toString());
                setResult(1,intent);
                finish();
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
            }
        };

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mHandler.obtainMessage(0).sendToTarget();
                String uname = username.getText().toString();
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("username",uname);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                query_user(jsonObject);
            }
        });

        pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String now_pwd = pwd.getText().toString();
                if(!isPassword(now_pwd)){
                    mHandler.obtainMessage(4).sendToTarget();
                }else{
                    mHandler.obtainMessage(3).sendToTarget();
                }
            }
        });

        confirm_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(confirm_pwd.getText().toString().equals(pwd.getText().toString())){
                    mHandler.obtainMessage(5).sendToTarget();
                }else{
                    mHandler.obtainMessage(6).sendToTarget();
                }
            }
        });
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.reg_btn:
                String uname = username.getText().toString();
                String password = pwd.getText().toString();
                Toast.makeText(RegisterActivity.this,"注册成功 即将跳转到主页",Toast.LENGTH_SHORT).show();
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("username",uname);
                    jsonObject.put("pwd",password);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                uploadPic(profilephotoPath,jsonObject);
//                register(jsonObject);
                break;
            case R.id.profile_photo:
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,0);
                break;
            case R.id.return_btn:
                finish();
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
        }
    }

    public boolean isPassword(String password){
        String regex="^(?![0-9])(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";
        Pattern p= Pattern.compile(regex);
        Matcher m=p.matcher(password);
        boolean isMatch=m.matches();
        Log.i("ccc", "isPassword: 是否密码正则匹配"+isMatch);
        return isMatch;
    }

    private void register(final JSONObject jsonObject) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                httpGetContext.getData("http://47.98.239.237/word/php_file2/register.php",jsonObject);
                mHandler.postDelayed(toLogin,2000);
            }
        }).start();
    }


    private void query_user(final JSONObject jsonObject) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                String wordjson = httpGetContext.getData("http://47.98.239.237/word/php_file2/getuserdata.php",jsonObject);
                userdata = jsonRe.userData(wordjson);
                if(userdata.size()!=0){
                    mHandler.obtainMessage(1).sendToTarget();
                }
            }
        }).start();
    }
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    user_warn.setVisibility(View.INVISIBLE);
                    reg_btn.setClickable(true);
                    break;
                case 1:
                    user_warn.setVisibility(View.VISIBLE);
                    reg_btn.setClickable(false);
                    break;
                case 2:

                    break;
                case 3:
                    pwd_warn.setVisibility(View.INVISIBLE);
                    reg_btn.setClickable(true);
                    break;
                case 4:
                    pwd_warn.setVisibility(View.VISIBLE);
                    reg_btn.setClickable(false);
                    break;
                case 5:
                    confirm_warn.setVisibility(View.INVISIBLE);
                    reg_btn.setClickable(true);
                    break;
                case 6:
                    confirm_warn.setVisibility(View.VISIBLE);
                    reg_btn.setClickable(false);
                    break;
                case 7:
                    profile_photo.setImageBitmap((Bitmap)msg.obj);
                    break;
            }
        }
    };

    private void uploadPic(final String file_path,final JSONObject userdata){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                httpGetContext.userRegister(file_path,userdata);
                mHandler.postDelayed(toLogin,2000);
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

//                    uploadPic("http://47.98.239.237/word/php_file2/upload_picture.php",picturePath);
                    profilephotoPath = android.os.Environment.getExternalStorageDirectory()+"/temp.jpg";
                    mHandler.obtainMessage(7,bitmap).sendToTarget();
                    cursor.close();
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
