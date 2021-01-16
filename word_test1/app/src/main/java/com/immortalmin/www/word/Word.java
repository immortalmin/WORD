package com.immortalmin.www.word;

/**
 * 单词类
 */
public class Word {
    private String wid;//单词的id
    private String word_en;//英文单词

    public Word(){}

    public Word(String wid, String word_en) {
        this.wid = wid;
        this.word_en = word_en;
    }

    public String getWid() {
        return wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }

    public String getWord_en() {
        return word_en;
    }

    public void setWord_en(String word_en) {
        this.word_en = word_en;
    }

    @Override
    public String toString() {
        return "Word{" +
                "wid='" + wid + '\'' +
                ", word_en='" + word_en + '\'' +
                '}';
    }
}
