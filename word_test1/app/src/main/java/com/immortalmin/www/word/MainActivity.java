package com.immortalmin.www.word;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BitmapTransformation;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * 主界面
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private JsonRe jsonRe;
    private BlurImageView blurImageView = new BlurImageView();
    private Context context;
    List<Map<String,Object>> word_list=null;
    Button btn_wordlist,btn_recite,btn_test,btn_spell,search1;
    private ImageView imgview;
    EditText editText;
    SearchView search_bar;
    private RelativeLayout main_relative;
    WordDAO wordDAO = new WordDAO();
    private SoundPool soundPool;
    private int sound_success,sound_fail;
    private DBAdapter dbAdapter;
    private CircleImageView profile_photo;
    private ImageUtils imageUtils = new ImageUtils();
    private int screen_width,screen_height;
    Intent intent;
    Boolean flag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        screen_width = metric.widthPixels;     // 屏幕宽度（像素）
        screen_height = metric.heightPixels;   // 屏幕高度（像素）
        context = this;
        btn_wordlist = (Button)findViewById(R.id.btn_wordlist);
        btn_recite = (Button)findViewById(R.id.btn_recite);
        btn_spell = (Button)findViewById(R.id.btn_spell);
        btn_test = (Button)findViewById(R.id.btn_test);
        search_bar = (SearchView) findViewById(R.id.search_bar);
        profile_photo = (CircleImageView) findViewById(R.id.profile_photo);
        main_relative = (RelativeLayout)findViewById(R.id.main_relative);
        imgview = (ImageView)findViewById(R.id.imgview);
        btn_wordlist.setOnClickListener(this);
        btn_recite.setOnClickListener(this);
        btn_test.setOnClickListener(this);
        btn_spell.setOnClickListener(this);
//        search1.setOnClickListener(this);
        search_bar.setOnClickListener(this);
        profile_photo.setOnClickListener(this);
        search_bar.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
            }
        });
        dbAdapter = new DBAdapter(this);
        dbAdapter.open();
        jsonRe=new JsonRe();
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        sound_success = soundPool.load(this, R.raw.success, 1);
        sound_fail = soundPool.load(this, R.raw.fail, 1);
//        search1.setBackgroundColor(Color.TRANSPARENT); //背景透明
//        search1.getBackground().setAlpha(150); //int 在0-255之间, 设置半透明

        //广播关闭
        CloseActivityReceiver closeReceiver = new CloseActivityReceiver();
        IntentFilter intentFilter = new IntentFilter("com.immortalmin.www.MainActivity");
        registerReceiver(closeReceiver, intentFilter);

        init();
        //高斯模糊
        mHandler.obtainMessage(2).sendToTarget();


    }

    private void init() {
        SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        setImage(sp.getString("profile_photo",null));
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
                mHandler.obtainMessage(0,bitmap).sendToTarget();
            }
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
                    btn_wordlist.setBackground(new BitmapDrawable(bitmaps.get(0)));
                    btn_recite.setBackground(new BitmapDrawable(bitmaps.get(1)));
                    btn_spell.setBackground(new BitmapDrawable(bitmaps.get(2)));
                    btn_test.setBackground(new BitmapDrawable(bitmaps.get(3)));
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
            case R.id.btn_wordlist:
                intent = new Intent(MainActivity.this,word1Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
            case R.id.btn_spell:
                intent = new Intent(MainActivity.this,collectActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
            case R.id.btn_recite:
                intent = new Intent(MainActivity.this,ReciteWordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
            case R.id.btn_test:
                intent = new Intent(MainActivity.this,UpdatePwdActivity.class);
                intent.putExtra("telephone","12345678912");
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
            case R.id.search_bar:
                intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                break;
            case R.id.profile_photo:
                intent = new Intent(MainActivity.this,SettingActivity.class);
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
        Double ratio = w/Double.valueOf(screen_width);
//        int btn_h = btn_wordlist.getHeight();
//        int btn_w = btn_wordlist.getWidth();
        int btn_h = 220;
        int btn_w = 220;
        //half margin
        int MarginAndBtn_h = (int)((btn_h+15)*ratio);
        int MarginAndBtn_w = (int)((btn_w+15)*ratio);
        int justMargin = (int)(15*ratio);
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
