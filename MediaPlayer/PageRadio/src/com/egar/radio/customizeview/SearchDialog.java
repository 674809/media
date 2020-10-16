package com.egar.radio.customizeview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.view.Window;
import com.egar.mediaui.R;

/**
 * author : liangxiaobo
 * date : 2019-12-1915:17
 */
public class SearchDialog  extends Dialog {
    private   Context mContext;

    public SearchDialog(@NonNull Context context) {
        super(context);
        setOwnerActivity((Activity) context);
        mContext = context;
    }

    public SearchDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.radioui_search_dialog_layout);
        //触摸Dialog边界外不消失
        setCanceledOnTouchOutside(true);
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
