package com.immortalmin.www.word;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class DataUtil {

    private Context context;
    private HashMap<String,Object> userdata=null;
    private HashMap<String,Object> userSetting=null;
    private JsonRe jsonRe = new JsonRe();

    public DataUtil(Context context){
        this.context = context;
    }

    /**
     * ********************************
     * ***** 根据username获取数据 *****
     * ********************************
     * 从服务器获取用户数据
     * 保存在本地
     * 再返回UserData
     * @return
     */
    public UserData getdata() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                try {
                    SharedPreferences sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
                    jsonObject.put("username",sp.getString("username",null));
                }catch (JSONException e){
                    e.printStackTrace();
                }
                HttpGetContext httpGetContext = new HttpGetContext();
                String wordjson = httpGetContext.getData("http://47.98.239.237/word/php_file2/getuserdata.php",jsonObject);
                userdata = jsonRe.userData(wordjson);
                //将用户数据保存到本地
                SharedPreferences sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
                sp.edit().putString("username", userdata.get("username").toString())
                        .putString("password", userdata.get("password").toString())
                        .putString("profile_photo", userdata.get("profile_photo").toString())
                        .putString("status","1")
                        .putString("email",userdata.get("email").toString())
                        .putString("telephone",userdata.get("telephone").toString())
                        .putString("motto",userdata.get("motto").toString())
                        .putLong("last_login",Long.valueOf(userdata.get("last_login").toString()))
                        .apply();
                //获取setting
                jsonObject = new JSONObject();
                try{
                    jsonObject.put("uid",userdata.get("uid").toString());
                }catch (JSONException e){
                    e.printStackTrace();
                }
                httpGetContext = new HttpGetContext();
                String s = httpGetContext.getData("http://47.98.239.237/word/php_file2/getsetting.php",jsonObject);
                userSetting = jsonRe.userSetting(s);
                sp = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
                sp.edit().putString("uid",userSetting.get("uid").toString())
                        .putInt("recite_num",Integer.valueOf(userSetting.get("recite_num").toString()))
                        .putInt("recite_scope",Integer.valueOf(userSetting.get("recite_scope").toString()))
                        .apply();
            }
        }).start();
        return set_user();
    }

    private UserData set_user(){
        UserData userData = new UserData();
        SharedPreferences sp = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        userData.setUid(sp.getString("uid",null));
        userData.setRecite_num(sp.getInt("recite_num",20));
        userData.setRecite_scope(sp.getInt("recite_scope",10));
        sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        userData.setUsername(sp.getString("username",null));
        userData.setPassword(sp.getString("password",null));
        userData.setProfile_photo(sp.getString("profile_photo",null));
        userData.setStatus(sp.getString("status","0"));
        userData.setLast_login(sp.getLong("last_login",946656000000L));
        userData.setEmail(sp.getString("email",null));
        userData.setTelephone(sp.getString("telephone",null));
        userData.setMotto(sp.getString("motto",null));
        return userData;
    }

}
