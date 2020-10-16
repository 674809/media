package com.egar.radio.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egar.mediaui.R;
import com.egar.radio.customizeview.ModifyNickNameDialog;
import com.egar.radio.utils.LogUtil;

public class ModifyNickNameActivity extends Activity implements View.OnClickListener,ModifyNickNameDialog.ModifyNickNameDelegate {

    private static final String TAG = "ModifyNickNameActivity";

    private EditText mETRadiouiModityNickname;
    private RelativeLayout mRLRadiouiMocifyNicknameBack;
    private ModifyNickNameDialog mModifyNickNameDialog;
    private ImageView mIVRadiouiRenameDeleteAll;
    private ImageView mIVRadiouiRenameRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_nick_name);
        setStatusBar();
        initView();
    }

    private void initView() {
        mETRadiouiModityNickname = (EditText)findViewById(R.id.radioui_modity_nickname_et);
        mIVRadiouiRenameDeleteAll = (ImageView)findViewById(R.id.radioui_modify_nickname_rename_deleteall_iv);
        mIVRadiouiRenameRemove = (ImageView)findViewById(R.id.radioui_modify_nickname_rename_remove_iv);
        mRLRadiouiMocifyNicknameBack = (RelativeLayout)findViewById(R.id.radioui_modity_nickname_back_rl);
        mIVRadiouiRenameDeleteAll.setOnClickListener(this);
        mIVRadiouiRenameRemove.setOnClickListener(this);
        mRLRadiouiMocifyNicknameBack.setOnClickListener(this);

        initModifyNickNameDialog();

    }

    private void initModifyNickNameDialog() {
        mModifyNickNameDialog = new ModifyNickNameDialog(this);
        mModifyNickNameDialog.register(this);

    }


    /**
     * 沉浸式状态栏
     *
     */
    private void setStatusBar() {

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);

        ViewGroup mContentView = (ViewGroup) this.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, true);
        }

    }

    @Override
    public void onClick(View v) {
        LogUtil.d(TAG,"");
        switch (v.getId()){
            case R.id.radioui_modify_nickname_rename_remove_iv:
                int index=mETRadiouiModityNickname.getSelectionStart();   //获取Edittext光标所在位置
                String deleteCharNewName=mETRadiouiModityNickname.getText().toString();
                if (!deleteCharNewName.equals("")) {//判断输入框不为空，执行删除
                    mETRadiouiModityNickname.getText().delete(index-1,index);
                }
                break;

            case R.id.radioui_modify_nickname_rename_deleteall_iv:
                String removeName=mETRadiouiModityNickname.getText().toString();
                if (!removeName.equals("")) {//判断输入框不为空，执行清空
                    mETRadiouiModityNickname.setText("");
                }
                break;
            case R.id.radioui_modity_nickname_back_rl:
                showConfirmDialog();
                break;
        }
    }

    private void showConfirmDialog() {
        mModifyNickNameDialog.show();
    }

    @Override
    public void onOkClick() {
        String name=mETRadiouiModityNickname.getText().toString();
        Intent i= new Intent();

        String newName = "";
        if((null != name )&&(!name.isEmpty())&&(name.length() != 0)&&(!name.trim().isEmpty())){
            newName = mETRadiouiModityNickname.getText().toString();
        }
        i.putExtra("result", newName);
        setResult(3, i);
        mModifyNickNameDialog.dismiss();
        finish();
    }

    @Override
    public void onCancelClick() {
        Intent i= new Intent();
        String newName = "";
        i.putExtra("result", newName);
        setResult(3, i);
        mModifyNickNameDialog.dismiss();
        finish();
    }

}
