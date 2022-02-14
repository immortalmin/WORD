package com.immortalmin.www.word;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener,
        PickerDialog.OnDialogInteractionListener,ImgTipDialog.OnDialogInteractionListener{

    private Button return_btn;
    private TextView prof_tv,recite_num,recite_scope;
    private Switch sign_in_switch;
    private LinearLayout finish_num_layout,scope_num_layout;
    private PickerDialog pickerDialog;
    private MyAsyncTask myAsyncTask;
    private String[] settingStr = {"recite_num","recite_scope"};
    private User user = new User();
    private ImgTipDialog imgTipDialog;
    private UserDataUtil userDataUtil = new UserDataUtil(SettingActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        return_btn = findViewById(R.id.return_btn);
        prof_tv = findViewById(R.id.prof_tv);
        recite_num = findViewById(R.id.recite_num);
        recite_scope = findViewById(R.id.recite_scope);
        finish_num_layout = findViewById(R.id.finish_num_layout);
        scope_num_layout = findViewById(R.id.scope_num_layout);
        sign_in_switch = findViewById(R.id.sign_in_switch);
        return_btn.setOnClickListener(this);
        prof_tv.setOnClickListener(this);
        finish_num_layout.setOnClickListener(this);
        scope_num_layout.setOnClickListener(this);
        sign_in_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                user.setSign_in_type(1);
                userDataUtil.updateUserDataInServer(user,true);
            }else{
                //获取权限
                if(isNoOption()&&!isNoSwitch()){
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.usage_authority_tip);
                    show_img_tip_dialog(bitmap);
                }else{
                    user.setSign_in_type(0);
                    userDataUtil.updateUserDataInServer(user,true);
                }
            }
        });
        userDataUtil.getUserDataFromServer(null,false,new UserDataUtil.HttpCallbackStringListener() {
            @Override
            public void onFinish(User userdata) {
                user = userdata;
                mHandler.sendEmptyMessage(0);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void show_img_tip_dialog(Bitmap img){
        imgTipDialog = new ImgTipDialog(this,R.style.MyDialog,img);
        imgTipDialog.setOnDismissListener(dialog -> {
            if(user.getSign_in_type()==1) sign_in_switch.setChecked(true);
        });
        imgTipDialog.show();
    }

    @Override
    public void ImgTipInteraction(int res){
        if(res==1){
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            this.startActivityForResult(intent,1);
        }
    }



    public void onClick(View view){
        switch (view.getId()){
            case R.id.return_btn:
                finish();
                overridePendingTransition(R.anim.slide_right_in,R.anim.slide_to_left);
                break;
            case R.id.prof_tv:

                break;
            case R.id.finish_num_layout:
                ArrayList<Object> arrayList = new ArrayList<>(Arrays.asList(5,10,20,30,50));
                int position = 0;
                for(int i=0;i<arrayList.size();i++){
                    if(arrayList.get(i).equals(user.getRecite_num())){
                        position = i;
                        break;
                    }
                }
                pickerDialog = new PickerDialog(this,R.style.MyDialog,arrayList,0,position);
                pickerDialog.show();
                break;
            case R.id.scope_num_layout:
                arrayList = new ArrayList<>(Arrays.asList(5,10,15,20,25));
                position = 0;
                for(int i=0;i<arrayList.size();i++){
                    if(arrayList.get(i).equals(user.getRecite_scope())){
                        position = i;
                        break;
                    }
                }
                pickerDialog = new PickerDialog(this,R.style.MyDialog,arrayList,1,position);
                pickerDialog.show();
                break;
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    recite_num.setText(String.valueOf(user.getRecite_num()));
                    recite_scope.setText(String.valueOf(user.getRecite_scope()));
                    if(user.getSign_in_type()==0) sign_in_switch.setChecked(false);
                    else sign_in_switch.setChecked(true);
                    break;
            }
            return false;
        }
    });

    @Override
    public void PickerInteraction(JSONObject ret){
        try{
            if("0".equals(ret.get("what").toString())){
                user.setRecite_num(Integer.parseInt(ret.get("value").toString()));
            }else{
                user.setRecite_scope(Integer.parseInt(ret.get("value").toString()));
            }
            userDataUtil.updateUserDataInServer(user,true);
            mHandler.sendEmptyMessage(0);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private boolean isNoOption() {
        PackageManager packageManager = getApplicationContext()
                .getPackageManager();
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private boolean isNoSwitch() {
        long ts = System.currentTimeMillis();
        UsageStatsManager usageStatsManager = (UsageStatsManager) getApplicationContext()
                .getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST, 0, ts);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            overridePendingTransition(R.anim.slide_right_in,R.anim.slide_to_left);
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            imgTipDialog.dismiss();
            if(isNoOption()&&!isNoSwitch()){
                sign_in_switch.setChecked(true);
            }else{
                sign_in_switch.setChecked(false);
                user.setSign_in_type(0);
                userDataUtil.updateUserDataInServer(user,true);
            }
        }

    }
}
