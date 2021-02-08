package com.immortalmin.www.word;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 第一个参数：传入doInBackground()方法的参数类型
 * 第二个参数：传入onProgressUpdate()方法的参数类型
 * 第三个参数：传入onPostExecute()方法的参数类型，也是doInBackground()方法返回的类型
 */
class MyAsyncTask extends AsyncTask<JSONObject, Integer, String> {

    private isLoadDataListener loadDataListener;

    // 作用：执行线程任务前的操作
    @Override
    protected void onPreExecute() {

    }

    //线程任务完成监听器
    public interface isLoadDataListener {
        void loadComplete(String result);
    }

    //设置监听器
    public void setLoadDataComplete(isLoadDataListener dataComplete) {
        this.loadDataListener = dataComplete;
    }

    // 作用：接收输入参数、执行任务中的耗时操作、返回 线程任务执行的结果

    /**
     * what:
     *  0:addexample      添加例句
     *  1:addword         添加单词
     *
     *  2:delete_example  删除例句
     *  3:delete_word     删除单词
     *
     *  4:getcollect      获取收藏的单词
     *  5:get_count       获取单词统计数据
     *  6:getworddata     获取单个单词的数据
     *  7:getwordlist     获取所有的单词
     *  8:getexampledata  获取一个单词的所有例句
     *  9:getkelinsiword  获取柯林斯词典中的数据
     *  10:getrecitelist   获取背诵列表
     *  11:getreviewlist   获取复习列表
     *  12:getsearchlist   获取搜索列表
     *  13:getsetting      获取用户设置
     *  14:getuserdata     获取用户数据  根据参数查询(username,telephone,email)
     *  15:getusetime      获取用户历史使用时间
     *
     *  16:register        注册
     *
     *  17:update_collect  收藏/取消收藏
     *  18:update_example  修改例句
     *  19:update_password 更新密码
     *  20:update_recite   更新背诵数据
     *  21:update_setting  更新设置
     *  22:update_time     更新上次登录的时间
     *  23:update_userdata 更新用户数据
     *  24:update_word     更新单词
     *
     *  25:upload_picture  上传用户头像
     *  26:getfeedbacklist 获取用户反馈列表
     *
     *
     *
     *
     *  101:register        注册
     *
     * @param params
     * @return 返回获取的JSON字符串
     */
    @Override
    protected String doInBackground(JSONObject... params) {
        int what=0;
        String[] php_name = new String[]{
                "addexample","addword","delete_example","delete_word","getcollect",
                "get_count","getworddata","getwordlist","getexampledata","getkelinsiword",
                "getrecitelist","getreviewlist","getsearchlist","getsetting","getuserdata",
                "getusetime","register","update_collect","update_example","update_password",
                "update_recite","update_setting","update_time","update_userdata","update_word",
                "upload_picture","getfeedbacklist"
        };
        try{
            what = Integer.valueOf(params[0].get("what").toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonStr="";
        HttpGetContext httpGetContext = new HttpGetContext();
        if(what<php_name.length){
            jsonStr = httpGetContext.getData("http://47.98.239.237/word/php_file2/"+php_name[what]+".php",params[0]);
        }else if(what==101){//register
            jsonStr = httpGetContext.userRegister(params[0]);
        }

        // 可调用publishProgress（）显示进度, 之后将执行onProgressUpdate（）
//        publishProgress(30);
        return jsonStr;
    }

    // 作用：在主线程 显示线程任务执行的进度
    @Override
    protected void onProgressUpdate(Integer... progresses) {
//        progressBar.setProgress(progresses[0]);

    }

    // 作用：接收线程任务执行结果、将执行结果显示到UI组件
    @Override
    protected void onPostExecute(String result) {
        if(loadDataListener != null){
            loadDataListener.loadComplete(result);
        }
    }

    // 作用：将异步任务设置为：取消状态
    @Override
    protected void onCancelled() {
//        progressBar.setProgress(0);

    }
}

/**
 *******  USAGE  ********
 public void getWordData(){
 JSONObject jsonObject = new JSONObject();
 try{
 jsonObject.put("what",6);
 jsonObject.put("uid",userData.getUid());
 jsonObject.put("wid",Integer.valueOf(wid));
 }catch (JSONException e){
 e.printStackTrace();
 }

 myAsyncTask = new MyAsyncTask();
 myAsyncTask.setLoadDataComplete((result)->{
 word = jsonRe.wordData(result);
 mHandler.obtainMessage(4).sendToTarget();
 });
 myAsyncTask.execute(jsonObject);
 }
 */
