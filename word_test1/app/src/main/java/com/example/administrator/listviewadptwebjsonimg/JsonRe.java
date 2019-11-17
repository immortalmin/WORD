package com.example.administrator.listviewadptwebjsonimg;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonRe {

    List<Map<String, Object>> wordInfoList;// 定义List容器，节点类型是map
    List<Map<String, Object>> wordList;// 定义List容器，节点类型是map
//    String hostip="http://192.168.57.1/";
    String hostip="http://47.98.239.237/";
    //商家数据json数据(商家基本信息)转arraylist格式商家数据
    public List<Map<String, Object>>  getWordList(String jsonStr) {
        wordInfoList = new ArrayList<Map<String, Object>>();//保存商家数据的list容器对象
        List<Map<String, Object>> ExampleList;// 定义List容器，节点类型是map
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> example_map = new HashMap<String, Object>();
        try {
            JSONArray  jsonArray=new JSONArray(jsonStr);
            for(int i=0;i<jsonArray.length();i++)
            {
                //jsonArray.length()获取json中数组元素的个数
                map = new HashMap<String, Object>();
                JSONObject jsonObject = (JSONObject) jsonArray.opt(i);//获取数组中第i个数组元素
                String id = jsonObject.getString("id");
                String Word = jsonObject.getString("word");
                JSONObject  word=new JSONObject(Word);
                map.put("page", word.getString("page"));
                map.put("C_meaning", word.getString("C_meaning"));
                String translate = word.getString("translate");
                map.put("word_group", word.getString("word_group"));
                JSONArray Examples = new JSONArray(translate);
                ExampleList = new ArrayList<Map<String, Object>>();//保存例句
                for(int j=0;j<Examples.length();j++){
                    example_map = new HashMap<String, Object>();
                    JSONObject example = (JSONObject) Examples.opt(j);
                    String word_meaning = example.getString("word_meaning");
                    String E_sentence = example.getString("E_sentence");
                    String C_translate = example.getString("C_translate");
                    word_meaning = word_meaning.replaceAll("\\\\n","\\\n");
                    E_sentence = E_sentence.replaceAll("\\\\n","\\\n");
                    C_translate = C_translate.replaceAll("\\\\n","\\\n");
                    example_map.put("word_meaning",word_meaning);
                    example_map.put("E_sentence",E_sentence);
                    example_map.put("C_translate",C_translate);
                    ExampleList.add(example_map);
                }
                map.put("translate",ExampleList);
                wordInfoList.add(map);// 将一个节点的数据（一条商家信息）添加到list容器中
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return wordInfoList;
    }
    public List<Map<String, Object>>  getReciteList(String jsonStr) {
        wordList = new ArrayList<Map<String, Object>>();//保存商家数据的list容器对象
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            JSONArray  jsonArray=new JSONArray(jsonStr);
            for(int i=0;i<jsonArray.length();i++)
            {
                //jsonArray.length()获取json中数组元素的个数
                map = new HashMap<String, Object>();
                JSONObject jsonObject = (JSONObject) jsonArray.opt(i);//获取数组中第i个数组元素
                String id = jsonObject.getString("id");
                String word_group = jsonObject.getString("word_group");
                word_group = word_group.replaceAll("\n","");
                String C_meaning = jsonObject.getString("C_meaning");
                C_meaning = C_meaning.replaceAll("\n","");
                String correct_times = jsonObject.getString("correct_times");
                String error_times = jsonObject.getString("error_times");
                String prof_flag = jsonObject.getString("prof_flag");
                map.put("id",id);
                map.put("word_group",word_group);
                map.put("C_meaning",C_meaning);
                map.put("correct_times",correct_times);
                map.put("error_times",error_times);
                map.put("prof_flag",prof_flag);
                map.put("today_correct_times",0);
                wordList.add(map);// 将一个节点的数据（一条商家信息）添加到list容器中
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return wordList;
    }
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
    }
    public String get_amount(String jsonStr) {
        String amount="0";
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            JSONObject num = (JSONObject) jsonArray.opt(0);
            amount = num.getString("count(*)");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return amount;
    }
}