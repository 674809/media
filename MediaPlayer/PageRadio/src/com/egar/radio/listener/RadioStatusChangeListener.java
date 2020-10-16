package com.egar.radio.listener;



import com.egar.manager.radio.Freq;
import com.egar.radio.presenter.RadioUIPresenter;
import com.egar.radio.utils.CollectionFreq;

import java.util.ArrayList;

public interface RadioStatusChangeListener {
    void onServiceConnected(int band, Freq freq, int maxFmFreq, int maxAmFreq, int minFmFreq, int minAmFreq, int fmStepFreq, int amStepFreq);
    void onOpenStateChanged(int band, Freq freq, boolean isOpen);
    void onBandChanged(int band, Freq freq, boolean isSearch, boolean resetCollectionPageFlag);
    void onFreqChanged(int band, Freq freq, boolean isSearch, boolean isPreOrNext);
    void onSearchStop(int type);
    void onSearchStart(int type);
    void onAllProcessChange(int process);
    void onCollectionListInitComplete(int band, int fmCollectionPageNumber, int amCollectionPageNumber, ArrayList<CollectionFreq[]> freqsList);
    void onCollectionListChanged(int band, ArrayList<CollectionFreq[]> freqsList);
    void onRegisterDetachViewListener(RadioUIPresenter.OnDetachViewListener listener);
    void onUnRegisterDetachViewListener(RadioUIPresenter.OnDetachViewListener listener);
    void  onAvailableFreqsListChanged();
    void onNicknameChanged(Freq freq);
    void OnAvailableListFavoriteStatusChanged();
    void onFavoritesListIsFull();
}
