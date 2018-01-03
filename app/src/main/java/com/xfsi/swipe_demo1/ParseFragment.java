package com.xfsi.swipe_demo1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by local-kieu on 3/6/16.
 */
public class ParseFragment extends Fragment {
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icile){
        return inflater.inflate(R.layout.fragment_parse, container, false);
    }

    @Override public void onViewCreated(View v, Bundle icicle ){

    }
}
