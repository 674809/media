package com.egar.radio.customizeview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egar.mediaui.R;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * author : liangxiaobo
 * date : 2019-12-2315:56
 */
public class ModifyNickNameDialog extends Dialog implements View.OnClickListener {
    private   Context mContext;
    private   Set<ModifyNickNameDelegate> mSetDelegates = new LinkedHashSet<>();
    private TextView mTVRadiouiModifyNickNameFullText;
    private TextView mTVRadiouiModifyNickNameHalfText;
    private boolean isFullScreen;
    private RelativeLayout mRLRadiouiModifyNickNameFull;
    private RelativeLayout mRLRadiouiModifyNickNameHalf;
    private TextView mRLRadiouiModifyNickNameFullOk;
    private TextView mRLRadiouiModifyNickNameFullCancle;
    private TextView mRLRadiouiModifyNickNameHalfOk;
    private TextView mRLRadiouiModifyNickNameHalfCancle;


    public  interface ModifyNickNameDelegate{
        void onOkClick();
        void onCancelClick();
    }

    public  void register(ModifyNickNameDelegate t) {
        if (t != null) {
            mSetDelegates.add(t);
        }
    }

    public  void unregister(ModifyNickNameDelegate t) {
        if (t != null) {
            mSetDelegates.remove(t);
        }
    }

    public ModifyNickNameDialog(@NonNull Context context) {
        super(context);
        mContext = context;

    }

    public ModifyNickNameDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    public void setScreenState(boolean isFullScreen){
        this.isFullScreen = isFullScreen;
        if((null!=mTVRadiouiModifyNickNameFullText)&&(null!=mTVRadiouiModifyNickNameHalfText)){
            if(isFullScreen){
                mTVRadiouiModifyNickNameFullText.setVisibility(View.VISIBLE);
                mTVRadiouiModifyNickNameHalfText.setVisibility(View.GONE);
            }else {
                mTVRadiouiModifyNickNameFullText.setVisibility(View.GONE);
                mTVRadiouiModifyNickNameHalfText.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.radioui_modify_nickname_confirm_dialog_layout);
        //触摸Dialog边界外不消失
        setCanceledOnTouchOutside(true);
        getWindow().setBackgroundDrawable(mContext.getDrawable(R.drawable.radioui_search_dialog_bg));
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mTVRadiouiModifyNickNameFullText = (TextView)findViewById(R.id.radioui_modify_nickname_dialog_text_full_tv);
        mTVRadiouiModifyNickNameHalfText = (TextView)findViewById(R.id.radioui_modify_nickname_dialog_text_half_tv);

        mRLRadiouiModifyNickNameFull = (RelativeLayout)findViewById(R.id.radioui_modify_nickname_dialog_full_rl);
        mRLRadiouiModifyNickNameHalf = (RelativeLayout)findViewById(R.id.radioui_modify_nickname_dialog_half_rl);
        mRLRadiouiModifyNickNameFullOk = (TextView)findViewById(R.id.radioui_modity_nickname_ok_full_tv);
        mRLRadiouiModifyNickNameFullCancle = (TextView)findViewById(R.id.radioui_modity_nickname_cancle_full_tv);
        mRLRadiouiModifyNickNameHalfOk = (TextView)findViewById(R.id.radioui_modity_nickname_ok_half_tv);
        mRLRadiouiModifyNickNameHalfCancle = (TextView)findViewById(R.id.radioui_modity_nickname_cancle_half_tv);
        mRLRadiouiModifyNickNameFullOk.setOnClickListener(this);
        mRLRadiouiModifyNickNameFullCancle.setOnClickListener(this);
        mRLRadiouiModifyNickNameHalfOk.setOnClickListener(this);
        mRLRadiouiModifyNickNameHalfCancle.setOnClickListener(this);

        if(isFullScreen){
            mRLRadiouiModifyNickNameFull.setVisibility(View.VISIBLE);
            mRLRadiouiModifyNickNameHalf.setVisibility(View.GONE);
        }else {
            mRLRadiouiModifyNickNameFull.setVisibility(View.GONE);
            mRLRadiouiModifyNickNameHalf.setVisibility(View.VISIBLE);
        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.radioui_modity_nickname_ok_full_tv:
            case R.id.radioui_modity_nickname_ok_half_tv:
                for (ModifyNickNameDelegate delegate :mSetDelegates){
                    if(delegate !=null){
                        delegate.onOkClick();
                    }
                }
                break;

            case R.id.radioui_modity_nickname_cancle_full_tv:
            case R.id.radioui_modity_nickname_cancle_half_tv:
                for (ModifyNickNameDelegate delegate :mSetDelegates){
                    if(delegate !=null){
                        delegate.onCancelClick();
                    }
                }
                break;
        }
    }
}
