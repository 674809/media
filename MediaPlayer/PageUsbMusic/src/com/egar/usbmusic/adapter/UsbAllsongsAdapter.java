package com.egar.usbmusic.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.egar.mediaui.R;
import com.egar.mediaui.util.LogUtil;
import com.egar.usbmusic.interfaces.CollectListener;

import java.util.ArrayList;
import java.util.List;

import juns.lib.media.bean.ProAudio;
import juns.lib.media.flags.MediaCollectState;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/11 15:28
 * @see {@link }
 */
public class UsbAllsongsAdapter extends BaseAdapter {
    private String TAG = "UsbAllsongsAdapter";
    private Context mContext;
    private List<ProAudio> mListData;
    /**
     * Playing media.
     */
    private ProAudio mPlayingMedia;
    private String mPlayingMediaFolderPath = "";
    private String mSelectedMediaUrl;

    private CollectListener mCollectListener;



    public void setCollectListener(CollectListener l) {
        mCollectListener = l;
    }

    public UsbAllsongsAdapter(Context mContext) {
        this.mContext = mContext;
        mListData = new ArrayList<>();
    }

    public void refreshData(List<ProAudio> lists, ProAudio currMedia) {
        synchronized (this) {
            mListData.clear();
            mListData.addAll(lists);
            setPlayingMedia(currMedia);
            notifyDataSetChanged();
        }

    }

    public void refreshData(String SelectedMediaUrl){
            this.mSelectedMediaUrl = SelectedMediaUrl;
            notifyDataSetChanged();
    }

    private void setPlayingMedia(ProAudio playingMedia) {
        try {
            this.mPlayingMedia = playingMedia;
            String playingMediaUrl = mPlayingMedia.getMediaUrl();
            this.mPlayingMediaFolderPath = playingMediaUrl.substring(0, playingMediaUrl.lastIndexOf("/"));
        } catch (Exception e) {
        }
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public Object getItem(int position) {
        return getItemId(position);
    }

    public ProAudio getItemInfo(int position) {
        try {
            return mListData.get(position);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (null == convertView) {
            convertView = View.inflate(mContext, R.layout.usb_music_folder_item, null);
            holder = new ViewHolder();
            holder.tv_text = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_id = (TextView) convertView.findViewById(R.id.tv_id);
            holder.image_c = (ImageView) convertView.findViewById(R.id.image_c);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_id.setText(position + 1 + "");
        holder.tv_text.setText(mListData.get(position).getTitle());
        holder.image_c.setOnClickListener(new CollectOnClick(holder.image_c, position));
        int collectState = mListData.get(position).getCollected();
        switch (collectState) {
            case MediaCollectState.COLLECTED:
                holder.image_c.setImageResource(R.drawable.favor_c);
                break;
            case MediaCollectState.UN_COLLECTED:
                holder.image_c.setImageResource(R.drawable.favor_c_n);
                break;
        }
        //////////////
       // TextUtils.equals(mSelectedMediaUrl, item.getMediaUrl());position == selectItme
        ProAudio audio = mListData.get(position);
        if (TextUtils.equals(mSelectedMediaUrl, audio.getMediaUrl())) {
            convertView.setBackgroundColor(Color.BLUE);
        } else {
            convertView.setBackgroundResource(R.drawable.keyboard_n);
        }
        return convertView;

    }


    public int selectItme = -1;

    public void setSelectItem(int selectItme) {
        this.selectItme = selectItme;

    }

    static class ViewHolder {
        TextView tv_text;
        TextView tv_id;
        ImageView image_c;
    }

    public int getPositionForSection(char c) {
        int position = -1;
        try {
            for (int idx = 0; idx < getCount(); idx++) {
                ProAudio media = getItemInfo(idx);
                if (media == null) {
                    continue;
                }

                //
                char firstChar = media.getTitlePinYin().charAt(0);
                if (firstChar == c) {
                    position = idx;
                    break;
                }
            }
        } catch (Exception e) {

        }
        return position;
    }

    public int getSectionForPosition(int position) {
        int section = -1;
        try {
            ProAudio media = getItemInfo(position);
            if (media != null) {
                section = media.getTitlePinYin().charAt(0);
            }
        } catch (Exception e) {

        }
        return section;
    }

    /**
     * Collect icon click event
     */
    private class CollectOnClick implements View.OnClickListener {
        private ImageView ivCollect;
        private int mmPosition;

        CollectOnClick(ImageView iv, int position) {
            ivCollect = iv;
            mmPosition = position;
        }

        @Override
        public void onClick(View v) {
            LogUtil.i(TAG,"click");
            if (mCollectListener != null) {
                mCollectListener.onClickCollectBtn(ivCollect, mmPosition);
            }
        }
    }

}
