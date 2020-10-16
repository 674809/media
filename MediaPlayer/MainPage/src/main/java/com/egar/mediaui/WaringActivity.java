package com.egar.mediaui;

import android.app.Activity;
import android.egar.CanbusProxyClient;
import android.egar.CarManager;
import android.egar.ICarSpeed;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;

import com.egar.mediaui.present.MainPresent;

public class WaringActivity extends Activity {
    private static String TAG = "WaringActivity";
    private CanbusProxyClient canbusProxyClient;
    private CarManager mCarManager;
    private CanSpeed carpeed;
    private float carspeed = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waring);
        if (mCarManager == null) {
            mCarManager = new CarManager(App.getContext());
        }

    }

    public void regiest() {
        canbusProxyClient = mCarManager.getCanbusProxy(App.getContext());
        carpeed = new CanSpeed();
        canbusProxyClient.registerCarSpeed(carpeed);
    }

    class CanSpeed extends ICarSpeed.Stub {
        @Override
        public void onCarSpeed(float v) throws RemoteException {
            carspeed = v;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent");
        if (carspeed <= 5) {
            finish();
        }
        return super.onTouchEvent(event);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(canbusProxyClient !=null){
            canbusProxyClient.registerCarSpeed(null);
        }

    }
}
