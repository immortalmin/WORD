package com.immortalmin.www.word;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * javadoc
 * @author qiubin
 *
 * 网络请求类
 *
 * */
public class HttpGetContext {
    private static int feedback_res = -1;
    private JsonRe jsonRe = new JsonRe();
    private ImageUtils imageUtils = new ImageUtils();

    public String  httpclientgettext(String url) {
        String result="";

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                // 使用utf-8参数保证从网页获取的内容中文能正常显示
                result = EntityUtils.toString(entity, "utf-8");
                //去除返回文本消息中的换行回车字符
                result = result.replace("\r\n", "");
              //  mHandler.obtainMessage(1,result).sendToTarget();
            } else {
                Log.i("***httpclientgettext***","服务器未响应");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("***httpclientgettext***","HttpClient执行异常应");
        }
        return result;
    }

    //使用httpclient对象访问服务器获取服务器中图片，返回图片的bitmap对象
    public Bitmap HttpclientGetImg(String url) {
        Bitmap bmp =null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            //使用HttpClient时，我们提前设置好参数，比如超时时间3000ms
            httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpresponse = httpclient.execute(httpGet);
            if (httpresponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {//获取图片数据成功
                HttpEntity httpentity = httpresponse.getEntity();//获取的图片资源保存在HttpEntity实体中
                InputStream in = httpentity.getContent();//获取图片数据的输入流
                bmp = BitmapFactory.decodeStream(in); //解码图片
                in.close();// 关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  bmp;
    }
    //http://dict.youdao.com/dictvoice?type=1&audio=accuse%20of
//    public int update_recite_list(String url) {
//        int res=0;
//        try {
//            HttpClient httpClient = new DefaultHttpClient();
//            HttpGet httpGet = new HttpGet(url);
//            HttpResponse response = httpClient.execute(httpGet);
//            if (response.getStatusLine().getStatusCode() == 200) {
//                HttpEntity entity = response.getEntity();
//                res=1;
//                // 使用utf-8参数保证从网页获取的内容中文能正常显示
////                result = EntityUtils.toString(entity, "utf-8");
//                //去除返回文本消息中的换行回车字符
////                result = result.replace("\r\n", "");
//                //  Log.i("HTTP", "GET:" + result);
//                //  mHandler.obtainMessage(1,result).sendToTarget();
//            } else {
//                res=2;
//                // mHandler.sendEmptyMessage(2);//2表示服务器未响应
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            res=3;
//            //mHandler.sendEmptyMessage(3);//3表示HttpClient执行异常应
//        }
//        return res;
//    }

    /**
     * @param url
     * @param jsonParam
     * @return
     */
    public String getData(String url,JSONObject jsonParam) {
        HttpPost httpPost = new HttpPost(url);
        StringEntity entity = null;//解决中文乱码问题
        try {
            entity = new StringEntity(jsonParam.toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (entity != null) {
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
        }
        httpPost.setEntity(entity);
        HttpClient httpClient = new DefaultHttpClient();
        // 获取HttpResponse实例
        HttpResponse httpResp = null;
        try {
            httpResp = httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 判断是够请求成功
        if (httpResp != null) {
            if (httpResp.getStatusLine().getStatusCode() == 200) {
                // 获取返回的数据
                String result = null;
                try {
                    result = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
                    result = result.replace("\r\n", "");
//                    Log.e("HttpPost方式请求成功，返回数据如下：", result);
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("打印数据", "HttpPost方式请求失败" + httpResp.getStatusLine().getStatusCode());
            }
        }
        return null;
    }

    public void uploadpic(String url,String imagePath,String uid){
        try{
            imagePath = imageUtils.compressImage(imagePath,uid);
            File file = new File(imagePath);
            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody image = RequestBody.create(MediaType.parse("image/*"), file);
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", imagePath, image)
                    .addFormDataPart("uid",uid)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i("ccc","upload failure");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(response.isSuccessful()){
                        ResponseBody responseBody = response.body();
                        String res = responseBody.string();
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //XXX:代码似乎有些冗余
    //stop using from 10/24/2020
//    /**
//     * 上传功能建议(0)或错误反馈(1)
//     * @param data
//     */
//    public void uploadFeedback(HashMap<String,Object> data){
////        try{
////            String imagePath = data.get("image").toString();
////            RequestBody requestBody = null;
////            OkHttpClient okHttpClient = new OkHttpClient();
////            if("".equals(imagePath)){
////                if("0".equals(data.get("what").toString())){
////                    requestBody = new MultipartBody.Builder()
////                            .setType(MultipartBody.FORM)
////                            .addFormDataPart("what",data.get("what").toString())
////                            .addFormDataPart("uid",data.get("uid").toString())
////                            .addFormDataPart("description",data.get("description").toString())
////                            .addFormDataPart("contact",data.get("contact").toString())
////                            .build();
////                }else{
////                    requestBody = new MultipartBody.Builder()
////                            .setType(MultipartBody.FORM)
////                            .addFormDataPart("what",data.get("what").toString())
////                            .addFormDataPart("uid",data.get("uid").toString())
////                            .addFormDataPart("phone_model",data.get("phone_model").toString())
////                            .addFormDataPart("description",data.get("description").toString())
////                            .addFormDataPart("contact",data.get("contact").toString())
////                            .build();
////                }
////            }else{
////                File file = new File(imagePath);
////                RequestBody image = RequestBody.create(MediaType.parse("image/*"), file);
////                if("0".equals(data.get("what").toString())){
////                    requestBody = new MultipartBody.Builder()
////                            .setType(MultipartBody.FORM)
////                            .addFormDataPart("what",data.get("what").toString())
////                            .addFormDataPart("uid",data.get("uid").toString())
////                            .addFormDataPart("image", imagePath, image)
////                            .addFormDataPart("description",data.get("description").toString())
////                            .addFormDataPart("contact",data.get("contact").toString())
////                            .build();
////                }else{
////                    requestBody = new MultipartBody.Builder()
////                            .setType(MultipartBody.FORM)
////                            .addFormDataPart("what",data.get("what").toString())
////                            .addFormDataPart("uid",data.get("uid").toString())
////                            .addFormDataPart("phone_model",data.get("phone_model").toString())
////                            .addFormDataPart("image", imagePath, image)
////                            .addFormDataPart("description",data.get("description").toString())
////                            .addFormDataPart("contact",data.get("contact").toString())
////                            .build();
////                }
////
////            }
////            Request request = new Request.Builder()
////                    .url("http://47.98.239.237/word/php_file2/upload_feedback.php")
////                    .post(requestBody)
////                    .build();
////            Call call = okHttpClient.newCall(request);
////            call.enqueue(new Callback() {
////                @Override
////                public void onFailure(Call call, IOException e) {
////                    Log.i("ccc","upload feedback failure");
////                }
////
////                @Override
////                public void onResponse(Call call, Response response) throws IOException {
////                    if(response.isSuccessful()){
////                        Log.i("ccc","upload feedback success");
////                        ResponseBody responseBody = response.body();
////                        String res = responseBody.string();
////                        Log.i("ccc",res);
////                    }
////                }
////            });
////        }catch (Exception e){
////            e.printStackTrace();
////        }
////
////    }

    /**
     * 上传反馈（可以上传多张图片）
     * @param data
     * @param img_list
     */

    public int uploadFeedback(HashMap<String,Object> data, ArrayList<String> img_list){
        Log.i("ccc",img_list.toString());
        try{
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            for (int i = 0; i <img_list.size() ; i++) {
                File file=new File(img_list.get(i));
                RequestBody image = RequestBody.create(MediaType.parse("image/*"), file);
                builder.addFormDataPart("image"+i,img_list.get(i),image);
            }
            if("1".equals(data.get("what").toString())){
                builder.addFormDataPart("phone_model",data.get("phone_model").toString());
            }
            builder.addFormDataPart("what",data.get("what").toString())
                    .addFormDataPart("uid",data.get("uid").toString())
                    .addFormDataPart("description",data.get("description").toString())
                    .addFormDataPart("contact",data.get("contact").toString());
            MultipartBody requestBody = builder.build();
            //构建请求
            Request request = new Request.Builder()
                    .url("http://47.98.239.237/word/php_file2/upload_feedback.php")//地址
                    .post(requestBody)//添加请求体
                    .build();
            OkHttpClient okHttpClient = new OkHttpClient();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i("ccc","upload feedback failure");
                    e.printStackTrace();
                    feedback_res = -1;
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(response.isSuccessful()){
                        Log.i("ccc","upload feedback success");
                        feedback_res = 1;
//                        ResponseBody responseBody = response.body();
//                        String res = responseBody.string();
//                        Log.i("ccc",res);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        return feedback_res;
    }

    public ArrayList<HashMap<String,Object>> getFeedbackList(JSONObject jsonObject) {
        String url = "http://47.98.239.237/word/php_file2/getfeedbacklist.php";
        ArrayList<HashMap<String,Object>> feedbackList = new ArrayList<>();
        Bitmap bmp =null;

        HttpPost httpPost = new HttpPost(url);
        StringEntity entity = null;//解决中文乱码问题
        try {
            entity = new StringEntity(jsonObject.toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (entity != null) {
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
        }
        httpPost.setEntity(entity);
        HttpClient httpClient = new DefaultHttpClient();
        // 获取HttpResponse实例
        HttpResponse httpResp = null;
        try {
            httpResp = httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 判断是够请求成功
        if (httpResp != null) {
            if (httpResp.getStatusLine().getStatusCode() == 200) {
                // 获取返回的数据
                String result = null;
                try {
                    result = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
                    result = result.replace("\r\n", "");
//                    Log.e("HttpPost方式请求成功，返回数据如下：", result);
                    feedbackList = jsonRe.feedbackData(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("打印数据", "HttpPost方式请求失败" + httpResp.getStatusLine().getStatusCode());
            }
        }
//        Log.i("ccc",feedbackList.toString());
        String img_url = "http://www.immortalmin.com/word/img/feedback/";
        for(int i=0;i<feedbackList.size();i++){
            String[] img_paths = feedbackList.get(i).get("img_path").toString().split("#");
            ArrayList<Object> img_list = new ArrayList<>();
            for(String img:img_paths){
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    //使用HttpClient时，我们提前设置好参数，比如超时时间3000ms
                    httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);
                    HttpGet httpGet = new HttpGet(img_url+img);
                    HttpResponse httpresponse = httpclient.execute(httpGet);
                    if (httpresponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {//获取图片数据成功
                        HttpEntity httpentity = httpresponse.getEntity();//获取的图片资源保存在HttpEntity实体中
                        InputStream in = httpentity.getContent();//获取图片数据的输入流
                        bmp = BitmapFactory.decodeStream(in); //解码图片
                        in.close();// 关闭输入流
                        img_list.add(bmp);
                    }
//                    Log.i("ccc",img+"获取成功");
                } catch (Exception e) {
                    e.printStackTrace();
//                    Log.i("ccc",img+"获取失败");
                }
            }
            feedbackList.get(i).put("img_list",img_list);

        }
//        Log.i("ccc","获取图片结束");
        Log.i("ccc",feedbackList.toString());
        return  feedbackList;
    }


    public void userRegister(JSONObject jsonObject){
        try{
            String url = "http://47.98.239.237/word/php_file2/register.php";
            String imgpath = jsonObject.getString("imgpath");
            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody requestBody = null;
            //判断用户是否有上传头像
            if(imgpath.equals("null")){
                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("username",jsonObject.getString("username"))
                        .addFormDataPart("pwd",jsonObject.getString("pwd"))
                        .addFormDataPart("img_flag","0")
                        .addFormDataPart("telephone",jsonObject.getString("telephone"))
                        .addFormDataPart("email",jsonObject.getString("email"))
                        .build();
            }else{
                File file = new File(imgpath);
                RequestBody image = RequestBody.create(MediaType.parse("image/*"), file);
                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image", imgpath, image)
                        .addFormDataPart("username",jsonObject.getString("username"))
                        .addFormDataPart("pwd",jsonObject.getString("pwd"))
                        .addFormDataPart("img_flag","1")
                        .addFormDataPart("telephone",jsonObject.getString("telephone"))
                        .addFormDataPart("email",jsonObject.getString("email"))
                        .build();
            }
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i("ccc","upload failure");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(response.isSuccessful()){
                        Log.i("ccc","upload success");
                        ResponseBody responseBody = response.body();
//                        String res = responseBody.string();
//                        Log.i("ccc",res);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
