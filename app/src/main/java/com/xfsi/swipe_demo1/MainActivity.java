package com.xfsi.swipe_demo1;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.xfsi.swipe_demo1.common.activities.SampleActivityBase;
import com.xfsi.swipe_demo1.common.logger.Log;
import com.xfsi.swipe_demo1.common.logger.LogFragment;
import com.xfsi.swipe_demo1.common.logger.LogWrapper;
import com.xfsi.swipe_demo1.common.logger.MessageOnlyLogFilter;
import com.xfsi.swipe_demo1.contents.ContentsActivity;
import com.xfsi.swipe_demo1.contents.SnackbarActivity;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private boolean mLogShown;
    private CoordinatorLayout coordLayout;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordLayout = (CoordinatorLayout) findViewById(R.id.main_content);

        if (savedInstanceState == null) {
            FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
            com.xfsi.swipe_demo1.SlidingTabsBasicFragment fragment =
                    new com.xfsi.swipe_demo1.SlidingTabsBasicFragment();
            tr.replace(R.id.sample_content_fragment, fragment);
            tr.commit();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "K main FAB current Battery Level = " + getBatteryLevelStr(getApplicationContext()), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logToggle = menu.findItem(R.id.menu_toggle_log);
        logToggle.setVisible(findViewById(R.id.sample_output) instanceof ViewAnimator);
        logToggle.setTitle(mLogShown ? R.string.sample_hide_log : R.string.sample_show_log);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_toggle_log:
                mLogShown = !mLogShown;
                ViewAnimator output = (ViewAnimator) findViewById(R.id.sample_output);
                if (mLogShown) {
                    output.setDisplayedChild(1);
                } else {
                    output.setDisplayedChild(0);
                }
                supportInvalidateOptionsMenu();
                return true;
            case R.id.sample_snackbar:
                // create intent and start SnackbarActivity
               // Toast.makeText(this, "Before Snackbar Activity", Toast.LENGTH_SHORT);
               // Snackbar.make(coordLayout,"select Snackbar", Snackbar.LENGTH_SHORT);
                Intent sbIntent = new Intent(this,SnackbarActivity.class);
                startActivity(sbIntent);

                return true;
            case R.id.contents_provider:
                Intent cpIntent = new Intent(this, ContentsActivity.class);
                startActivity(cpIntent);
                return true;
            case R.id.parse_activity:
                Intent pIntent = new Intent(this, ParseActivity.class);
                startActivity(pIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onStart() {
        super.onStart();
        initializeLogging();
    }

    public void initializeLogging() {
        LogWrapper logWrapper = new LogWrapper();
        Log.setmLogNode(logWrapper);

        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setmNext(msgFilter);  //k starts memory problem

        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);
        msgFilter.setmNext(logFragment.getLogView());

        Log.i(TAG, "Ready");
    }

    public static String getBatteryLevelStr(Context context) {

        Intent batIntent = context.registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        int level = batIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        float scale = batIntent.getFloatExtra(BatteryManager.EXTRA_SCALE, -1);
        float batLevel = Math.abs(level / scale);

        String batLevelStr = Float.valueOf(batLevel).toString() + "%";

        return batLevelStr;
    }
}
