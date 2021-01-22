package com.immortalmin.www.word;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class DataUtil {

    private Context context;
    private User user;
    private HashMap<String,Object> userSetting=null;
    private JsonRe jsonRe = new JsonRe();
    private MyAsyncTask myAsyncTask;

    public DataUtil(Context context){
        this.context = context;
    }

    public interface HttpCallbackStringListener{
        void onFinish(User userdata);
        void onError(Exception e);
    }

    /**
     * ********************************
     * ***** 根据username获取数据 *****
     * ********************************
     * 从服务器获取用户数据
     * 保存在本地
     * 在回调函数中返回UserData
     * @return
     */
    public void getdata(final HttpCallbackStringListener listener) {
        JSONObject jsonObject = new JSONObject();
        try {
            SharedPreferences sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
            jsonObject.put("username",sp.getString("username",null));
            jsonObject.put("what",14);
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            user = jsonRe.userData(result);
            //将用户数据保存到本地
            SharedPreferences sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
            sp.edit().putString("username", user.getUsername())
                    .putString("password",user.getPassword())
                    .putString("profile_photo", user.getProfile_photo())
                    .putString("status","1")
                    .putString("email",user.getEmail())
                    .putString("telephone",user.getTelephone())
                    .putString("motto",user.getMotto())
                    .putLong("last_login",Long.valueOf(user.getLast_login()))
                    .apply();
            getSetting(listener);
        });
        myAsyncTask.execute(jsonObject);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {

//                HttpGetContext httpGetContext = new HttpGetContext();
//                String wordjson = httpGetContext.getData("http://47.98.239.237/word/php_file2/getuserdata.php",jsonObject);
//                userdata = jsonRe.userData(wordjson);
//                //将用户数据保存到本地
//                SharedPreferences sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
//                sp.edit().putString("username", userdata.get("username").toString())
//                        .putString("profile_photo", userdata.get("profile_photo").toString())
//                        .putString("status","1")
//                        .putString("email",userdata.get("email").toString())
//                        .putString("telephone",userdata.get("telephone").toString())
//                        .putString("motto",userdata.get("motto").toString())
//                        .putLong("last_login",Long.valueOf(userdata.get("last_login").toString()))
//                        .apply();
                //获取setting
//                ...
//                httpGetContext = new HttpGetContext();
//                String s = httpGetContext.getData("http://47.98.239.237/word/php_file2/getsetting.php",jsonObject);
//                userSetting = jsonRe.userSetting(s);
//                sp = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
//                sp.edit().putString("uid",userSetting.get("uid").toString())
//                        .putInt("recite_num",Integer.valueOf(userSetting.get("recite_num").toString()))
//                        .putInt("recite_scope",Integer.valueOf(userSetting.get("recite_scope").toString()))
//                        .apply();
//            }
//        }).start();
        return ;
    }

    private void getSetting(final HttpCallbackStringListener listener){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("uid",user.getUid());
            jsonObject.put("what",13);
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            userSetting = jsonRe.userSetting(result);
            SharedPreferences sp = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
            sp.edit().putString("uid",userSetting.get("uid").toString())
                    .putInt("recite_num",Integer.valueOf(userSetting.get("recite_num").toString()))
                    .putInt("recite_scope",Integer.valueOf(userSetting.get("recite_scope").toString()))
                    .apply();
            listener.onFinish(set_user());
        });
        myAsyncTask.execute(jsonObject);
    }


    /**
     * 从本地获取UserData?
     * @return
     */
    private User set_user(){
        User user = new User();
        SharedPreferences sp = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        user.setUid(sp.getString("uid",null));
        user.setRecite_num(sp.getInt("recite_num",20));
        user.setRecite_scope(sp.getInt("recite_scope",10));
        sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        user.setUsername(sp.getString("username",null));
        user.setPassword(sp.getString("password",null));
        user.setProfile_photo(sp.getString("profile_photo",null));
        user.setStatus(sp.getString("status","0"));
        user.setLast_login(sp.getLong("last_login",946656000000L));
        user.setEmail(sp.getString("email",null));
        user.setTelephone(sp.getString("telephone",null));
        user.setMotto(sp.getString("motto",null));
        return user;
    }

}
