package com.egar.radio.fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.egar.mediaui.util.LogUtil;

/**
 * Base Fragment
 *
 */
public abstract class BaseAppV4Fragment extends android.support.v4.app.Fragment {
    private static final String TAG = "BaseAppV4Fragment";

    private void unbindDrawables(View view) {
        LogUtil.d(TAG,"unbindDrawables()   view: " + view);
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtil.i(TAG,"onDestroyView()");
    }
}
