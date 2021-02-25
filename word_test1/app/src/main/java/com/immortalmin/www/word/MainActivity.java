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

    private JsonRe jsonRe = new JsonRe();
    private UseTimeDataManager mUseTimeDataManager = new UseTimeDataManager(this);
    private User user = new User();
    private BlurImageView blurImageView = new BlurImageView();
    private MyAsyncTask myAsyncTask;
    private SyncUtil syncUtil;
    private CollectDbDao collectDbDao = new CollectDbDao(this);
    private Context context;
    private List<Map<String,Object>> word_list=null;
    private Button btn_collect,btn_recite,btn_review,btn_spell;
    private ImageView imgview;
    private EditText editText;
    private SearchView search_bar;
    private RelativeLayout main_relative;
    private SoundPool soundPool;
    private int sound_success,sound_fail;
    private CircleImageView profile_photo;
    private ImageUtils imageUtils = new ImageUtils();
    private NetworkUtil networkUtil = new NetworkUtil(this);
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
        context = this;
        btn_collect = findViewById(R.id.btn_collect);
        btn_recite = findViewById(R.id.btn_recite);
        btn_spell = findViewById(R.id.btn_spell);
        btn_review = findViewById(R.id.btn_review);
        search_bar = findViewById(R.id.search_bar);
        profile_photo = findViewById(R.id.profile_photo);
        main_relative = findViewById(R.id.main_relative);
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

    /**
     * 如果是刚登录，则会自动将云端的数据同步到本地
     */
    private void SyncData(){
        intent = getIntent();
        String source = intent.getStringExtra("source");//0:login 1:launch
        if("0".equals(source)){
            RecordDbDao recordDbDao = new RecordDbDao(this);
            recordDbDao.deleteData();//新用户登录，清空旧的历史记录
            syncUtil = new SyncUtil(this);
            syncUtil.setFinishListener(new SyncUtil.FinishListener() {
                @Override
                public void finish() {
//                    Log.i("ccc","同步成功");
                    getReviewCount();
                    intent.putExtra("source", "1");//同步数据后修改source，避免重复同步
                }

                @Override
                public void fail() {
                    Log.i("ccc","无网络");
                }
            });
            syncUtil.downloadData();
        }
    }

    private void init() {
        //获取用户信息
        init_user();
        if(networkUtil.isNetworkConnected()){
            //同步数据
            SyncData();
            //检查用户登录时间并更新数据
            inspect_usetime();
        }
        //更新单词复习数量
        getReviewCount();
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
        //设置头像
        setImage();
    }
    private void setImage() {
        Bitmap bitmap;
        if(user.getLogin_mode()==0){
            bitmap=ImageUtils.getPhotoFromStorage(user.getProfile_photo());
        }else{
            bitmap=ImageUtils.getPhotoFromStorage(user.getUid()+".jpg");
        }
        if(bitmap==null){
            Log.i("ccc","照片不存在 正从服务器下载...");
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
//                    ArrayList<Drawable> drawables = (ArrayList<Drawable>) message.obj;
//                    btn_wordlist.setBackground(drawables.get(0));
//                    btn_recite.setBackground(drawables.get(1));
//                    btn_spell.setBackground(drawables.get(2));
//                    btn_test.setBackground(drawables.get(3));
                    break;
                case 2:
                    Resources res = getResources();
                    Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.main_img);
                    cropBitmap(bmp);
                    break;
                case 3:
                    Resources res2 = getResources();
                    Bitmap bmp2 = BitmapFactory.decodeResource(res2, R.drawable.main_img);
                    imgview.setImageBitmap(getRoundedCornerBitmap(bmp2,500));
                    break;
            }
            return false;
        }
    });

    /**
     * 检查是否需要上传时间
     */
    private void inspect_usetime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
        //获取当前时间
        long now_time_stamp = System.currentTimeMillis();
        Date date = new Date(now_time_stamp);
        String nowday = simpleDateFormat.format(date);

        //代表是第一次登录
        if(user.getLast_login()==946656000000L){
            SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
            sp.edit().putLong("last_login",now_time_stamp).apply();
            user.setLast_login(now_time_stamp);
            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("uid", user.getUid());
                jsonObject.put("last_login", user.getLast_login());
            }catch (JSONException e){
                e.printStackTrace();
            }
            update_last_login(jsonObject);
            return;
        }
        date = new Date(user.getLast_login());
        String last_day = simpleDateFormat.format(date);
        if(!nowday.equals(last_day)){
//            Log.i("ccc","不是同一天");
            //获取上一次使用到现在使用的数据
            mUseTimeDataManager = UseTimeDataManager.getInstance(MainActivity.this);
            mUseTimeDataManager.refreshData(user.getLast_login(),now_time_stamp);
            JSONObject jsonObject = new JSONObject();
            List<PackageInfo> packageInfos = mUseTimeDataManager.getmPackageInfoListOrderByTime();
            for (int i = 0; i < packageInfos.size(); i++) {
                if ("com.immortalmin.www.word".equals(packageInfos.get(i).getmPackageName())) {
                    try {
//                        jsonObject.put("count",packageInfos.get(i).getmUsedCount());
//                        jsonObject.put("name",packageInfos.get(i).getmPackageName());
//                        jsonObject.put("appname",packageInfos.get(i).getmAppName());
//                        use_time = packageInfos.get(i).getmUsedTime();
                        long minutes = packageInfos.get(i).getmUsedTime()/60000;
                        jsonObject.put("uid", user.getUid());
                        jsonObject.put("utime",(int)minutes);
                        jsonObject.put("udate",last_day);
                        jsonObject.put("utimestamp",now_time_stamp);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }

            }
            //上传昨天的数据
            update_time(jsonObject);
            //上传 上一次登录的日期 到 昨天（不包括昨天） 之间的 使用时间数据
            Calendar calendar = Calendar.getInstance();
            for(int i=0;i<100;i++){
                calendar.add(Calendar.DAY_OF_MONTH,-1);
                String pre_day = simpleDateFormat.format(calendar.getTime());
                if(pre_day.equals(last_day)){
                    break;
                }else{
                    jsonObject = new JSONObject();
                    try{
                        jsonObject.put("uid", user.getUid());
                        jsonObject.put("utime",0);
                        jsonObject.put("udate",pre_day);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    update_time(jsonObject);
                }
            }
            SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
            sp.edit().putLong("last_login",now_time_stamp).apply();
            user.setLast_login(now_time_stamp);
        }else{
//            Log.i("ccc","是同一天");
        }

    }

    /**
     * 上传使用时间
     */
    private void update_time(final JSONObject jsonObject) {
        new Thread(() -> {
            HttpGetContext httpGetContext = new HttpGetContext();
            httpGetContext.getData("http://47.98.239.237/word/php_file2/update_time.php",jsonObject);
        }).start();
    }

    private void update_last_login(final JSONObject jsonObject) {
        new Thread(() -> {
            HttpGetContext httpGetContext = new HttpGetContext();
            httpGetContext.getData("http://47.98.239.237/word/php_file2/update_userdata.php",jsonObject);
        }).start();
    }

    //从2021/2/21开始停止使用
//    /**
//     * 获取单词复习列表
//     */
//    private void getReviewList(){
//        JSONObject jsonObject = new JSONObject();
//        try{
//            jsonObject.put("what",11);
//            jsonObject.put("uid",user.getUid());
//            //获取当前时间
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
//            jsonObject.put("review_date",simpleDateFormat.format(new Date(System.currentTimeMillis())));
//        }catch (JSONException e){
//            e.printStackTrace();
//        }
//        myAsyncTask = new MyAsyncTask();
//        myAsyncTask.setLoadDataComplete((result)->{
//            ArrayList<DetailWord> review_list =jsonRe.detailWordData(result);
//            int review_num = review_list.size();
//            if(review_num == 0){
//                btn_review.setText("复习\n完成");
//            }else{
//                btn_review.setText("待复习\n"+review_num);
//            }
//
//        });
//        myAsyncTask.execute(jsonObject);
//    }

    /**
     * 显示复习单词的数量
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
//                intent = new Intent(MainActivity.this,QQTestActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                Log.i("ccc","network:"+networkUtil.isNetworkConnected());
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
     * 图片裁剪
     * @param bitmap
     * @return
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
        bitmaps.add(getRoundedCornerBitmap(blurImageView.BoxBlurFilter(bitmap1),80));
        bitmaps.add(getRoundedCornerBitmap(blurImageView.BoxBlurFilter(bitmap2),80));
        bitmaps.add(getRoundedCornerBitmap(blurImageView.BoxBlurFilter(bitmap3),80));
        bitmaps.add(getRoundedCornerBitmap(blurImageView.BoxBlurFilter(bitmap4),80));

//        ArrayList<Drawable> drawables = new ArrayList<>();
//        drawables.add(blurImageView.BoxBlurFilter(getRoundedCornerBitmap(bitmap1,100)));
//        drawables.add(blurImageView.BoxBlurFilter(getRoundedCornerBitmap(bitmap2,100)));
//        drawables.add(blurImageView.BoxBlurFilter(getRoundedCornerBitmap(bitmap3,100)));
//        drawables.add(blurImageView.BoxBlurFilter(getRoundedCornerBitmap(bitmap4,100)));
        mHandler.obtainMessage(1,bitmaps).sendToTarget();
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx){

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * 设置圆角
     * @param source
     * @return
     */
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP , BitmapShader.TileMode.CLAMP ));
        paint.setAntiAlias(true);
//        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
//        paint.setShader(shader);
//        paint.setAntiAlias(true);

        float r = size/2f;
        float r2 = size/1.8f;
//        canvas.drawCircle(r, r, r, paint);
        RectF rect = new RectF(0f, 0f, source.getWidth(), source.getHeight());
        canvas.drawRoundRect(rect,100f,100f,paint);

        squaredBitmap.recycle();
        return bitmap;
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
