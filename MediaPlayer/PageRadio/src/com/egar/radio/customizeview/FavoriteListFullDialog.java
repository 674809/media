package com.egar.radio.customizeview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.view.Window;

import com.egar.mediaui.R;

/**
 * author : liangxiaobo
 * date : 2020-1-310:45
 */
public class FavoriteListFullDialog extends Dialog {
    private  Context mContext;

    public FavoriteListFullDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public FavoriteListFullDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    protected FavoriteListFullDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.radioui_favoritelistfull_dialog);
        //触摸Dialog边界外消失Dialog
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawable(mContext.getDrawable(R.drawable.radioui_search_dialog_bg));
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mContext = null;
    }
}
