package com.egar.usbimage.adapter;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.egar.mediaui.R;

import java.util.ArrayList;
import java.util.List;

import juns.lib.media.bean.FilterFolder;
import juns.lib.media.bean.ProImage;

public class ImageGroupAdapter<T> extends RecyclerView.Adapter<ImageGroupAdapter.ViewHolder> implements SectionIndexer {

    private final String TAG = "ImageGroupAdapter";

    private List<T> mListData;
    private boolean mIsGrid = false;

    Fragment mFragment;

    public ImageGroupAdapter(Fragment fragment){
        this.mFragment = fragment;
    }

    public void refresh(List<T> listData, boolean isGrid) {
        synchronized (this) {
            setLayoutFlag(isGrid);
            setListData(listData);
            notifyDataSetChanged();
        }
    }

    public void setLayoutFlag(boolean isGrid) {
        mIsGrid = isGrid;
    }

    public void setListData(List<T> listData) {
        if (mListData == null) {
            mListData = new ArrayList<>();
        } else {
            mListData.clear();
        }
        if (listData == null) {
            mListData.clear();
        } else {
            mListData.addAll(listData);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewHolder vh;
        if (mIsGrid) {
            vh = new ViewHolder(layoutInflater.inflate(R.layout.usb_image_v_item_grid, parent, false));
        } else {
            vh = new ViewHolder(layoutInflater.inflate(R.layout.usb_image_v_item_list, parent, false));
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get item object.
        T itemObj = null;
        try {
            itemObj = mListData.get(position);
        } catch (Exception ignored) {
        }

        // Fill with FilterFolder information.
        if (itemObj instanceof FilterFolder) {
            FilterFolder filter = (FilterFolder) itemObj;
            holder.tv.setText(filter.sortStr);
            holder.iv.setImageResource(R.drawable.image_folder);

            // Fill with ProImage information.
        } else if (itemObj instanceof ProImage) {
            ProImage media = (ProImage) itemObj;
            holder.tv.setText(media.getFileName());

            // Load as GifDrawable
           /* try {
                GifDrawable gifDrawable = new GifDrawable(media.getMediaUrl());
                holder.iv.setImageDrawable(gifDrawable);
            } catch (Exception e) {
                // Load as NORMAL drawable
                holder.iv.setImageURI(Uri.parse(media.getMediaUrl()));
            }*/

            Glide.with(mFragment).load(media.getMediaUrl()).into(holder.iv);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        ImageView imageView=holder.iv;
        if (imageView!=null){
            Glide.with(mFragment).clear(imageView);
        }
    }

    @Override
    public int getItemCount() {
        if (mListData == null) {
            return 0;
        }
        return mListData.size();
    }

    public T getItem(int position) {
        try {
            //noinspection UnnecessaryLocalVariable
            T item = mListData.get(position);
            return item;
        } catch (Exception ignored) {
        }
        return null;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        int position = -1;
        try {
            for (int idx = 0; idx < mListData.size(); idx++) {
                ProImage media = (ProImage) getItem(idx);
                if (media == null) {
                    continue;
                }

                //
                char firstChar = media.getTitlePinYin().charAt(0);
                if (firstChar == sectionIndex) {
                    position = idx;
                    break;
                }

            }
        } catch (Exception e) {
            Log.e(TAG, "getPositionForSection: "+e.getMessage());
        }
        return position;
    }

    @Override
    public int getSectionForPosition(int position) {
        int section = -1;
        try {
            ProImage media = (ProImage) getItem(position);
            if (media != null) {
                section = media.getTitlePinYin().charAt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "getSectionForPosition: "+e.getMessage());
        }
        return section;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView iv;
        final TextView tv;

        ViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.iv_pic);
            tv = (TextView) itemView.findViewById(R.id.tv_desc);
        }
    }
}
