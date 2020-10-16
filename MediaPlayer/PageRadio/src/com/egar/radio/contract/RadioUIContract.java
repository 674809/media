package com.egar.radio.contract;

import android.app.Activity;

import com.egar.manager.radio.Freq;
import com.egar.radio.utils.AvailableFreq;
import com.egar.radio.utils.CollectionFreq;

import java.util.ArrayList;

public interface RadioUIContract {

    /**
     * RadioUIPresenter 实现RadioUIPresenterInterface的接口，
     * RadioMainFragment 持有RadioUIPresenterInterface对象，
     * 便于将用户的操作从View层传给Presenter层
     *
     */
    interface RadioUIPresenterInterface {
        void seekBarSetFreq(int process);
        void openRadio();
        void stepPrev();
        void stepNext();
        void setFreq(int freq);
        int getCurrentBand();
        void startSearchFreq(int type);
        void processClickUIView(int type);
        void onListViewItemClick(int position);
        int getCollectionPageNum();
        void modifyCollectFreq(int band,int listPosition,boolean isReplace,boolean isFromStationList);
        boolean checkIsCollected(int freq);
        void onViewDestroy();
        void setResetFragAndAddView(boolean reset, boolean addView);
        void onGetModifyNickName(String newName);
    }

    /**
     * RadioUIModel 实现RadioUIModelInterface 的接口，
     * RadioUIPresenter持有RadioUIModelInterface对象，
     * 便于Presenter层控制Model层对收音机进行操作
     *
     */
    interface RadioUIModelInterface {
        int openRadio();
        void stepPrev();
        void stepNext();
        int openRadio(int band, Freq freq);
        int openRadio(int band, int freq);
        int setBand(int band);
        int getCurrentBand();
        ArrayList<AvailableFreq> getAvailableFreqsList(int band);
        Freq getCurrentFreq();
        void setFreq(Freq freq);
        void setFreq(int freq);
        void modifyCollection(int band,int position,boolean isReplace,boolean isFromStationList);
        boolean checkIsCollected(int freq);
        ArrayList<CollectionFreq[]> getCollection(int band);
        int modifyNickName(int freq, String newNickName);
        void startSearchFreq(int type);
        void stopSearch();
        int getFreqSeekBarMax(int band);
        int getFreqSeekBarMin(int band);
        int getFreqStepOfBand(int band);
        boolean isSearching();
        boolean isOpen();
        int getCollectionPageNum(int band);
        void onPresenterDestroy();
    }

    /**
     * RadioMainFragment 实现RadioUIViewInterface 的接口，
     * RadioUIPresenter持有RadioUIViewInterface对象，
     * 便于Presenter层控制View层更新UI显示
     *
     */
    interface RadioUIViewInterface {
        void setFreqInfo(int band, String freq);
        void showFirstOpenToast();
        void showStationList(boolean show,int band);
        void refreshCollect(int collectionPageNumber, ArrayList<CollectionFreq[]> freqsList, Freq freq, boolean resetView);
        Activity getAttachedActivity();
        void showDialog();
        void setFreqSeekBarProcess(int process);
        void setFreqSeekBarMax(int maxValue);
        void refreshPageOnScanning(int band, boolean isScanning, boolean preOrNext, boolean needResetCollectionPage);
        void setAllProcess(int process);
        void  destroyView();
        void refreshFragmentPage(int band, int currentFreq, boolean isSearch, boolean isPreOrNext);
        boolean isCollectAnimRunning();
        void startSwitchBandAnimation();
        void clickStationEditView();
        void onFavoritesListIsFull();
    }

}
