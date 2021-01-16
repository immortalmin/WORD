package com.immortalmin.www.word;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 柯林斯词典的item类
 */
public class KelinsiItem {
    private String iid;//id
    private String number;//item序号
    private String label;//标签
    private String word_ch;//单词的中文释义
    private String explanation;//单词的英文释义
    private String gram;//语法
    private String wid;//对应的单词id
    private ArrayList<String> en_tips = null;
    private ArrayList<KelinsiSentence> sentences = null;//例句

    public KelinsiItem(){}

    public KelinsiItem(String iid, String number, String label, String word_ch, String explanation, String gram, String wid, ArrayList<String> en_tips, ArrayList<KelinsiSentence> sentences) {
        this.iid = iid;
        this.number = number;
        this.label = label;
        this.word_ch = word_ch;
        this.explanation = explanation;
        this.gram = gram;
        this.wid = wid;
        this.en_tips = en_tips;
        this.sentences = sentences;
    }

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getWord_ch() {
        return word_ch;
    }

    public void setWord_ch(String word_ch) {
        this.word_ch = word_ch;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getGram() {
        return gram;
    }

    public void setGram(String gram) {
        this.gram = gram;
    }

    public String getWid() {
        return wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }

    public ArrayList<String> getEn_tips() {
        return en_tips;
    }

    public void setEn_tips(ArrayList<String> en_tips) {
        this.en_tips = en_tips;
    }

    public ArrayList<KelinsiSentence> getSentences() {
        return sentences;
    }

    public void setSentences(ArrayList<KelinsiSentence> sentences) {
        this.sentences = sentences;
    }

    @Override
    public String toString() {
        return "KelinsiItem{" +
                "iid='" + iid + '\'' +
                ", number='" + number + '\'' +
                ", label='" + label + '\'' +
                ", word_ch='" + word_ch + '\'' +
                ", explanation='" + explanation + '\'' +
                ", gram='" + gram + '\'' +
                ", wid='" + wid + '\'' +
                ", en_tips=" + en_tips +
                ", kelinsiSentences=" + sentences +
                '}';
    }
}
