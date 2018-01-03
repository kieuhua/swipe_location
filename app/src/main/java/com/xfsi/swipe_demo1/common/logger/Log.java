package com.xfsi.swipe_demo1.common.logger;

/**
 * Created by local-kieu on 1/17/16.
 * only used in logger system: LogNode, LogView, LogWrapper
 * Log uses LogNode.println, but it doesn't implement it, what that means
 *      not like in LogWrapper it declare implements ???
 * define 7 constants
 * define mLogNode, getter & setter
 * define 16 methods - 2(println); 8(v,i,d,e); 3(w); 3(wtf)
 */
public class Log {
    //define 7 constants
    public static final int NONE = -1;
    public static final int ASSERT = android.util.Log.ASSERT;
    public static final int VERBOSE = android.util.Log.VERBOSE;
    public static final int INFO = android.util.Log.INFO;
    public static final int WARN = android.util.Log.WARN;
    public static final int DEBUG = android.util.Log.DEBUG;
    public static final int ERROR = android.util.Log.ERROR;

    // define mLogNode
    private static LogNode mLogNode;
    public static LogNode getmLogNode() { return mLogNode;}
    public static void setmLogNode(LogNode node){mLogNode=node;}

    // define 2(println)
    public static void println(int priority, String tag, String msg, Throwable tr) {
        if (mLogNode != null) { mLogNode.println(priority, tag, msg, tr); }
    }
    public static void println(int priority, String tag, String msg) {
        println(priority, tag, msg, null);
    }

    // 8(v,i,d,e)
    public static void v(String tag, String msg, Throwable tr) { println(VERBOSE, tag, msg, tr);}
    public static void v(String tag, String msg) { v(tag, msg, null);}
    public static void i(String tag, String msg, Throwable tr) { println(INFO, tag, msg, tr);}
    public static void i(String tag, String msg) { v(tag, msg, null);}
    public static void d(String tag, String msg, Throwable tr) { println(DEBUG, tag, msg, tr);}
    public static void d(String tag, String msg) { d(tag, msg, null);}
    public static void e(String tag, String msg, Throwable tr) { println(ERROR, tag, msg, tr);}
    public static void e(String tag, String msg) { e(tag, msg, null);}

    // 3(w)
    public static void w(String tag, String msg, Throwable tr) { println(WARN, tag, msg, tr);}
    public static void w(String tag, String msg) { w(tag, msg, null);}
    public static void w(String tag, Throwable tr) { w(tag, null, tr);}

    // 3(wtf)
    public static void wtf(String tag, String msg, Throwable tr) { println(ASSERT, tag, msg, tr);}
    public static void wtf(String tag, String msg) { w(tag, msg, null);}
    public static void wtf(String tag, Throwable tr) { w(tag, null, tr);}
}
