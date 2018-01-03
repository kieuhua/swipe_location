package com.xfsi.swipe_demo1.common.logger;

/**
 * Created by local-kieu on 1/17/16.
 */
public interface LogNode {
    public void println(int priority, String tag, String msg, Throwable tr);
}
