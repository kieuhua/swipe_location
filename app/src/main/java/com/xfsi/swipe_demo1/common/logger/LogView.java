package com.xfsi.swipe_demo1.common.logger;

import android.app.Activity;
import android.content.Context;
import android.util.*;
//import android.util.Log;
import android.widget.TextView;

/**
 * Created by local-kieu on 1/17/16.
 * purpose to implement println as following:
 *  - convert int prority and Throwable tr to string
 *  - concate priority, tag, msg, tr with delimiter
 *  - display this string on UI thread
 */
public class LogView extends TextView implements LogNode {
    // 3 constructors
    public LogView(Context context) { super(context);}
    public LogView(Context context, AttributeSet attrs) { super(context, attrs);}
    public LogView(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle);}

    // 1 field, getter&setter
    LogNode mNext;
    public LogNode getmNext() { return mNext; }
    public void setmNext(LogNode node) { mNext = node; }

    /* implement println - concatenate 4 params into StringBuilder,
    then display on Ui thread
    */
    public void println(int priority, String tag, String msg, Throwable tr) {

        // convert priority into String
        String priorityStr = null;
        switch (priority) {
            case android.util.Log.VERBOSE:
                priorityStr = "VERBOSE";
                break;
            case Log.INFO:
                priorityStr = "INFO";
                break;
            case Log.WARN:
                priorityStr = "WARN";
                break;
            case Log.DEBUG:
                priorityStr = "DEBUG";
                break;
            case Log.ERROR:
                priorityStr = "ERROR";
                break;
            case Log.ASSERT:
                priorityStr = "ASSERT";
                break;
            default:
                break;
        }

        // convert Throwable to String
        String exceptionStr = null;
        if (tr != null) { exceptionStr = android.util.Log.getStackTraceString(tr);}

        // concate 4 params into StringBuilder
        final StringBuilder output = new StringBuilder();
        String del = "\t";
        appendIfNotNull(output, priorityStr, del);
        appendIfNotNull(output, tag, del);
        appendIfNotNull(output, msg, del);
        appendIfNotNull(output, exceptionStr, del);

        // display output on UI thread
        ((Activity)getContext()).runOnUiThread(new Thread(new Runnable() {
            @Override
            public void run() {
                appendToLog(output.toString());
            }
        }));

        // call next LogNode
        if ( mNext != null) { mNext.println(priority, tag, msg, tr);}
    }

    // 2 helpers
    public StringBuilder appendIfNotNull(StringBuilder source, String addStr, String del) {
        if (addStr != null) {
            if (addStr.length() == 0 ) { del = ""; }
            return source.append(addStr).append(del);
        }
        return source;
    }

    public void appendToLog(String s) { append("\n" + s);}
}
