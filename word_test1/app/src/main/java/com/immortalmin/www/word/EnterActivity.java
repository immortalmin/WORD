package com.immortalmin.www.word;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import java.util.HashMap;

public class EnterActivity extends AppCompatActivity
        implements View.OnClickListener,
        LoginFragment.OnFragmentInteractionListener,
        RegisterFragment.OnFragmentInteractionListener{

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentTransaction transaction = fragmentManager.beginTransaction();
    private LoginFragment loginFragment = new LoginFragment();
    private RegisterFragment registerFragment = new RegisterFragment();
    private FrameLayout enter_frame;
    private UserData userData = new UserData();
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);
        enter_frame = (FrameLayout)findViewById(R.id.enter_frame);
        init_user();
        login();
    }

    private void login(){
        Bundle bundle = new Bundle();
        bundle.putSerializable("userData",userData);
        loginFragment.setArguments(bundle);
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.enter_frame,loginFragment);
        transaction.commit();
    }

    private void init_user(){
        SharedPreferences sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
        userData.setUid(sp.getString("uid",null));
        userData.setRecite_num(sp.getInt("recite_num",20));
        userData.setRecite_scope(sp.getInt("recite_scope",10));
        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        userData.setUsername(sp.getString("username",null));
        userData.setPassword(sp.getString("password",null));
        userData.setStatus(sp.getString("status",null));
        userData.setProfile_photo(sp.getString("profile_photo",null));
    }

    public void onClick(View view){
        switch (view.getId()){

        }
    }

    @Override
    public void loginFragmentInteraction(HashMap<String,Object> data) {
        Log.i("ccc",userData.toString());
        switch (Integer.valueOf(data.get("what").toString())){
            case 0:
                userData = (UserData)data.get("userData");
                SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                sp.edit().putString("username", userData.getUsername())
                        .putString("password", userData.getPassword())
                        .putString("profile_photo", userData.getProfile_photo())
                        .putString("status",userData.getStatus())
                        .apply();
                sp = getSharedPreferences("setting",Context.MODE_PRIVATE);
                sp.edit().putString("uid",userData.getUid())
                        .putInt("recite_num",userData.getRecite_num())
                        .putInt("recite_scope",userData.getRecite_scope())
                        .apply();

//                intent = new Intent(EnterActivity.this, MainActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
//                EnterActivity.this.finish();
                break;
            case 1:
                transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.enter_frame,registerFragment);
                transaction.hide(loginFragment).show(registerFragment);
                transaction.commit();
                break;
        }

    }

    @Override
    public void registerFragmentInteraction(HashMap<String,Object> data) {
        Log.i("ccc",data.toString());
    }
}
