package com.immortalmin.www.word;

/**
 * 单词类
 */
public class Word {
    private String wid;//单词的id
    private String word_en;//英文单词
    private String word_ch;//中文释义

    public Word(){}

    public Word(String wid, String word_en, String word_ch) {
        this.wid = wid;
        this.word_en = word_en;
        this.word_ch = word_ch;
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

    public String getWord_ch() {
        return word_ch;
    }

    public void setWord_ch(String word_ch) {
        this.word_ch = word_ch;
    }

    @Override
    public String toString() {
        return "Word{" +
                "wid='" + wid + '\'' +
                ", word_en='" + word_en + '\'' +
                ", word_ch='" + word_ch + '\'' +
                '}';
    }
}
