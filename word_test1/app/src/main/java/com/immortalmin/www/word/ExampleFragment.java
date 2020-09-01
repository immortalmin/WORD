package com.immortalmin.www.word;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


public class ExampleFragment extends Fragment implements View.OnClickListener{


    private OnFragmentInteractionListener mListener;
    private ListView example_list;
    private TextView non_example;
    private ImageView backdrop;
    private CheckBox checkbox;
    private ExampleAdapter exampleAdapter;
    private ArrayList<HashMap<String,Object>> examplelist = new ArrayList<>();
    private ArrayList<HashMap<String,Object>> temp = new ArrayList<>();
    private int mode=0,wid=1,edit_index,del_index;
    private String dict_source="0";
    private UserData userData = new UserData();
    private JSONObject jsonObject;
    private JsonRe jsonRe = new JsonRe();
    private CaptureUtil captureUtil = new CaptureUtil();
    private MyAsyncTask myAsyncTask;

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
        checkbox = (CheckBox)getActivity().findViewById(R.id.checkbox);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    //API level:21->24
                    examplelist.removeIf(
                            example->!"immortalmin".equals(example.get("source"))
                    );
                    exampleAdapter.notifyDataSetChanged();
                }else{
                    examplelist.clear();
                    examplelist.addAll(temp);
                    exampleAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void exampleFragmentInteraction(String res);
    }

    public void setData(int wid,UserData userData,ImageView backdrop,String dict_source){
        this.wid = wid;
        this.userData = userData;
        this.backdrop = backdrop;
        this.dict_source = dict_source;
        getExampleData();
    }

    public void setExamplelist(ArrayList<HashMap<String,Object>> data,boolean isTobottom){
        //如果原本是没有例句的话，添加后要把“暂无例句”去除
        if(examplelist.size()==0){
            mHandler.obtainMessage(6,1).sendToTarget();
        }
        examplelist.clear();
        examplelist.addAll(data);
        if(exampleAdapter==null){
            mHandler.obtainMessage(0).sendToTarget();
        }else{
            exampleAdapter.notifyDataSetChanged();
        }

        if(isTobottom){
            example_list.setSelection(examplelist.size()-1);
        }

    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    if(examplelist.size() == 0){
                        non_example.setVisibility(View.VISIBLE);
                        example_list.setVisibility(View.INVISIBLE);
                        checkbox.setVisibility(View.INVISIBLE);
                    }else{
                        non_example.setVisibility(View.INVISIBLE);
                        example_list.setVisibility(View.VISIBLE);
                        checkbox.setVisibility(View.VISIBLE);
                        exampleAdapter = new ExampleAdapter(getActivity(),examplelist,mode,userData.getUsername());
                        example_list.setAdapter(exampleAdapter);
                        exampleAdapter.setOnItemClickListener(new ExampleAdapter.onItemListener() {
                            @Override
                            public void onDeleteClick(int i) {
                                del_warning(examplelist.get(i).get("eid").toString());
                            }
                            @Override
                            public void onEditClick(int i) {
                                edit_index = i;
                                updateExampleDialog(examplelist.get(i));

                            }
                        });
                    }
                    break;
                case 1:

                    break;
                case 2:

                    break;
                case 3:

                    break;
                case 4:

                    break;
                case 5:
                    Glide.with(getActivity()).load(captureUtil.getcapture(getActivity()))
                            .apply(bitmapTransform(new BlurTransformation(25))).into(backdrop);
                    backdrop.setVisibility(View.VISIBLE);
                    break;
                case 6:
                    backdrop.setVisibility(View.INVISIBLE);
                    if("1".equals(message.obj.toString())){//显示例句
                        non_example.setVisibility(View.INVISIBLE);
                        example_list.setVisibility(View.VISIBLE);
                    }else if("2".equals(message.obj.toString())){//显示“暂无例句”
                        non_example.setVisibility(View.VISIBLE);
                        example_list.setVisibility(View.INVISIBLE);
                    }
                    break;
            }
            return false;
        }
    });

    public void onClick(View view) {
        switch (view.getId()) {

        }
    }

    public void change_mode(int mode){
        if(examplelist.size()>0){
            exampleAdapter.setMode(mode);
            exampleAdapter.notifyDataSetChanged();
        }
        this.mode = mode;
    }

//    public void update_data(int what,JSONObject data){
//        try{
//            switch (what){
//                case 0://新增例句
//                    //有时间把translate换成sentences
//                    JSONArray jsonArray = (JSONArray)data.get("translate");
//                    HashMap<String,Object> sentence = new HashMap<>();
//                    for(int i=0;i<jsonArray.length();i++){
//                        sentence = new HashMap<>();
//                        JSONObject sentence_json = (JSONObject)jsonArray.opt(i);
//                        String word_meaning = sentence_json.getString("word_meaning").replaceAll("\\\\n","\\\n");
//                        String E_sentence = sentence_json.getString("E_sentence").replaceAll("\\\\n","\\\n");
//                        String C_translate = sentence_json.getString("C_translate").replaceAll("\\\\n","\\\n");
//                        if(word_meaning.charAt(word_meaning.length()-1) == '\n'){
//                            word_meaning = word_meaning.substring(0,word_meaning.length()-1);
//                        }
//                        if(E_sentence.charAt(E_sentence.length()-1) == '\n'){
//                            E_sentence = E_sentence.substring(0,E_sentence.length()-1);
//                        }
//                        if(C_translate.charAt(C_translate.length()-1) == '\n'){
//                            C_translate = C_translate.substring(0,C_translate.length()-1);
//                        }
//                        sentence.put("word_meaning",word_meaning);
//                        sentence.put("E_sentence",E_sentence);
//                        sentence.put("C_translate",C_translate);
//                        sentence.put("source",data.getString("source"));
//
//                    }
//                    examplelist.add(sentence);
//                    exampleAdapter.notifyDataSetChanged();
//                    Log.i("ccc","add_example:"+examplelist.toString());
//                    example_list.setSelection(examplelist.size()-1);
//                    getwordlist(false);
//                    break;
//                case 1://修改例句
//                    sentence = examplelist.get(edit_index);
//                    sentence.put("word_meaning",data.getString("word_meaning"));
//                    sentence.put("E_sentence",data.getString("E_sentence"));
//                    sentence.put("C_translate",data.getString("C_translate"));
//                    examplelist.set(edit_index,sentence);
//                    exampleAdapter.notifyDataSetChanged();
//                    break;
//            }
//        }catch (JSONException e){
//            e.printStackTrace();
//        }
//
//    }

    //examplelist将改变
    //是否需要更新UI
//    private void getwordlist(boolean isupdateUI) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                JSONObject jsonObject = new JSONObject();
//                try{
//                    jsonObject.put("uid",userData.getUid());
//                    jsonObject.put("wid",Integer.valueOf(wid));
//                }catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                HttpGetContext httpGetContext = new HttpGetContext();
//                String examplejson = httpGetContext.getData("http://47.98.239.237/word/php_file2/getexampledata.php",jsonObject);
////                examplelist = jsonRe.exampleData(examplejson);
//                temp = jsonRe.exampleData(examplejson);
////                temp.clear();
////                temp.addAll(jsonRe.exampleData(examplejson));
//                examplelist.clear();
//                examplelist.addAll(temp);
//                if(isupdateUI){
//                    mHandler.obtainMessage(0).sendToTarget();
//                }
//            }
//        }).start();
//
//    }

    private void getExampleData(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("what",8);//getexampledata
            jsonObject.put("uid",userData.getUid());
            jsonObject.put("wid",Integer.valueOf(wid));
            jsonObject.put("dict_source",dict_source);
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            examplelist.clear();
            examplelist.addAll(jsonRe.exampleData(result));
            temp.clear();
            temp.addAll(jsonRe.exampleData(result));
            mHandler.obtainMessage(0).sendToTarget();
        });
        myAsyncTask.execute(jsonObject);
    }

    /**
     * 删除警告
     */
    private void del_warning(String eid){
        mHandler.obtainMessage(5).sendToTarget();
        SweetAlertDialog del_alert = new SweetAlertDialog(getActivity(),SweetAlertDialog.WARNING_TYPE);
        del_alert.setTitleText("Really?")
                .setContentText("Data will be permanently deleted.")
                .setConfirmText("OK")
                .setCancelText("No,cancel del!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        deleteExample(eid);
                        sweetAlertDialog.cancel();
                    }
                })
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.cancel();
                        mHandler.obtainMessage(6,0).sendToTarget();
                    }
                });
        del_alert.setCancelable(false);
        del_alert.show();
    }

    private void deleteExample(String eid){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("what",2);
            jsonObject.put("eid",eid);//记得改成eid
            jsonObject.put("wid",wid);
            jsonObject.put("dict_source",dict_source);
        }catch (JSONException e){
            e.printStackTrace();
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.setLoadDataComplete((result)->{
            examplelist.clear();
            examplelist.addAll(jsonRe.exampleData(result));
            exampleAdapter.notifyDataSetChanged();
            Toast.makeText(getActivity(),"删除成功",Toast.LENGTH_SHORT).show();
            if(examplelist.size()==0){
                mHandler.obtainMessage(6,2).sendToTarget();
            }else{
                mHandler.obtainMessage(6,0).sendToTarget();
            }

        });
        myAsyncTask.execute(jsonObject);
    }

    private void updateExampleDialog(HashMap<String,Object> data){
        mHandler.obtainMessage(5).sendToTarget();
        UpdateExampleDialog updateExampleDialog = new UpdateExampleDialog(getActivity(),R.style.MyDialog,data);
        updateExampleDialog.show();
        updateExampleDialog.setCancelable(false);
        updateExampleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mHandler.obtainMessage(6,0).sendToTarget();
            }
        });
    }


}
