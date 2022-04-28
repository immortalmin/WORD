package com.immortalmin.www.word;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

public class RegisterActivity extends MyAppCompatActivity
        implements View.OnClickListener, Register0Fragment.OnFragmentInteractionListener,
        Register1Fragment.OnFragmentInteractionListener,Register2Fragment.OnFragmentInteractionListener,
        Register3EmailFragment.OnFragmentInteractionListener,Register3QuestionFragment.OnFragmentInteractionListener,
        Register3FingerprintFragment.OnFragmentInteractionListener,Register3MultipasswordFragment.OnFragmentInteractionListener,
        Register3PwdhintFragment.OnFragmentInteractionListener{

    private Button return_btn;
    private TextView nav_text;
    private User user = new User();
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentTransaction transaction = fragmentManager.beginTransaction();
    private Register0Fragment register0Fragment = new Register0Fragment();
    private Register1Fragment register1Fragment = new Register1Fragment();
    private Register2Fragment register2Fragment = new Register2Fragment();
    private Register3EmailFragment register3EmailFragment = new Register3EmailFragment();
    private Register3QuestionFragment register3QuestionFragment = new Register3QuestionFragment();
    private Register3FingerprintFragment register3FingerprintFragment = new Register3FingerprintFragment();
    private Register3MultipasswordFragment register3MultipasswordFragment = new Register3MultipasswordFragment();
    private Register3PwdhintFragment register3PwdhintFragment = new Register3PwdhintFragment();
    private int stepIndex=0;
    private String[] titleString = {"注册","绑定手机","其他方式","绑定邮箱","设置问题","添加指纹","设置多个密码","设置密码提示"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        return_btn = findViewById(R.id.return_btn);
        nav_text = findViewById(R.id.nav_text);

        return_btn.setOnClickListener(this);
        init();


    }

    private void init() {
        init_fragment();
    }

    /**
     * 加载所有的fragment
     */
    private void init_fragment() {
        register0Fragment.setActivity(this);
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.stepsFrameLayout, register0Fragment);
        transaction.add(R.id.stepsFrameLayout, register1Fragment);
        transaction.add(R.id.stepsFrameLayout, register2Fragment);
        transaction.add(R.id.stepsFrameLayout, register3EmailFragment);
        transaction.add(R.id.stepsFrameLayout, register3QuestionFragment);
        transaction.add(R.id.stepsFrameLayout, register3FingerprintFragment);
        transaction.add(R.id.stepsFrameLayout, register3MultipasswordFragment);
        transaction.add(R.id.stepsFrameLayout, register3PwdhintFragment);
        transaction.hide(register1Fragment).hide(register2Fragment).hide(register3EmailFragment)
                .hide(register3QuestionFragment).hide(register3FingerprintFragment)
                .hide(register3MultipasswordFragment).hide(register3PwdhintFragment);
        transaction.commit();
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.return_btn:
                goPreStep();
                break;
        }
    }

    private Handler mHandler = new Handler(message -> {
        switch (message.what){
            case 0://change the nav_text
                nav_text.setText(titleString[stepIndex]);
                break;
        }
        return false;
    });

    /**
     * 回到上一步
     */
    public void goPreStep(){
        if(stepIndex==0){
            finishActivity();
            return;
        }
        transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_left_in,R.anim.slide_to_right);
        if(stepIndex==1){
            transaction.hide(register1Fragment).show(register0Fragment);
            stepIndex--;
        }else if(stepIndex==2){
            transaction.hide(register2Fragment).show(register1Fragment);
            stepIndex--;
        }else if(stepIndex==3){
            transaction.hide(register3EmailFragment).show(register2Fragment);
            stepIndex=2;
        }else if(stepIndex==4){
            transaction.hide(register3QuestionFragment).show(register2Fragment);
            stepIndex=2;
        }else if(stepIndex==5){
            transaction.hide(register3FingerprintFragment).show(register2Fragment);
            stepIndex=2;
        }else if(stepIndex==6){
            transaction.hide(register3MultipasswordFragment).show(register2Fragment);
            stepIndex=2;
        }else if(stepIndex==7){
            transaction.hide(register3PwdhintFragment).show(register2Fragment);
            stepIndex=2;
        }
        transaction.commit();
        mHandler.obtainMessage(0).sendToTarget();
    }

    public void finishActivity(){
        finish();
        overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
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

                    /**
                     * 显示头像
                     */
                    register0Fragment.showProfilePhoto(bitmap,picturePath);

                    cursor.close();
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            goPreStep();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void Register0FragmentInteraction(HashMap<String,Object> data) {
        int what = Integer.parseInt(data.get("what").toString());
        switch (what){
            case 0://选择图片作为头像
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,0);
                break;
            case 1://注册成功后跳转到下一步
                user = (User)data.get("user");
                transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_right_in,R.anim.slide_to_left);
                transaction.hide(register0Fragment).show(register1Fragment);
                transaction.commit();
                register1Fragment.setUser(user);
                stepIndex=1;
                mHandler.obtainMessage(0).sendToTarget();
                break;
        }
    }
    @Override
    public void Register1FragmentInteraction(int what) {

        switch(what){
            case 0: case 1://commit 跳转到登录界面 **记得放回用户名**
                Intent intent = new Intent();
                intent.putExtra("user",user);
                setResult(1,intent);
                finish();
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
//            case 1://not binding
//                //这里先跳转到登录界面
//                finish();
//                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
//                break;
            case 2://other ways
                transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_right_in,R.anim.slide_to_left);
                transaction.hide(register1Fragment).show(register2Fragment);
                transaction.commit();
                stepIndex=2;
                break;
        }
        mHandler.obtainMessage(0).sendToTarget();
    }

    @Override
    public void Register2FragmentInteraction(int what) {
        transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_right_in,R.anim.slide_to_left);
        transaction.hide(register2Fragment);
        switch(what){
            case 0:
                transaction.show(register3EmailFragment);
                stepIndex=3;
                break;
            case 1:
                transaction.show(register3QuestionFragment);
                stepIndex=4;
                break;
            case 2:
                transaction.show(register3FingerprintFragment);
                stepIndex=5;
                break;
            case 3:
                transaction.show(register3MultipasswordFragment);
                stepIndex=6;
                break;
            case 4:
                transaction.show(register3PwdhintFragment);
                stepIndex=7;
                break;
        }
        transaction.commit();
        mHandler.obtainMessage(0).sendToTarget();
    }

    @Override
    public void Register3EmailFragmentInteraction() {
        finishActivity();
    }


    @Override
    public void Register3FingerprintFragmentInteraction() {
        finishActivity();
    }

    @Override
    public void Register3MultipasswordFragmentInteraction() {
        finishActivity();
    }

    @Override
    public void Register3PwdhintFragmentInteraction() {
        finishActivity();
    }

    @Override
    public void Register3QuestionFragmentInteraction() {
        finishActivity();
    }
}
