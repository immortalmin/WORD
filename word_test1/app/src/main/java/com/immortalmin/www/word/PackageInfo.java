package com.immortalmin.www.word;


/**
 * 统计数据---记录每个应用的包名，使用时长和使用次数
 * <p>
 *
 */

public class PackageInfo {
    private int mUsedCount;
    private long mUsedTime;
    private String mPackageName;
    private String mAppName;

    public PackageInfo(int mUsedCount, long mUsedTime, String mPackageName,String appName) {
        this.mUsedCount = mUsedCount;
        this.mUsedTime = mUsedTime;
        this.mPackageName = mPackageName;
        this.mAppName=appName;
    }

    public void addCount() {
        mUsedCount++;
    }

    public int getmUsedCount() {
        return mUsedCount;
    }

    public void setmUsedCount(int mUsedCount) {
        this.mUsedCount = mUsedCount;
    }

    public long getmUsedTime() {
        return mUsedTime;
    }

    public void setmUsedTime(long mUsedTime) {
        this.mUsedTime = mUsedTime;
    }

    public String getmPackageName() {
        return mPackageName;
    }

    public void setmPackageName(String mPackageName) {
        this.mPackageName = mPackageName;
    }

    public String getmAppName() {
        return mAppName;
    }

    public void setmAppName(String mAppName) {
        this.mAppName = mAppName;
    }

    @Override
    public boolean equals(Object o) {
        //return super.equals(o);
        if (o == null) return false;
        if (this == o) return true;
        PackageInfo standardDetail = (PackageInfo) o;
        if (standardDetail.getmPackageName().equals(this.mPackageName)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        //return super.hashCode();
        return (mPackageName + mUsedTime).hashCode();
    }

    @Override
    public String toString() {
        return "PackageInfo{" +
                "mUsedCount=" + mUsedCount +
                ", mUsedTime=" + mUsedTime +
                ", mPackageName='" + mPackageName + '\'' +
                ", mAppName='" + mAppName + '\'' +
                '}';
    }
}