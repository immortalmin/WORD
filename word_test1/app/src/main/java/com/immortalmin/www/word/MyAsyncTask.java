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

    private JsonRe jsonRe = new JsonRe();
    private isLoadDataListener loadDataListener;

    // 作用：执行线程任务前的操作
    @Override
    protected void onPreExecute() {
        Log.i("ccc","onPreExecute");
    }

    //线程任务完成监听器
    public interface isLoadDataListener {
        public void loadComplete(String result);
    }

    //设置监听器
    public void setLoadDataComplete(isLoadDataListener dataComplete) {
        this.loadDataListener = dataComplete;
    }

    // 作用：接收输入参数、执行任务中的耗时操作、返回 线程任务执行的结果

    /**
     * what:
     *  :getworddata     获取单个单词的数据
     *  :getwordlist     获取所有的单词
     *  :getexampledata  获取一个单词的所有例句
     *  :
     *  :getrecitelist   获取背诵列表
     *  :getreviewlist   获取复习列表
     *  :getsearchlist   获取搜索列表
     *  :getsetting      获取用户设置
     *  :getuserdata     获取用户数据
     *  :getusetime      获取用户历史使用时间
     *
     *  :register        注册
     *  :
     *
     *  :
     * @param params
     * @return
     */
    @Override
    protected String doInBackground(JSONObject... params) {
        int what=0;
        String[] php_name = new String[]{
                "getworddata","getexampledata","getrecitelist","getreviewlist","getsearchlist",
                "getsetting","getuserdata","getusetime",""
        };
        try{
            what = Integer.valueOf(params[0].get("what").toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
        HttpGetContext httpGetContext = new HttpGetContext();
        String json_str = httpGetContext.getData("http://47.98.239.237/word/php_file2/"+php_name[what]+".php",params[0]);
//        String word = jsonRe.wordData(wordjson).toString();
        // 可调用publishProgress（）显示进度, 之后将执行onProgressUpdate（）
//        publishProgress(30);
        return json_str;
    }

    // 方法3：onProgressUpdate（）
    // 作用：在主线程 显示线程任务执行的进度
    @Override
    protected void onProgressUpdate(Integer... progresses) {
        Log.i("ccc","onProgressUpdate:"+progresses[0]);
//        progressBar.setProgress(progresses[0]);
//        text.setText("loading..." + progresses[0] + "%");

    }

    // 方法4：onPostExecute（）
    // 作用：接收线程任务执行结果、将执行结果显示到UI组件
    @Override
    protected void onPostExecute(String result) {
        Log.i("ccc","onPostExecute");
        Log.i("ccc","result:"+result);
        if(loadDataListener != null){
            loadDataListener.loadComplete(result);
        }
        // 执行完毕后，则更新UI
//        text.setText("加载完毕");
    }

    // 方法5：onCancelled()
    // 作用：将异步任务设置为：取消状态
    @Override
    protected void onCancelled() {
        Log.i("ccc","onCancelled");
//        text.setText("已取消");
//        progressBar.setProgress(0);

    }
}
