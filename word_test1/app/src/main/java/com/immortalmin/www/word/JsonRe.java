package com.immortalmin.www.word;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将JSON字符串转换成相应数据的工具类
 */
public class JsonRe {

    public ArrayList<HashMap<String,Object>> feedbackData(String jsonStr){
        ArrayList<HashMap<String,Object>> feedbackList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = (JSONObject)jsonArray.opt(i);
                HashMap<String,Object> feedback = new HashMap<>();
                feedback.put("fid",jsonObject.getString("fid"));
                feedback.put("uid",jsonObject.getString("uid"));
                feedback.put("username",jsonObject.getString("username"));
                feedback.put("profile_photo",jsonObject.getString("profile_photo"));
                feedback.put("phone_model",jsonObject.getString("phone_model"));
                feedback.put("description",jsonObject.getString("description"));
                feedback.put("contact",jsonObject.getString("contact"));
                feedback.put("add_time",jsonObject.getString("add_time"));
                feedback.put("progress",jsonObject.getString("progress"));
                feedback.put("img_path",jsonObject.getString("img_path"));
                feedback.put("what",jsonObject.getString("what"));
                feedbackList.add(feedback);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return feedbackList;
    }

    public HashMap<String,Object>getcount(String jsonStr){
        HashMap<String,Object> word = new HashMap<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            JSONObject jsonObject = (JSONObject) jsonArray.opt(0);
            word.put("sum",jsonObject.getString("sum"));
            word.put("prof_count",jsonObject.getString("prof_count"));
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return word;
    }

    /*
    stopping using from 2021/1/15
    public ArrayList<HashMap<String,Object>> collectData(String jsonStr){
        ArrayList<HashMap<String,Object>> words = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            for(int i=0;i<jsonArray.length();i++){
                HashMap<String,Object> word = new HashMap<>();
                JSONObject jsonObject = (JSONObject) jsonArray.opt(i);
                word.put("cid",jsonObject.getString("cid"));
                word.put("gid",jsonObject.getString("gid"));
                word.put("wid",jsonObject.getString("wid"));
                word.put("word_en",jsonObject.getString("word_en").replaceAll("\n",""));
                word.put("word_ch",jsonObject.getString("word_ch").replaceAll("\n",""));
                word.put("correct_times",jsonObject.getString("correct_times"));
                word.put("error_times",jsonObject.getString("error_times"));
                word.put("last_date",jsonObject.getString("last_date"));
                word.put("review_date",jsonObject.getString("review_date"));
                word.put("dict_source",jsonObject.getString("dict_source"));
                words.add(word);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return words;
    }*/

    //XXX:取个好点的名字吧
    public ArrayList<DetailWord> collectData(String jsonStr){
        ArrayList<DetailWord> words = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            for(int i=0;i<jsonArray.length();i++){
                DetailWord word = new DetailWord();
                JSONObject jsonObject = (JSONObject) jsonArray.opt(i);
                word.setCid(jsonObject.getString("cid"));
                word.setGid(jsonObject.getString("gid"));
                word.setWid(jsonObject.getString("wid"));
                word.setWord_en(jsonObject.getString("word_en").replaceAll("\n",""));
                word.setWord_ch(jsonObject.getString("word_ch").replaceAll("\n",""));
                word.setCorrect_times(jsonObject.getString("correct_times"));
                word.setError_times(jsonObject.getString("error_times"));
                word.setLast_date(jsonObject.getString("last_date"));
                word.setReview_date(jsonObject.getString("review_date"));
                word.setDict_source(jsonObject.getString("dict_source"));
                words.add(word);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return words;
    }

    /*
    stopping using from 2021/1/15
    public List<HashMap<String,Object>> getSearchData(String jsonStr){
        List<HashMap<String,Object>> wordList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = (JSONObject)jsonArray.opt(i);
                HashMap<String,Object> word = new HashMap<>();
                word.put("wid",jsonObject.getString("wid"));
                word.put("word_en",jsonObject.getString("word_en").replaceAll("\n",""));
                word.put("word_ch",jsonObject.getString("word_ch").replaceAll("\n",""));
                word.put("dict_source",jsonObject.getString("dict_source"));
                word.put("cid","null".equals(jsonObject.getString("cid"))?-1:jsonObject.getString("cid"));
                word.put("gid","null".equals(jsonObject.getString("gid"))?-1:jsonObject.getString("gid"));
                word.put("correct_times","null".equals(jsonObject.getString("correct_times"))?0:jsonObject.getString("correct_times"));
                word.put("error_times","null".equals(jsonObject.getString("error_times"))?0:jsonObject.getString("error_times"));
                word.put("last_date","null".equals(jsonObject.getString("last_date"))?"1970-1-1":jsonObject.getString("last_date"));
                wordList.add(word);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return wordList;
    }*/

    /*
    stopping using from 2021/1/15
    public HashMap<String,Object> wordData(String jsonStr){
        HashMap<String,Object> word = new HashMap<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            JSONObject jsonObject = (JSONObject) jsonArray.opt(0);
            word.put("cid",jsonObject.getString("cid"));
            word.put("uid",jsonObject.getString("uid"));
            word.put("gid",jsonObject.getString("gid"));
            word.put("wid",jsonObject.getString("wid"));
            word.put("word_en",jsonObject.getString("word_en").replaceAll("\n",""));
            word.put("word_ch",jsonObject.getString("word_ch").replaceAll("\n",""));
            word.put("correct_times",jsonObject.getString("correct_times"));
            word.put("error_times",jsonObject.getString("error_times"));
            word.put("last_date",jsonObject.getString("last_date"));
            word.put("source",jsonObject.getString("source"));
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return word;
    }*/
    public DetailWord wordData(String jsonStr){
        DetailWord word = new DetailWord();
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            JSONObject jsonObject = (JSONObject) jsonArray.opt(0);
            word.setWid(jsonObject.getString("wid"));
            word.setWord_en(jsonObject.getString("word_en").replaceAll("\n",""));
            word.setWord_ch(jsonObject.getString("word_ch").replaceAll("\n",""));
            word.setCorrect_times(jsonObject.getString("correct_times"));
            word.setError_times(jsonObject.getString("error_times"));
            word.setLast_date(jsonObject.getString("last_date"));
            word.setReview_date(jsonObject.getString("review_date"));
            word.setCid(jsonObject.getString("cid"));
            word.setGid(jsonObject.getString("gid"));
            word.setDict_source(jsonObject.getString("dict_source"));
            word.setSource(jsonObject.getString("source"));
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return word;
    }

    public HashMap<String,Object> kelinsiwordData(String jsonStr){
        HashMap<String,Object> kelinsiword = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray json_items = new JSONArray(jsonObject.getString("items"));
            ArrayList<HashMap<String,Object>> items = new ArrayList<>();
            for(int i=0;i<json_items.length();i++){
                HashMap<String,Object> item = new HashMap<>();
                JSONObject json_item = (JSONObject)json_items.opt(i);
                item.put("explanation",json_item.getString("explanation"));
                item.put("gram",json_item.getString("gram"));
                item.put("iid",json_item.getString("iid"));
                item.put("label",json_item.getString("label"));
                item.put("number",json_item.getString("number"));
                item.put("word_ch",json_item.getString("word_ch"));
                ArrayList<String> en_tips = new ArrayList<>();
                JSONArray json_en_tips = new JSONArray(json_item.getString("en_tip"));
                for(int j=0;j<json_en_tips.length();j++){
                    en_tips.add(json_en_tips.getString(j));
                }
                item.put("en_tips",en_tips);
                ArrayList<HashMap<String,Object>> sentences = new ArrayList<>();
                JSONArray json_sentences = new JSONArray(json_item.getString("sentences"));
                for(int j=0;j<json_sentences.length();j++){
                    HashMap<String,Object> sentence = new HashMap<>();
                    JSONObject json_sentence = (JSONObject)json_sentences.opt(j);
                    sentence.put("sentence_ch",json_sentence.getString("sentence_ch"));
                    sentence.put("sentence_en",json_sentence.getString("sentence_en"));
                    sentence.put("sid",json_sentence.getString("sid"));
                    sentences.add(sentence);
                }
                item.put("sentences",sentences);
                items.add(item);
            }

            kelinsiword.put("items",items);
            kelinsiword.put("star",jsonObject.getString("star"));
            kelinsiword.put("wid",jsonObject.getString("wid"));
            kelinsiword.put("word_en",jsonObject.getString("word_en"));

        }catch (JSONException e) {
            e.printStackTrace();
        }
        return kelinsiword;
    }


    public ArrayList<HashMap<String,Object>> exampleData(String jsonStr){
        ArrayList<HashMap<String,Object>> exampleList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = (JSONObject)jsonArray.opt(i);
                HashMap<String,Object> example = new HashMap<>();
                example.put("eid",jsonObject.getString("eid"));
                String word_en,E_sentence,C_translate;
                word_en = jsonObject.getString("word_en").replaceAll("\\\\n","\\\n");
                E_sentence = jsonObject.getString("E_sentence").replaceAll("\\\\n","\\\n");
                C_translate = jsonObject.getString("C_translate").replaceAll("\\\\n","\\\n");
                if(word_en.charAt(word_en.length()-1) == '\n'){
                    word_en = word_en.substring(0,word_en.length()-1);
                }
                if(E_sentence.charAt(E_sentence.length()-1) == '\n'){
                    E_sentence = E_sentence.substring(0,E_sentence.length()-1);
                }
                if(C_translate.charAt(C_translate.length()-1) == '\n'){
                    C_translate = C_translate.substring(0,C_translate.length()-1);
                }
                example.put("word_en",word_en);
                example.put("E_sentence",E_sentence);
                example.put("C_translate",C_translate);
                example.put("source",jsonObject.getString("source"));
                exampleList.add(example);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return exampleList;
    }

    /*
    stopping using from 2021/1/15
    public List<HashMap<String,Object>> reciteData(String jsonStr){
        List<HashMap<String,Object>> reciteList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = (JSONObject)jsonArray.opt(i);
                HashMap<String,Object> word = new HashMap<>();
                word.put("cid",jsonObject.getString("cid"));
                word.put("wid",jsonObject.getString("wid"));
                word.put("word_en",jsonObject.getString("word_en").replaceAll("\n",""));
                word.put("word_ch",jsonObject.getString("word_ch").replaceAll("\n",""));
                word.put("correct_times",jsonObject.getString("correct_times"));
                word.put("error_times",jsonObject.getString("error_times"));
                word.put("last_date",jsonObject.getString("last_date"));
                word.put("review_date",jsonObject.getString("review_date"));
                word.put("dict_source",jsonObject.getString("dict_source"));
                word.put("today_correct_times",0);
                reciteList.add(word);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return reciteList;
    }*/

    public HashMap<String,Object> userData(String jsonStr){
        HashMap<String,Object> word = new HashMap<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            JSONObject jsonObject = (JSONObject) jsonArray.opt(0);
            word.put("uid",jsonObject.getString("uid"));
            word.put("username",jsonObject.getString("username"));
            word.put("password",jsonObject.getString("pwd"));
            word.put("profile_photo",jsonObject.getString("profile_photo"));
            word.put("telephone",jsonObject.getString("telephone"));
            word.put("email",jsonObject.getString("email"));
            word.put("motto",jsonObject.getString("motto"));
            word.put("last_login",jsonObject.getLong("last_login"));
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return word;
    }

    public HashMap<String,Object> userSetting(String jsonStr){
        HashMap<String,Object> word = new HashMap<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            JSONObject jsonObject = (JSONObject) jsonArray.opt(0);
            word.put("uid",jsonObject.getString("uid"));
            word.put("recite_num",jsonObject.getString("recite_num"));
            word.put("recite_scope",jsonObject.getString("recite_scope"));
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return word;
    }

    public ArrayList<Integer> usetimeData(String jsonStr){
        ArrayList<Integer> usetime = new ArrayList<Integer>();
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = (JSONObject) jsonArray.opt(i);
                usetime.add(Integer.valueOf(jsonObject.get("utime").toString()));
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return usetime;
    }

    /*
    stopping using from 2021/1/15
    public ArrayList<Integer> return_id(String jsonstr){
        ArrayList<Integer> id_list = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(jsonstr);
            for(int i=0;i<jsonArray.length();i++){
                id_list.add(Integer.valueOf(jsonArray.opt(i).toString()));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return id_list;
    }*/

    /*
    stopping using from 2021/1/15
    public List<Map<String, Object>>  getSpanish(String jsonStr) {
        wordList = new ArrayList<Map<String, Object>>();//保存商家数据的list容器对象
        Map<String, Object> map = new HashMap<String, Object>();
        List<String> shitai = new ArrayList<String>();
        shitai.add("陈述式-现在时");
        shitai.add("虚拟式-现在时");
        shitai.add("陈述式-现在完成时");
        shitai.add("虚拟式-现在完成时");
        shitai.add("陈述式-将来完成时");
        shitai.add("虚拟式-将来完成时");
        shitai.add("陈述式-将来时");
        shitai.add("虚拟式-将来时");
        shitai.add("陈述式-过去未完成时");
        shitai.add("虚拟式-过去未完成时A");
        shitai.add("陈述式-简单过去时");
        shitai.add("虚拟式-过去未完成时B");
        shitai.add("陈述式-过去完成时");
        shitai.add("虚拟式-过去完成时A");
        shitai.add("陈述式-先过去时");
        shitai.add("虚拟式-过去完成时B");
        shitai.add("简单条件式");
        shitai.add("复合条件式");
        shitai.add("命令式-肯定时");
        shitai.add("命令式-否定时");
        try {
            JSONArray  jsonArray=new JSONArray(jsonStr);
            for(int i=0;i<jsonArray.length();i++)
            {
                //jsonArray.length()获取json中数组元素的个数
                map = new HashMap<String, Object>();
                JSONObject jsonObject = (JSONObject) jsonArray.opt(i);//获取数组中第i个数组元素
                String word = jsonObject.getString("word");
                String va_b = jsonObject.getString("variable bit");
                JSONObject variable_bit = new JSONObject(va_b);
//                Log.i("variable_bit",variable_bit.toString());
                Map<String, Object> v_bit = new HashMap<String, Object>();
                for(int j=0;j<shitai.size();j++){
                    String st = variable_bit.getString(shitai.get(j));
                    Map<String, Object> six = new HashMap<String, Object>();
                    JSONObject va_b2 = new JSONObject(st);
                    six.put("yo",va_b2.get("yo"));
                    six.put("tú",va_b2.get("tú"));
                    six.put("él",va_b2.get("él"));
                    six.put("nos.",va_b2.get("nos."));
                    six.put("vos.",va_b2.get("vos."));
                    six.put("ellos.",va_b2.get("ellos."));
                    v_bit.put(shitai.get(j),six);
                }
                map.put("word",word);
                map.put("va_b",v_bit);
                wordList.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return wordList;
    }*/
}