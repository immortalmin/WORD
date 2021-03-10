package com.immortalmin.www.word;

import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class SyncUtil {

    private FinishListener finishListener;
    private Context context;
    private CollectDbDao collectDbDao;
    private UsageTimeDbDao usageTimeDbDao;
    private DataUtil dataUtil;
    private User user;
    private MyAsyncTask myAsyncTask;
    private JsonRe jsonRe = new JsonRe();
    private NetworkUtil networkUtil;
    private int execCnt;

    SyncUtil(Context context) {
        this.context = context;
        collectDbDao = new CollectDbDao(context);
        usageTimeDbDao = new UsageTimeDbDao(context);
        dataUtil = new DataUtil(context);
        networkUtil = new NetworkUtil(context);
        init();
    }

    private void init() {
        user = dataUtil.set_user();
    }


    void syncExecutor(int Cnt,boolean isUploadCollectData,boolean isDownloadCollectData,boolean isUploadUsageTime,boolean isDownloadUsageTime){
        //如果没有网络，则不进行同步
        if(!networkUtil.isNetworkConnected()){
            if(finishListener!=null) finishListener.fail();
            return ;
        }
        this.execCnt = Cnt;
        if(isUploadCollectData) uploadCollectData();
        if(isDownloadCollectData) downloadCollectData();
        if(isUploadUsageTime) uploadUsageTime();
        if(isDownloadUsageTime) downloadUsageTime();
    }

    private synchronized void syncFinish(){
        execCnt--;
        if(execCnt==0){
            if(finishListener!=null) finishListener.finish();
        }
    }


    /**
     * 上传Collect数据
     * 收藏的变化
     * 背诵数据的变化
     * 单词本身数据的变化（暂时不处理）
     */
    private void uploadCollectData() {
        ArrayList<DetailWord> wordList = collectDbDao.getSyncList();
        new Thread(()->{
            for(int i=0;i<wordList.size();i++){
                DetailWord syncWord = wordList.get(i);
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("what",27);
                    jsonObject.put("uid",user.getUid());
                    jsonObject.put("wid",syncWord.getWid());
                    /**
                     * 如果直接为null的话，似乎无法正常存入JSONObject，所以改成"null"
                     */
//                jsonObject.put("cid",wordList.get(i).getCid());
                    if(wordList.get(i).getCid()==null) jsonObject.put("cid","null");
                    else jsonObject.put("cid",syncWord.getCid());
                    jsonObject.put("gid",syncWord.getGid());
                    jsonObject.put("correct_times",syncWord.getCorrect_times());
                    jsonObject.put("error_times",syncWord.getError_times());
                    jsonObject.put("last_date",syncWord.getLast_date());
                    jsonObject.put("review_date",syncWord.getReview_date());
                    jsonObject.put("dict_source",syncWord.getDict_source());
                    jsonObject.put("isCollect",syncWord.isCollect()?1:0);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                myAsyncTask = new MyAsyncTask();
                myAsyncTask.setLoadDataComplete((result -> {
                    try {
                        JSONObject jsonObject1 = new JSONObject(result);
                        int what = jsonObject1.getInt("what");
                        if(what==0){//在云端collect添加了新数据,需要在本地修改isSynchronized=1和cid
                            int cid = jsonObject1.getInt("cid");
                            collectDbDao.execCommonSQL("update collect set cid="+cid+",isSynchronized=1 where id="+syncWord.getHid());
                        }else if(what==1||what==3){//在云端collect删除了该条记录或无效数据，需要在本地删除这条记录
                            collectDbDao.execCommonSQL("delete from collect where id="+syncWord.getHid());
                        }else if(what==2){//在云端collect更新了背诵数据，需要在本地修改isSynchronized=1
                            collectDbDao.execCommonSQL("update collect set isSynchronized=1 where id="+syncWord.getHid());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }));
                myAsyncTask.execute(jsonObject);
            }
            syncFinish();
        }).start();
    }

    /**
     * 下载Collect数据
     */
    private void downloadCollectData() {
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("what",4);
            jsonObject.put("uid", user.getUid());
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            ArrayList<DetailWord> wordList = jsonRe.detailWordData(result);
            collectDbDao.deleteData();//清除本地的数据
            new Thread(()->{
                for(int i=0;i<wordList.size();i++){
                    collectDbDao.insertData(wordList.get(i),false);
                }
                syncFinish();
            }).start();
        });
        myAsyncTask.execute(jsonObject);
    }

    /**
     * 上传本地usageTime数据
     */
    private void uploadUsageTime() {
        ArrayList<UsageTime> timeList = usageTimeDbDao.getSyncList();
        new Thread(()->{
            for(int i=0;i<timeList.size();i++){
                UsageTime usageTime = timeList.get(i);
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("what",22);
                    jsonObject.put("uid",user.getUid());
                    jsonObject.put("udate",usageTime.getUdate());
                    jsonObject.put("utime",usageTime.getUtime());
                }catch (JSONException e){
                    e.printStackTrace();
                }
                myAsyncTask = new MyAsyncTask();
                myAsyncTask.setLoadDataComplete((result -> {
                    try {
                        JSONObject resultJson = new JSONObject(result);
                        if("1".equals(resultJson.getString("what"))){//本地数据与服务器上的数据不一致，则将服务器上的数据同步到本地
                            usageTimeDbDao.updateUtimeByUdate(resultJson.getString("udate"),resultJson.getString("utime"));
                        }else{
                            usageTimeDbDao.updateIsSyncByUdate(resultJson.getString("udate"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }));
                myAsyncTask.execute(jsonObject);
            }
            if(timeList.size()>0){
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("what",23);
                    jsonObject.put("uid",user.getUid());
                    //在上传使用数据之后，只是将服务器上用户的last_login与本地的数据进行同步，而不会设置成当下的时间戳
                    jsonObject.put("last_login",user.getLast_login());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                myAsyncTask = new MyAsyncTask();
                myAsyncTask.setLoadDataComplete((result -> {

                }));
                myAsyncTask.execute(jsonObject);
            }
            syncFinish();
        }).start();
    }

    /**
     * 下载UsageTime
     */
    private void downloadUsageTime() {
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("what",15);
            jsonObject.put("uid", user.getUid());
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            ArrayList<UsageTime> usageTimeList = jsonRe.usageTimeData(result);
            //清空旧的usageTime
            usageTimeDbDao.deleteData();
            new Thread(()->{
                for(int i=0;i<usageTimeList.size();i++){
                    usageTimeDbDao.insertUsageTime(usageTimeList.get(i),1);
                }
                syncFinish();
            }).start();
        });
        myAsyncTask.execute(jsonObject);
    }

    interface FinishListener{
        void finish();
        void fail();
    }



    public void setFinishListener(FinishListener finishListener) {
        this.finishListener = finishListener;
    }
}
