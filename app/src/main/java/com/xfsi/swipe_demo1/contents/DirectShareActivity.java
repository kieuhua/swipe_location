package com.xfsi.swipe_demo1.contents;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import com.xfsi.swipe_demo1.R;

/**
 * Created by local-kieu on 3/22/16.
 */
public class DirectShareActivity extends Activity {
    EditText mBodyET;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directshare);
        // setActionBar level 21
        //if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {

        if (Build.VERSION.SDK_INT >= 23) {
            setActionBar((Toolbar) findViewById(R.id.toolbar));
        }
        mBodyET = (EditText)findViewById(R.id.body);
        findViewById(R.id.share).setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.share:
                    share();
                    return;
            }
        }
    };

    private void share() {
        // create implicite intent, startActivity(createChooser(intent, title);
        if (Build.VERSION.SDK_INT >= 23) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, mBodyET.getText().toString());
            intent.setType("text/plain");
            /* k here is the problem, I can see a list of chooser: gmail, facebook, text...
            but I didn't see MyChooserTargetService.
             */
            startActivity(Intent.createChooser(intent, getString(R.string.send_intent_title)));
        }
    }
}
