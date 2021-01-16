package com.immortalmin.www.word;

/**
 * 带有数据记录的单词
 */
public class DetailWord extends Word{

    private String word_ch;
    private int correct_times;
    private int error_times;
    private String last_date;
    private String review_date;
    private String hid;
    private String cid;
    private String gid;
    private String dict_source;
    private String source;
    private int today_correct_times = 0;

    public DetailWord(){}

    /**
     * 搜索框、收藏
     * @param wid
     * @param word_en
     * @param word_ch
     * @param correct_times
     * @param error_times
     * @param last_date
     * @param review_date
     * @param cid
     * @param gid
     * @param dict_source
     */
    public DetailWord(String wid, String word_en, String word_ch, String correct_times, String error_times, String last_date, String review_date, String cid, String gid, String dict_source) {
        super(wid, word_en);
        this.word_ch = word_ch;
        this.correct_times = "null".equals(correct_times)?0:Integer.parseInt(correct_times);
        this.error_times =  "null".equals(error_times)?0:Integer.parseInt(error_times);
        this.last_date = "null".equals(last_date)?"1970-01-01":last_date;
        this.review_date = "null".equals(review_date)?"1970-01-01":review_date;
        this.cid = cid;
        this.gid = gid;
        this.dict_source = dict_source;
    }

    /**
     * wordData
     * @param wid
     * @param word_en
     * @param word_ch
     * @param correct_times
     * @param error_times
     * @param last_date
     * @param review_date
     * @param cid
     * @param gid
     * @param dict_source
     * @param source
     */
    public DetailWord(String wid, String word_en, String word_ch, String correct_times, String error_times, String last_date, String review_date, String cid, String gid, String dict_source, String source) {
        super(wid, word_en);
        this.word_ch = word_ch;
        this.correct_times = "null".equals(correct_times)?0:Integer.parseInt(correct_times);
        this.error_times =  "null".equals(error_times)?0:Integer.parseInt(error_times);
        this.last_date = "null".equals(last_date)?"1970-01-01":last_date;
        this.review_date = "null".equals(review_date)?"1970-01-01":review_date;
        this.cid = cid;
        this.gid = gid;
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

    public String getLast_date() {
        return last_date;
    }

    public void setLast_date(String last_date) {
        this.last_date = last_date;
    }

    public String getReview_date() {
        return review_date;
    }

    public void setReview_date(String review_date) {
        this.review_date = review_date;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getHid() {
        return hid;
    }

    public void setHid(String hid) {
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

    public int getToday_correct_times() {
        return today_correct_times;
    }

    public void setToday_correct_times(int today_correct_times) {
        this.today_correct_times = today_correct_times;
    }
    public void setToday_correct_times(String today_correct_times) {
        this.today_correct_times = Integer.parseInt(today_correct_times);
    }


    @Override
    public String toString() {
        return "CollectWord{" +
                "wid=" + getWid() +
                ", word_en=" + getWord_en() +
                ", word_ch=" + word_ch +
                ", correct_times=" + correct_times +
                ", error_times=" + error_times +
                ", last_date='" + last_date + '\'' +
                ", review_date='" + review_date + '\'' +
                ", cid='" + cid + '\'' +
                ", gid='" + gid + '\'' +
                ", hid='" + hid + '\'' +
                ", dict_source='" + dict_source + '\'' +
                ", source='" + source + '\'' +
                ", today_correct_times=" + today_correct_times +
                '}';
    }
}
