package com.xfsi.swipe_demo1.common.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.xfsi.swipe_demo1.common.logger.Log;
import com.xfsi.swipe_demo1.common.logger.LogWrapper;

/**
 * Created by local-kieu on 1/17/16.
 */
public class SampleActivityBase extends FragmentActivity {
    // TAG, onCreate, onStart, helper= intializeLogging
    public static final String TAG = "SampleActivityBase";

    @Override protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    }

    @Override public void onStart() {
        super.onStart();
        initializeLogging();
    }
    public void initializeLogging() {
        LogWrapper logWrapper = new LogWrapper();
        Log.setmLogNode(logWrapper);
        Log.i(TAG, "Ready");
    }
}
