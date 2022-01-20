package com.immortalmin.www.word;

import java.util.Objects;

/**
 * 带有数据记录的单词
 */
public class DetailWord extends Word{

    private String word_ch;
    private int correct_times = 0;
    private int error_times = 0;
    private String last_date = "2000-01-01";
    private String review_date = "2000-01-01";
    private String hid;
    private String cid;
    private String gid;
    private String dict_source;
    private String source;
    private boolean isCollect;
    private int today_correct_times = 0;
    private boolean isCached=false;

    public DetailWord(){}

    /**
     * 搜索框、收藏
     */
    public DetailWord(String wid, String word_en, String word_ch, String correct_times, String error_times, String last_date, String review_date, String cid, String gid, String dict_source) {
        super(wid, word_en);
        this.word_ch = word_ch;
        this.correct_times = "null".equals(correct_times)?0:Integer.parseInt(correct_times);
        this.error_times =  "null".equals(error_times)?0:Integer.parseInt(error_times);
        this.last_date = "null".equals(last_date)?"2000-01-01":last_date;
        this.review_date = "null".equals(review_date)?"2000-01-01":review_date;
        this.cid = cid;
        this.gid = "null".equals(gid)?"0":gid;
        this.dict_source = dict_source;
    }

    /**
     * wordData
     */
    public DetailWord(String wid, String word_en, String word_ch, String correct_times, String error_times, String last_date, String review_date, String cid, String gid, String dict_source, String source) {
        super(wid, word_en);
        this.word_ch = word_ch;
        this.correct_times = "null".equals(correct_times)?0:Integer.parseInt(correct_times);
        this.error_times =  "null".equals(error_times)?0:Integer.parseInt(error_times);
        this.last_date = "null".equals(last_date)?"2000-01-01":last_date;
        this.review_date = "null".equals(review_date)?"2000-01-01":review_date;
        this.cid = cid;
        this.gid = "null".equals(gid)?"0":gid;
        this.dict_source = dict_source;
        this.source = source;
    }

    public String getWord_ch() {
        return word_ch;
    }

    public void setWord_ch(String word_ch) {
        this.word_ch = word_ch;
    }

    public int getCorrect_times() {
        return correct_times;
    }

    public void setCorrect_times(int correct_times) {
        this.correct_times = correct_times;
    }

    public void setCorrect_times(String correct_times) {
        this.correct_times = "null".equals(correct_times)?0:Integer.parseInt(correct_times);
    }

    public int getError_times() {
        return error_times;
    }

    public void setError_times(int error_times) {
        this.error_times = error_times;
    }

    public void setError_times(String error_times) {
        this.error_times =  "null".equals(error_times)?0:Integer.parseInt(error_times);
    }

    String getLast_date() {
        return last_date;
    }

    void setLast_date(String last_date) {
        this.last_date = "null".equals(last_date)?"2000-01-01":last_date;;
    }

    String getReview_date() {
        return review_date;
    }

    void setReview_date(String review_date) {
        this.review_date = "null".equals(review_date)?"2000-01-01":review_date;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    String getGid() {
        return gid;
    }

    void setGid(String gid) {
        this.gid = "null".equals(gid)?"0":gid;
    }

    String getHid() {
        return hid;
    }

    void setHid(String hid) {
        this.hid = hid;
    }

    public String getDict_source() {
        return dict_source;
    }

    public void setDict_source(String dict_source) {
        this.dict_source = dict_source;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    int getToday_correct_times() {
        return today_correct_times;
    }

    void setToday_correct_times(int today_correct_times) {
        this.today_correct_times = today_correct_times;
    }
    public void setToday_correct_times(String today_correct_times) {
        this.today_correct_times = Integer.parseInt(today_correct_times);
    }

    boolean isCollect() {
        return isCollect;
    }

    void setCollect(boolean collect) {
        isCollect = collect;
    }

    public boolean isCached() {
        return isCached;
    }

    public void setCached(boolean cached) {
        isCached = cached;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetailWord that = (DetailWord) o;
        return correct_times == that.correct_times &&
                error_times == that.error_times &&
                isCollect == that.isCollect &&
                today_correct_times == that.today_correct_times &&
                isCached == that.isCached &&
                Objects.equals(word_ch, that.word_ch) &&
                Objects.equals(last_date, that.last_date) &&
                Objects.equals(review_date, that.review_date) &&
                Objects.equals(hid, that.hid) &&
                Objects.equals(cid, that.cid) &&
                Objects.equals(gid, that.gid) &&
                Objects.equals(dict_source, that.dict_source) &&
                Objects.equals(source, that.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word_ch, correct_times, error_times, last_date, review_date, hid, cid, gid, dict_source, source, isCollect, today_correct_times, isCached);
    }

    @Override
    public String toString() {
        return "DetailWord{" +
                "word_ch='" + word_ch + '\'' +
                ", correct_times=" + correct_times +
                ", error_times=" + error_times +
                ", last_date='" + last_date + '\'' +
                ", review_date='" + review_date + '\'' +
                ", hid='" + hid + '\'' +
                ", cid='" + cid + '\'' +
                ", gid='" + gid + '\'' +
                ", dict_source='" + dict_source + '\'' +
                ", source='" + source + '\'' +
                ", isCollect=" + isCollect +
                ", today_correct_times=" + today_correct_times +
                ", isCached=" + isCached +
                '}';
    }
}
