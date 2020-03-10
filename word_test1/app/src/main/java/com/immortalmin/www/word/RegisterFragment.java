package com.immortalmin.www.word;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterFragment extends Fragment implements View.OnClickListener{
    private OnFragmentInteractionListener mListener;
    private ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);
    private Button reg_btn,return_btn;
    private EditText username_et,pwd_et,confirm_pwd;
    private TextView user_warn,pwd_warn,confirm_warn;
    private CircleImageView profile_photo;
    private JsonRe jsonRe;
    private Runnable toLogin;
    private String profilephotoPath="null";
    private HashMap<String,Object> userdata=null;
    private UserData userData = new UserData();

    /**
     * Activity绑定上Fragment时，调用该方法
     * 这个是第一次被调用的
     * @param context
     */
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Fragment显示的内容是怎样的，就是通过下面这个方法返回回去的(view)
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_register,null);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        reg_btn = (Button)getActivity().findViewById(R.id.reg_btn);
        return_btn = (Button)getActivity().findViewById(R.id.return_btn);
        username_et = (EditText)getActivity().findViewById(R.id.username_et);
        pwd_et = (EditText)getActivity().findViewById(R.id.pwd_et);
        confirm_pwd = (EditText)getActivity().findViewById(R.id.confirm_pwd);
        user_warn = (TextView) getActivity().findViewById(R.id.user_warn);
        pwd_warn = (TextView) getActivity().findViewById(R.id.pwd_warn);
        confirm_warn = (TextView) getActivity().findViewById(R.id.confirm_warn);
        profile_photo = (CircleImageView) getActivity().findViewById(R.id.profile_photo);
        reg_btn.setOnClickListener(this);
        return_btn.setOnClickListener(this);
        profile_photo.setOnClickListener(this);

        jsonRe = new JsonRe();
        init();

//        Bundle bundle = getArguments();
//        userData = (UserData)bundle.getSerializable("userData");

//        username.setText(userData.getUsername());
//        password.setText(userData.getPassword());
//        getImage(userData.getProfile_photo());
//        login();
//        init();

    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void registerFragmentInteraction(HashMap<String,Object> data);
    }


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 0:
                    user_warn.setVisibility(View.INVISIBLE);
                    reg_btn.setClickable(true);
                    break;
                case 1:
                    user_warn.setVisibility(View.VISIBLE);
                    reg_btn.setClickable(false);
                    break;
                case 2:

                    break;
                case 3:
                    pwd_warn.setVisibility(View.INVISIBLE);
                    reg_btn.setClickable(true);
                    break;
                case 4:
                    pwd_warn.setVisibility(View.VISIBLE);
                    reg_btn.setClickable(false);
                    break;
                case 5:
                    confirm_warn.setVisibility(View.INVISIBLE);
                    reg_btn.setClickable(true);
                    break;
                case 6:
                    confirm_warn.setVisibility(View.VISIBLE);
                    reg_btn.setClickable(false);
                    break;
                case 7:
                    profile_photo.setImageBitmap((Bitmap)message.obj);
                    break;
            }
            return false;
        }
    });


    private void init() {
        /**
         * 延迟跳转（等toast结束后跳转）
         */
        toLogin = new Runnable() {
            @Override
            public void run() {
                /*
                Intent intent = new Intent();
                intent.putExtra("username",username.getText().toString());
                setResult(1,intent);
                finish();
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);*/
            }
        };

        username_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mHandler.obtainMessage(0).sendToTarget();
                String uname = username_et.getText().toString();
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("username",uname);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                query_user(jsonObject);
            }
        });

        pwd_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String now_pwd = pwd_et.getText().toString();
                if(!isPassword(now_pwd)){
                    mHandler.obtainMessage(4).sendToTarget();
                }else{
                    mHandler.obtainMessage(3).sendToTarget();
                }
            }
        });

        confirm_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(confirm_pwd.getText().toString().equals(pwd_et.getText().toString())){
                    mHandler.obtainMessage(5).sendToTarget();
                }else{
                    mHandler.obtainMessage(6).sendToTarget();
                }
            }
        });
    }
    /**
     * 选项按钮点击事件
     * @param view
     */
    public void onClick(View view){
        switch (view.getId()){
            case R.id.reg_btn:
                String uname = username_et.getText().toString();
                String password = pwd_et.getText().toString();
                Toast.makeText(getActivity(),"注册成功 即将跳转到主页",Toast.LENGTH_SHORT).show();
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("username",uname);
                    jsonObject.put("pwd",password);
                    jsonObject.put("imgpath",profilephotoPath);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                register(jsonObject);
                break;
            case R.id.profile_photo:
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,0);
                break;
            case R.id.return_btn:
                /*
                finish();
                overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
                */
                break;
        }
    }

    public boolean isPassword(String password){
        String regex="^(?![0-9])(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";
        Pattern p= Pattern.compile(regex);
        Matcher m=p.matcher(password);
        boolean isMatch=m.matches();
        return isMatch;
    }

    private void query_user(final JSONObject jsonObject) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                String wordjson = httpGetContext.getData("http://47.98.239.237/word/php_file2/getuserdata.php",jsonObject);
                userdata = jsonRe.userData(wordjson);
                Log.i("ccc",userdata.toString());
                if(userdata.size()!=0){
                    mHandler.obtainMessage(1).sendToTarget();
                }
            }
        }).start();
    }

    /**
     * 进行注册
     * @param userdata
     */
    private void register(final JSONObject userdata){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                httpGetContext.userRegister(userdata);
                mHandler.postDelayed(toLogin,2000);
            }
        }).start();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
                if(data==null){
                    Log.i("ccc","数据为空");
                    break;
                }
                //打开相册并选择照片，这个方式选择单张
                // 获取返回的数据，这里是android自定义的Uri地址
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                // 获取选择照片的数据视图
                if(selectedImage!=null){
                    Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    // 从数据视图中获取已选择图片的路径
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    // 将图片显示到界面上
                    Bitmap bitmap = ImageUtils.getBitmapFromPath(picturePath, 80, 80);
                    profilephotoPath = android.os.Environment.getExternalStorageDirectory()+"/temp.jpg";
                    mHandler.obtainMessage(7,bitmap).sendToTarget();
                    cursor.close();
                }
                break;
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
//            finish();
//            overridePendingTransition(R.anim.fade_out,R.anim.fade_away);
//            return false;
//        } else {
//            return super.onKeyDown(keyCode, event);
//        }
//    }


    /**
     * 向activity回送数据
     */
    public void send_to_activity(int what){
        HashMap<String,Object> data = new HashMap<>();
        data.put("what",what);
        if (mListener != null) {
            mListener.registerFragmentInteraction(data);
        }
    }



}
