package com.immortalmin.www.word;

public class UsageTime {
    private String id;
    private String udate;
    private int utime;

    public UsageTime() {}

    public UsageTime(String udate, int utime) {
        this.udate = udate;
        this.utime = utime;
    }

    public String getUdate() {
        return udate;
    }

    public void setUdate(String udate) {
        this.udate = udate;
    }

    public int getUtime() {
        return utime;
    }

    public void setUtime(int utime) {
        this.utime = utime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "UsageTime{" +
                "id='" + id + '\'' +
                ", udate='" + udate + '\'' +
                ", utime=" + utime +
                '}';
    }
}
