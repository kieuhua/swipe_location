package com.xfsi.swipe_demo1.common.logger;

/**
 * Created by local-kieu on 1/17/16.
 */
public class MessageOnlyLogFilter implements LogNode {
    LogNode mNext;
    public MessageOnlyLogFilter(LogNode next){ mNext = next;}
    public MessageOnlyLogFilter() {}
    public LogNode getmNext() { return mNext;}
    public void setmNext(LogNode node) { mNext = node; }

    @Override public void println(int priority, String tag, String msg, Throwable tr) {
        //if (mNext != null) {mNext.println( Log.NONE, null, msg, null); } k maybe hree not here neither
        if (mNext != null) {getmNext().println( Log.NONE, null, msg, null); }
    }
}
