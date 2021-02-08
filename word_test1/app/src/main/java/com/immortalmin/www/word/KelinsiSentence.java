package com.immortalmin.www.word;

/**
 * 柯林斯词典中的例句类
 */
public class KelinsiSentence extends ExampleSentence {
    private String sid;//例句id
    private String iid;//对应item的id

    KelinsiSentence(){}

    public KelinsiSentence(String sid,String iid,String sentence_en, String sentence_ch) {
        super(sentence_en, sentence_ch);
        this.sid = sid;
        this.iid = iid;
    }

    public String getSid() {
        return sid;
    }

    void setSid(String sid) {
        this.sid = sid;
    }

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    @Override
    public String toString() {
        return "KelinsiSentence{" +
                "sid='" + sid + '\'' +
                ", iid='" + iid + '\'' +
                ", sentence_en='" + getSentence_en() + '\'' +
                ", sentence_ch='" + getSentence_ch() + '\'' +
                '}';
    }
}
