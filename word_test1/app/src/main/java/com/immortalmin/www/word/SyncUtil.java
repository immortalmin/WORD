package com.immortalmin.www.word;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SyncUtil {

    private FinishListener finishListener;
    private Context context;
    private CollectDbDao collectDbDao;
    private DataUtil dataUtil;
    private User user;
    private MyAsyncTask myAsyncTask;
    private JsonRe jsonRe = new JsonRe();

    public SyncUtil(Context context) {
        this.context = context;
        collectDbDao = new CollectDbDao(context);
        dataUtil = new DataUtil(context);
        init();
    }

    private void init() {
        user = dataUtil.set_user();
    }


    /**
     * 上传数据
     * 收藏的变化
     * 背诵数据的变化
     * 单词本身数据的变化（暂时不处理）
     */
    void uploadData() {
        ArrayList<DetailWord> wordList = collectDbDao.getSyncList();
        if(wordList.size()==0){
            if(finishListener!=null) finishListener.finish();
            return ;
        }
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
            if(finishListener!=null) finishListener.finish();
        }).start();

    }

    /**
     * 下载数据
     */
    void downloadData() {
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
                if(finishListener!=null) finishListener.finish();
            }).start();
        });
        myAsyncTask.execute(jsonObject);
    }

    interface FinishListener{
        void finish();
    }

    public void setFinishListener(FinishListener finishListener) {
        this.finishListener = finishListener;
    }
}
