package com.immortalmin.www.word;

/**
 * 所有例句的父类
 */
public class ExampleSentence {
    private String sentence_en;//英文例句
    private String sentence_ch;//中文翻译

    public ExampleSentence(){}

    public ExampleSentence(String sentence_en, String sentence_ch) {
        this.sentence_en = sentence_en;
        this.sentence_ch = sentence_ch;
    }

    public String getSentence_en() {
        return sentence_en;
    }

    public void setSentence_en(String sentence_en) {
        this.sentence_en = sentence_en;
    }

    public String getSentence_ch() {
        return sentence_ch;
    }

    public void setSentence_ch(String sentence_ch) {
        this.sentence_ch = sentence_ch;
    }

    @Override
    public String toString() {
        return "ExampleSentence{" +
                "sentence_en='" + sentence_en + '\'' +
                ", sentence_ch='" + sentence_ch + '\'' +
                '}';
    }
}
