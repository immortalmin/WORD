package com.immortalmin.www.word;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tencent.tauth.Tencent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener,
        EditDialog.OnDialogInteractionListener{

    private Button return_btn,logout_btn,motto_edit_btn,setting_btn;
    private TextView nickname,motto,changePwd,feedback;
    private CircleImageView photo;
    private ImageView backdrop;
    private SignIn signIn;
    private User user = new User();
    private JsonRe jsonRe = new JsonRe();
    private DataUtil dataUtil = new DataUtil(ProfileActivity.this);
    private CaptureUtil captureUtil = new CaptureUtil();
    private MyAsyncTask myAsyncTask;
    private UseTimeDataManager mUseTimeDataManager = new UseTimeDataManager(this);
    private HashMap<String,Object> edit_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        return_btn = findViewById(R.id.return_btn);
        logout_btn = findViewById(R.id.logout_btn);
        motto_edit_btn = findViewById(R.id.motto_edit_btn);
        setting_btn = findViewById(R.id.setting_btn);
        nickname = findViewById(R.id.nickname);
        motto = findViewById(R.id.motto);
        changePwd = findViewById(R.id.changePwd);
        feedback = findViewById(R.id.feedback);
        photo = findViewById(R.id.photo);
        signIn = findViewById(R.id.signIn);
        backdrop = findViewById(R.id.backdrop);
        photo.setOnClickListener(this);
        return_btn.setOnClickListener(this);
        logout_btn.setOnClickListener(this);
        motto_edit_btn.setOnClickListener(this);
        setting_btn.setOnClickListener(this);
        nickname.setOnClickListener(this);
        motto.setOnClickListener(this);
        changePwd.setOnClickListener(this);
        feedback.setOnClickListener(this);
        init();
    }

    private void init() {
        dataUtil.getdata(new DataUtil.HttpCallbackStringListener() {
            @Override
            public void onFinish(User userdata) {
                user = userdata;
                mHandler.sendEmptyMessage(1);
                //获取使用时间并显示
                getUseTime();
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
                setResult(1,intent);
                finish();
                overridePendingTransition(R.anim.slide_right_in,R.anim.slide_to_left);
                break;
            case R.id.logout_btn:
                SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                int login_mode = sp.getInt("login_mode",0);
                if(login_mode==0){
                    sp.edit().putInt("status",0).apply();
                }else{
                    Tencent tencent = Tencent.createInstance("101933564", this.getApplicationContext());
                    tencent.logout(this);
                    sp.edit().putString("open_id",null).putString("access_token",null).putString("expires_in",null).apply();
                }
                intent = new Intent();
                intent.setAction("com.immortalmin.www.MainActivity");
                sendBroadcast(intent);
                intent = new Intent(ProfileActivity.this,LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                finish();
                break;
            case R.id.photo:
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,0);
                break;
            case R.id.nickname:
                edit_data = new HashMap<>();
                edit_data.put("attr","username");
                edit_data.put("title","修改用户名");
                edit_data.put("max_length",15);
                edit_data.put("is_null",false);
                edit_data.put("hint","昵称不能为空");
                edit_data.put("content",nickname.getText());
                show_edit_dialog(edit_data);
                mHandler.obtainMessage(2).sendToTarget();
                break;
            case R.id.motto_edit_btn: case R.id.motto:
                edit_data = new HashMap<>();
                edit_data.put("attr","motto");
                edit_data.put("title","修改个性签名");
                edit_data.put("max_length",50);
                edit_data.put("is_null",true);
                edit_data.put("hint","为这里的荒凉增添生气");
                if("你还没有设置个性签名".equals(motto.getText().toString())){
                    edit_data.put("content","");
                }else{
                    edit_data.put("content",motto.getText());
                }
                show_edit_dialog(edit_data);
                mHandler.obtainMessage(2).sendToTarget();
                break;
            case R.id.setting_btn:
                intent = new Intent(ProfileActivity.this,SettingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_in,R.anim.slide_to_right);
                break;
            case R.id.changePwd:
                intent = new Intent(ProfileActivity.this,ChangePwdActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
            case R.id.feedback:
                intent = new Intent(ProfileActivity.this,FeedbackActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
        }
    }

    /**
     * 更新用户数据
     * @param jsonObject
     */
    private void updateUserdata(JSONObject jsonObject){
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute(jsonObject);
    }

    /**
     * 获取用户每天的使用时长
     */
    private void getUseTime(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("what",15);
            jsonObject.put("uid",user.getUid());
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            ArrayList<Integer> usetime = jsonRe.useTimeData(result);
            //加入今天的数据
            usetime.add(0,getTodayUseTime());
            signIn.setSign_in_times(usetime);
        });
        myAsyncTask.execute(jsonObject);
    }

    /**
     * 获取今天已使用的时间
     * @return
     */
    private int getTodayUseTime(){
        int minutes = 0;
        mUseTimeDataManager = UseTimeDataManager.getInstance(ProfileActivity.this);
        mUseTimeDataManager.refreshData(user.getLast_login(),System.currentTimeMillis());
        List<PackageInfo> packageInfos = mUseTimeDataManager.getmPackageInfoListOrderByTime();
        for (int i = 0; i < packageInfos.size(); i++) {
            if ("com.immortalmin.www.word".equals(packageInfos.get(i).getmPackageName())) {
                minutes = (int)(packageInfos.get(i).getmUsedTime()/60000);
                break;
            }
        }
        return minutes;
    }


    private void setImage(String pic) {
        Bitmap bitmap=ImageUtils.getPhotoFromStorage(pic);
        if(bitmap==null){
            Log.i("ccc","照片不存在 正从服务器下载...");
            getImage(pic);
        }else{
            mHandler.obtainMessage(0,bitmap).sendToTarget();
        }
    }

    private void getImage(final String pic){
        new Thread(() -> {
            HttpGetContext httpGetContext = new HttpGetContext();
            Bitmap bitmap;
            if(user.getLogin_mode()==0){
                bitmap = httpGetContext.HttpclientGetImg("http://47.98.239.237/word/img/profile/"+pic,0);
            }else{
                bitmap = HttpGetContext.getbitmap(user.getProfile_photo());
            }
            ImageUtils.savePhotoToStorage(bitmap,user.getUid()+".jpg");
            mHandler.obtainMessage(0,bitmap).sendToTarget();
        }).start();
    }

    private void show_edit_dialog(HashMap<String,Object> data){
        EditDialog editDialog = new EditDialog(this,R.style.MyDialog,data);
        editDialog.setCancelable(false);
        editDialog.setOnDismissListener(dialogInterface -> mHandler.sendEmptyMessage(3));
        editDialog.show();
    }

    private Handler mHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    photo.setImageBitmap((Bitmap)msg.obj);
                    break;
                case 1:
                    nickname.setText(user.getUsername());
                    if(user.getMotto()==null||"".equals(user.getMotto())||"null".equals(user.getMotto())){
                        motto.setText("你还没有设置个性签名");
                    }else{
                        motto.setText(user.getMotto());
                    }
                    setImage(user.getUid()+".jpg");
                    break;
                case 2:
                    Glide.with(ProfileActivity.this).load(captureUtil.getcapture(ProfileActivity.this))
                            .apply(bitmapTransform(new BlurTransformation(25))).into(backdrop);
                    backdrop.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    backdrop.setVisibility(View.INVISIBLE);
                    break;
            }
            return false;
        }
    });


    private void uploadPic(final String url,final String file){
        new Thread(() -> {
            HttpGetContext httpGetContext = new HttpGetContext();
            httpGetContext.uploadpic(url,file, user.getUid());
        }).start();

    }

    @Override
    public void EditInteraction(HashMap<String,Object> res){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("what",23);
            jsonObject.put("uid", user.getUid());
            jsonObject.put(res.get("attr").toString(),res.get("content").toString());
        }catch (JSONException e){
            e.printStackTrace();
        }
        //更新用户数据
        updateUserdata(jsonObject);
        SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        //如果修改了数据，显示toast
        if(!sp.getString(res.get("attr").toString(),"none").equals(res.get("content").toString())){
            Toast.makeText(ProfileActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
        }
        //修改本地用户数据
        sp.edit().putString(res.get("attr").toString(),res.get("content").toString()).apply();
        switch (res.get("attr").toString()){
            case "username":
                user.setUsername(res.get("content").toString());
                break;
            case "motto":
                user.setMotto(res.get("content").toString());
                break;
        }
        //刷新界面的数据
        mHandler.obtainMessage(1).sendToTarget();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (data == null) {
                Log.i("ccc", "数据为空");
                return;
            }
            //打开相册并选择照片，这个方式选择单张
            // 获取返回的数据，这里是android自定义的Uri地址
            Uri selectedImage = data.getData();
            // 获取选择照片的数据视图
            if (selectedImage != null) {
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                // 从数据视图中获取已选择图片的路径
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                // 将图片显示到界面上
                Bitmap bitmap = ImageUtils.getBitmapFromPath(picturePath, 80, 80);
                //上传图片到服务器
                uploadPic("http://47.98.239.237/word/php_file2/upload_picture.php", picturePath);
                //删除老的，添加新的
                ImageUtils.deletePhotoFromStorage(user.getProfile_photo());
                ImageUtils.savePhotoToStorage(bitmap, user.getProfile_photo());
                SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                sp.edit().putString("profile_photo", user.getProfile_photo()).apply();
                mHandler.obtainMessage(0, bitmap).sendToTarget();
                cursor.close();
            } else {
                Log.i("ccc", "数据为空");
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
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
