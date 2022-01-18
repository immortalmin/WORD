package com.immortalmin.www.word;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

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
     * 可能的调用者：
     *      1、如果是第一次通过QQ登录，还需要传入open_id,profile_photo
     *      2、登录界面，普通用户，还需要传入username（从输入框获取的）
     * 从服务器获取用户数据
     * 保存在本地
     * 在回调函数中返回UserData
     */
    public void getdata(final HttpCallbackStringListener listener,JSONObject jsonObject) {
        if(jsonObject==null) jsonObject = new JSONObject();
        try {
            SharedPreferences sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
            jsonObject.put("what",14);
            if(!jsonObject.has("login_mode")) jsonObject.put("login_mode",0);//默认为普通用户获取数据
            if(!jsonObject.has("username")) jsonObject.put("username",sp.getString("username",null));
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            user = jsonRe.userData(result);
            try{
                //将用户数据保存到本地
                SharedPreferences sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
                sp.edit().putString("username", user.getUsername())
                        .putString("password",user.getPassword())
                        .putString("profile_photo", user.getProfile_photo())
                        .putString("motto", user.getMotto())
                        .putInt("status",1)
                        .putInt("login_mode",user.getLogin_mode())
                        .putString("open_id",user.getOpen_id())
                        .putLong("last_login",Long.valueOf(user.getLast_login()))
                        .putInt("ignore_version",user.getIgnore_version())
                        .apply();
            }catch (Exception e){
                listener.onError(e);
            }
            getSetting(listener);
        });
        myAsyncTask.execute(jsonObject);
        return ;
    }

    private void getSetting(final HttpCallbackStringListener listener){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("uid",user.getUid());
            jsonObject.put("what",13);
        }catch (Exception e){
            listener.onError(e);
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            userSetting = jsonRe.userSetting(result);
            SharedPreferences sp = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
            try{
                sp.edit().putString("uid",userSetting.get("uid").toString())
                        .putInt("recite_num",Integer.valueOf(userSetting.get("recite_num").toString()))
                        .putInt("recite_scope",Integer.valueOf(userSetting.get("recite_scope").toString()))
                        .apply();
            }catch (Exception e){
                listener.onError(e);
            }
            listener.onFinish(set_user());
        });
        myAsyncTask.execute(jsonObject);
    }


    /**
     * 从本地获取UserData?
     */
    User set_user(){
        User user = new User();
        SharedPreferences sp = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        user.setUid(sp.getString("uid",null));
        user.setRecite_num(sp.getInt("recite_num",20));
        user.setRecite_scope(sp.getInt("recite_scope",10));
        sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        user.setUsername(sp.getString("username",null));
        user.setPassword(sp.getString("password",null));
        user.setProfile_photo(sp.getString("profile_photo",null));
        user.setStatus(sp.getInt("status",0));
        user.setLogin_mode(sp.getInt("login_mode",0));
        user.setOpen_id(sp.getString("open_id",null));
        user.setLast_login(sp.getLong("last_login",946656000000L));
        user.setEmail(sp.getString("email",null));
        user.setTelephone(sp.getString("telephone",null));
        user.setMotto(sp.getString("motto",null));
        user.setIgnore_version(sp.getInt("ignore_version",1));
        return user;
    }


}
