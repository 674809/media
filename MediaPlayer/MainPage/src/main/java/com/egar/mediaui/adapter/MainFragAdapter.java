package com.egar.mediaui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.egar.mediaui.lib.VPFragStateAdapter;

import java.util.List;

public class MainFragAdapter<T extends Fragment> extends VPFragStateAdapter<T> {
    public MainFragAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public void refresh(List<T> listFms) {
        super.refresh(listFms);
    }
}