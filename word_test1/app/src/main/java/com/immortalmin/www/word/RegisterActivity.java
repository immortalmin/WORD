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

    private Button register_reg_btn,return_btn;
    private EditText register_username_edit,register_password_edit,confirm_pwd,telephone,email;
    private TextView user_warn,pwd_warn,confirm_warn,telephone_warn,email_warn;
    private CircleImageView register_profile_photo;
    private JsonRe jsonRe;
    private Runnable toLogin;
    private String profilephotoPath="null";
    private HashMap<String,Object> userdata=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        register_reg_btn = (Button)findViewById(R.id.register_reg_btn);
        return_btn = (Button)findViewById(R.id.return_btn);
        register_username_edit = (EditText)findViewById(R.id.register_username_edit);
        register_password_edit = (EditText)findViewById(R.id.register_password_edit);
        confirm_pwd = (EditText)findViewById(R.id.confirm_pwd);
        telephone = (EditText)findViewById(R.id.telephone);
        email = (EditText)findViewById(R.id.email);

        user_warn = (TextView) findViewById(R.id.user_warn);
        pwd_warn = (TextView) findViewById(R.id.pwd_warn);
        confirm_warn = (TextView) findViewById(R.id.confirm_warn);
        telephone_warn = (TextView) findViewById(R.id.telephone_warn);
        email_warn = (TextView) findViewById(R.id.email_warn);
        register_profile_photo = (CircleImageView) findViewById(R.id.register_profile_photo);
        register_reg_btn.setOnClickListener(this);
        return_btn.setOnClickListener(this);
        register_profile_photo.setOnClickListener(this);

        jsonRe = new JsonRe();
        init();


    }

    private void init() {
        register_reg_btn.setEnabled(false);
        /**
         * 延迟跳转（等toast结束后跳转）
         */
        toLogin = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.putExtra("username",register_username_edit.getText().toString());
                setResult(1,intent);
                finish();
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
            }
        };

        register_username_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mHandler.obtainMessage(0).sendToTarget();
                String uname = register_username_edit.getText().toString();
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("username",uname);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                query_user(jsonObject);
            }
        });

        register_password_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String now_pwd = register_password_edit.getText().toString();
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
                if(confirm_pwd.getText().toString().equals(register_password_edit.getText().toString())){
                    mHandler.obtainMessage(5).sendToTarget();
                }else{
                    mHandler.obtainMessage(6).sendToTarget();
                }
            }
        });
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.register_reg_btn:
                String uname = register_username_edit.getText().toString();
                String password = register_password_edit.getText().toString();
                if(telephone.getText().toString().length()==0||email.getText().toString().length()==0){
                    Toast.makeText(RegisterActivity.this,"请填写完整",Toast.LENGTH_SHORT).show();
                    break;
                }
                Toast.makeText(RegisterActivity.this,"注册成功 即将跳转到主页",Toast.LENGTH_SHORT).show();
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("username",uname);
                    jsonObject.put("pwd",password);
                    jsonObject.put("imgpath",profilephotoPath);
                    jsonObject.put("telephone",telephone.getText().toString());
                    jsonObject.put("email",email.getText().toString());
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                register(jsonObject);
                break;
            case R.id.register_profile_photo:
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
        return isMatch;
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
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 0:
                    user_warn.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    user_warn.setVisibility(View.VISIBLE);
                    break;
                case 2:

                    break;
                case 3:
                    pwd_warn.setVisibility(View.INVISIBLE);
                    break;
                case 4:
                    pwd_warn.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    confirm_warn.setVisibility(View.INVISIBLE);
                    register_reg_btn.setEnabled(true);
                    break;
                case 6:
                    confirm_warn.setVisibility(View.VISIBLE);
                    register_reg_btn.setEnabled(false);
                    break;
                case 7:
                    register_profile_photo.setImageBitmap((Bitmap)message.obj);
                    break;
            }
            return false;
        }
    });

    /**
     * 进行注册
     * @param userdata
     */
    private void register(final JSONObject userdata){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                httpGetContext.userRegister(userdata);
                mHandler.postDelayed(toLogin,2000);
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
