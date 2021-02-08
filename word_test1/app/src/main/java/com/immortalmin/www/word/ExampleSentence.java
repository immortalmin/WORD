package com.immortalmin.www.word;

/**
 * 所有例句的父类
 */
public class ExampleSentence {
    private String sentence_en;//英文例句
    private String sentence_ch;//中文翻译

    ExampleSentence(){}

    ExampleSentence(String sentence_en, String sentence_ch) {
        this.sentence_en = sentence_en;
        this.sentence_ch = sentence_ch;
    }

    String getSentence_en() {
        return sentence_en;
    }

    void setSentence_en(String sentence_en) {
        this.sentence_en = sentence_en;
    }

    String getSentence_ch() {
        return sentence_ch;
    }

    void setSentence_ch(String sentence_ch) {
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
