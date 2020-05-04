package com.immortalmin.www.word;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


public class ExampleFragment extends Fragment implements UpdateExampleDialog.OnDialogInteractionListener{


    private OnFragmentInteractionListener mListener;
    private ListView example_list;
    private TextView non_example;
    private ImageView backdrop;
    private ExampleAdapter exampleAdapter;
    private ArrayList<HashMap<String,Object>> examplelist = null;
    private int mode=1,wid=1;
    private UserData userData = new UserData();
    private JSONObject jsonObject;
    private JsonRe jsonRe = new JsonRe();
    private CaptureUtil captureUtil = new CaptureUtil();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_example, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        example_list = (ListView)getActivity().findViewById(R.id.example_list);
        non_example = (TextView)getActivity().findViewById(R.id.non_example);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void exampleFragmentInteraction(String res);
    }

    public void setData(int wid,UserData userData,ImageView backdrop){
        this.wid = wid;
        this.userData = userData;
        this.backdrop = backdrop;
        getwordlist();
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    if(examplelist.size() == 0){
                        non_example.setVisibility(View.VISIBLE);
                        example_list.setVisibility(View.INVISIBLE);
                    }else{
                        non_example.setVisibility(View.INVISIBLE);
                        example_list.setVisibility(View.VISIBLE);
                        exampleAdapter = new ExampleAdapter(getActivity(),examplelist,mode,userData.getUsername());
                        example_list.setAdapter(exampleAdapter);
                        exampleAdapter.setOnItemClickListener(new ExampleAdapter.onItemListener() {
                            @Override
                            public void onDeleteClick(int i) {
                                jsonObject = new JSONObject();
                                try{
                                    jsonObject.put("id",examplelist.get(i).get("eid").toString());
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                                del_warning();
                            }
                            @Override
                            public void onEditClick(int i) {
                                updateExampleDialog(examplelist.get(i));
                            }
                        });
                    }
                    break;
                case 5:
//                    Glide.with(ExampleActivity.this).load(getcapture())
//                            .apply(bitmapTransform(new BlurTransformation(25))).into(backdrop);
                    Glide.with(getActivity()).load(captureUtil.getcapture(getActivity()))
                            .apply(bitmapTransform(new BlurTransformation(25))).into(backdrop);
                    backdrop.setVisibility(View.VISIBLE);
                    break;
                case 6:
                    backdrop.setVisibility(View.INVISIBLE);
                    break;
            }
            return false;
        }
    });

    private void getwordlist() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("uid",userData.getUid());
                    jsonObject.put("wid",Integer.valueOf(wid));
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpGetContext httpGetContext = new HttpGetContext();
//                String wordjson = httpGetContext.getData("http://47.98.239.237/word/php_file2/getworddata.php",jsonObject);
//                word = jsonRe.wordData(wordjson);
                String examplejson = httpGetContext.getData("http://47.98.239.237/word/php_file2/getexampledata.php",jsonObject);
                examplelist = jsonRe.exampleData(examplejson);
                mHandler.obtainMessage(0).sendToTarget();
            }
        }).start();

    }

    /**
     * 删除警告
     */
    private void del_warning(){
        mHandler.obtainMessage(5).sendToTarget();
        SweetAlertDialog del_alert = new SweetAlertDialog(getActivity(),SweetAlertDialog.WARNING_TYPE);
        del_alert.setTitleText("Really?")
                .setContentText("Data will be permanently deleted.")
                .setConfirmText("OK")
                .setCancelText("No,cancel del!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        delete_example(jsonObject);
                        Toast.makeText(getActivity(),"删除成功",Toast.LENGTH_SHORT).show();
                        sweetAlertDialog.cancel();
                    }
                })
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.cancel();
                        mHandler.obtainMessage(6).sendToTarget();
                    }
                });
        del_alert.setCancelable(false);
        del_alert.show();
    }

    private void delete_example(final JSONObject jsonObject){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                httpGetContext.getData("http://47.98.239.237/word/php_file2/delete_example.php",jsonObject);
            }
        }).start();
        mHandler.obtainMessage(6).sendToTarget();
        getwordlist();
    }

    private void updateExampleDialog(HashMap<String,Object> data){
        mHandler.obtainMessage(5).sendToTarget();
        UpdateExampleDialog updateExampleDialog = new UpdateExampleDialog(getActivity(),R.style.MyDialog,data);
        updateExampleDialog.show();
        updateExampleDialog.setCancelable(false);
        updateExampleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mHandler.obtainMessage(6).sendToTarget();
            }
        });
    }

    private void update_example(final JSONObject jsonObject){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGetContext httpGetContext = new HttpGetContext();
                httpGetContext.getData("http://47.98.239.237/word/php_file2/update_example.php",jsonObject);
            }
        }).start();
        getwordlist();
    }

    @Override
    public void updateExampleInteraction(JSONObject jsonObject){
        update_example(jsonObject);

    }
}
