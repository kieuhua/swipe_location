package com.xfsi.swipe_demo1.contents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xfsi.swipe_demo1.R;
import com.xfsi.swipe_demo1.MainActivity;

/**
 * Created by local-kieu on 2/3/16.
 */
public class SnackbarActivity extends AppCompatActivity {
    private CoordinatorLayout coordLayout;

    @Override public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_snackbar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordLayout = (CoordinatorLayout) findViewById(R.id.coordLayout);

        // find 3 buttons, 1 floating button do something with them
        Button simple_btn = (Button) findViewById(R.id.simple_snackbar);
        simple_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(coordLayout, "K Simple snackbar", Snackbar.LENGTH_SHORT).show();
            }
        });

        final Button action_btn = (Button) findViewById(R.id.sb_action);
        action_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar actionSB = Snackbar.make(coordLayout, "K Message is deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar snackbar1 = Snackbar.make(coordLayout,"K Message is restored!", Snackbar.LENGTH_SHORT );
                        snackbar1.show();
                    }
                });
                actionSB.show();
            }
        });

        Button custom_btn = (Button) findViewById(R.id.custom_color);
        custom_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar sb = Snackbar.make(coordLayout, "K No Internet Connection!", Snackbar.LENGTH_LONG)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        });
                // set the "Retry" to Red
                sb.setActionTextColor(Color.RED);
                // set "K No ..." to Yellow
                View sbV = sb.getView();
                TextView tx = (TextView) sbV.findViewById(android.support.design.R.id.snackbar_text);
                tx.setTextColor(Color.YELLOW);
                sb.show();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_battery);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // here I need to get battery level info and display
                String batLevelStr = MainActivity.getBatteryLevelStr(getApplicationContext());
                Snackbar.make(coordLayout, "K the current battery level = " + batLevelStr, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }




}
