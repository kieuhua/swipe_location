package com.xfsi.swipe_demo1.contents;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.xfsi.swipe_demo1.R;
import com.xfsi.swipe_demo1.common.logger.Log;

/**
 * Created by local-kieu on 3/18/16.
 */
public class MyCloudFragment extends Fragment {
    private static final String TAG = "MyCloudFragment";

    @Override public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    // k this is very important, if I don't have it, I will get Fragment inflate error
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b){
        return inflater.inflate(R.layout.fragment_cloud, container, false);
    }


}
