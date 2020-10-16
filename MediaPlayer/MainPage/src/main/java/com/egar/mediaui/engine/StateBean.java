package com.egar.mediaui.engine;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/9/23 19:26
 * @see {@link }
 */
public class StateBean  implements Parcelable {
    private String mName;// radio,usbMusic,usbVieo,usbImage,btMusic
    private int mState; // playing 1; pause 0;
    private String mInfo;

    public StateBean() {
    }
    public StateBean(String name,String info,int state) {
        this.mName = name;
        this.mInfo = info;
        this.mState = state;
    }



    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        this.mState = state;
    }

    public String getInfo() {
        return mInfo;
    }

    public void setInfo(String info) {
        this.mInfo = info;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mState);
        parcel.writeString(mName);
        parcel.writeString(mInfo);
    }
    // 添加一个静态成员,名为CREATOR,该对象实现了Parcelable.Creator接口
    public static final Creator<StateBean> CREATOR
            = new Creator<StateBean>() {
        @Override
        public StateBean createFromParcel(Parcel source) {
            // 从Parcel中读取数据，返回Person对象
            return new StateBean(source.readString()
                    , source.readString()
                    , source.readInt());
        }
        @Override
        public StateBean[] newArray(int size) {
            return new StateBean[size];
        }
    };

}
