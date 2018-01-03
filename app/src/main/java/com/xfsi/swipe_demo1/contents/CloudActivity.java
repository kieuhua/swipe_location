package com.xfsi.swipe_demo1.contents;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.xfsi.swipe_demo1.R;
import com.xfsi.swipe_demo1.common.logger.Log;

/**
 * Created by local-kieu on 3/18/16.
 */
public class CloudActivity extends AppCompatActivity {
    public static final String TAG = "CloudActivity";
    public static final String FRAGTAG2 = "CloudFragment";
    private CoordinatorLayout coordLayout;

    private static final String AUTHORITY = "com.xfsi.swipe_demo1.storageprovider.documents";
    private boolean mLoggedIn = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud);

        coordLayout = (CoordinatorLayout) findViewById(R.id.main_cloud);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (null == savedInstanceState) {
            FragmentTransaction tr3 = getSupportFragmentManager().beginTransaction();
            MyCloudFragment fg3 = new MyCloudFragment();
            tr3.replace(R.id.cloud_fragment, fg3);
            tr3.commit();
            Snackbar.make(coordLayout, "CloudActivity:cloud_long", Snackbar.LENGTH_LONG).show();
        }

        mLoggedIn = readLoginValue();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_cloud);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(coordLayout, "These are cloud features.", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cloud_login) {
            toggleLogin();
            item.setTitle(mLoggedIn ? R.string.logged_out_info : R.string.logged_in_info);
            Toast.makeText(this, "MyCloudFragment:ItemSelected.", Toast.LENGTH_LONG).show();
            // this is where you call MyCloudProvider.java
            getContentResolver().notifyChange(DocumentsContract.buildRootsUri(AUTHORITY), null, false);

        }
        return true;
    }

    // I think I have problem here, why do I have two cloud login on action bar???
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cloud, menu);
        MenuItem item= menu.findItem(R.id.cloud_login);
        item.setTitle(mLoggedIn ? R.string.logged_out_info : R.string.logged_in_info);
        return true;
    }
    /*
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.cloud_login);
        item.setTitle(mLoggedIn ? R.string.logged_out_info : R.string.logged_in_info);
    }*/

    private void toggleLogin() {
        mLoggedIn = !mLoggedIn;
        writeLoginValue(mLoggedIn);
        // I need getString here, because R.string.logged_in_info => int
        Log.i(TAG, mLoggedIn ? getString(R.string.logged_in_info) : getString(R.string.logged_out_info));
    }

    private void writeLoginValue(boolean loggedIn) {
        mLoggedIn = loggedIn;
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        sp.edit().putBoolean(getString(R.string.key_logged_in), loggedIn).commit();
    }

    private boolean readLoginValue() {
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        return sp.getBoolean(getString(R.string.key_logged_in), false);
    }
}