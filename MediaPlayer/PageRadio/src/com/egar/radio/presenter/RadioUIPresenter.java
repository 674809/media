package com.egar.radio.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

import com.egar.manager.radio.Freq;
import com.egar.manager.radio.RadioManager;
import com.egar.mediaui.App;
import com.egar.mediaui.util.LogUtil;
import com.egar.radio.adapter.AvailableFreqsListAdapter;
import com.egar.radio.contract.RadioUIContract;
import com.egar.radio.listener.RadioStatusChangeListener;
import com.egar.radio.model.RadioUIModel;
import com.egar.radio.utils.CollectionFreq;
import com.egar.radio.utils.EgarRadioPreferUtils;
import com.egar.radio.utils.FreqFormatUtil;
import com.egar.radio.utils.Values;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class RadioUIPresenter  implements RadioStatusChangeListener, RadioUIContract.RadioUIPresenterInterface{

    private static  final String TAG = "RadioUIPresenter";
    private static final int MESSAGE_BAND_OR_FREQS_CHANGED = 1001;
    private  ListView mRadioui_station_list_lv;
    private RadioUIContract.RadioUIViewInterface mRadioUIViewInterface;

    private  RadioUIModel mRadioUIModel;
    private Context mContext;
    private int mBand = RadioManager.RADIO_BAND_FM;
    private Freq mFreq = null;
    private int mFmCollectionPageNumber;
    private int mAmCollectionPageNumber;
    private boolean hasConnect;

    private AvailableFreqsListAdapter mAvailableFreqsListAdapter;

    private  Handler mRadioUIPresenterHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MESSAGE_BAND_OR_FREQS_CHANGED :
                    setFreqInfo(mBand,mFreq);
                    boolean resetView = (boolean)msg.obj;
                    int favorateNumber = Values.FM_COLLECT_PAGE_NUMBER;
                    if(mBand == RadioManager.RADIO_BAND_FM){
                        favorateNumber = mFmCollectionPageNumber;
                    }else {
                        favorateNumber = mAmCollectionPageNumber;
                    }

                    if(null!= mRadioUIModel){
                        if(!mRadioUIModel.isSearching()){
                            LogUtil.d(TAG,"MESSAGE_BAND_OR_FREQS_CHANGED   mRadioUIViewInterface:  " + mRadioUIViewInterface   +  "   /resetView:   " + resetView);

                            if(null != mRadioUIViewInterface){
                                mRadioUIViewInterface.refreshCollect(favorateNumber,mRadioUIModel.getCollection(mBand),mRadioUIModel.getCurrentFreq(),resetView);
                            }
                        }
                    }

                    notifyAdapterRefreshList();
                    break;
            }
        }
    };


    public RadioUIPresenter( ListView radiouiStationListLv) {
        LogUtil.d(TAG, "RadioUIPresenter() ");
        mContext = App.getContext();
        mRadioui_station_list_lv = radiouiStationListLv;
        mRadioUIModel = new RadioUIModel(mContext);
        mRadioUIModel.setRadioStatusChangeListener(this);
    }

    public void setRadioUIViewInterface(RadioUIContract.RadioUIViewInterface radioUIViewInterface){
        this.mRadioUIViewInterface = radioUIViewInterface;
    }

    public void removeRadioUIViewInterface(){
        this.mRadioUIViewInterface = null;
    }

    @Override
    public void seekBarSetFreq(int progress) {
    }



    public int switchBand() {
        LogUtil.i(TAG,"  switchBand()");
        int band = (mBand == RadioManager.RADIO_BAND_FM)?RadioManager.RADIO_BAND_AM:RadioManager.RADIO_BAND_FM;
        return mRadioUIModel.setBand(band);
    }

    @Override
    public void openRadio() {
        LogUtil.i(TAG,"  openRadio()");
        if(hasConnect){
            if((null != mRadioUIModel)&&(!mRadioUIModel.isOpen())){
                mRadioUIModel.openRadio();
            }
        }
    }

    /**
     * 向上步进
     *
     */
    @Override
    public void stepPrev() {
        LogUtil.d(TAG,"  stepPrev()");
        if(hasConnect){
            if(null != mRadioUIModel){
                mRadioUIModel.stepPrev();
            }
        }
    }

    /**
     * 向下步进
     *
     */
    @Override
    public void stepNext() {
        LogUtil.d(TAG,"  stepNext()");
        if(hasConnect){
            if(null != mRadioUIModel){
                mRadioUIModel.stepNext();
            }
        }
    }

    /**
     * 设置频率值
     *
     * @param freq
     */
    @Override
    public void setFreq(int freq) {
        if(null !=  mRadioUIModel){
            mRadioUIModel.setFreq(freq);
        }
    }

    @Override
    public int getCurrentBand() {
        if(null!=mRadioUIModel){
            return  mRadioUIModel.getCurrentBand();
        }
        return RadioManager.RADIO_BAND_FM;
    }


    @Override
    public void startSearchFreq(int type) {
        if(null!=mRadioUIModel){
            mRadioUIModel.startSearchFreq(type);
        }
    }


    /**
     * 处理点击按钮后的逻辑
     *
     * @param type
     */
    @Override
    public void processClickUIView(int type) {
        LogUtil.i(TAG,"processClickUIView()  type:  " + type);

        if(type == Values.CLICK_STATION_LIST_EDIT_VIEW){
            if(null!=mRadioUIViewInterface){
                mRadioUIViewInterface.clickStationEditView();
            }
        }

        if(type == Values.CLICK_STATION_LIST_BAND_TYEP_FM){
            if((mRadioUIViewInterface!= null)&&(mRadioUIModel!=null)){
                notifyAdapterRefreshList(RadioManager.RADIO_BAND_FM);
                mRadioUIViewInterface.showStationList(true,RadioManager.RADIO_BAND_FM);
            }
            return;
        }

        if(type == Values.CLICK_STATION_LIST_BAND_TYEP_AM){
            if((mRadioUIViewInterface!= null)&&(mRadioUIModel!=null)){
                notifyAdapterRefreshList(RadioManager.RADIO_BAND_AM);
                mRadioUIViewInterface.showStationList(true,RadioManager.RADIO_BAND_AM);
            }
            return;
        }

        if(type == Values.CLICK_STATION_LIST_BACK_VIEW){
            if((mRadioUIViewInterface!= null)&&(mRadioUIModel!=null)){
                mRadioUIViewInterface.showStationList(false,mRadioUIModel.getCurrentBand());
            }
            return;
        }

        if(type == Values.CLICK_STATION_LIST_VIEW){
            if((mRadioUIViewInterface!= null)&&(mRadioUIModel!=null)){
                notifyAdapterRefreshList();
                mRadioUIViewInterface.showStationList(true,mRadioUIModel.getCurrentBand());
            }
            return;
        }
        if(type == Values.CLICK_BAND_FM_VIEW){
            if((mRadioUIViewInterface!= null)&&(mRadioUIModel!=null)){
                mRadioUIModel.setBand(RadioManager.RADIO_BAND_FM);
                mRadioUIViewInterface.showStationList(false,mRadioUIModel.getCurrentBand());
            }
            return;
        }
        if(type == Values.CLICK_BAND_AM_VIEW){
            if((mRadioUIViewInterface!= null)&&(mRadioUIModel!=null)){
                mRadioUIModel.setBand(RadioManager.RADIO_BAND_AM);
                mRadioUIViewInterface.showStationList(false,mRadioUIModel.getCurrentBand());
            }
            return;
        }

        if(null!=mRadioUIModel){
            if(mRadioUIModel.isSearching()){
                mRadioUIModel.stopSearch();
                return;
            }
        }

        switch (type){
            case Values.CLICK_SEARCH_PRE_VIEW:
                if(null!=mRadioUIModel){
                    mRadioUIModel.startSearchFreq(RadioManager.RADIO_SEARCH_FREQ_PREV);
                }
                break;
            case Values.CLICK_SEARCH_NEXT_VIEW:
                if(null!=mRadioUIModel){
                    mRadioUIModel.startSearchFreq(RadioManager.RADIO_SEARCH_FREQ_NEXT);
                }
                break;
            case Values.CLICK_STEP_NEXT_VIEW:
                stepNext();
                break;
            case Values.CLICK_STEP_PRE_VIEW:
                stepPrev();
                break;
            case Values.CLICK_STATION_LIST_REFRESH_VIEW:
                if(null!= mRadioUIModel){
                    int searchBandType = (mRadioUIModel.getCurrentBand() == RadioManager.RADIO_BAND_FM)? RadioManager.RADIO_SEARCH_FREQ_FM :RadioManager.RADIO_SEARCH_FREQ_AM;
                    mRadioUIModel.startSearchFreq(searchBandType);
                }
                break;

        }
    }

    @Override
    public void onListViewItemClick(int position) {
        if((null!= mRadioUIModel)&&(null!=mAvailableFreqsListAdapter)){
            mRadioUIModel.setFreq(mAvailableFreqsListAdapter.getItemFreq(position));
        }
    }


    public Freq getRadioFreq() {
        if(null!=mRadioUIModel){
            return  mRadioUIModel.getCurrentFreq();
        }
        return mFreq;
    }


    public int getRadioBand() {
        if(null!=mRadioUIModel){
            return  mRadioUIModel.getCurrentBand();
        }
        return mBand;
    }


    @Override
    public int getCollectionPageNum() {
        if(null!=mRadioUIModel){
            return mRadioUIModel.getCollectionPageNum(mBand);
        }else {
            return Values.FM_COLLECT_PAGE_NUMBER;
        }
    }


    /**
     * 修改收藏夹列表
     *
     * @param listPositon  当长按收藏级列表时listposition 表示修改收藏级列表中的哪个位置的元素
     *                                当点击可用列表收藏按钮时listposition表示收藏可用列表中的哪个元素
     * @param isReplace
     * @param isFromStationList
     */
    @Override
    public void modifyCollectFreq(int band,int listPositon,boolean isReplace,boolean isFromStationList) {
        LogUtil.d(TAG,"modifyCollectFreq()   listPositon:   " + listPositon + "   /isReplace:  " + isReplace  + "   /isFromStationList:  " + isFromStationList);
        if(null != mRadioUIModel){
            mRadioUIModel.modifyCollection(band,listPositon,isReplace,isFromStationList);
        }
    }

    @Override
    public boolean checkIsCollected(int freq) {
        if(null!= mRadioUIModel){
            mRadioUIModel.checkIsCollected(freq);
        }
        return false;
    }

    /**
     * 通知Model层关闭收音机
     *
     */
    @Override
    public void onViewDestroy() {
        LogUtil.d(TAG,"onViewDestroy() ");
        mRadioUIViewInterface = null;
        mRadioui_station_list_lv = null;
        mContext = null;
        if(null!= mAvailableFreqsListAdapter){
            mAvailableFreqsListAdapter.onViewDestroy();
            mAvailableFreqsListAdapter = null;
        }
        if(mRadioUIModel!= null){
            mRadioUIModel.removeRadioStatusChangeListener();
            mRadioUIModel.onPresenterDestroy();
            mRadioUIModel = null;
        }
    }


    @Override
    public void setResetFragAndAddView(boolean reset, boolean addView) {

    }

    @Override
    public void onGetModifyNickName(String newName) {
        LogUtil.d(TAG,"onGetModifyNickName()   newName:  " + newName);
        if((null!= mRadioUIModel)&&(null!=mAvailableFreqsListAdapter)){
            mRadioUIModel.modifyNickName(mAvailableFreqsListAdapter.getModifyNickNameFreq(),newName);
        }
    }


    /**
     *
     * RadioUIModel 监听到RadioService端服务连接后进行初始化，
     * 初始化完成后给RadioUIPresenter的回调，RadioUIPresenter进一步
     * 初始化收音机的一些状态和View层的一些控件
     *
     * @param band
     * @param freq
     * @param maxFmFreq
     * @param maxAmFreq
     * @param minFmFreq
     * @param minAmFreq
     * @param fmStepFreq
     * @param amStepFreq
     */
    @Override
    public void onServiceConnected(int band, Freq freq, int maxFmFreq, int maxAmFreq, int minFmFreq, int minAmFreq, int fmStepFreq, int amStepFreq) {
        LogUtil.i(TAG,"onServiceConnected()");
        mBand = band;
        mFreq = freq;
        if(mRadioUIViewInterface!= null){
            hasConnect = true;
            setFreqInfo(band,freq);
            if(EgarRadioPreferUtils.isFirstOpenRadioUI()){
                //if(true){
                //if(false){
                if(mRadioUIModel.openRadio(band,RadioManager.RADIO_FREQ_CLEAR) == RadioManager.SUCCESS){
                    mRadioUIViewInterface.showFirstOpenToast();
                    mRadioUIModel.startSearchFreq(RadioManager.RADIO_SEARCH_FREQ_ALL);
                }
            }else{
                if(!mRadioUIModel.isOpen()){
                    mRadioUIModel.openRadio();
                }
            }

            mAvailableFreqsListAdapter = new AvailableFreqsListAdapter(mContext,mRadioUIModel.getAvailableFreqsList(mRadioUIModel.getCurrentBand()),this);
            mRadioui_station_list_lv.setAdapter(mAvailableFreqsListAdapter);

        }
    }

    @Override
    public void onOpenStateChanged(int band, Freq freq, boolean isOpen) {
        mBand = band;
        mFreq = freq;
    }

    /**
     * 波段变化
     *
     * @param band
     * @param freq
     * @param isSearch
     * @param resetCollectionPageFlag
     */
    @Override
    public void onBandChanged(int band, Freq freq, boolean isSearch, boolean resetCollectionPageFlag) {
        LogUtil.d(TAG,"onFreqChanged()   band:  " + band  +  "   /Freq:  " + freq +  "   /isSearch:  " + isSearch   +  "   /resetCollectionPageFlag: " + resetCollectionPageFlag);
        mBand = band;
        mFreq = freq;
        Message msg = new Message();
        msg.what = MESSAGE_BAND_OR_FREQS_CHANGED;
        msg.obj = true;// resetView = true
        if(mRadioUIPresenterHandler.hasMessages(MESSAGE_BAND_OR_FREQS_CHANGED)){
            mRadioUIPresenterHandler.removeMessages(MESSAGE_BAND_OR_FREQS_CHANGED);
        }
        mRadioUIPresenterHandler.sendMessage(msg);
    }


    /**
     * 频率变化
     *
     * @param band
     * @param freq
     * @param isSearch
     * @param isPreOrNext
     */
    @Override
    public void onFreqChanged(int band, Freq freq, boolean isSearch, boolean isPreOrNext) {
        LogUtil.d(TAG,"onFreqChanged()   band:  " + band  +  "   /Freq:  " + freq +  "   /isSearch:  " + isSearch   +  "   /isPreOrNext: " + isPreOrNext);
        mBand = band;
        mFreq = freq;
        Message msg = new Message();
        msg.what = MESSAGE_BAND_OR_FREQS_CHANGED;
        msg.obj = false;// resetView = false
        if(mRadioUIPresenterHandler.hasMessages(MESSAGE_BAND_OR_FREQS_CHANGED)){
            mRadioUIPresenterHandler.removeMessages(MESSAGE_BAND_OR_FREQS_CHANGED);
        }
        mRadioUIPresenterHandler.sendMessage(msg);
    }


    /**
     * 搜台结束
     *
     * @param type
     */
    @Override
    public void onSearchStop(int type) {
        LogUtil.d(TAG,"onSearchStop()");

        if(null!=mRadioUIModel){
            if(!mRadioUIModel.isSearching()){
                notifyAdapterRefreshList();
            }
        }

        if(null!= mRadioUIViewInterface){
            mRadioUIViewInterface.refreshPageOnScanning(mRadioUIModel.getCurrentBand(),false,false,false);
        }
    }


    /**
     * 搜台开始
     *
     * @param type
     */
    @Override
    public void onSearchStart(int type) {
        LogUtil.d(TAG," onSearchStart()");
        switch (type){
            case RadioManager.RADIO_SEARCH_FREQ_ALL:
            case RadioManager.RADIO_SEARCH_FREQ_FM:
            case RadioManager.RADIO_SEARCH_FREQ_AM:
                if((null!= mRadioUIViewInterface)&&(null!=mRadioUIModel)){
                    mRadioUIViewInterface.refreshPageOnScanning(mRadioUIModel.getCurrentBand(),true,false,false);
                }
                break;

            case  RadioManager.RADIO_SEARCH_FREQ_PREV:
            case  RadioManager.RADIO_SEARCH_FREQ_NEXT:
                if((null!= mRadioUIViewInterface)&&(null!=mRadioUIModel)){
                    mRadioUIViewInterface.refreshPageOnScanning(mRadioUIModel.getCurrentBand(),false,true,false);
                }
                break;
        }
    }

    @Override
    public void onAllProcessChange(int process) {
        LogUtil.d(TAG,"onAllProcessChange()  /process: " + process);

    }


    /**
     *
     * 控制View层刷新收藏夹列表显示，同时通知可用列表更新列表中电台是否已收藏显示
     *
     * @param band
     * @param fmCollectionPageNumber
     * @param amCollectionPageNumber
     * @param freqsList
     */
    @Override
    public void onCollectionListInitComplete(int band,int fmCollectionPageNumber, int amCollectionPageNumber, ArrayList<CollectionFreq[]> freqsList) {
        LogUtil.i(TAG,"onCollectionListInitComplete()  fmCollectionPageNumber:  "  + fmCollectionPageNumber   +  "   /amCollectionPageNumber:   " + amCollectionPageNumber + "    /freqsList:   " + Arrays.asList(freqsList));
        LogUtil.d(TAG,"onCollectionListInitComplete()    mRadioUIViewInterface:  " + mRadioUIViewInterface);
        mBand = band;
        mFmCollectionPageNumber = fmCollectionPageNumber;
        mAmCollectionPageNumber = amCollectionPageNumber;

        if(mBand == RadioManager.RADIO_BAND_FM){
            if((null!= mRadioUIViewInterface)&&(null!=mRadioUIModel)){
                mRadioUIViewInterface.refreshCollect(mFmCollectionPageNumber,freqsList,mRadioUIModel.getCurrentFreq(),true);
            }
        }else {
            if((null!= mRadioUIViewInterface)&&(null!=mRadioUIModel)){
                mRadioUIViewInterface.refreshCollect(mAmCollectionPageNumber,freqsList,mRadioUIModel.getCurrentFreq(),true);
            }
        }

        notifyAdapterRefreshList();

    }

    /**
     * 控制View层刷新收藏夹显示
     *
     * @param band
     * @param freqsList
     */
    @Override
    public void onCollectionListChanged(int band,ArrayList<CollectionFreq[]> freqsList) {
        LogUtil.d(TAG,"onCollectionListChanged()");
        if(null != freqsList){
            if(band == RadioManager.RADIO_BAND_FM){
                if(null!= mRadioUIViewInterface){
                    mRadioUIViewInterface.refreshCollect(mFmCollectionPageNumber,freqsList,mFreq,false);
                }
            }else{
                if(null!=mRadioUIViewInterface){
                    mRadioUIViewInterface.refreshCollect(mAmCollectionPageNumber,freqsList,mFreq,false);
                }
            }

            notifyAdapterRefreshList();
        }
    }

    /**
     * 刷线View层波段和频率相关的控件显示
     *
     * @param band
     * @param freq
     */
    public void setFreqInfo(int band, Freq freq) {
        LogUtil.i(TAG,"setFreqInfo()   band:  " + band + "   /freq:  " + freq);
        String txtBand = " ";
        String txtFreq = " ";

        switch (band) {
            case RadioManager.RADIO_BAND_FM:
                txtFreq =   FreqFormatUtil.getFmFreqStr(freq.getFreq());
                if(!freq.getNickname().equals("")){
                    txtFreq = freq.getNickname();
                }
                LogUtil.d(TAG,"setFreqInfo()   band:  " + band + "   /txtFreq:  " + txtFreq);
                break;
            case RadioManager.RADIO_BAND_AM:
                txtFreq = FreqFormatUtil.getAmFreqStr(freq.getFreq());
                if(!freq.getNickname().equals("")){
                    txtFreq = freq.getNickname();
                }
                LogUtil.d(TAG,"setFreqInfo()   band:  " + band + "   /txtFreq:  " + txtFreq);
                break;
        }

        if(mRadioUIViewInterface != null){
            mRadioUIViewInterface.setFreqInfo(band,txtFreq);
        }

    }

    private int getSeekBarMax(int band) {
        LogUtil.i(TAG,"band: " + band);
        int maxFreq = mRadioUIModel.getFreqSeekBarMax(band);
        int minFreq = mRadioUIModel.getFreqSeekBarMin(band);
        int stepFreq = mRadioUIModel.getFreqStepOfBand(band);
        int maxValue = (maxFreq - minFreq)/stepFreq;
        LogUtil.d(TAG,"maxFreq:  " +  maxFreq  + "  minFreq: " + minFreq  + "  stepFreq: " + stepFreq  + " maxValue: " + maxValue);
        return maxValue;
    }

    public void onCollectionPageChanged(){

    }


    public interface OnDetachViewListener{
        public void onPresenterDetached();
    }

    private  Set<OnDetachViewListener> mDetachViewListenerList = new LinkedHashSet<>();



    @Override
    public void onRegisterDetachViewListener(OnDetachViewListener listener) {
        if(listener != null){
            mDetachViewListenerList.add(listener);
        }
    }

    @Override
    public void onUnRegisterDetachViewListener(OnDetachViewListener listener) {
        if(listener != null){
            mDetachViewListenerList.remove(listener);
        }
    }

    @Override
    public void onAvailableFreqsListChanged() {
        if(null!= mRadioUIModel){
            if(!mRadioUIModel.isSearching()){
                notifyAdapterRefreshList();
            }
        }
    }


    /**
     * 通知可用电台列表更新列表显示
     *
     */
    public void notifyAdapterRefreshList(){
        if(null != mAvailableFreqsListAdapter){
            if(null != mRadioUIModel){
                mAvailableFreqsListAdapter.refreshAvailableList(mRadioUIModel.getAvailableFreqsList(mRadioUIModel.getCurrentBand()));
            }
        }
    }
    /**
     * 通知可用电台列表显示指定波段列表
     *
     */
    public void notifyAdapterRefreshList(int band){
        if((null != mAvailableFreqsListAdapter)&&(null!=mRadioUIModel)){
            mAvailableFreqsListAdapter.refreshAvailableList(mRadioUIModel.getAvailableFreqsList(band));
        }
    }

    @Override
    public void onNicknameChanged(Freq freq) {
        notifyAdapterRefreshList();
    }


    /**
     * 当可用电台列表中电台被收藏时回调
     *
     */
    @Override
    public void OnAvailableListFavoriteStatusChanged() {
        if(null!=mRadioUIModel){
            notifyAdapterRefreshList(mRadioUIModel.getCurrentBand());
        }
    }

    @Override
    public void onFavoritesListIsFull() {
        if(null!=mRadioUIViewInterface){
            mRadioUIViewInterface.onFavoritesListIsFull();
        }
    }


    public void modifyNickName(){

    }

/*    @Override
    public void detachView() {
        super.detachView();
        LogUtil.i(TAG,"onDetachView()");
        for(OnDetachViewListener listener: mDetachViewListenerList){
            if(listener != null){
                listener.onPresenterDetached();
            }
        }
        mRadioUIModel = null;
    }*/
}
