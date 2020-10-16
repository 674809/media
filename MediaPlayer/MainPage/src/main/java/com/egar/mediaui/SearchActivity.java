package com.egar.mediaui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.egar.usbimage.engine.AllImageSearch;
import com.egar.usbmusic.adapter.SearchAdapter;
import com.egar.usbmusic.model.AllSongsSearch;
import com.egar.usbmusic.present.MusicPresent;
import com.egar.usbvideo.model.AllVideoSearch;
import com.egar.usbvideo.present.VideoPresent;

import java.util.ArrayList;
import java.util.List;

import juns.lib.media.bean.ProAudio;
import juns.lib.media.bean.ProImage;
import juns.lib.media.bean.ProVideo;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2020/6/19 16:54
 * @see {@link }
 */
public class SearchActivity extends Activity implements View.OnClickListener, AllSongsSearch.IAllSongsDataChange,
        AdapterView.OnItemClickListener, AllVideoSearch.IVideoFilesDataListener, AllImageSearch.IImageFilesDataListener {
    private String TAG = "SearchActivity";
    private EditText editText;
    private AllSongsSearch mAllSongsSearch;
    private AllVideoSearch mAllVideoSearch;
    private ImageView mImg_delete;
    private ListView mListSearch;
    private SearchAdapter mSearchAdapter;
    public static String MUSIC = "MUSIC";
    public static String VIDEO = "VIDEO";
    public static String IMAGE = "IMAGE";
    private List mListData = new ArrayList<>();

    public String mType;
    private AllImageSearch mAllImageSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();
        mType = intent.getStringExtra("TYPE");
        Log.i(TAG, "mType = " + mType);
        initView();
        initData();
    }


    private void initView() {
        editText = (EditText) findViewById(R.id.ed_search);
        findViewById(R.id.back).setOnClickListener(this);
        mImg_delete = (ImageView) findViewById(R.id.delete);
        mImg_delete.setOnClickListener(this);
        mListSearch = (ListView) findViewById(R.id.list_search);
        editText.addTextChangedListener(new EditListener());
        mListSearch.setOnItemClickListener(this);
    }

    private void initData() {
        if (mType.equals(MUSIC)) {
            mAllSongsSearch = new AllSongsSearch();
            mAllSongsSearch.setAllSongDataChangeListener(this);
        } else if (mType.equals(VIDEO)) {
            mAllVideoSearch = new AllVideoSearch();
            mAllVideoSearch.setVideoFilsDataListener(this);
        }else if (mType.equals(IMAGE)) {
            mAllImageSearch = new AllImageSearch();
            mAllImageSearch.setImageFilsDataListener(this);
        }
        mSearchAdapter = new SearchAdapter(this, mType, mListData);
        mListSearch.setAdapter(mSearchAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
              finish();
                break;
            case R.id.delete:
                deleteText();
                break;
        }
    }



    @Override
    public void AllSongsDateChage(List<ProAudio> list) {
        Log.i(TAG, "list size =" + list.size());
        mListData.clear();
        mListData.addAll(list);
        if (mSearchAdapter != null) {
            mSearchAdapter.refresh(mListData);
        }
    }

    @Override
    public void VideoFilesDataChange(List<ProVideo> aVoid) {
        Log.i(TAG, "list size =" + aVoid.size());
        mListData.clear();
        mListData.addAll(aVoid);
        if (mSearchAdapter != null) {
            mSearchAdapter.refresh(mListData);
        }
    }

    @Override
    public void imageFilesDataChange(List<ProImage> aVoid) {
        Log.i(TAG, "list size =" + aVoid.size());
        mListData.clear();
        mListData.addAll(aVoid);
        if (mSearchAdapter != null) {
            mSearchAdapter.refresh(mListData);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (mType.equals(MUSIC)) {
            ProAudio itemMedia = (ProAudio) mListData.get(position);
            //MusicPresent.getInstance().playMusic(itemMedia.getMediaUrl());
            MusicPresent.getInstance().playByUrlByUser(itemMedia.getMediaUrl());
        } else if (mType.equals(VIDEO)) {
            ProVideo proVideo = (ProVideo) mListData.get(position);
            VideoPresent.getInstance().play(proVideo.getMediaUrl());
        } else if (mType.equals(IMAGE)) {
            ProImage proImage = (ProImage) mListData.get(position);
            Log.i(TAG, "onItemClick: proImage = "+proImage.getMediaUrl());
            Intent intent = new Intent();
            intent.putExtra("play_image",proImage);
            setResult(333,intent);
        }
        finish();

    }



    public class EditListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //  Log.i(TAG,"beforeTextChanged s = "+s);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.i(TAG, "onTextChanged   mType = " + mType + ">>> s = " + s);
            if (mType.equals(MUSIC)) {
                if (s.toString().isEmpty() || s == null) {
                    mAllSongsSearch.loadFilters("-1");
                } else {
                    mAllSongsSearch.loadFilters(s.toString());
                }
            } else if (mType.equals(VIDEO)) {
                Log.i(TAG, "VIDEO   search = " + s);
                if (s.toString().isEmpty() || s == null) {
                    mAllVideoSearch.LoadData("-1");
                } else {
                    mAllVideoSearch.LoadData(s.toString());
                }
            } else if (mType.equals(IMAGE)) {
                if (s.toString().isEmpty() || s == null) {
                    mAllImageSearch.LoadData("-1");
                } else {
                    mAllImageSearch.LoadData(s.toString());
                }
            }


        }

        @Override
        public void afterTextChanged(Editable s) {
            //   Log.i(TAG,"afterTextChanged  s = "+s);
        }
    }


    public void deleteText() {
        KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
        editText.dispatchKeyEvent(event);
    }

}
