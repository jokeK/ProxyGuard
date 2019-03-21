package com.mik.mikdex;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * @author Lance
 * @date 2017/12/28
 */

public class MyService extends Service {

    public static final String TAG = "MyService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "service:" + getApplication());
        Log.e(TAG, "service:" + getApplicationContext());
        Log.e(TAG, "service:" + getApplicationInfo().className);
    }
}
