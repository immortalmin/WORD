package com.immortalmin.www.word;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 将JSON字符串转换成相应数据的工具类
 */
public class JsonRe {

    /**
     * 获取用户反馈的数据
     */
    ArrayList<HashMap<String,Object>> feedbackData(String jsonStr){
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

//    2021/3/14
//    /**
//     * 获取用户收藏的单词数和掌握的单词数
//     */
//    HashMap<String,Object> getCount(String jsonStr){
//        HashMap<String,Object> word = new HashMap<>();
//        try {
//            JSONArray jsonArray = new JSONArray(jsonStr);
//            JSONObject jsonObject = (JSONObject) jsonArray.opt(0);
//            word.put("sum",jsonObject.getString("sum"));
//            word.put("prof_count",jsonObject.getString("prof_count"));
//        }catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return word;
//    }

    /**
     * 恋练有词及用户添加的单词
     */
    ArrayList<DetailWord> detailWordData(String jsonStr){
        ArrayList<DetailWord> words = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            for(int i=0;i<jsonArray.length();i++){
                DetailWord word = new DetailWord();
                JSONObject jsonObject = (JSONObject) jsonArray.opt(i);
                word.setCid(jsonObject.getString("cid"));
                word.setCollect(!("null".equals(word.getCid())||word.getCid()==null));
                word.setGid(jsonObject.getString("gid"));
                word.setWid(jsonObject.getString("wid"));
                word.setWord_en(jsonObject.getString("word_en").replaceAll("\n",""));
                word.setWord_ch(jsonObject.getString("word_ch").replaceAll("\n",""));
                word.setCorrect_times(jsonObject.getString("correct_times"));
                word.setError_times(jsonObject.getString("error_times"));
                word.setLast_date(jsonObject.getString("last_date"));
                word.setReview_date(jsonObject.getString("review_date"));
                word.setDict_source(jsonObject.getString("dict_source"));
                //比如在搜索界面以及历史记录不需要保存单词的来源；而在其他情况可能需要source。简单地说就是，有则保存
                if(jsonObject.has("source")) word.setSource(jsonObject.getString("source"));
                words.add(word);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return words;
    }

    /**
     * 恋练有词及用户添加的单词（包含单词来源）
     */
    DetailWord wordData(String jsonStr){
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
            word.setCollect(!("null".equals(word.getCid())||word.getCid()==null));
            word.setGid(jsonObject.getString("gid"));
            word.setDict_source(jsonObject.getString("dict_source"));
            word.setSource(jsonObject.getString("source"));
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return word;
    }

    /**
     * 柯林斯词典
     */
    KelinsiWord kelinsiWordData(String jsonStr){
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

    /**
     * 例句
     */
    ArrayList<OtherSentence> exampleData(String jsonStr){
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

    /**
     * 用户数据，联合user表和setting表
     */
    User userData(String jsonStr){
        User user = null;
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            if(!jsonArray.isNull(0)){
                JSONObject jsonObject = (JSONObject) jsonArray.opt(0);
                user = new User();
                user.setUid(jsonObject.getString("uid"));
                user.setOpen_id(jsonObject.getString("open_id"));
                user.setUsername(jsonObject.getString("username"));
                user.setPassword(jsonObject.getString("pwd"));
                user.setProfile_photo(jsonObject.getString("profile_photo"));
                user.setMotto(jsonObject.getString("motto"));
                user.setEmail(jsonObject.getString("email"));
                user.setTelephone(jsonObject.getString("telephone"));
                user.setLogin_mode(Integer.parseInt(jsonObject.getString("login_mode")));
                user.setIgnore_version(Integer.parseInt(jsonObject.getString("ignore_version")));
                user.setLast_login(jsonObject.getLong("last_login"));
                user.setSign_in_type(jsonObject.getInt("sign_in_type"));
                user.setRecite_num(Integer.parseInt(jsonObject.getString("recite_num")));
                user.setRecite_scope(Integer.parseInt(jsonObject.getString("recite_scope")));
            }
        }catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return user;
    }


    /**
     * 用户使用时间
     */
    ArrayList<UsageTime> usageTimeData(String jsonStr){
        ArrayList<UsageTime> usageTimeList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = (JSONObject) jsonArray.opt(i);
                UsageTime usageTime = new UsageTime();
                usageTime.setUdate(jsonObject.get("udate").toString());
                usageTime.setUtime(Integer.valueOf(jsonObject.get("utime").toString()));
                usageTimeList.add(usageTime);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return usageTimeList;
    }

    /**
     * 每日背诵的数据
     */
    ArrayList<DailyRecitation> DailyRecitationData(String jsonStr){
        ArrayList<DailyRecitation> res = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = (JSONObject) jsonArray.opt(i);
                DailyRecitation dailyRecitation = new DailyRecitation();
                dailyRecitation.setRecord_date(jsonObject.getString("record_date"));
                dailyRecitation.setRecite_num(jsonObject.getInt("recite_num"));
                dailyRecitation.setReview_num(jsonObject.getInt("review_num"));
                dailyRecitation.setGrasp_num(jsonObject.getInt("grasp_num"));
                res.add(dailyRecitation);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    HashMap<String,String> versionData(String jsonStr){
        HashMap<String,String> data = new HashMap<>();
        try{
            JSONObject jsonObject = new JSONObject(jsonStr);
            data.put("vid",jsonObject.getString("vid"));
            data.put("version_code",jsonObject.getString("version_code"));
            data.put("version_name",jsonObject.getString("version_name"));
            data.put("update_url",jsonObject.getString("update_url"));
            data.put("is_force",jsonObject.getString("is_force"));
            data.put("description",jsonObject.getString("description"));
            data.put("update_date",jsonObject.getString("update_date"));
        }catch (JSONException e){
            e.printStackTrace();
        }
        return data;
    }
}