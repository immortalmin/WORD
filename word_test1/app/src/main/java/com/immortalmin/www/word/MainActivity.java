package com.immortalmin.www.word;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

/**
 * 主界面
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private UseTimeDataManager mUseTimeDataManager = new UseTimeDataManager(this);
    private User user = new User();
    private SyncUtil syncUtil;
    private CollectDbDao collectDbDao = new CollectDbDao(this);
    private Button btn_collect,btn_recite,btn_review,btn_spell;
    private ImageView imgview;
    private SearchView search_bar;
    private CircleImageView profile_photo;
    private UsageTime usageTime;
    private UsageTimeDbDao usageTimeDbDao = new UsageTimeDbDao(this);
    private int screen_width,screen_height;
    private Intent intent;
    private Boolean flag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SQLiteStudioService.instance().start(this);//连接SQLiteStudio
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        screen_width = metric.widthPixels;     // 屏幕宽度（像素）
        screen_height = metric.heightPixels;   // 屏幕高度（像素）
        btn_collect = findViewById(R.id.btn_collect);
        btn_recite = findViewById(R.id.btn_recite);
        btn_spell = findViewById(R.id.btn_spell);
        btn_review = findViewById(R.id.btn_review);
        search_bar = findViewById(R.id.search_bar);
        profile_photo = findViewById(R.id.profile_photo);
        imgview = findViewById(R.id.imgview);
        btn_collect.setOnClickListener(this);
        btn_recite.setOnClickListener(this);
        btn_review.setOnClickListener(this);
        btn_spell.setOnClickListener(this);
        search_bar.setOnClickListener(this);
        profile_photo.setOnClickListener(this);
        search_bar.setOnSearchClickListener(view -> {
            intent = new Intent(MainActivity.this,SearchActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
        });

        //广播关闭
        CloseActivityReceiver closeReceiver = new CloseActivityReceiver();
        IntentFilter intentFilter = new IntentFilter("com.immortalmin.www.MainActivity");
        registerReceiver(closeReceiver, intentFilter);

        init();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //FIXME:debug的时候会卡住
        //等Activity初始化完毕后设置按钮高斯模糊
        mHandler.obtainMessage(2).sendToTarget();
    }

    private void init() {
        init_user();//获取用户信息
        SyncData();//同步数据以及更新使用时间
        getReviewCount();//更新单词复习数量
    }

    private void init_user(){
        SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        user.setUid(sp.getString("uid",null));
        user.setUsername(sp.getString("username",null));
        user.setPassword(sp.getString("password",null));
        user.setProfile_photo(sp.getString("profile_photo",null));
        user.setStatus(sp.getInt("status",0));
        user.setLast_login(sp.getLong("last_login",946656000000L));
        user.setLogin_mode(sp.getInt("login_mode",0));
        setImage();//设置头像
    }

    /**
     * 如果是刚登录，则会自动将云端的数据同步到本地
     * 同时包含更新 使用时间 的操作
     */
    private void SyncData(){
        intent = getIntent();
        String source = intent.getStringExtra("source");//0:login 1:launch
        if("0".equals(source)){//新用户注册or新用户登录
            //清空旧的历史记录
            RecordDbDao recordDbDao = new RecordDbDao(this);
            recordDbDao.deleteData();
            //将服务器上该用户的collect和usageTime数据同步到本地
            syncUtil = new SyncUtil(this);
            syncUtil.setFinishListener(new SyncUtil.FinishListener() {
                @Override
                public void finish() {
                    getReviewCount();
                    intent.putExtra("source", "1");//同步数据后修改source，避免重复同步
                    //获取使用时间的数据
                    getUsageTime();
                }

                @Override
                public void fail() {
                    Log.i("ccc","无网络");
                }
            });
            syncUtil.syncExecutor(2,false,true,false,true);
        }else{
            //获取使用时间的数据
            getUsageTime();
        }
    }


    private void setImage() {
        Bitmap bitmap;
        bitmap=ImageUtils.getPhotoFromStorage(user.getUid()+".jpg");
        if(bitmap==null){
            Log.i("ccc","头像不存在 正从服务器下载...");
            getImage();
        }else{
            mHandler.obtainMessage(0,bitmap).sendToTarget();
        }
    }

    private void getImage(){
        new Thread(() -> {
            HttpGetContext httpGetContext = new HttpGetContext();
            Bitmap bitmap;
            if(user.getLogin_mode()==0){
                bitmap = httpGetContext.HttpclientGetImg("http://47.98.239.237/word/img/profile/"+user.getProfile_photo(),0);
            }else{
                bitmap = HttpGetContext.getbitmap(user.getProfile_photo());
            }
            ImageUtils.savePhotoToStorage(bitmap,user.getUid()+".jpg");
            mHandler.obtainMessage(0,bitmap).sendToTarget();
        }).start();
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 0:
                    profile_photo.setImageBitmap((Bitmap)message.obj);
                    break;
                case 1:
                    ArrayList<Bitmap> bitmaps = (ArrayList<Bitmap>) message.obj;
                    btn_collect.setBackground(new BitmapDrawable(bitmaps.get(0)));
                    btn_recite.setBackground(new BitmapDrawable(bitmaps.get(1)));
                    btn_spell.setBackground(new BitmapDrawable(bitmaps.get(2)));
                    btn_review.setBackground(new BitmapDrawable(bitmaps.get(3)));
                    break;
                case 2:
                    Resources res = getResources();
                    Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.main_img);
                    cropBitmap(bmp);
                    break;
                case 3:
                    Resources res2 = getResources();
                    Bitmap bmp2 = BitmapFactory.decodeResource(res2, R.drawable.main_img);
                    imgview.setImageBitmap(ImageUtils.getRoundedCornerBitmap(bmp2,500));
                    break;
            }
            return false;
        }
    });

//2021/3/14
//    /**
//     * 检查是否需要上传时间
//     */
//    private void inspectUsageTime() {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
//        //获取当前时间
//        long now_time_stamp = System.currentTimeMillis();
//        Date date = new Date(now_time_stamp);
//        String nowday = simpleDateFormat.format(date);
//
//
//        //代表是第一次登录
//        if(user.getLast_login()==946656000000L){
//            SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
//            sp.edit().putLong("last_login",now_time_stamp).apply();
//            user.setLast_login(now_time_stamp);
//            JSONObject jsonObject = new JSONObject();
//            try{
//                jsonObject.put("uid", user.getUid());
//                jsonObject.put("last_login", user.getLast_login());
//            }catch (JSONException e){
//                e.printStackTrace();
//            }
//            update_last_login(jsonObject);
//            return;
//        }
//        date = new Date(user.getLast_login());
//        String last_day = simpleDateFormat.format(date);
//        if(!nowday.equals(last_day)){
//            //获取上一次使用到现在使用的数据
//            mUseTimeDataManager = UseTimeDataManager.getInstance(MainActivity.this);
//            mUseTimeDataManager.refreshData(user.getLast_login(),now_time_stamp);
//            List<PackageInfo> packageInfos = mUseTimeDataManager.getmPackageInfoListOrderByTime();
//            for (int i = 0; i < packageInfos.size(); i++) {
//                if ("com.immortalmin.www.word".equals(packageInfos.get(i).getmPackageName())) {
//                    long minutes = packageInfos.get(i).getmUsedTime()/60000;
////                  jsonObject.put("count",packageInfos.get(i).getmUsedCount());
////                  jsonObject.put("name",packageInfos.get(i).getmPackageName());
////                  jsonObject.put("appname",packageInfos.get(i).getmAppName());
////                  use_time = packageInfos.get(i).getmUsedTime();
//                    //上传昨天的使用时间
//                    usageTime = new UsageTime();
//                    usageTime.setUdate(last_day);
//                    usageTime.setUtime((int)minutes);
//                    usageTimeDbDao.insertUsageTime(usageTime,0);
//                    break;
//
//                }
//
//            }
//            //上传 上一次登录的日期 到 昨天（不包括昨天） 之间的 使用时间数据
//            Calendar calendar = Calendar.getInstance();
//            for(int i=0;i<100;i++){
//                calendar.add(Calendar.DAY_OF_MONTH,-1);
//                String pre_day = simpleDateFormat.format(calendar.getTime());
//                if(pre_day.equals(last_day)){
//                    break;
//                }else{
//                    usageTime = new UsageTime();
//                    usageTime.setUdate(pre_day);
//                    usageTime.setUtime(0);
//                    usageTimeDbDao.insertUsageTime(usageTime,0);
//                }
//            }
//            SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
//            sp.edit().putLong("last_login",now_time_stamp).apply();
//            user.setLast_login(now_time_stamp);
//        }
//    }

    /**
     * 获取使用时间的数据
     */
    private void getUsageTime() {
        long nowTimeStamp = System.currentTimeMillis();//获取当前时间戳
        String today = DateTransUtils.getDateAfterToday(0);//今天的日期 YYYY-MM-DD
        if(user.getLast_login()==946656000000L){//代表是第一次登录
            SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
            sp.edit().putLong("last_login",nowTimeStamp).apply();
            user.setLast_login(nowTimeStamp);
            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("uid", user.getUid());
                jsonObject.put("last_login", user.getLast_login());
            }catch (JSONException e){
                e.printStackTrace();
            }
            updateLastLogin(jsonObject);
            return;
        }
        String lastLogin = DateTransUtils.stampToDate(user.getLast_login());
        if(!today.equals(lastLogin)){//如果上次登录的日期不是今天，则需要计算这之间每一天的使用时间
            //获取上一次使用到现在使用的数据
            mUseTimeDataManager = UseTimeDataManager.getInstance(MainActivity.this);
            mUseTimeDataManager.refreshData(user.getLast_login(),nowTimeStamp);
            List<PackageInfo> packageInfos = mUseTimeDataManager.getmPackageInfoListOrderByTime();
            for (int i = 0; i < packageInfos.size(); i++) {
                if ("com.immortalmin.www.word".equals(packageInfos.get(i).getmPackageName())) {
                    long minutes = packageInfos.get(i).getmUsedTime()/60000;
                    //上传昨天的使用时间
                    usageTime = new UsageTime();
                    usageTime.setUdate(lastLogin);
                    usageTime.setUtime((int)minutes);
                    usageTimeDbDao.insertUsageTime(usageTime,0);
                    break;
                }
            }
            //保存上一次登录的日期 到 昨天(不包括昨天)之间的使用时间数据
            Calendar calendar = Calendar.getInstance();
            for(int i=0;i<100;i++){
                calendar.add(Calendar.DAY_OF_MONTH,-1);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String pre_day = simpleDateFormat.format(calendar.getTime());
                if(pre_day.equals(lastLogin)){
                    break;
                }else{
                    usageTime = new UsageTime();
                    usageTime.setUdate(pre_day);
                    usageTime.setUtime(0);
                    usageTimeDbDao.insertUsageTime(usageTime,0);
                }
            }
            //更新本地文件中的last_login
            SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
            sp.edit().putLong("last_login",nowTimeStamp).apply();
            user.setLast_login(nowTimeStamp);
        }
    }

    /**
     * 上传用户上一次登录的时间
     */
    private void updateLastLogin(final JSONObject jsonObject) {
        new Thread(() -> {
            HttpGetContext httpGetContext = new HttpGetContext();
            httpGetContext.getData("http://47.98.239.237/word/php_file2/update_userdata.php",jsonObject);
        }).start();
    }

    /**
     * 获取并显示复习单词的数量
     */
    private void getReviewCount(){
        int count = collectDbDao.getReviewCount();
        if(count == 0){
            btn_review.setText("复习\n完成");
        }else{
            btn_review.setText("待复习\n" + count);
        }
    }


    /**
     * 实现Activity的广播接收
     * @author LCry
     */
    public class CloseActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            MainActivity.this.finish();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_collect:
                intent = new Intent(MainActivity.this,collectActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
            case R.id.btn_spell:
//                intent = new Intent(MainActivity.this,CountDownTestActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
            case R.id.btn_recite:
                intent = new Intent(MainActivity.this,ReciteWordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
            case R.id.btn_review:
                intent = new Intent(MainActivity.this,ReviewWordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
            case R.id.search_bar:
                intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
            case R.id.profile_photo:
                intent = new Intent(MainActivity.this,ProfileActivity.class);
                startActivityForResult(intent,1);
                overridePendingTransition(R.anim.slide_left_in,R.anim.slide_to_right);
                break;
        }
    }

    /**
     * 将主界面背景图片的中间部分切割成四个方形，设置高斯模糊后作为按钮的背景图片
     */
    private void cropBitmap(Bitmap bitmap) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();
        double ratio = w/ (double) screen_width;
        int btn_h = btn_collect.getHeight();
        int btn_w = btn_collect.getWidth();
        int border = DisplayUtil.dip2px(this,7.5f);
        int MarginAndBtn_h = (int)((btn_h+border)*ratio);
        int MarginAndBtn_w = (int)((btn_w+border)*ratio);
        int justMargin = (int)(border*ratio);
        int justBtn_h = (int)(btn_h*ratio);
        int justBtn_w = (int)(btn_w*ratio);
        Bitmap bitmap1 = Bitmap.createBitmap(bitmap, w / 2-MarginAndBtn_w, h/2-MarginAndBtn_h,justBtn_w, justBtn_h, null, false);
        Bitmap bitmap2 = Bitmap.createBitmap(bitmap, w / 2+justMargin, h/2-MarginAndBtn_h,justBtn_w, justBtn_h, null, false);
        Bitmap bitmap3 = Bitmap.createBitmap(bitmap, w / 2-MarginAndBtn_w, h/2+justMargin,justBtn_w, justBtn_h, null, false);
        Bitmap bitmap4 = Bitmap.createBitmap(bitmap, w / 2+justMargin, h/2+justMargin,justBtn_w, justBtn_h, null, false);
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        bitmaps.add(ImageUtils.getRoundedCornerBitmap(BlurImageView.BoxBlurFilter(bitmap1),80));
        bitmaps.add(ImageUtils.getRoundedCornerBitmap(BlurImageView.BoxBlurFilter(bitmap2),80));
        bitmaps.add(ImageUtils.getRoundedCornerBitmap(BlurImageView.BoxBlurFilter(bitmap3),80));
        bitmaps.add(ImageUtils.getRoundedCornerBitmap(BlurImageView.BoxBlurFilter(bitmap4),80));
        mHandler.obtainMessage(1,bitmaps).sendToTarget();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (flag && (keyCode == KeyEvent.KEYCODE_BACK)) {
            Toast.makeText(this, "按下了back键   onKeyDown()", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            init();
        }
    }
}
