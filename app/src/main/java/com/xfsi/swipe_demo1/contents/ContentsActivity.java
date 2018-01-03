package com.xfsi.swipe_demo1.contents;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.xfsi.swipe_demo1.R;
import com.xfsi.swipe_demo1.common.logger.Log;

/**
 * Created by local-kieu on 3/11/16.
 */
public class ContentsActivity extends AppCompatActivity {
    public static final String TAG = "ContentsActivity";
    public static final String FRAGTAG1 = "StorageClientFragment";
    private CoordinatorLayout coordLayout;

    @Override public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents);

        coordLayout = (CoordinatorLayout)findViewById(R.id.main_content);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab_contents);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(coordLayout, "These are contents provider features.", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    //k becareful, it is MenuInflater not LayoutInflater, and R.menu not R.layout
    @Override public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_contents, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.client:

                //Toast.makeText(this,"I am in ContentsActivity.", Toast.LENGTH_LONG).show();
                FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
                StorageClientFragment fg = new StorageClientFragment();
                tr.replace(R.id.contents_fragment, fg);
                tr.commit();

                Snackbar.make(coordLayout, "ContentsActivity:client", Snackbar.LENGTH_LONG).show();
                break;
            case R.id.cloud:
                Intent cIntent = new Intent(this, CloudActivity.class);
                startActivity(cIntent);
                return true;
            case R.id.directshare:
                // start DirectShareActivity with R.layout.activity_directshare
                Intent dsIntent = new Intent(this, DirectShareActivity.class);
                startActivity(dsIntent);
                return true;
            case R.id.permissionrequest:
                Intent prIntent = new Intent(this, PermissionRequestActivity.class);
                startActivity(prIntent);
                return true;
        }
        return true;
    }

}
