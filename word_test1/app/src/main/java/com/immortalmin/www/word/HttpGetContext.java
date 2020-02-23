package com.immortalmin.www.word;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * javadoc
 * @author qiubin
 *
 * 网络请求类
 *
 * */
public class HttpGetContext {

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
    public int update_recite_list(String url) {
        int res=0;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                res=1;
                // 使用utf-8参数保证从网页获取的内容中文能正常显示
//                result = EntityUtils.toString(entity, "utf-8");
                //去除返回文本消息中的换行回车字符
//                result = result.replace("\r\n", "");
                //  Log.i("HTTP", "GET:" + result);
                //  mHandler.obtainMessage(1,result).sendToTarget();
            } else {
                res=2;
                // mHandler.sendEmptyMessage(2);//2表示服务器未响应
            }
        } catch (Exception e) {
            e.printStackTrace();
            res=3;
            //mHandler.sendEmptyMessage(3);//3表示HttpClient执行异常应
        }
        return res;
    }

    public String httpclient_worddate(String url){
        String result="";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpPost httpPost = new HttpPost(url);
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                // 使用utf-8参数保证从网页获取的内容中文能正常显示
                result = EntityUtils.toString(entity, "utf-8");
                //去除返回文本消息中的换行回车字符
                result = result.replace("\r\n", "");
                //  Log.i("HTTP", "GET:" + result);
                //  mHandler.obtainMessage(1,result).sendToTarget();
            } else {
                // mHandler.sendEmptyMessage(2);//2表示服务器未响应
            }
        } catch (Exception e) {
            e.printStackTrace();
            //mHandler.sendEmptyMessage(3);//3表示HttpClient执行异常应
        }
        return result;
    }

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
}
