package com.egar.radio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.egar.manager.radio.RadioManager;
import com.egar.mediaui.R;
import com.egar.mediaui.util.LogUtil;
import com.egar.radio.listener.ModifyFreqNameListener;
import com.egar.radio.presenter.RadioUIPresenter;
import com.egar.radio.utils.AvailableFreq;
import com.egar.radio.utils.FreqFormatUtil;
import com.egar.radio.utils.Values;

import java.util.ArrayList;

/**
 * author : liangxiaobo
 * date : 2019-11-2015:06
 */
public class AvailableFreqsListAdapter extends BaseAdapter implements ModifyFreqNameListener {
    private static final String TAG = "AvailableFreqsListAdapter";

    private Context mContext;
    private ArrayList<AvailableFreq> mAvailableFreqsList;
    private RadioUIPresenter  mRadioUIPresenter;
    private int mModifyNickNameFreq;
    private int mCollectFreq;


    public AvailableFreqsListAdapter(Context context, ArrayList<AvailableFreq> availableFreqsList, RadioUIPresenter radioUIPresenter ) {
        this.mContext = context;
        this.mAvailableFreqsList = availableFreqsList;
        this.mRadioUIPresenter = radioUIPresenter;

    }

    @Override
    public int getCount() {
        if(null != mAvailableFreqsList){
            return  mAvailableFreqsList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        LogUtil.d(TAG,"getItem()  position:  " + position);
        if(null != mAvailableFreqsList){
            return  mAvailableFreqsList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getItemFreq(int position){
        if(position < mAvailableFreqsList.size()){
            return mAvailableFreqsList.get(position).getFreq();
        }else {
            return  0;
        }
    }

    /**
     * 刷新数据源
     *
     * @param availableFreqList  最新的列表数据源
     */
    public void refreshAvailableList(ArrayList<AvailableFreq>  availableFreqList){
        this.mAvailableFreqsList = availableFreqList;
        notifyDataSetChanged();
    }

    @Override
    public void onModifyFreqNickName(String nickName) {

    }


    public int getModifyNickNameFreq(){
        return  mModifyNickNameFreq;
    }


    public final  class  ViewHolder{
        private ImageView mSelectedImageView;
        private TextView mFreqTextView;
        private ImageView mCelectedImageView;
        private ImageView mRenameImageView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(mAvailableFreqsList.size()==0){
            return convertView;
        }

        ViewHolder viewHolder;

        final AvailableFreq availableFreq = mAvailableFreqsList.get(position);
        LogUtil.d(TAG,"getView()   /position:  " + position  +   "    /availableFreq:   " + availableFreq.getFreq());
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.radioui_station_list_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.mSelectedImageView = (ImageView)convertView.findViewById(R.id.radioui_station_list_item_selected_iv);
            viewHolder.mFreqTextView = (TextView)convertView.findViewById(R.id.radioui_station_list_item_freq_tv);
            viewHolder.mCelectedImageView = (ImageView)convertView.findViewById(R.id.radioui_station_list_item_collected_status_iv);
            viewHolder.mRenameImageView = (ImageView)convertView.findViewById(R.id.radioui_station_list_item_rename_iv);
            convertView.setTag(viewHolder);

        } else {
            //复用
            viewHolder = (ViewHolder)convertView.getTag();
        }


        if((null != mRadioUIPresenter)&&(availableFreq.getFreq() == mRadioUIPresenter.getRadioFreq().getFreq())){
            viewHolder.mSelectedImageView.setVisibility(View.VISIBLE);
            viewHolder.mFreqTextView.setTextColor(mContext.getColor(R.color.radioui_availablelist_text_selected));
        }else {
            viewHolder.mSelectedImageView.setVisibility(View.INVISIBLE);
            viewHolder.mFreqTextView.setTextColor(mContext.getColor(R.color.radioui_availablelist_text_unselected));
        }



        if((availableFreq.getNickName()== null)||(availableFreq.getNickName().isEmpty())){
            viewHolder.mFreqTextView.setText(((availableFreq.getBand() == RadioManager.RADIO_BAND_FM)?(FreqFormatUtil.getFmFreqStr(availableFreq.getFreq())):(FreqFormatUtil.getAmFreqStr(availableFreq.getFreq()))) +"");
        }else {
            viewHolder.mFreqTextView.setText(availableFreq.getNickName());
        }

        if(availableFreq.getIsCollected()){
            viewHolder.mCelectedImageView.setBackground(mContext.getDrawable(R.drawable.radioui_station_list_collected));
        }else {
            viewHolder.mCelectedImageView.setBackground(mContext.getDrawable(R.drawable.radioui_station_list_uncollected));
        }

       /* viewHolder.mCelectedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != mRadioUIPresenter){
                    mCollectFreq = availableFreq.getFreq();
                    LogUtil.d(TAG,"    mCollectFreq111  " + mCollectFreq);
                    if(availableFreq.getIsCollected()){
                        mRadioUIPresenter.modifyCollectFreq(availableFreq.getBand(),availableFreq.getPosition(),false,true);
                    }else {
                        mRadioUIPresenter.modifyCollectFreq(availableFreq.getBand(),availableFreq.getPosition(),true,true);
                    }
                }

            }
        });*/

        viewHolder.mRenameImageView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mModifyNickNameFreq = availableFreq.getFreq();
                LogUtil.d(TAG,"    mModifyNickNameFreq  " + mModifyNickNameFreq);
                if(null != mRadioUIPresenter){
                    mRadioUIPresenter.processClickUIView(Values.CLICK_STATION_LIST_EDIT_VIEW);
                }
            }
        });

        return convertView;
    }

    public  void onViewDestroy(){
        mRadioUIPresenter = null;
    }
}
