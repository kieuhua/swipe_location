package com.xfsi.swipe_demo1.common.logger;

import android.util.Log;

/**
 * Created by local-kieu on 1/17/16.
 */
public class LogWrapper implements LogNode {
    private LogNode mNext;
    public LogNode getmNext() {return mNext;}
    public void setmNext(LogNode node) { mNext=node;}

    @Override public void println(int priority, String tag, String msg, Throwable tr) {
        String useMsg = msg;
        if (null == useMsg) { useMsg = ""; }
        if (tr != null) { msg += "\n" + android.util.Log.getStackTraceString(tr); }
       //k  Log.println(priority, tag, msg); doesn't fix memory leak
        Log.println(priority, tag, useMsg);
        // if (mNext != null) { println(priority, tag, msg, tr);} here is the problem
        if (mNext != null) { mNext.println(priority, tag, msg, tr);}
    }
}
