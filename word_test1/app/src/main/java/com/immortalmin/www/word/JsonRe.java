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
                feedback.put("login_mode",jsonObject.getString("login_mode"));
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


    public ArrayList<DetailWord> detailWordData(String jsonStr){
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


    public KelinsiWord kelinsiWordData(String jsonStr){
        KelinsiWord kelinsiWord = new KelinsiWord();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray json_items = new JSONArray(jsonObject.getString("items"));
            ArrayList<KelinsiItem> items = new ArrayList<>();
            for(int i=0;i<json_items.length();i++){
                KelinsiItem item = new KelinsiItem();
                JSONObject json_item = (JSONObject)json_items.opt(i);
                item.setExplanation(json_item.getString("explanation"));
                item.setGram(json_item.getString("gram"));
                item.setIid(json_item.getString("iid"));
                item.setLabel(json_item.getString("label"));
                item.setNumber(json_item.getString("number"));
                item.setWord_ch(json_item.getString("word_ch"));
                ArrayList<String> en_tips = new ArrayList<>();
                JSONArray json_en_tips = new JSONArray(json_item.getString("en_tip"));
                for(int j=0;j<json_en_tips.length();j++){
                    en_tips.add(json_en_tips.getString(j));
                }
                item.setEn_tips(en_tips);
                ArrayList<KelinsiSentence> sentences = new ArrayList<>();
                JSONArray json_sentences = new JSONArray(json_item.getString("sentences"));
                for(int j=0;j<json_sentences.length();j++){
                    KelinsiSentence sentence = new KelinsiSentence();
                    JSONObject json_sentence = (JSONObject)json_sentences.opt(j);
                    sentence.setSentence_ch(json_sentence.getString("sentence_ch"));
                    sentence.setSentence_en(json_sentence.getString("sentence_en"));
                    sentence.setSid(json_sentence.getString("sid"));
                    sentences.add(sentence);
                }
                item.setSentences(sentences);
                items.add(item);
            }
            kelinsiWord.setItems(items);
            kelinsiWord.setStar(jsonObject.getString("star"));
            kelinsiWord.setWid(jsonObject.getString("wid"));
            kelinsiWord.setWord_en(jsonObject.getString("word_en"));

        }catch (JSONException e) {
            e.printStackTrace();
        }
        return kelinsiWord;
    }


    public ArrayList<OtherSentence> exampleData(String jsonStr){
        ArrayList<OtherSentence> sentences = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = (JSONObject)jsonArray.opt(i);
                OtherSentence sentence = new OtherSentence();
                sentence.setEid(jsonObject.getString("eid"));
                String word_meaning,sentence_en,sentence_ch;
                word_meaning = jsonObject.getString("word_en").replaceAll("\\\\n","\\\n");
                sentence_en = jsonObject.getString("E_sentence").replaceAll("\\\\n","\\\n");
                sentence_ch = jsonObject.getString("C_translate").replaceAll("\\\\n","\\\n");
                if(word_meaning.charAt(word_meaning.length()-1) == '\n'){
                    word_meaning = word_meaning.substring(0,word_meaning.length()-1);
                }
                if(sentence_en.charAt(sentence_en.length()-1) == '\n'){
                    sentence_en = sentence_en.substring(0,sentence_en.length()-1);
                }
                if(sentence_ch.charAt(sentence_ch.length()-1) == '\n'){
                    sentence_ch = sentence_ch.substring(0,sentence_ch.length()-1);
                }
                sentence.setWord_meaning(word_meaning);
                sentence.setSentence_en(sentence_en);
                sentence.setSentence_ch(sentence_ch);
                sentence.setSource(jsonObject.getString("source"));
                sentences.add(sentence);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return sentences;
    }

    User userData(String jsonStr){
        User user = null;
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            if(!jsonArray.isNull(0)){
                JSONObject jsonObject = (JSONObject) jsonArray.opt(0);
                user = new User();
                user.setLogin_mode(Integer.parseInt(jsonObject.getString("login_mode")));
                user.setUid(jsonObject.getString("uid"));
                user.setOpen_id(jsonObject.getString("open_id"));
                user.setUsername(jsonObject.getString("username"));
                user.setPassword(jsonObject.getString("pwd"));
                user.setProfile_photo(jsonObject.getString("profile_photo"));
                user.setTelephone(jsonObject.getString("telephone"));
                user.setEmail(jsonObject.getString("email"));
                user.setMotto(jsonObject.getString("motto"));
                user.setLast_login(jsonObject.getLong("last_login"));
            }
        }catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return user;
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