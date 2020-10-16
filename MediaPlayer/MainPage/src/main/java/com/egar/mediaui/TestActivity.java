package com.egar.mediaui;

import android.app.Activity;
import android.content.res.Configuration;
import android.egar.ActivityPolicyClient;
import android.egar.CarManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.egar.mediaui.util.LogUtil;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/11/20 15:34
 * @see {@link }
 */
public class TestActivity extends Activity {
    private   ActivityPolicyClient activityPolicyClient;
    private String TAG ="TestActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        CarManager  carManager = new CarManager(App.getContext());
         activityPolicyClient = carManager.getActivityPolicy(App.getContext());

        findViewById(R.id.bt1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityPolicyClient.switchLayout(TestActivity.this,false);
            }
        });
        findViewById(R.id.bt2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityPolicyClient.switchLayout(TestActivity.this,true);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogUtil.i(TAG,"onConfigurationChanged");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG,"onDestroy");
    }
}
