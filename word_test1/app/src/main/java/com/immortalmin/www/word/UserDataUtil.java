package com.immortalmin.www.word;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 对用户数据进行处理的工具类
 * 包括：1、从服务器获取用户数据；2、将用户数据保存到SharedPreferences；3、返回用户数据（User对象）给调用者;4、更新服务器和本地的用户数据
 */
public class UserDataUtil {

    private Context context;
    private JsonRe jsonRe = new JsonRe();

    public UserDataUtil(Context context){
        this.context = context;
    }

    public interface HttpCallbackStringListener{
        void onFinish(User u);
        void onError(Exception e);
    }

    /**
     * 可能的调用者：
     *      1、如果是第一次通过QQ登录，还需要传入open_id,profile_photo。在获取用户数据之前，将会进行注册
     *      2、登录界面，普通用户，还需要传入username（从输入框获取的）
     *      3、还可以通过电话号码获取，用于修改密码等情节
     *      4、用户注册时，检查是否用户名是否已使用，需要提供username
     */
    public void getUserDataFromServer(JSONObject jsonObject,boolean updateLocally,final HttpCallbackStringListener listener) {
        if(jsonObject==null) jsonObject = new JSONObject();
        try {
            SharedPreferences sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
            jsonObject.put("what",14);
            if(!jsonObject.has("login_mode")) jsonObject.put("login_mode",0);//默认为普通用户获取数据
            if(!jsonObject.has("username")) jsonObject.put("username",sp.getString("username",null));
        }catch (JSONException e){
            listener.onError(e);
        }
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            User user = jsonRe.userData(result);
            if(updateLocally) updateUserDataInLocal(user);
            listener.onFinish(user);
        });
        myAsyncTask.execute(jsonObject);
    }

    /**
     * 从SharedPreferences获取用户数据
     */
    public User getUserDataFromSP(){
        User user = new User();
        try{
            SharedPreferences sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
            user.setUid(sp.getString("uid",null));
            user.setOpen_id(sp.getString("open_id",null));
            user.setUsername(sp.getString("username",null));
            user.setPassword(sp.getString("password",null));
            user.setProfile_photo(sp.getString("profile_photo",null));
            user.setMotto(sp.getString("motto",null));
            user.setEmail(sp.getString("email",null));
            user.setTelephone(sp.getString("telephone",null));
//            user.setAccess_token(sp.getString("access_token",null));
//            user.setExpires_in(sp.getString("expires_in",null));
            user.setStatus(sp.getInt("status",0));
            user.setLogin_mode(sp.getInt("login_mode",0));
            user.setIgnore_version(sp.getInt("ignore_version",1));
            user.setLast_login(sp.getLong("last_login",946656000000L));
            sp = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
            user.setRecite_num(sp.getInt("recite_num",20));
            user.setRecite_scope(sp.getInt("recite_scope",10));
        }catch (Exception e){
            e.printStackTrace();
        }
        return user;
    }

    /**
     * 更新SharedPreferences中的用户数据
     */
    public void updateUserDataInLocal(User user){
        SharedPreferences sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        sp.edit().putString("uid",user.getUid())
                .putString("open_id",user.getOpen_id())
                .putString("username", user.getUsername())
                .putString("password",user.getPassword())
                .putString("profile_photo", user.getProfile_photo())
                .putString("motto", user.getMotto())
                .putString("email",user.getEmail())
                .putString("telephone",user.getTelephone())
//                .putString("access_token",user.getAccess_token())
//                .putString("expires_in",user.getExpires_in())
                .putInt("status",user.getStatus())
                .putInt("login_mode",user.getLogin_mode())
                .putInt("ignore_version",user.getIgnore_version())
                .putLong("last_login", user.getLast_login())
                .apply();
        sp = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        sp.edit().putInt("recite_num",user.getRecite_num())
                .putInt("recite_scope",user.getRecite_scope())
                .apply();
    }

    /**
     * 更新服务器中的用户数据
     */
    public void updateUserDataInServer(User user,boolean updateLocal){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("what",23);
            jsonObject.put("uid",user.getUid());
            jsonObject.put("username",user.getUsername());
//            jsonObject.put("password",);
            jsonObject.put("motto",user.getMotto());
//            jsonObject.put("email",);
            jsonObject.put("telephone",user.getTelephone());
//            jsonObject.put("access_token",);
//            jsonObject.put("expires_in",);
            jsonObject.put("recite_num",user.getRecite_num());
            jsonObject.put("recite_scope",user.getRecite_scope());
//            jsonObject.put("status",);
//            jsonObject.put("login_mode",);
            jsonObject.put("ignore_version",user.getIgnore_version());
            jsonObject.put("last_login",user.getLast_login());

        }catch (JSONException e){
            e.printStackTrace();
        }
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            if(updateLocal) updateUserDataInLocal(user);
        });
        myAsyncTask.execute(jsonObject);
    }

//    private void getSetting(final HttpCallbackStringListener listener){
//        JSONObject jsonObject = new JSONObject();
//        try{
//            jsonObject.put("uid",user.getUid());
//            jsonObject.put("what",13);
//        }catch (Exception e){
//            listener.onError(e);
//        }
//        myAsyncTask = new MyAsyncTask();
//        myAsyncTask.setLoadDataComplete((result)->{
//            userSetting = jsonRe.userSetting(result);
//            SharedPreferences sp = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
//            try{
//                sp.edit().putString("uid",userSetting.get("uid").toString())
//                        .putInt("recite_num",Integer.valueOf(userSetting.get("recite_num").toString()))
//                        .putInt("recite_scope",Integer.valueOf(userSetting.get("recite_scope").toString()))
//                        .apply();
//            }catch (Exception e){
//                listener.onError(e);
//            }
//            listener.onFinish(set_user());
//        });
//        myAsyncTask.execute(jsonObject);
//    }


    /**
     * 从本地获取UserData?
     */
    /*User set_user(){
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
    }*/


}
