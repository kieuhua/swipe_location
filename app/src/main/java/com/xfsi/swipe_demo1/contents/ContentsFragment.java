package com.xfsi.swipe_demo1.contents;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.xfsi.swipe_demo1.R;

/**
 * Created by local-kieu on 3/11/16.
 */
public class ContentsFragment extends Fragment {
    public static final String TAG = "ContentsFragment";



    @Override public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    // k need this
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_contents, container, false);
    }

}
