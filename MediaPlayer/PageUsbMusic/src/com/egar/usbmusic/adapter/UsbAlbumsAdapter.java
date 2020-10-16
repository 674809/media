package com.egar.usbmusic.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.egar.mediaui.R;
import com.egar.mediaui.util.LogUtil;
import com.egar.usbmusic.interfaces.CollectListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import juns.lib.media.bean.FilterMedia;
import juns.lib.media.bean.ProAudio;
import juns.lib.media.flags.MediaCollectState;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/17 19:46
 * @see {@link }
 */
public class UsbAlbumsAdapter<T> extends BaseAdapter {
    private String TAG = "FoldersAdapter";
    private Context mContext;
    private List<T> mListData;
    /**
     * Playing media.
     */
    private ProAudio mPlayingMedia;
    private String mPlayingMediaFolderPath = "";
    private int mPage = 0;//一级界面
    private CollectListener mCollectListener;
    private boolean mIsScrolling = false;


    public void setCollectListener(CollectListener l) {
        mCollectListener = l;
    }

    public UsbAlbumsAdapter(Context context) {
        this.mContext = context;
        mListData = (List<T>) new ArrayList<>();
    }

    public void refreshData(List<T> lists, ProAudio currMedia) {
        synchronized (this) {
            mListData.clear();
            mListData.addAll(lists);
            setPlayingMedia(currMedia);
            notifyDataSetChanged();
        }

    }

    private void setPlayingMedia(ProAudio playingMedia) {
        try {
            this.mPlayingMedia = playingMedia;
            String playingMediaUrl = mPlayingMedia.getMediaUrl();
            this.mPlayingMediaFolderPath = playingMediaUrl.substring(0, playingMediaUrl.lastIndexOf("/"));
        } catch (Exception e) {
        }
    }

    public void setmPage(int i) {
        mPage = i;
    }

    public int getmPage() {
        return mPage;
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public Object getItem(int position) {
        return getItemId(position);
    }

    public T getItemInfo(int position) {
        try {
            return mListData.get(position);
        } catch (Exception e) {
            return null;
        }
    }
    public void setScrollState(boolean isScrolling) {
        mIsScrolling = isScrolling;
        if (!isScrolling) {
            notifyDataSetChanged();
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
            convertView = View.inflate(mContext, R.layout.usb_music_albums_item, null);
            holder = new ViewHolder();
            holder.tv_text = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_id = (TextView) convertView.findViewById(R.id.tv_id);
            holder.imageView = (ImageView) convertView.findViewById(R.id.image_c);
            holder.image_pic = (ImageView) convertView.findViewById(R.id.image_pic);
            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_id.setText(position + 1 + "");
        if (0 == mPage) {
            FilterMedia filterMedia = (FilterMedia) mListData.get(position);
            holder.tv_text.setText(filterMedia.sortStr);
            holder.tv_id.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.GONE);
            holder.image_pic.setVisibility(View.GONE);
        } else {
            ProAudio audio = (ProAudio) mListData.get(position);
            holder.tv_text.setText(audio.getTitle());
            holder.tv_id.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.image_pic.setVisibility(View.VISIBLE);
            if(!mIsScrolling){
                try {
                    String path = audio.getCoverUrl();
                  //  Bitmap bitmap = FragUtil.getLoacalBitmap(path);
                    File coverPicFile = new File(path);
                    if (coverPicFile.exists()) {
                        holder.image_pic.setImageURI(Uri.parse(path));
                    } else {
                         holder.image_pic.setImageResource(R.drawable.album_bg_em);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            int collectState = audio.getCollected();
            switch (collectState) {
                case MediaCollectState.COLLECTED:
                    holder.imageView.setImageResource(R.drawable.favor_c);
                    break;
                case MediaCollectState.UN_COLLECTED:
                    holder.imageView.setImageResource(R.drawable.favor_c_n);
                    break;
            }
            holder.imageView.setOnClickListener(new CollectOnClick(holder.imageView, position));
        }


        //////////////
        if (position == selectItme) {
            convertView.setBackgroundColor(Color.BLUE);
        } else {
            convertView.setBackgroundResource(R.drawable.keyboard_n);
        }
        return convertView;

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
            LogUtil.i(TAG, "click");
            if (mCollectListener != null) {
                mCollectListener.onClickCollectBtn(ivCollect, mmPosition);
            }
        }
    }

    public int selectItme = -1;

    public void setSelectItem(int selectItme) {
        this.selectItme = selectItme;

    }

    static class ViewHolder {
        TextView tv_text;
        TextView tv_id;
        ImageView imageView;
        ImageView image_pic;
    }

    public int getPositionForSection(char c) {
        int position = -1;
        if (0 == mPage) {
            try {
                for (int idx = 0; idx < getCount(); idx++) {
                    FilterMedia filterMedia = (FilterMedia) getItemInfo(idx);
                    if (filterMedia == null) {
                        continue;
                    }
                    char firstChar = filterMedia.sortStrPinYin.charAt(0);
                    if (firstChar == c) {
                        position = idx;
                        break;
                    }
                }
            } catch (Exception e) {
            }
        } else {
            try {
                for (int idx = 0; idx < getCount(); idx++) {
                    ProAudio media = (ProAudio) getItemInfo(idx);
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
                e.printStackTrace();
            }
        }

        return position;
    }

    public int getSectionForPosition(int position) {
        int section = -1;
        if (0 == mPage) {
            try {
                FilterMedia filterMedia = (FilterMedia) getItemInfo(position);
                if (filterMedia != null) {
                    section = filterMedia.sortStrPinYin.charAt(0);

                }
            } catch (Exception e) {

            }
        } else {
            try {
                ProAudio media = (ProAudio) getItemInfo(position);
                if (media != null) {
                    section = media.getTitlePinYin().charAt(0);
                }
            } catch (Exception e) {

            }
        }

        return section;
    }
}
