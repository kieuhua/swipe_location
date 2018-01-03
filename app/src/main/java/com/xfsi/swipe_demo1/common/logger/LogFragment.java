package com.xfsi.swipe_demo1.common.logger;


import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import java.util.zip.Inflater;

/**
 * Created by local-kieu on 1/17/16.
 * Create a Fragment to house LogView
 *  - 2 fields(LogView, ScrollView), def constructor, getter LogView
 *  - @Override View onCreateView() add TextWatch listener interface to LogView
 *  - View inflateViews() used by onCreateView()
 */
public class LogFragment extends Fragment {
    // 2 fields, 1 defconstructor, 1 getter
    private LogView lv;
    private ScrollView sv;

    public LogFragment() {}
    public LogView getLogView() { return lv; }

    /* 1 helper, use in onCreateView
        set Layout Params for both(LogView, ScrollView)
        for lv, set clickable, focusable, typeface, paddings, gravity, textappearance
        add lv View to sv.
        return sv
    */
    public View inflateViews() {
        lv = new LogView(getActivity());
        sv = new ScrollView(getActivity());

        // set Layout params for both
        ViewGroup.LayoutParams svParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        sv.setLayoutParams(svParams);
        ViewGroup.LayoutParams lvParams = new ViewGroup.LayoutParams(svParams);
        lvParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lv.setLayoutParams(lvParams);

        // for lv set clickable, focusable, typeface, paddings, gravity, textappearance
        lv.setClickable(true);
        lv.setFocusable(true);
        lv.setTypeface(Typeface.MONOSPACE);

        // compute paddings
        int padDips = 16;
        double scale = getResources().getDisplayMetrics().density;
        int padPixels = (int)((scale * padDips) + .5) ;
        lv.setPadding(padPixels, padPixels, padPixels, padPixels);

        lv.setGravity(Gravity.BOTTOM);
        if ( Build.VERSION.SDK_INT >= 23) {
            lv.setTextAppearance(android.R.style.TextAppearance_Holo_Medium);
        }
        sv.addView(lv);
        return sv;
    }

    // add TextWatcherListener interface to LogView, only implement afterTextChanged()
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        View result;

        result = inflateViews();

        lv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                sv.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
        return result;
    }
}
