package com.immortalmin.www.word;

import java.sql.Date;
import java.util.HashMap;

public class DailyRecitation {
    int rid,uid,review_num=0,recite_num=0,grasp_num=0,total_num= 0;
    String record_date;
    boolean is_synchronized;

    DailyRecitation(){}

    DailyRecitation(int uid,int review_num,int recite_num,int grasp_num,String record_date,boolean is_synchronized){
        this.uid = uid;
        this.review_num = review_num;
        this.recite_num = recite_num;
        this.grasp_num = grasp_num;
        this.record_date = record_date;
        this.is_synchronized = is_synchronized;
    }

    DailyRecitation(int rid,int uid,int review_num,int recite_num,int grasp_num,String record_date,boolean is_synchronized){
        this.rid = rid;
        this.uid = uid;
        this.review_num = review_num;
        this.recite_num = recite_num;
        this.grasp_num = grasp_num;
        this.record_date = record_date;
        this.is_synchronized = is_synchronized;
    }

    DailyRecitation(HashMap<String,Object> data){
        try{
            this.rid=Integer.parseInt(data.get("rid").toString());
            this.uid=Integer.parseInt(data.get("uid").toString());
            this.review_num=Integer.parseInt(data.get("review_num").toString());
            this.recite_num=Integer.parseInt(data.get("recite_num").toString());
            this.grasp_num=Integer.parseInt(data.get("grasp_num").toString());
            this.record_date=data.get("record_date").toString();
            this.is_synchronized=Boolean.parseBoolean(data.get("is_synchronized").toString());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getReview_num() {
        return review_num;
    }

    public void setReview_num(int review_num) {
        this.review_num = review_num;
    }

    public int getRecite_num() {
        return recite_num;
    }

    public void setRecite_num(int recite_num) {
        this.recite_num = recite_num;
    }

    public int getGrasp_num() {
        return grasp_num;
    }

    public void setGrasp_num(int grasp_num) {
        this.grasp_num = grasp_num;
    }

    public String getRecord_date() {
        return record_date;
    }

    public void setRecord_date(String record_date) {
        this.record_date = record_date;
    }

    public boolean isIs_synchronized() {
        return is_synchronized;
    }

    public void setIs_synchronized(boolean is_synchronized) {
        this.is_synchronized = is_synchronized;
    }

    public int getTotal_num() {
        total_num = recite_num+review_num;
        return total_num;
    }

    public void setTotal_num(int total_num) {
        this.total_num = total_num;
    }

    @Override
    public String toString() {
        return "DailyRecitation{" +
                "rid=" + rid +
                ", uid=" + uid +
                ", review_num=" + review_num +
                ", recite_num=" + recite_num +
                ", grasp_num=" + grasp_num +
                ", total_num=" + total_num +
                ", record_date='" + record_date + '\'' +
                ", is_synchronized=" + is_synchronized +
                '}';
    }
}
