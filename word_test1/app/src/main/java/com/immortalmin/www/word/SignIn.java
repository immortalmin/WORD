package com.immortalmin.www.word;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.validation.Validator;

public class SignIn extends View {

    private Paint mPaint = new Paint();
    private Context context;
    private int total_column;
    private int type=0;//0:使用时间     1:背词数量
    private DailyRecitationDbDao dailyRecitationDbDao = null;
    private UsageTimeDbDao usageTimeDbDao = null;
    private int[] color = {
            Color.parseColor("#50ebedf0"),
            Color.parseColor("#50c6e48b"),
            Color.parseColor("#507bc96f"),
            Color.parseColor("#50239a3b"),
            Color.parseColor("#50196127")
    };
//    private ArrayList<Integer> sign_in_times = new ArrayList<>();
//    private ArrayList<Integer> dataList = new ArrayList<>();
    private ArrayList<TwoTuple<String,Integer>> data = new ArrayList<>();
    private ArrayList<Integer> month_column_num = new ArrayList<>();
    private ArrayList<Integer> month_column_str = new ArrayList<>();
    private String[] toMonth = { "","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

//    private int[] dataList = {
//            0,1,4,2,2,3,2,
//            1,4,1,0,2,3,2,
//            1,4,1,0,2,3,2,
//            1,1,0,2,3,2,1,
//            4,1,0,2,3,4,1,
//            0,2,3,2,1,2,1,
//            0,2,3,2,1,4,1,
//            0,2,3,2,1,4,1,
//            0,2,3,2,1,4,2,
//            1,0,2,3,4,1,0,
//            3,2,1,4,1,0,4,
//            3,2,1,4,1,0,2,
//            3,2,1,4,1,0,2,
//            3,0,2,1,4,1,0,
//            2,3,4,1,0,3,2,
//            1,4,1,0,4,3,2,
//            1,4,1,0,2,3,0,
//            4,1,0,2,3,0,1,
//            1,1,0,2,3,0,4,
//            1,1,2,3,0,4,0,
//            2,1,4,1,0,2,3,
//            4,1,0,3,2,4,1,
//            0,4,3,2,1,4,1,
//            0,2,3,2,1,4,1,
//            2,3,2,1,4,1,0,
//            0,2,3,2,1,4,1,
//            0,3,0,2,1,1,4,
//            2,3,0
//    };//27个星期+

//    private int[] dataList = {0,1,4,2,2,3,2,1,4,1,0,0,2,3,4,2,3,2,4,0,
//            1,4,2,2,3,2,1,4,1,0,0,2,3,4,2,3,2,4,0,1,4,2,2,3,2,1,4,1,0,0,2,3,
//            4,2,3,2,4,0,1,4,2,2,3,2,1,4,1,0,0,2,3,4,2,3,2,4,0,1,4,2,2,3,2,1,
//            4,1,0,0,2,3,4,2,3,2,4,0,1,4,2,2,3,2,1,4,1,0,0,2,3,4,2,3,2,4,0,
//            1,4,2,2,3,2,1,4,1,0,0,2,3,4,2,3,2,4,0,1,4,2,2,3,2};

    public SignIn(Context context){
        this(context,null);
        this.context = context;
        init();
    }
    public SignIn(Context context, AttributeSet attrs){
        this(context,attrs,0);
        this.context = context;
        init();
    }
    public SignIn(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        this.context = context;
        init();
    }

    private void init(){
        dailyRecitationDbDao = new DailyRecitationDbDao(context);
        usageTimeDbDao = new UsageTimeDbDao(context);
        calcTotalColumn();
    }

    @Override
    protected void onDraw(Canvas canvas){
        drawweek(canvas);
        drawbody(canvas);
        drawicon(canvas);
        drawmonth(canvas);
    }

    private void calcTotalColumn() {
        int screen_width = context.getResources().getDisplayMetrics().widthPixels;
        total_column = screen_width/DisplayUtil.dp2px(context,11)-3;//减3是因为左边星期腾出两个方块的宽度，右边边界腾出一个方块的宽度
    }

    /**
     * 绘制月份
     */
    private void drawmonth(Canvas canvas) {
        canvas.translate(-total_column*DisplayUtil.dp2px(context,11),-DisplayUtil.dp2px(context,10));
        for(int i=0;i<month_column_num.size();i++){
            canvas.drawText(toMonth[month_column_str.get(i)],(month_column_num.get(i)-1)*DisplayUtil.dp2px(context,11),DisplayUtil.dp2px(context,8),mPaint);
        }

    }

    /**
     * 绘制右下角的图例
     * @param canvas
     */
    private void drawicon(Canvas canvas) {
        canvas.save();
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(DisplayUtil.sp2px(context,7));
        mPaint.setColor(Color.parseColor("#bddac3"));
        canvas.translate(-DisplayUtil.dp2px(context,85),DisplayUtil.dp2px(context,80));
        canvas.drawText("Less",0,DisplayUtil.dp2px(context,7),mPaint);
        canvas.translate(DisplayUtil.dp2px(context,17),DisplayUtil.dp2px(context,1));
        for(int i=0;i<5;i++){
            mPaint.setColor(color[i]);
            canvas.drawRect(0,0,DisplayUtil.dp2px(context,8),DisplayUtil.dp2px(context,9),mPaint);
            canvas.translate(DisplayUtil.dp2px(context,9),0);
        }
        canvas.translate(DisplayUtil.dp2px(context,1),-DisplayUtil.dp2px(context,1));
        mPaint.setColor(Color.parseColor("#bddac3"));
        canvas.drawText("More",0,DisplayUtil.dp2px(context,8),mPaint);
        canvas.restore();
    }

    /**
     * 绘制坐标的星期
     * @param canvas
     */
    private void drawweek(Canvas canvas) {
        int span = DisplayUtil.dp2px(context,10);
        int margin = DisplayUtil.dp2px(context,3);
        //为月份腾出空间
        canvas.translate(0,span);
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(DisplayUtil.sp2px(context,7));
        mPaint.setColor(Color.parseColor("#bddac3"));
        canvas.drawText("Mon",0,(float)2.1*span-margin,mPaint);
        canvas.drawText("Wed",0,(float)4.3*span-margin,mPaint);
        canvas.drawText("Fri",0,(float)6.5*span-margin,mPaint);

    }
    /**
     * 绘制主体
     * @param canvas
     */
    private void drawbody(Canvas canvas){
        canvas.translate(DisplayUtil.dp2px(context,16),0);
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.FILL);

        //根据今天是星期几设置显示的数量
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = simpleDateFormat.format(calendar.getTime());
        int column = 0;//列数
        int val;
        calendar.add(Calendar.DAY_OF_MONTH,8-total_column*7-calendar.get(Calendar.DAY_OF_WEEK));//-[7*total_column-(7-calendar.get(Calendar.DAY_OF_WEEK))-1]
        String date = simpleDateFormat.format(calendar.getTime());
        if(type==0){
            data = usageTimeDbDao.getUsageTime(date);
            data.add(new TwoTuple<>(today,getTodayUseTime()));
        }else{
            data = dailyRecitationDbDao.getTotalNums(date);
        }
        for(int i=0,week=0;!today.equals(date);week++){
            if(week%7==0){
                canvas.save();
                column++;
            }
            //找出每个月一号的位置
            if(calendar.get(Calendar.DAY_OF_MONTH)==1){
                mPaint.setColor(Color.RED);
                month_column_num.add(column);
                month_column_str.add(calendar.get(Calendar.MONTH)+1);
            }
            date = simpleDateFormat.format(calendar.getTime());
            if(i<data.size()&&date.equals(data.get(i).first)){
                val=data.get(i).second;
                i++;
            }else val=0;
            calendar.add(Calendar.DAY_OF_MONTH,1);
            mPaint.setColor(color[timeToColor(val)]);//mPaint.setColor(color[timeToColor(data.get(i).second)]);
            canvas.drawRect(0,0,DisplayUtil.dp2px(context,10),DisplayUtil.dp2px(context,10),mPaint);
            canvas.translate(0,DisplayUtil.dp2px(context,11));
            if(week%7==6||today.equals(date)){//if(week%7==6||i==data.size()-1){
                canvas.restore();
                canvas.translate(DisplayUtil.dp2px(context,11),0);
            }
        }
    }

//    public ArrayList<TwoTuple<String,Integer>> getData() {
//        return data;
//    }
//
//    public void setData(ArrayList<TwoTuple<String,Integer>> data) {
//        this.data = data;
//        invalidate();
//    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        invalidate();
    }

    private int timeToColor(int utime){
        if(type==0){//使用时间
            if(utime<=0){
                return 0;
            }else if(utime<30){
                return 1;
            }else if(utime<60){
                return 2;
            }else if(utime<100){
                return 3;
            }
            return 4;
        }else{//根据背词数量，后期可以按照recite和review一定的比例来设置颜色
            if(utime<=0){
                return 0;
            }else if(utime<20){
                return 1;
            }else if(utime<40){
                return 2;
            }else if(utime<80){
                return 3;
            }
            return 4;
        }
    }

    /**
     * 获取今天已使用的时间
     * @return
     */
    private int getTodayUseTime(){
        int minutes = 0;
        User user = new User();
        UserDataUtil userDataUtil = new UserDataUtil(context);
        user = userDataUtil.getUserDataFromSP();
        UseTimeDataManager mUseTimeDataManager = UseTimeDataManager.getInstance(context);
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

}
