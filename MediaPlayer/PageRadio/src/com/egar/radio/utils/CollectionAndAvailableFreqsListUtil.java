package com.egar.radio.utils;

import android.util.Log;

import com.egar.manager.radio.Freq;
import com.egar.manager.radio.RadioManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectionAndAvailableFreqsListUtil {
    private static  final String TAG = "CollectionAndAvailableFreqsListUtil";

    private int mCollectFreqPageNumber;
    private ArrayList<CollectionFreq[]> mFmCollectionFreqList = new ArrayList<>();
    private ArrayList<CollectionFreq[]> mAmCollectionFreqList = new ArrayList<>();
    private ArrayList<AvailableFreq> mFmAvailableFreqList = new ArrayList<>();
    private ArrayList<AvailableFreq> mAmAvailableFreqList = new ArrayList<>();
    private ArrayList<AvailableFreq> mAdapterAvailableFreqList = new ArrayList<>();
    private static CollectionAndAvailableFreqsListUtil mCollectionAndAvailableFreqsListUtil;
    private int mFmCollectPageNumber = Values.FM_COLLECT_PAGE_NUMBER;
    private int mAmCollectPageNumber = Values.AM_COLLECT_PAGE_NUMBER;
    private Freq mCurrentFreq;
    private Set<OnAvailableListStatusChangedListener> mListenerList = new LinkedHashSet<>();



    public interface OnAvailableListStatusChangedListener {
        /**
         * 当可用电台列表中的电台被收藏时回调
         *
         */
        void OnAvailableListFavoriteStatusChanged();
    }

    public  void register(OnAvailableListStatusChangedListener listener) {
        if (listener!= null) {
            mListenerList.add(listener);
        }
    }

    public  void unregister(OnAvailableListStatusChangedListener listener) {
        if (listener != null) {
            mListenerList.remove(listener);
        }
    }


    public static CollectionAndAvailableFreqsListUtil getCollectionUtilInstance(){
        if(mCollectionAndAvailableFreqsListUtil == null){
            mCollectionAndAvailableFreqsListUtil = new CollectionAndAvailableFreqsListUtil();
        }
        return mCollectionAndAvailableFreqsListUtil;
    }

    public CollectionAndAvailableFreqsListUtil() {
    }


    public ArrayList<CollectionFreq[]> getCollectionFreqs(int band){
        if(band == RadioManager.RADIO_BAND_FM){
            return mFmCollectionFreqList;
        }else {
            return mAmCollectionFreqList;
        }
    }

    public ArrayList<CollectionFreq[]> setCollectionFreqs(int band , List<Freq> freqs){
        Log.d(TAG,"setCollectionFreqs()");
        refreshAvailableFreqsList(band,freqs);


        if(band == RadioManager.RADIO_BAND_FM){
            mFmCollectionFreqList.clear();
        }else {
            mAmCollectionFreqList.clear();
        }

        if(null != freqs){
            mCollectFreqPageNumber = (freqs.size()%Values.COLLECT_ITEM_NUMBER == 0) ? (freqs.size()/Values.COLLECT_ITEM_NUMBER):((freqs.size()/Values.COLLECT_ITEM_NUMBER)+1);

            for(int pageIndex = 0;pageIndex<mCollectFreqPageNumber;pageIndex++){
                CollectionFreq[] group = new CollectionFreq[Values.COLLECT_ITEM_NUMBER];
                for(int collectionPosition =0;collectionPosition<Values.COLLECT_ITEM_NUMBER;collectionPosition++){
                    CollectionFreq collectionFreq = new CollectionFreq();
                    collectionFreq.setListPosition(pageIndex*Values.COLLECT_ITEM_NUMBER+collectionPosition);
                    collectionFreq.setFreq(freqs.get(pageIndex*Values.COLLECT_ITEM_NUMBER+collectionPosition).getFreq());
                    collectionFreq.setNickName(freqs.get(pageIndex*Values.COLLECT_ITEM_NUMBER+collectionPosition).getNickname());
                    collectionFreq.setPageIndex(pageIndex);
                    collectionFreq.setBand(band);
                    collectionFreq.setCollectionPosition(collectionPosition);
                    group[collectionPosition] = collectionFreq;
                }
                if(band == RadioManager.RADIO_BAND_FM){
                    mFmCollectionFreqList.add(group);
                }else {
                    mAmCollectionFreqList.add(group);
                }
            }
            for(int i = 0; i < mFmCollectionFreqList.size(); i++){
           //     Log.d(TAG,"mFmCollectionFreqList.[" + i + "]:  " + Arrays.asList(mFmCollectionFreqList.get(i)));
            }

            for(int i = 0; i < mAmCollectionFreqList.size(); i++){
          //      Log.d(TAG,"mAmCollectionFreqList.[" + i + "]:  " + Arrays.asList(mAmCollectionFreqList.get(i)));
            }

        }

        notifyAvailableListUpdate();

        if(band == RadioManager.RADIO_BAND_FM){
            return  mFmCollectionFreqList;
        }else {
            return  mAmCollectionFreqList;
        }


    }

    private void notifyAvailableListUpdate() {
        for (OnAvailableListStatusChangedListener listener : mListenerList) {
            if (listener != null) {
                listener.OnAvailableListFavoriteStatusChanged();
            }
        }
    }

    private void refreshAvailableFreqsList(int band, List<Freq> collectedFreqsList) {
        Log.d(TAG,"refreshAvailableFreqsList()   band:  " + band +  "    /collectedFreqsList:   " + Arrays.asList(collectedFreqsList));
        List<Integer> maxList = new ArrayList<Integer>();
        List<Integer> minList = new ArrayList<Integer>();

        Map<String, Integer> map = new HashMap<String, Integer>(collectedFreqsList.size() + mFmAvailableFreqList.size());
        if(band == RadioManager.RADIO_BAND_FM){
       //     Log.d(TAG,"refreshAvailableFreqsList()0   mFmAvailableFreqList.size():  " + mFmAvailableFreqList.size() +  "    /collectedFreqsList.size():   " + collectedFreqsList.size());
            if(mFmAvailableFreqList.size() > collectedFreqsList.size()){
                if(mFmAvailableFreqList.size()!=0){
                    for (int i = 0 ; i < mFmAvailableFreqList.size(); i++){
                        int freq = mFmAvailableFreqList.get(i).getFreq();
                        maxList.add(Integer.valueOf(freq));
                    }
                }

                if(collectedFreqsList.size()!=0){
                    for(int j = 0 ; j < collectedFreqsList.size(); j++){
                        int freq = collectedFreqsList.get(j).getFreq();
                        minList.add(Integer.valueOf(freq));
                    }
                }
            }else {

                if(collectedFreqsList.size()!=0){
                    for (int i = 0 ; i < collectedFreqsList.size(); i++){
                        int freq = collectedFreqsList.get(i).getFreq();
                        maxList.add(Integer.valueOf(freq));
                    }
                }

                if(mFmAvailableFreqList.size()!=0){
                    for(int j = 0 ; j < mFmAvailableFreqList.size(); j++){
                        int freq = mFmAvailableFreqList.get(j).getFreq();
                        minList.add(Integer.valueOf(freq));
                    }
                }

            }

            for (Integer freq : maxList) {
                map.put(freq.toString(), 1);
            }

            if(mFmAvailableFreqList.size() > collectedFreqsList.size()){
                for (Integer freq : minList) {
                    Integer count = map.get(freq.toString());
                //    LogUtil.d(TAG," refreshAvailableFreqsList()1   count:   " + count );
                    if (count != null) {
                        mFmAvailableFreqList.get(maxList.indexOf(freq)).setIsCollected(true);
                        //continue;
                    }else {
                        mFmAvailableFreqList.get(maxList.indexOf(freq)).setIsCollected(false);
                    }
                //    LogUtil.d(TAG,"refreshAvailableFreqsList()2    mFmAvailableFreqList:   "   + Arrays.asList(mFmAvailableFreqList));
                }
            }else {
                for (Integer freq : minList) {
                    Integer count = map.get(freq.toString());
                 //   LogUtil.d(TAG," refreshAvailableFreqsList()3   count:   " + count  + "   /freq:   " + freq);
                    if (count != null) {
                        mFmAvailableFreqList.get(minList.indexOf(freq)).setIsCollected(true);
                        //continue;
                    }else {
                        mFmAvailableFreqList.get(minList.indexOf(freq)).setIsCollected(false);
                    }
                 //   LogUtil.d(TAG,"refreshAvailableFreqsList()4    mFmAvailableFreqList:   "   + Arrays.asList(mFmAvailableFreqList));
                }
            }

        }else  if(band == RadioManager.RADIO_BAND_AM){

            if(mAmAvailableFreqList.size() > collectedFreqsList.size()){
                if(mAmAvailableFreqList.size()!=0){
                    for (int i = 0 ; i < mAmAvailableFreqList.size(); i++){
                        int freq = mAmAvailableFreqList.get(i).getFreq();
                        maxList.add(Integer.valueOf(freq));
                    }
                }

                if(collectedFreqsList.size()!=0){
                    for(int j = 0 ; j < collectedFreqsList.size(); j++){
                        int freq = collectedFreqsList.get(j).getFreq();
                        minList.add(Integer.valueOf(freq));
                    }
                }
            }else {

                if(collectedFreqsList.size()!=0){
                    for (int i = 0 ; i < collectedFreqsList.size(); i++){
                        int freq = collectedFreqsList.get(i).getFreq();
                        maxList.add(Integer.valueOf(freq));
                    }
                }


                if(mAmAvailableFreqList.size()!=0){
                    for(int j = 0 ; j < mAmAvailableFreqList.size(); j++){
                        int freq = mAmAvailableFreqList.get(j).getFreq();
                        minList.add(Integer.valueOf(freq));
                    }
                }

            }

            for (Integer freq : maxList) {
                map.put(freq.toString(), 1);
            }

            if(mAmAvailableFreqList.size() > collectedFreqsList.size()){
                for (Integer freq : minList) {
                    Integer count = map.get(freq.toString());
                //    LogUtil.d(TAG," refreshAvailableFreqsList()5   count:   " + count );
                    if (count != null) {
                        mAmAvailableFreqList.get(maxList.indexOf(freq)).setIsCollected(true);
                        continue;
                    }
                 //   LogUtil.d(TAG,"refreshAvailableFreqsList()6    mFmAvailableFreqList:   "   + Arrays.asList(mAmAvailableFreqList));
                }
            }else {
                for (Integer freq : minList) {
                    Integer count = map.get(freq.toString());
                 //   LogUtil.d(TAG," refreshAvailableFreqsList()7   count:   " + count );
                    if (count != null) {
                        mAmAvailableFreqList.get(minList.indexOf(freq)).setIsCollected(true);
                        continue;
                    }
                 //   LogUtil.d(TAG,"refreshAvailableFreqsList()8    mFmAvailableFreqList:   "   + Arrays.asList(mAmAvailableFreqList));
                }
            }
        }
    }

    public  ArrayList<AvailableFreq> getAvailableFreqsList(int band){
        if(band == RadioManager.RADIO_BAND_FM){
            mAdapterAvailableFreqList.clear();
            mAdapterAvailableFreqList.addAll(mFmAvailableFreqList);
        }else {
            mAdapterAvailableFreqList.clear();
            mAdapterAvailableFreqList.addAll(mAmAvailableFreqList);
        }
        for(int i = 0; i<mAdapterAvailableFreqList.size();i++){
         //   Log.d(TAG,"getAvailableFreqsList()   availableFreqs.get(i):   " + mAdapterAvailableFreqList.get(i));
        }
        return  mAdapterAvailableFreqList;
    }


    public ArrayList<AvailableFreq> setAvailableFreqsList(int band , List<Freq> freqs){
     //   Log.d(TAG,"setAvailableFreqsList()   band:  " + band);
        if(band == RadioManager.RADIO_BAND_FM){
            mFmAvailableFreqList.clear();
      //      Log.d(TAG,"setAvailableFreqsList:    mFmCollectionFreqList.size():   " +  mFmAvailableFreqList.size());
        }else {
            mAmAvailableFreqList.clear();
         //   Log.d(TAG,"setAvailableFreqsList:    mAmCollectionFreqList.size():   " +  mAmAvailableFreqList.size());
        }

        if(null != freqs){
            //有时服务层上报的列表排列顺序错乱，需要重新排序
            freqs = listSort(freqs);

            for(int i = 0 ; i < freqs.size();i++){
                Freq freq = freqs.get(i);
                AvailableFreq availableFreq = new AvailableFreq();
                availableFreq.setFreq(freq.getFreq());
                availableFreq.setIsCollected(false);
                availableFreq.setBand(band);
                availableFreq.setPosition(i);

                if(!freq.getNickname().isEmpty()){
                    availableFreq.setNickName(freq.getNickname());
                }

                if(band == RadioManager.RADIO_BAND_FM){
                //    Log.d(TAG,"setAvailableFreqsList()  add fm  availableFreq:  " + availableFreq);
                    mFmAvailableFreqList.add(availableFreq);
                }else {
               //     Log.d(TAG,"setAvailableFreqsList()  add am  availableFreq:  " + availableFreq);
                    mAmAvailableFreqList.add(availableFreq);
                }

            }
        }

        for(int i = 0; i < mFmAvailableFreqList.size(); i++){
         //   Log.d(TAG,"setAvailableFreqsList()      mFmAvailableFreqList.[" + i + "]:  " + Arrays.asList(mFmAvailableFreqList.get(i)));
        }
        for(int i = 0; i < mAmAvailableFreqList.size(); i++){
         //   Log.d(TAG,"setAvailableFreqsList()     mAmAvailableFreqList.[" + i + "]:  " + Arrays.asList(mAmAvailableFreqList.get(i)));
        }

        if(band == RadioManager.RADIO_BAND_FM){
            return  mFmAvailableFreqList;
        }else {
            return  mAmAvailableFreqList;
        }
    }

    private List<Freq> listSort(List<Freq> freqs) {
        Collections.sort(freqs,new Comparator<Freq>(){
            @Override
            public int compare(Freq o1, Freq o2) {
                return (o1.getFreq())-(o2.getFreq());//升序排列
            }
        });

        return freqs;
    }

    public void setCurrentFreq(Freq freq){
        mCurrentFreq = freq;
    }

    public void setCollectPageNum(int band,int num){
        if(band == RadioManager.RADIO_BAND_FM){
            mFmCollectPageNumber = num;
        }else {
            mAmCollectPageNumber = num;
        }
    }

    public CollectionFreq[] loadCollectFreqList(int band ,int pageIndex){
        if(band == RadioManager.RADIO_BAND_FM){
            if(mFmCollectionFreqList.size() != 0){
                if(pageIndex >= mFmCollectPageNumber -1){
                    pageIndex =mFmCollectPageNumber -1;
                }
                return mFmCollectionFreqList.get(pageIndex);
            }else {
                return null;
            }
        }else {
            if(mAmCollectionFreqList.size() != 0){
                if(pageIndex >= mAmCollectPageNumber -1){
                    pageIndex = mAmCollectPageNumber -1;
                }
                return mAmCollectionFreqList.get(pageIndex);
            }else {
                return null;
            }
        }
    }
}
