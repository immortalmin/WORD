package com.immortalmin.www.word;

/**
 * 恋练不忘中的单词和用户添加的单词
 */
public class OtherWord extends Word {
    private String source;//添加该单词的用户id

    public OtherWord(){}

    public OtherWord(String wid, String word_en, String word_ch, String source) {
        super(wid, word_en, word_ch);
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "OtherWord{" +
                "wid='" + getWid() + '\'' +
                ", word_en='" + getWord_en() + '\'' +
                ", word_ch='" + getWord_ch() + '\'' +
                ", source='" + getSource() + '\'' +
                '}';
    }
}
