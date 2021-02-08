package com.immortalmin.www.word;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
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

//    String httpclientgettext(String url) {
//        String result="";
//
//        try {
//            HttpClient httpClient = new DefaultHttpClient();
//            HttpGet httpGet = new HttpGet(url);
//            HttpResponse response = httpClient.execute(httpGet);
//            if (response.getStatusLine().getStatusCode() == 200) {
//                HttpEntity entity = response.getEntity();
//                // 使用utf-8参数保证从网页获取的内容中文能正常显示
//                result = EntityUtils.toString(entity, "utf-8");
//                //去除返回文本消息中的换行回车字符
//                result = result.replace("\r\n", "");
//              //  mHandler.obtainMessage(1,result).sendToTarget();
//            } else {
//                Log.i("***httpclientgettext***","服务器未响应");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.i("***httpclientgettext***","HttpClient执行异常应");
//        }
//        return result;
//    }

    /**
     * 获取网络音频，并保存到sd卡上
     * @param word
     * @return
     */
    public void saveMp3IntoSD(String word){
        String filePath = Environment.getExternalStorageDirectory()+"/WORD/word-audio/";
        File file = new File(filePath);
        if(!file.exists()) file.mkdirs();
        file = new File(filePath+word+".mp3");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try{
            URL url = new URL("http://dict.youdao.com/dictvoice?type=1&audio="+ URLEncoder.encode(word.toLowerCase()));
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            int contentLength = urlConnection.getContentLength();
            InputStream inputStream = new BufferedInputStream(url.openStream());
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int count;
            long total = 0;
            while((count=inputStream.read(buffer))!=-1){
                total += count;
//                Log.i("ccc", String.format(Locale.CHINA, "Download progress: %.2f%%", 100 * (total / (double) contentLength)));//进度
                outputStream.write(buffer,0,count);
            }
            outputStream.flush();
            inputStream.close();
            outputStream.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static Bitmap getbitmap(String imageUri) {
        Bitmap bitmap = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(imageUri);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            InputStream is = urlConnection.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return bitmap;
    }

    //XXX:本来想直接将图片返回的，要怎么处理获取端再自己处理，可是似乎容易图片还没获取到，就开始处理了
    /**
     * 使用httpclient对象访问服务器获取服务器中图片，返回图片的bitmap对象
     * @param url
     * @param opt 0:不做任何处理  1：方形
     * @return
     */
    Bitmap HttpclientGetImg(String url,int opt) {
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
                if(opt==1){//将图片处理成方形的
                    int img_width = bmp.getWidth();
                    int img_height = bmp.getHeight();
                    int side_length = Math.min(img_height,img_width);
                    bmp = Bitmap.createBitmap(bmp,(img_width-side_length)/2,(img_height-side_length)/2,side_length,side_length);
                }
                in.close();// 关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  bmp;
    }
    //http://dict.youdao.com/dictvoice?type=1&audio=accuse%20of

    /**
     * @param url 获取数据的URL
     * @param jsonParam 参数
     * @return String格式的json数据
     */
    String getData(String url,JSONObject jsonParam) {
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

    void uploadpic(String url,String imagePath,String uid){
        try{
            imagePath = ImageUtils.compressImage(imagePath,uid);
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
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.i("ccc","upload failure");
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if(response.isSuccessful()){
                        ResponseBody responseBody = response.body();
                        assert responseBody != null;
                        String res = responseBody.string();
                        Log.i("ccc","uploadPic"+res);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 上传反馈（可以上传多张图片）
     * @param data 反馈数据
     * @param img_list 图片列表
     */
    int uploadFeedback(HashMap<String,Object> data, ArrayList<String> img_list){
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
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.i("ccc","upload feedback failure");
                    e.printStackTrace();
                    feedback_res = -1;
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if(response.isSuccessful()){
                        feedback_res = 1;
                        ResponseBody responseBody = response.body();
                        assert responseBody != null;
//                        String res = responseBody.string();
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        return feedback_res;
    }

    String userRegister(JSONObject jsonObject){
        String result = "";
        try{
            String url = "http://47.98.239.237/word/php_file2/register.php";
            String imgPath = jsonObject.getString("imgpath");
            imgPath = ImageUtils.compressImage(imgPath,"temp_profile");
            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody requestBody;
            //判断用户是否有上传头像
            if(imgPath.equals("null")){
                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("username",jsonObject.getString("username"))
                        .addFormDataPart("pwd",jsonObject.getString("pwd"))
                        .addFormDataPart("img_flag","0")
                        .build();
            }else{
                File file = new File(imgPath);
                RequestBody image = RequestBody.create(MediaType.parse("image/*"), file);
                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image", imgPath, image)
                        .addFormDataPart("username",jsonObject.getString("username"))
                        .addFormDataPart("pwd",jsonObject.getString("pwd"))
                        .addFormDataPart("img_flag","1")
                        .build();
            }
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            Call call = okHttpClient.newCall(request);

            /**
             * 将enqueue换了，因为不知道怎么在获取数据后返回
             * 但是这样就没法直接知道是上传成功还是失败了
             * 虽然没有什么大碍
             *
             * 目前是根据返回的字符串来判断到底是不是成功了
             * 比如如果图片上传失败了，返回的字符串，经过JsonRe返回的User对象会是null
             * 但是正常的话是会返回一个非null的User对象的
             */
            ResponseBody responseBody = call.execute().body();
            assert responseBody != null;
            result = responseBody.string();
            /*被取代的代码
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.i("ccc","upload failure");
                    Log.i("ccc",e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if(response.isSuccessful()){
                        Log.i("ccc","upload success");
                        ResponseBody responseBody = response.body();
                        assert responseBody != null;
                        res = responseBody.string();
                        Log.i("ccc",res);
                    }
                }
            });*/
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }


}
