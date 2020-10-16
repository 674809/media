package com.egar.radio.model;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.egar.manager.radio.Freq;
import com.egar.manager.radio.RadioListener;
import com.egar.manager.radio.RadioManager;
import com.egar.manager.radio.RadioRegionalData;
import com.egar.manager.radio.RadioSearchFreqListener;
import com.egar.mediaui.util.LogUtil;
import com.egar.radio.contract.RadioUIContract;
import com.egar.radio.fragment.RadioApp;
import com.egar.radio.listener.RadioStatusChangeListener;
import com.egar.radio.presenter.RadioUIPresenter;
import com.egar.radio.utils.AvailableFreq;
import com.egar.radio.utils.CollectionAndAvailableFreqsListUtil;
import com.egar.radio.utils.CollectionFreq;
import com.egar.radio.utils.Values;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RadioUIModel extends RadioUIBaseModel implements RadioUIContract.RadioUIModelInterface,RadioApp.ServiceConnectListener,RadioSearchFreqListener,RadioListener,RadioUIPresenter.OnDetachViewListener,CollectionAndAvailableFreqsListUtil.OnAvailableListStatusChangedListener {
    private static final String TAG = "RadioUIModel";

    private static final int MESSAGE_EMPTY =1000 ;
    private static final int MESSAGE_ONSERVICECONNECTE =1001 ;
    private static final int MESSAGE_RADIO_SEARCH_FREQ_ALL_START = 1002;
    private static final int MESSAGE_RADIO_SEARCH_FREQ_ALL_END = 1003;
    private static final int MESSAGE_RADIO_SEARCH_FREQ_PREV_OR_NEXT_START = 1004;
    private static final int MESSAGE_RADIO_SEARCH_FREQ_PREV_OR_NEXT_END = 1005;
    private static final int MESSAGE_RADIO_SEARCH_FREQ_NEXT_START = 1006;
    private static final int MESSAGE_RADIO_SEARCH_FREQ_NEXT_END = 1007;
    private static final int MESSAGE_RADIO_SEARCH_FREQ_PREVIEW_START = 1008;
    private static final int MESSAGE_RADIO_SEARCH_FREQ_PREVIEW_END = 1009;
    private static final int MESSAGE_RADIO_ON_FREQ_CHANGED = 1010;
    private static final int MESSAGE_RADIO_ON_BAND_CHANGED = 1011;
    private static final int MESSAGE_RADIO_OPEN_STATE_CHANGED = 1012;
    private static final int MESSAGE_COLLECTION_LIST_CHANGED = 1013;
    private static final int MESSAGE_AVAILABLE_LIST_CHANGED = 1014;
    private static final int MESSAGE_NICKNAME_CHANGED = 1015;
    private static final int RADIOMANAGER_NULL = -1 ;
    private static final int RADIOMANAGER_DEFAULT_FREQ = -1 ;
    private static final String RADIOMANAGER_DEFAULT_NICKNAME = "" ;
    private static final int RADIO_OPEN_STATE = 1 ;
    private static final int RADIO_CLOE_STATE = 0 ;
    private final RadioApp mRadioApp;


    private Context mContext;
    private RadioStatusChangeListener mRadioStatusChangeListener;
    private RadioManager mRadioManager;
    private String mRegional;
    private int mBand = RadioManager.RADIO_BAND_FM;
    private Freq mFreq;
    private RadioRegionalData mRadioRegionalData;
    private int mMaxFmFreq;
    private int mMaxAmFreq;
    private int mMinFmFreq;
    private int mMinAmFreq;
    private int mFmStepFreq;
    private int mAmStepFreq;
    private ArrayList<CollectionFreq[]> mCollectionFreqsList;

    private CollectionAndAvailableFreqsListUtil mCollectionAndAvailableFreqsListUtil;
    private int mFmCollectionPageNumber;
    private int mAmCollectionPageNumber;
    private boolean mIsSearch;
    private boolean mIsPreOrNext;

    //Todo
   private Handler mRadioHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            boolean isOpen = false;
            switch (msg.what){
                case MESSAGE_ONSERVICECONNECTE:
                    registerListener();
                    initRaginalData();
                    initAvailebleFreqsList();
                    initCollectList();
                    if(null!=mRadioStatusChangeListener){
                        mRadioStatusChangeListener.onServiceConnected(mBand,mFreq,mMaxFmFreq,mMaxAmFreq,mMinFmFreq,mMinAmFreq,mFmStepFreq,mAmStepFreq);
                    }
                    break;

                case MESSAGE_RADIO_SEARCH_FREQ_ALL_START:
                    LogUtil.d(TAG,"handleMessage()  MESSAGE_RADIO_SEARCH_FREQ_ALL_START ");
                    if(null!=mRadioStatusChangeListener){
                        mRadioStatusChangeListener.onSearchStart(RadioManager.RADIO_SEARCH_FREQ_ALL);
                    }

                    break;

                case MESSAGE_RADIO_SEARCH_FREQ_ALL_END:
                    if(null!=mRadioStatusChangeListener){
                        mRadioStatusChangeListener.onCollectionListInitComplete(mBand,mFmCollectionPageNumber,mAmCollectionPageNumber,mCollectionFreqsList);
                        mRadioStatusChangeListener.onSearchStop(RadioManager.RADIO_SEARCH_FREQ_ALL);
                    }

                    break;

                case MESSAGE_RADIO_ON_BAND_CHANGED:
                {
                    if(null != mRadioManager){
                        LogUtil.d(TAG," handleMessage()   MESSAGE_RADIO_ON_BAND_CHANGED   /band:  " + mBand  +  "   /freq:  " + mFreq);
                        boolean isSearch = mRadioManager.isSearchFreq();
                        if(null!=mRadioStatusChangeListener){
                            mRadioStatusChangeListener.onBandChanged(mBand,mFreq,mIsSearch,mIsPreOrNext);
                            if(!isSearch){
                                mRadioStatusChangeListener.onCollectionListInitComplete(mBand,mFmCollectionPageNumber,mAmCollectionPageNumber, mCollectionAndAvailableFreqsListUtil.getCollectionFreqs(mBand));
                            }
                        }
                    }
                    break;
                }

                case MESSAGE_RADIO_ON_FREQ_CHANGED:
                    LogUtil.d(TAG," handleMessage()   MESSAGE_RADIO_ON_FREQ_CHANGED   /band: " + mBand  + "    /freq:  " + mFreq);
                    if(null!=mRadioStatusChangeListener){
                        mRadioStatusChangeListener.onFreqChanged(mBand,mFreq,mIsSearch,mIsPreOrNext);
                    }
                    break;

                case MESSAGE_RADIO_OPEN_STATE_CHANGED:
                    mBand = msg.arg1;
                    if(msg.arg2 == RADIO_OPEN_STATE){
                        isOpen = true;
                    }else if(msg.arg2 == RADIO_CLOE_STATE){
                        isOpen = false;
                    }
                    if(isOpen){
                        LogUtil.d(TAG,"onOpenStateChanged()  mFreq: " +mFreq);
                        if(null!=mRadioStatusChangeListener){
                            mRadioStatusChangeListener.onOpenStateChanged(mBand,mFreq,isOpen);
                        }
                    }
                    break;

                case MESSAGE_RADIO_SEARCH_FREQ_PREV_OR_NEXT_START:
                    if(null!=mRadioStatusChangeListener){
                        mRadioStatusChangeListener.onSearchStart(RadioManager.RADIO_SEARCH_FREQ_PREV);
                    }
                    break;

                case MESSAGE_RADIO_SEARCH_FREQ_PREV_OR_NEXT_END:
                    if(null!=mRadioStatusChangeListener){
                        mRadioStatusChangeListener.onSearchStop(RadioManager.RADIO_SEARCH_FREQ_PREV);
                    }
                    break;

                case MESSAGE_COLLECTION_LIST_CHANGED:
                {
                    if (null != mRadioManager) {
                        int band = msg.arg1;
                        List<Freq> freqs = (List<Freq>) msg.obj;
                        Log.d(TAG, "MESSAGE_COLLECTION_LIST_CHANGED111111  freqs: " + Arrays.asList(freqs));
                        mCollectionFreqsList = mCollectionAndAvailableFreqsListUtil.setCollectionFreqs(band, freqs);
                        if(null!=mRadioStatusChangeListener){
                            if (!mRadioManager.isSearchFreq()) {
                                mRadioStatusChangeListener.onCollectionListChanged(band, mCollectionFreqsList);
                            }
                        }
                    }
                    break;

                }

                case MESSAGE_AVAILABLE_LIST_CHANGED:
                {
                    if(null != mRadioStatusChangeListener){
                        mRadioStatusChangeListener.onAvailableFreqsListChanged();
                    }
                }
                break;

                case MESSAGE_NICKNAME_CHANGED:
                {
                    if(null!= mCollectionAndAvailableFreqsListUtil){
                        mCollectionAndAvailableFreqsListUtil.setAvailableFreqsList(RadioManager.RADIO_BAND_FM,mRadioManager.getAvailableFreqs(RadioManager.RADIO_BAND_FM));
                        mCollectionAndAvailableFreqsListUtil.setAvailableFreqsList(RadioManager.RADIO_BAND_AM,mRadioManager.getAvailableFreqs(RadioManager.RADIO_BAND_AM));
                    }
                    if(null!= mRadioStatusChangeListener){
                        mRadioStatusChangeListener.onNicknameChanged((Freq)msg.obj);
                        mRadioStatusChangeListener.onFreqChanged(mBand,mFreq,false,false);
                        mRadioStatusChangeListener.onCollectionListChanged(mRadioManager.getBand(),mCollectionAndAvailableFreqsListUtil.setCollectionFreqs(mBand,mRadioManager.getCollectFreqs(mBand)));
                    }
                }
                break;
            }
        }
    };

    /**
     * 初始化可用电台列表
     *
     */
    private void initAvailebleFreqsList() {
        LogUtil.d(TAG,"initAvailebleFreqsList()");
        if(null!= mCollectionAndAvailableFreqsListUtil){
            List<Freq> fmAvailableFreqs = mRadioManager.getAvailableFreqs(RadioManager.RADIO_BAND_FM);
            List<Freq> amAvailableFreqs = mRadioManager.getAvailableFreqs(RadioManager.RADIO_BAND_AM);
            if(null!= fmAvailableFreqs){
             //   LogUtil.d(TAG,"initAvailebleFreqsList()1   fmAvailableFreqs:  " + Arrays.asList(fmAvailableFreqs));
            }

            if(null!= amAvailableFreqs){
               // LogUtil.d(TAG,"initAvailebleFreqsList()2   amAvailableFreqs:  " + Arrays.asList(amAvailableFreqs));
            }

            mCollectionAndAvailableFreqsListUtil.setAvailableFreqsList(RadioManager.RADIO_BAND_FM,mRadioManager.getAvailableFreqs(RadioManager.RADIO_BAND_FM));
            mCollectionAndAvailableFreqsListUtil.setAvailableFreqsList(RadioManager.RADIO_BAND_AM,mRadioManager.getAvailableFreqs(RadioManager.RADIO_BAND_AM));
        }
    }


    private void sendMessage(int message,int band,boolean isOpen){
        if(mRadioHandler.hasMessages(message)){
            mRadioHandler.removeMessages(message);
        }
        Message msg = new Message();
        msg.what = message;
        msg.arg1 = band;
        if(isOpen){
            msg.arg2 = 1;
        }else{
            msg.arg2 = 0;
        }
        mRadioHandler.sendMessage(msg);
    }

    private void sendMessage(int message,int band,int[] freqs){
        if(mRadioHandler.hasMessages(message)){
            mRadioHandler.removeMessages(message);
        }
        Message msg = new Message();
        msg.what = message;
        msg.arg1 = band;
        msg.obj = freqs;
        mRadioHandler.sendMessage(msg);
    }

    private void sendMessage(int message){
        if(mRadioHandler.hasMessages(message)){
            mRadioHandler.removeMessages(message);
        }
        Message msg = new Message();
        msg.what = message;
        mRadioHandler.sendMessage(msg);
    }



    public RadioUIModel(Context context) {
        LogUtil.i(TAG,"RadioUIModel()");
        mContext = context;
        mCollectionAndAvailableFreqsListUtil = CollectionAndAvailableFreqsListUtil.getCollectionUtilInstance();
        mCollectionAndAvailableFreqsListUtil.register(this);
        mRadioApp = RadioApp.getRadioApp();
        mRadioApp.registerServiceConnectListener(this);
        mRadioApp.requestRadioManager();
    }

    public void setRadioStatusChangeListener(RadioStatusChangeListener radioStatusChangeListener){
        this.mRadioStatusChangeListener = radioStatusChangeListener;
        LogUtil.i(TAG,"setRadioStatusChangeListener");
    }
    public void removeRadioStatusChangeListener(){
        LogUtil.i(TAG,"removeRadioStatusChangeListener");
        this.mRadioStatusChangeListener = null;
    }
    //====================ServiceListener Start========================================


    /**
     *
     *收到此回调后拿到RadioManger对象，通过RadioManger对象
     * 操作收音机
     * @param radioManager
     */
    @Override
    public void onServiceConnected(RadioManager radioManager) {
        LogUtil.d(TAG,"onServiceConnected()    radioManager:  " + radioManager);
        mRadioManager = radioManager;
        Message msg = new Message();
        msg.what = MESSAGE_ONSERVICECONNECTE;
        mRadioHandler.sendMessage(msg);
    }


    /**
     *
     * 当RadioManager与RadioService服务在断开连接时回调该接口
     * UI层就不能再调用RadioManager的API接口操作收音机，并需要把RadioManager对象句柄置空
     *
     */
    @Override
    public void onServiceDisconnected() {
        unRegisterListener();
        mRadioManager = null;
    }


    /**
     * application 销毁时的回调
     *
     */
    @Override
    public void onAppOnTerminate() {
        LogUtil.d(TAG,"onAppOnTerminate()   ");
        mCollectionAndAvailableFreqsListUtil.unregister(this);
        mRadioApp.unregisterServiceConnectListener(this);
        RadioManager.destory(mContext);
        mContext = null;
    }
    //===========================ServiceListener End==========================================

    //===========================RadioSearchFreqListener Start================================

    /**
     *
     *收音机开始搜台
     *
     * @param band
     * @param searchFreqType
     */
    @Override
    public void onSearchFreqStart(int band, int searchFreqType) {
        LogUtil.i(TAG,"onSearchFreqStart()");
        mIsSearch = true;
        switch (searchFreqType){
            case RadioManager.RADIO_SEARCH_FREQ_ALL:
            case RadioManager.RADIO_SEARCH_FREQ_FM:
            case RadioManager.RADIO_SEARCH_FREQ_AM:
                sendMessage(MESSAGE_RADIO_SEARCH_FREQ_ALL_START);
                break;
            case RadioManager.RADIO_SEARCH_FREQ_PREV:
            case RadioManager.RADIO_SEARCH_FREQ_NEXT:
                mIsPreOrNext = true;
                sendMessage(MESSAGE_RADIO_SEARCH_FREQ_PREV_OR_NEXT_START);
                break;
        }
    }


    /**
     * 收音机搜台结束
     *
     * @param band
     * @param searchFreqType
     * @param reason
     */
    @Override
    public void onSearchFreqStop(int band, int searchFreqType, int reason) {
        LogUtil.i(TAG,"onSearchFreqStop()");
        int message =MESSAGE_RADIO_SEARCH_FREQ_ALL_END;
        mIsSearch = false;
        switch (searchFreqType){
            case  RadioManager.RADIO_SEARCH_FREQ_ALL :
            case  RadioManager.RADIO_SEARCH_FREQ_FM :
            case  RadioManager.RADIO_SEARCH_FREQ_AM :
                message =MESSAGE_RADIO_SEARCH_FREQ_ALL_END;
                break;
            case  RadioManager.RADIO_SEARCH_FREQ_PREV :
            case  RadioManager.RADIO_SEARCH_FREQ_NEXT :
                mIsPreOrNext = false;
                message = MESSAGE_RADIO_SEARCH_FREQ_PREV_OR_NEXT_END;
                break;
        }
        sendMessage(message);
    }


    /**
     * 收音机搜到一个新的可用电台
     *
     * @param band  波段类型
     * @param list  收音机搜到的所有可用电台
     */
    @Override
    public void onAvailableFreqsChanged(int band, List<Freq> list) {
       // LogUtil.d(TAG,"onAvailableFreqsChanged()  band:  " + band  + "    /list:  " + Arrays.asList(list)  +  "      mCollectionAndAvailableFreqsListUtil:  "  + mCollectionAndAvailableFreqsListUtil);
        if(null != mCollectionAndAvailableFreqsListUtil){
            mCollectionAndAvailableFreqsListUtil.setAvailableFreqsList(band,list);
            Message msg = new Message();
            msg.what = MESSAGE_AVAILABLE_LIST_CHANGED;
            if(mRadioHandler.hasMessages(MESSAGE_AVAILABLE_LIST_CHANGED)){
                mRadioHandler.removeMessages(MESSAGE_AVAILABLE_LIST_CHANGED);
            }
            mRadioHandler.sendMessage(msg);
        }
    }

    @Override
    public ArrayList<AvailableFreq> getAvailableFreqsList(int band) {
        LogUtil.d(TAG,"getAvailableFreqsList()    mCollectionAndAvailableFreqsListUtil:   " + mCollectionAndAvailableFreqsListUtil);
        if(null != mCollectionAndAvailableFreqsListUtil){
            ArrayList<AvailableFreq> availableFreqs = mCollectionAndAvailableFreqsListUtil.getAvailableFreqsList(band);
            for(int i = 0; i<availableFreqs.size();i++){
                LogUtil.d(TAG,"getAvailableFreqsList()   availableFreqs.get(i):   " + availableFreqs.get(i));
            }
            return availableFreqs;
        }
        return null;
    }

    @Override
    public void onSearchFreqResume(int i, int i1) {

    }


    @Override
    public void onSearchFreqPause(int i, int i1) {

    }


    /**
     * 收音机搜台进度
     *
     * @param process
     */
    @Override
    public void onSearchFreqProgress(int process) {
        if(null!=mRadioStatusChangeListener){
            mRadioStatusChangeListener.onAllProcessChange(process);
        }
    }


    //===========================RadioSearchFreqListener End=================================





    //===========================RadioModelInterface Start=================================

    /**
     * 打开收音机
     *
     * @return  收音机打开成功或失败的值
     */
    @Override
    public int openRadio() {
        if(null != mRadioManager){
            return  mRadioManager.open();
        }else {
            return  RADIOMANAGER_NULL;
        }
    }


    /**
     * 向上步进
     *
     */
    @Override
    public void stepPrev() {
        LogUtil.d(TAG,"  stepPrev()");
        if(null != mRadioManager){
            mRadioManager.stepPrev();
        }
    }

    /**
     * 向下步进
     *
     */
    @Override
    public void stepNext() {
        LogUtil.d(TAG,"  stepNext()");
        if(null != mRadioManager){
            mRadioManager.stepNext();
        }
    }


    /**
     * 打开收音机指定波段和频率
     *
     * @param band  波段
     * @param freq  频率对象
     * @return
     */
    @Override
    public int openRadio(int band, Freq freq) {
        if(null != mRadioManager){
            return  mRadioManager.open(band,freq);
        }else {
            return  RADIOMANAGER_NULL;
        }
    }

    /**
     * 打开收音机指定波段和频率
     *
     * @param band  波段
     * @param freq  频率
     * @return
     */
    @Override
    public int openRadio(int band, int freq) {
        if(null != mRadioManager){
            return  mRadioManager.open(band,freq);
        }else {
            return  RADIOMANAGER_NULL;
        }
    }

    /**
     * 设置收音机波段
     *
     * @param band 波段
     * @return  设置波段成功或失败的值
     */
    @Override
    public int setBand(int band) {
        if(null != mRadioManager){
            int setBandResult = mRadioManager.setBand(band);
            LogUtil.d(TAG,"setBand()   setBandResult:  " + setBandResult);
            return  setBandResult;
        }else {
            return  RADIOMANAGER_NULL;
        }
    }

    @Override
    public int getCurrentBand() {
        return mBand;
    }

    @Override
    public Freq getCurrentFreq() {
        if(null!= mRadioManager){
            return  mRadioManager.getFreq();
        }else {
            Freq freq = new Freq();
            freq.setFreq(-1);
            return freq;
        }
    }

    /**
     * 设置频率
     *
     * @param freq
     */
    @Override
    public void setFreq(Freq freq) {
        LogUtil.i(TAG,"setFreq()  freq: " + freq);
        if(null != mRadioManager){
            mRadioManager.setFreq(freq);
        }
    }


    /**
     * 设置频率值
     *
     * @param freq
     */
    @Override
    public void setFreq(int freq) {
        LogUtil.i(TAG,"setFreq()  freq: " + freq);
        if(null != mRadioManager){
            mRadioManager.setFreq(freq);
        }
    }

    /**
     * 修改收藏夹列表
     *
     * @param listPosition
     */
    @Override
    public void modifyCollection(int band,int listPosition,boolean isReplace,boolean isFromStationList) {
        LogUtil.d(TAG,"modifyCollection()   listPosition:  " + listPosition + "   /band:  " + band + "  /isReplace:  " + isReplace  + "   /isFromStationList:  " + isFromStationList);
        if(null != mRadioManager){
            if(isReplace){
                List<Freq> collectionFreqsList = mRadioManager.getCollectFreqs(band);
                int availebleFreq = Values.DEFAULT_FREQ_VALUE;
                if(isFromStationList){
                    if(null != mCollectionAndAvailableFreqsListUtil){
                        availebleFreq = mCollectionAndAvailableFreqsListUtil.getAvailableFreqsList(band).get(listPosition).getFreq();
                        LogUtil.d(TAG,"modifyCollection()   availebleFreq:  " + availebleFreq);
                    }
                    if(checkIsCollected(availebleFreq)){
                        return;
                    }
                }else {
                    int favorateFreq = mRadioManager.getFreq().getFreq();
                    if(checkIsCollected(favorateFreq)){
                        return;
                    }
                }

                if(isFromStationList){
                    //优先填补空位进行收藏
                    for(int i = 0 ; i < collectionFreqsList.size(); i++){
                        if(collectionFreqsList.get(i).getFreq() ==RADIOMANAGER_DEFAULT_FREQ){
                            if(availebleFreq!=Values.DEFAULT_FREQ_VALUE){
                                /**
                                 * 由于服务层上报的可用电台列表概率性发生列表顺序不是按从小到大排列，顺序混乱，RadioUI获取到可用电台列表
                                 * 后会进行重新排序，导致UI层和服务层数据源不同，修改收藏夹列表时不能直接调用服务层的getAvailableList(band).get(listposition)
                                 * 来获取目标频率对象
                                 */
                                Freq modifyFreq = new Freq();
                                modifyFreq.setFreq( mCollectionAndAvailableFreqsListUtil.getAvailableFreqsList(band).get(listPosition).getFreq());
                                if(null!=mCollectionAndAvailableFreqsListUtil.getAvailableFreqsList(band).get(listPosition).getNickName()){
                                    modifyFreq.setNickname( mCollectionAndAvailableFreqsListUtil.getAvailableFreqsList(band).get(listPosition).getNickName());
                                }else {
                                    modifyFreq.setNickname(RADIOMANAGER_DEFAULT_NICKNAME);
                                }
                                int  modifyCollectionResult = mRadioManager.modifyCollectFreq(band,i, modifyFreq);
                                LogUtil.d(TAG,"   modifyCollection()   availablelist add collection    modifyCollectionResult:  " + modifyCollectionResult);
                                return;
                            }
                        }
                    }
                    //如果收藏夹已满，提示用户收藏夹已满
                    //TODO toast  the collection list is full
                    if(null!=mRadioStatusChangeListener){
                        mRadioStatusChangeListener.onFavoritesListIsFull();
                    }
                }else {
                    int  modifyCollectionResult = mRadioManager.modifyCollectFreq(mBand,listPosition,mRadioManager.getFreq());
                    LogUtil.d(TAG,"   modifyCollection()   replace    modifyCollectionResult:  " + modifyCollectionResult);
                }
            }else {

                if(isFromStationList){
                    listPosition = getCollectionFreqIndex(band,listPosition);
                }

                Freq deleteFreq = new Freq();
                deleteFreq.setFreq(RADIOMANAGER_DEFAULT_FREQ);
                deleteFreq.setNickname(RADIOMANAGER_DEFAULT_NICKNAME);
                int modifyCollectionResult = mRadioManager.modifyCollectFreq(band, listPosition, deleteFreq);
                LogUtil.d(TAG,"   modifyCollection()    delete    modifyCollectionResult:  " + modifyCollectionResult + "  /listposition:  " + listPosition);
            }
        }
    }

    private int getCollectionFreqIndex(int band,int listPosition) {
        LogUtil.d(TAG,"getCollectionFreqIndex()   listPosition: " + listPosition + "   /band:   " + band);
        int modifyCollectionFreqIndex = 0;
        ArrayList<AvailableFreq> availableFreqList = mCollectionAndAvailableFreqsListUtil.getAvailableFreqsList(band);
        ArrayList<CollectionFreq[]> collectionFreqList = mCollectionAndAvailableFreqsListUtil.getCollectionFreqs(band);
        for(int i = 0 ; i < collectionFreqList.size(); i++){
            CollectionFreq[] collectionFreqs = collectionFreqList.get(i);
            for(int j = 0 ; j <collectionFreqs.length; j ++){
                if(collectionFreqs[j].getFreq() == availableFreqList.get(listPosition).getFreq()){
                    modifyCollectionFreqIndex = collectionFreqs[j].getListPosition();
                    return modifyCollectionFreqIndex;
                }
                continue;
            }
        }
        return modifyCollectionFreqIndex;
    }

    /**
     *
     * 检测是否已收藏目标电台
     *
     * @param Freq 检测电台频率
     * @return true 已收藏   false 还未收藏
     */
    public boolean checkIsCollected(int Freq) {

        List<Freq> collectionFreqsList = mRadioManager.getCollectFreqs(mBand);
        for(int i = 0 ; i < collectionFreqsList.size(); i++){
            if(collectionFreqsList.get(i).getFreq() == Freq){
                return true;
            }
        }
        return false;
    }


    public ArrayList<CollectionFreq[]> getCollection(int band){
        if(null != mCollectionAndAvailableFreqsListUtil){
            return mCollectionAndAvailableFreqsListUtil.getCollectionFreqs(band);
        }
        return  null;
    }

    /**
     * 修改指定频率的昵称
     *
     * @param freq
     * @param newNickName
     * @return
     */
    @Override
    public int modifyNickName(int freq, String newNickName) {
        LogUtil.d(TAG,"modifyNickName()   freq:   " + freq  +  "   /newNickName:   " + newNickName + "   /mRadioManager:   " + mRadioManager);
        if(null != mRadioManager){
            LogUtil.d(TAG,"modifyNickName()2   freq:   " + freq  +  "   /newNickName:   " + newNickName);
            return mRadioManager.modifyNickname(freq,newNickName);
        }
        return -1;
    }


    /**
     * 开始搜台
     *
     * @param type 搜台类型
     */
    @Override
    public void startSearchFreq(int type) {
        if(null != mRadioManager){
            LogUtil.d(TAG,"startSearchFreq()");
            mRadioManager.startSearchFreq(type);
        }
    }

    /**
     * 停止搜台
     *
     */
    @Override
    public void stopSearch() {
        if(null != mRadioManager){
            mRadioManager.stopSearchFreq();
        }
    }


    /**
     *
     * 获取指定波段的频率的最大值
     * @param band 波段
     * @return 指定波段的频率最大值
     */
    @Override
    public int getFreqSeekBarMax(int band) {
        if(band == RadioManager.RADIO_BAND_FM){
            return mMaxFmFreq;
        }else {
            return  mMaxAmFreq;
        }
    }

    /**
     * 获取指定波段的频率的最小值
     *
     * @param band 波段
     * @return 指定波段的最小值
     */
    @Override
    public int getFreqSeekBarMin(int band) {
        if(band == RadioManager.RADIO_BAND_FM){
            return  mMinFmFreq;
        }else {
            return  mMinAmFreq;
        }
    }

    /**
     * 获取指定波段的步进值
     *
     * @param band
     * @return
     */
    @Override
    public int getFreqStepOfBand(int band) {
        if(band == RadioManager.RADIO_BAND_FM){
            return mFmStepFreq;
        }else {
            return mAmStepFreq;
        }

    }


    /**
     * 是否正在搜台
     *
     * @return
     */
    @Override
    public boolean isSearching() {
        if(null != mRadioManager){
            return mRadioManager.isSearchFreq();
        }else {
            return  false;
        }
    }


    /**
     * 获取指定波段的收藏夹页数
     *
     * @param band
     * @return 收藏夹页数
     */
    @Override
    public int getCollectionPageNum(int band) {
        if(band == RadioManager.RADIO_BAND_FM){
            return mFmCollectionPageNumber;
        }else {
            return mAmCollectionPageNumber;
        }

    }

    /**
     * 关闭收音机，释放资源
     *
     */
    @Override
    public void onPresenterDestroy() {
        LogUtil.d(TAG,"onPresenterDestroy()");
        unRegisterListener();
        mRadioHandler.removeCallbacksAndMessages(null);
        mRadioStatusChangeListener = null;
        if(mRadioManager != null){
            mRadioManager.close();
            mRadioManager = null;
        }
    }

    /**
     * 收音机是否已打开
     *
     * @return
     */
    @Override
    public boolean isOpen() {
        if(null != mRadioManager){
            boolean isOpen = mRadioManager.isOpen();
            LogUtil.i(TAG,"isOpen(): " + isOpen);
            return mRadioManager.isOpen();
        }else {
            return  false;
        }
    }

    //===========================RadioModelInterface End=================================




    //===========================RadioListener Start================================

    /**
     * 收音机打开状态发生改变
     *
     * @param band 波段
     * @param isOpen  true  打开    false 未打开
     */
    @Override
    public void onOpenStateChanged(int band, boolean isOpen) {
        LogUtil.i(TAG,"onOpenStateChanged()");
        sendMessage(MESSAGE_RADIO_OPEN_STATE_CHANGED,band,isOpen);
    }

    /**
     *收音机地区改变
     *
     * @param regional
     * @param freq
     */
    @Override
    public void onRegionalChanged(String regional, int freq) {
        initRaginalData();
    }

    /**
     * 波段发生变化
     *
     * @param band 波段
     * @param freq 频率对象
     */
    @Override
    public void onBandChanged(int band, Freq freq) {
        LogUtil.i(TAG,"onBandChanged()    /band: " + band + "  /freq:  " + freq);
        mBand = band;
        mFreq = freq;
        sendMessage(MESSAGE_RADIO_ON_BAND_CHANGED);
    }

    /**
     * 频率发生变化
     *
     * @param freq 频率对象
     */
    @Override
    public void onFreqChanged(Freq freq) {
        mFreq = freq;
        LogUtil.i(TAG,"onFreqChanged()  band:  " + mBand  + "    /freq: " + mFreq);
        sendMessage(MESSAGE_RADIO_ON_FREQ_CHANGED);
    }

    /**
     * 收藏夹列表发生变化
     *
     * @param band 波段
     * @param freqs 收藏夹列表
     */
    @Override
    public void onCollectFreqsChanged(int band, List<Freq> freqs) {
        LogUtil.i(TAG,"onCollectFreqsChanged  ");
        Message msg = new Message();
        msg.what = MESSAGE_COLLECTION_LIST_CHANGED;
        msg.arg1 = band;
        msg.obj = freqs;
        mRadioHandler.sendMessage(msg);
    }

    @Override
    public void onRDSEnabledChanged(boolean b) {

    }

    @Override
    public void onRDSDataChanged(int[] ints) {

    }

    /**
     * 昵称发生变化
     *
     * @param freq 昵称改变对应的频率对象
     */
    @Override
    public void onNicknameChanged(Freq freq) {
        LogUtil.d(TAG,"onNicknameChanged()   freq:  " + freq);
        Message msg = new Message();
        msg.obj = freq;
        msg.what = MESSAGE_NICKNAME_CHANGED;
        mRadioHandler.sendMessage(msg);

    }

    //===========================RadioListener End=================================


    //====================初始化 Start===================================
    private void registerListener() {
        if(null != mRadioManager){
            mRadioManager.registerRadioListener(this);
            mRadioManager.registerRadioSearchFreqListener(this);
        }
    }
    private void unRegisterListener() {
        if(null != mRadioManager){
            mRadioManager.unregisterRadioListener(this);
            mRadioManager.unregisterRadioSearchFreqListener(this);
        }
    }

    /**
     * 初始化地区数据
     *
     */
    private void initRaginalData() {
        if(null != mRadioManager){
            mRegional = mRadioManager.getRegional();
            mBand = mRadioManager.getBand();
            mFreq = mRadioManager.getFreq();
            mRadioRegionalData = mRadioManager.getRadioRegionalData(mRegional);
            LogUtil.d(TAG,"mRadioRegionalData:   " + mRadioRegionalData);
            mMaxFmFreq = mRadioRegionalData.getMaxFreq(RadioManager.RADIO_BAND_FM);
            mMaxAmFreq = mRadioRegionalData.getMaxFreq(RadioManager.RADIO_BAND_AM);
            mMinFmFreq = mRadioRegionalData.getMinFreq(RadioManager.RADIO_BAND_FM);
            mMinAmFreq = mRadioRegionalData.getMinFreq(RadioManager.RADIO_BAND_AM);
            mFmStepFreq =  mRadioRegionalData.getStepFreq(RadioManager.RADIO_BAND_FM);
            mAmStepFreq =  mRadioRegionalData.getStepFreq(RadioManager.RADIO_BAND_AM);
        }
    }


    /**
     * 初始化收藏夹列表
     *
     * 获取FM和AM的收藏夹列表，并通过CollectionAndAvailableFreqsListUtil对收藏夹列表进行封装，调用RadioStatusChan
     * gedListener将收藏夹列表、收音机当前波段、FM和AM收藏夹列表页数发送给RadioUIPresenter
     *
     */
    private void initCollectList(){
        if(null != mRadioManager){

            mBand = mRadioManager.getBand();
            List<Freq> freqs = mRadioManager.getCollectFreqs(mBand);
            LogUtil.d(TAG,"initCollectList()  freqs: " + Arrays.asList(freqs));

            List<Freq> fmFreqs = mRadioManager.getCollectFreqs(RadioManager.RADIO_BAND_FM);
            mFmCollectionPageNumber = (fmFreqs.size()%Values.COLLECT_ITEM_NUMBER==0)?(fmFreqs.size()/Values.COLLECT_ITEM_NUMBER):((fmFreqs.size()/Values.COLLECT_ITEM_NUMBER)+1);
            mCollectionAndAvailableFreqsListUtil.setCollectPageNum(RadioManager.RADIO_BAND_FM,mFmCollectionPageNumber);
            mCollectionAndAvailableFreqsListUtil.setCollectionFreqs(RadioManager.RADIO_BAND_FM, fmFreqs);

            List<Freq> amFreqs = mRadioManager.getCollectFreqs(RadioManager.RADIO_BAND_AM);
            mAmCollectionPageNumber = (amFreqs.size()%Values.COLLECT_ITEM_NUMBER==0)?(amFreqs.size()/Values.COLLECT_ITEM_NUMBER):((amFreqs.size()/Values.COLLECT_ITEM_NUMBER)+1);
            mCollectionAndAvailableFreqsListUtil.setCollectPageNum(RadioManager.RADIO_BAND_AM,mAmCollectionPageNumber);
            mCollectionAndAvailableFreqsListUtil.setCollectionFreqs(RadioManager.RADIO_BAND_AM, amFreqs);

            mCollectionFreqsList = mCollectionAndAvailableFreqsListUtil.getCollectionFreqs(mBand);
            LogUtil.d(TAG,"initCollectList()  mFmCollectionPageNumber: " + mFmCollectionPageNumber  + "   /mAmCollectionPageNumber: " + mAmCollectionPageNumber);
            if(null!=mRadioStatusChangeListener){
                mRadioStatusChangeListener.onCollectionListInitComplete(mBand,mFmCollectionPageNumber,mAmCollectionPageNumber,mCollectionFreqsList);
            }
        }
    }
//=========================初始化 End =============================


    @Override
    public void onPresenterDetached() {
        LogUtil.d(TAG,"onPresenterDetached()");

    }

    /**
     * 当可用电台列表中电台被收藏
     *
     */
    @Override
    public void OnAvailableListFavoriteStatusChanged() {
        LogUtil.d(TAG,"OnAvailableListFavoriteStatusChanged()");
        if(null!=mRadioStatusChangeListener){
            mRadioStatusChangeListener.OnAvailableListFavoriteStatusChanged();
        }
    }
}
