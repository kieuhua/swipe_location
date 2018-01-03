package com.xfsi.swipe_demo1.contents;

import android.widget.TextView;

/**
 * Created by local-kieu on 3/22/16.
 */
public class ContactViewBinder {
    public static void bind(Contact c, TextView tv) {
        tv.setText(c.getName());
        tv.setCompoundDrawablesRelativeWithIntrinsicBounds(c.getIcon(), 0, 0, 0);
    }
}
