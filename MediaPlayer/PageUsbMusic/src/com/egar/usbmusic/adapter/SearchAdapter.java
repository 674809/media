package com.egar.usbmusic.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.egar.mediaui.R;

import java.security.Provider;
import java.util.List;

import juns.lib.media.bean.ProAudio;
import juns.lib.media.bean.ProImage;
import juns.lib.media.bean.ProVideo;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2020/6/24 11:44
 * @see {@link }
 */
public class SearchAdapter extends BaseAdapter {

    private Context mContext;
    private List mListData;
    private String mType ;

    public SearchAdapter(Context context, String type, List ListData) {
        mContext = context;
        mType = type;
        refresh(ListData);
    }

    public void refresh(List ListData) {
        mListData = ListData;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public Object getItem(int position) {
        return getItemId(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (null == convertView) {
            convertView = View.inflate(mContext, R.layout.usb_music_search_item, null);
            holder = new ViewHolder();
            holder.tv_text = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(mType.equals("MUSIC")){
           ProAudio music = (ProAudio) mListData.get(position);
           holder.tv_text.setText(music.getFileName());
        }else if(mType.equals("VIDEO")){
            ProVideo video = (ProVideo)mListData.get(position);
            holder.tv_text.setText(video.getFileName());
        }else if(mType.equals("IMAGE")){
            ProImage image = (ProImage)mListData.get(position);
            holder.tv_text.setText(image.getFileName());
        }

        return convertView;
    }


    static class ViewHolder {
        TextView tv_text;
    }
}
