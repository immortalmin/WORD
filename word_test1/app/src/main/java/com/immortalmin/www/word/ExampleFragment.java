package com.immortalmin.www.word;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import cn.pedant.SweetAlert.SweetAlertDialog;
import jp.wasabeef.glide.transformations.BlurTransformation;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class ExampleFragment extends Fragment implements View.OnClickListener{

    private OnFragmentInteractionListener mListener;
    private ListView example_list;
    private TextView non_example;
    private ImageView backdrop,no_network;
    private CheckBox checkbox;
    private ExampleAdapter exampleAdapter;
    private ArrayList<OtherSentence> examplelist = new ArrayList<>();
    private ArrayList<OtherSentence> temp = new ArrayList<>();
    private int mode=0,wid=1,edit_index,del_index;
    private String dict_source="0";
    private User user = new User();
    private JsonRe jsonRe = new JsonRe();
    private CaptureUtil captureUtil = new CaptureUtil();
    private MyAsyncTask myAsyncTask;
    private boolean network = true;

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
        example_list = getActivity().findViewById(R.id.example_list);
        non_example = getActivity().findViewById(R.id.non_example);
        checkbox = getActivity().findViewById(R.id.checkbox);
        no_network = getActivity().findViewById(R.id.no_network_Others);
        checkbox.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){
                //API level:21->24
                examplelist.removeIf(
                        example->!user.getUsername().equals(example.getSource())
                );
                exampleAdapter.notifyDataSetChanged();
            }else{
                examplelist.clear();
                examplelist.addAll(temp);
                exampleAdapter.notifyDataSetChanged();
            }
        });
        if(!network) mHandler.sendEmptyMessage(3);
    }

    public interface OnFragmentInteractionListener {
        void exampleFragmentInteraction(String res);
    }

    public void setData(int wid, User user, ImageView backdrop, String dict_source,boolean network){
        this.wid = wid;
        this.user = user;
        this.backdrop = backdrop;
        this.dict_source = dict_source;
        this.network = network;
        getExampleData();
    }

    public void setExamplelist(ArrayList<OtherSentence> data,boolean isTobottom){
        //如果原本是没有例句的话，添加后要把“暂无例句”去除
        if(examplelist.size()==0){
            mHandler.obtainMessage(2,1).sendToTarget();
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
                        exampleAdapter = new ExampleAdapter(getActivity(),examplelist,mode, user.getUsername());
                        example_list.setAdapter(exampleAdapter);
                        exampleAdapter.setOnItemClickListener(new ExampleAdapter.onItemListener() {
                            @Override
                            public void onDeleteClick(int i) {
                                del_warning(examplelist.get(i).getEid());
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
                    Glide.with(getActivity()).load(captureUtil.getcapture(getActivity()))
                            .apply(bitmapTransform(new BlurTransformation(25))).into(backdrop);
                    backdrop.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    backdrop.setVisibility(View.INVISIBLE);
                    if("1".equals(message.obj.toString())){//显示例句
                        non_example.setVisibility(View.INVISIBLE);
                        example_list.setVisibility(View.VISIBLE);
                    }else if("2".equals(message.obj.toString())){//显示“暂无例句”
                        non_example.setVisibility(View.VISIBLE);
                        example_list.setVisibility(View.INVISIBLE);
                    }
                    break;
                case 3:
                    no_network.setVisibility(View.VISIBLE);
                    checkbox.setVisibility(View.INVISIBLE);
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

    private void getExampleData(){
        if(!network) return;
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("what",8);
            jsonObject.put("uid", user.getUid());
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
        mHandler.sendEmptyMessage(1);
        SweetAlertDialog del_alert = new SweetAlertDialog(getActivity(),SweetAlertDialog.WARNING_TYPE);
        del_alert.setTitleText("Really?")
                .setContentText("Data will be permanently deleted.")
                .setConfirmText("OK")
                .setCancelText("No,cancel del!")
                .setConfirmClickListener(sweetAlertDialog -> {
                    deleteExample(eid);
                    sweetAlertDialog.cancel();
                })
                .showCancelButton(true)
                .setCancelClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.cancel();
                    mHandler.obtainMessage(2,0).sendToTarget();
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
                mHandler.obtainMessage(2,2).sendToTarget();
            }else{
                mHandler.obtainMessage(2,0).sendToTarget();
            }

        });
        myAsyncTask.execute(jsonObject);
    }

    private void updateExampleDialog(OtherSentence data){
        mHandler.sendEmptyMessage(1);
        UpdateExampleDialog updateExampleDialog = new UpdateExampleDialog(getActivity(),R.style.MyDialog,data);
        updateExampleDialog.show();
        updateExampleDialog.setCancelable(false);
        updateExampleDialog.setOnDismissListener(dialogInterface -> mHandler.obtainMessage(2,0).sendToTarget());
    }


}
