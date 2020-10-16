package com.egar.radio.utils;

public class CollectionFreq {
    private  static final String TAG = "CollectionFreq";
    private  int mFreq;//频率值    收藏夹频率控件setText时使用
    private  String mNickName;//频率昵称
    private  int mListPosition;//收藏夹列表中位置    长按收藏夹按钮更新RadioService收藏夹数据源时使用
    private  int mPageIndex; //所属收藏夹下标    初始化收藏夹时定位此频率属于哪一页收藏夹时使用
    private  int mCollectionPosition;//在当前页收藏夹中位置   初始化收藏夹时定位此频率在当前页中哪一个控件时使用。
    private  int mBand;

    public CollectionFreq() {
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

    public int getListPosition() {
        return mListPosition;
    }

    public void setListPosition(int listPosition) {
        this.mListPosition = listPosition;
    }

    public int getPageIndex() {
        return mPageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.mPageIndex = pageIndex;
    }

    public int getCollectionPosition() {
        return mCollectionPosition;
    }

    public void setCollectionPosition(int collectionPosition) {
        this.mCollectionPosition = collectionPosition;
    }

    @Override
    public String toString() {
        return "CollectionFreq{" +
                "mFreq=" + mFreq +
                ", mNickName='" + mNickName + '\'' +
                ", mListPosition=" + mListPosition +
                ", mPageIndex=" + mPageIndex +
                ", mCollectionPosition=" + mCollectionPosition +
                ", mBand=" + mBand +
                '}';
    }
}
