package com.egar.mediaui.model;


import com.egar.btmusic.fragment.BtMusicMainFragment;
import com.egar.mediaui.fragment.BaseLazyLoadFragment;
import com.egar.mediaui.fragment.BaseUsbFragment;
import com.egar.radio.fragment.RadioMainFragment;
import com.egar.usbimage.fragment.UsbImageMainFragment;
import com.egar.usbmusic.fragment.UsbMusicMainFragment;
import com.egar.usbvideo.fragment.UsbVideoMainFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/9/23 14:16
 * @see {@link }
 */
public class FragmentFactory {
    private List<BaseLazyLoadFragment> mListFrags;
    private List<BaseLazyLoadFragment> mListUsbFrags = null;
    private BaseLazyLoadFragment fragRadio;
    private BaseLazyLoadFragment fragUsb;
    private BaseLazyLoadFragment fragBTMusic;

    private BaseLazyLoadFragment fragUsbMusic;
    private BaseLazyLoadFragment fragUsbVideo;
    private BaseLazyLoadFragment fragUsbImage;

    public List<BaseLazyLoadFragment> loadFragments() {
        if (mListFrags == null) {
            mListFrags = new ArrayList<>();
        } else {
            mListFrags.clear();
        }
        if (fragRadio == null) {
            fragRadio = new RadioMainFragment();
        }
        mListFrags.add(fragRadio);
        if (fragUsb == null) {
            fragUsb = new BaseUsbFragment();
        }
        mListFrags.add(fragUsb);
        if (fragBTMusic == null) {
            fragBTMusic = new BtMusicMainFragment();
        }
        mListFrags.add(fragBTMusic);
        return mListFrags;
    }

    public void cleanFragment() {
        if(mListFrags !=null){
            mListFrags.clear();
        }
       if(mListUsbFrags !=null){
           mListUsbFrags.clear();
       }
        fragRadio = null;
        fragUsb = null;
        fragBTMusic = null;
        fragUsbMusic = null;
        fragUsbVideo = null;
        fragUsbImage = null;
    }

    /**
     * 根据position 获取对应页面
     *
     * @param position
     *
     * @return
     */
    public BaseLazyLoadFragment getMainCurrentFragmet(int position) {
        if (mListFrags.size() > 0) {
            return mListFrags.get(position);
        }
        return null;
    }


    public List<BaseLazyLoadFragment> loadUsbFragments() {

        if (mListUsbFrags == null) {
            mListUsbFrags = new ArrayList<>();
        } else {
            mListUsbFrags.clear();
        }
        if (fragUsbMusic == null) {
            fragUsbMusic = new UsbMusicMainFragment();
        }
        mListUsbFrags.add(fragUsbMusic);

        if (fragUsbVideo == null) {
            fragUsbVideo = new UsbVideoMainFragment();
        }
        mListUsbFrags.add(fragUsbVideo);

        if (fragUsbImage == null) {
            fragUsbImage = new UsbImageMainFragment();
        }
        mListUsbFrags.add(fragUsbImage);


        return mListUsbFrags;
    }

    /**
     * 根据position 获取对应页面
     *
     * @param position
     *
     * @return
     */
    public BaseLazyLoadFragment getUsbCurrentFragmet(int position) {
        return mListUsbFrags.get(position);
    }
}
