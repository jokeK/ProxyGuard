package com.mik.mikdex;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mik.mikdex.R;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(TAG, "activity:" + getApplication());
        Log.e(TAG, "activity:" + getApplicationContext());
        Log.e(TAG, "activity:" + getApplicationInfo().className);

        startService(new Intent(this, MyService.class));

        Intent intent = new Intent("com.mik.broadcast.test");
        sendBroadcast(intent);

        getContentResolver().delete(Uri.parse("content://com.mik.myprovider/test"), null,
                null);
    }


}
