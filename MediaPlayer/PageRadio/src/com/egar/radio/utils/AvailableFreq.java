package com.egar.radio.utils;

public class AvailableFreq {
    private  static final String TAG = "AvailableFreq";
    private  int mFreq;//频率值    可用电台列表频率控件setText时使用
    private int position;//此频率在可用电台列表中的位置
    private  String mNickName;//频率昵称
    private  boolean mIsCurrentFreq;//是否是当前频率
    private  boolean mIsCollected; //是否已被收藏
    private  int mBand;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    public AvailableFreq() {
    }

    public String getNickName() {
        return mNickName;
    }

    public void setNickName(String nickName) {
        this.mNickName = nickName;
    }

    public int getBand() {
        return mBand;
    }

    public void setBand(int band) {
        this.mBand = band;
    }

    public int getFreq() {
        return mFreq;
    }

    public void setFreq(int freq) {
        this.mFreq = freq;
    }

    public boolean isCurrentFreq() {
        return mIsCurrentFreq;
    }

    public void setIsCurrentFreq(boolean isCurrentFreq) {
        this.mIsCurrentFreq = isCurrentFreq;
    }

    public boolean getIsCollected() {
        return mIsCollected;
    }

    public void setIsCollected(boolean isCollected) {
        this.mIsCollected = isCollected;
    }


    @Override
    public String toString() {
        return "AvailableFreq{" +
                "mFreq=" + mFreq +
                ", position=" + position +
                ", mNickName='" + mNickName + '\'' +
                ", mIsCurrentFreq=" + mIsCurrentFreq +
                ", mIsCollected=" + mIsCollected +
                ", mBand=" + mBand +
                '}';
    }
}
