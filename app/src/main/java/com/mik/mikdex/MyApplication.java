package com.mik.mikdex;

import android.app.Application;
import android.support.annotation.Keep;
import android.util.Log;

/**
 * @author Lance
 * @date 2017/12/25
 */
@Keep
public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Application", "MyApplication onCreate");
    }
}
