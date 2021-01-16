package com.immortalmin.www.word;

/**
 * 恋练不忘中的例句以及由用户添加的例句类
 */
public class OtherSentence extends ExampleSentence{
    private String eid;//例句id
    private String wid;//对应单词的id
    private String kid;//对应柯林斯词典中单词的id
    private String word_meaning;//单词在例句中的释义
    private String source;//添加该例句的用户id

    public OtherSentence(){}

    public OtherSentence( String eid, String wid, String kid, String word_meaning,String sentence_en, String sentence_ch, String source) {
        super(sentence_en, sentence_ch);
        this.eid = eid;
        this.wid = (wid==null?"null":wid);
        this.kid = (kid==null?"null":kid);
        this.word_meaning = word_meaning;
        this.source = source;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getWid() {
        return wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }

    public String getKid() {
        return kid;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }

    public String getWord_meaning() {
        return word_meaning;
    }

    public void setWord_meaning(String word_meaning) {
        this.word_meaning = word_meaning;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "OtherSentence{" +
                "eid='" + eid + '\'' +
                ", sentence_en='" + getSentence_en() + '\'' +
                ", sentence_ch='" + getSentence_ch() + '\'' +
                ", wid='" + wid + '\'' +
                ", kid='" + kid + '\'' +
                ", word_meaning='" + word_meaning + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
