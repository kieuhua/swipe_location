package com.xfsi.swipe_demo1.contents;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.app.DialogFragment;
import android.webkit.ConsoleMessage;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.xfsi.swipe_demo1.R;
import com.xfsi.swipe_demo1.common.logger.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by local-kieu on 3/26/16.
 */
public class PermissionRequestActivity extends Activity implements ConfirmationDialogFragment.Listener {
    public static final String TAG = PermissionRequestActivity.class.getSimpleName();
    public static final String FRAGMENT_DIALOG = "dialog";

    int mPort;
    //AssetManager mAssets = null;
    private SimpleWebServer mWebServer;

    private WebView mWebView;
    private WebChromeClient mWebChromeClient;
    private PermissionRequest mPermissionRequest;
    private ConsoleMonitor mConsoleMonitor;

    @Override public void onCreate(Bundle savedInstantceState) {
        super.onCreate(savedInstantceState);
        setContentView(R.layout.activity_permission_request);

        mWebView = (WebView)findViewById(R.id.web_view);
        mWebView.setWebChromeClient(mWebChromeClient);
        configureWebSettings(mWebView.getSettings());

        mWebChromeClient = new WebChromeClient() {

            FragmentManager fm = getFragmentManager();

            @Override
            public void onPermissionRequest(PermissionRequest req) {
                mPermissionRequest = req;
                String[] resources = null;
                if (Build.VERSION.SDK_INT >= 23) {
                    resources = req.getResources();
                }
                ConfirmationDialogFragment.newInstance(resources)
                        .show(fm, FRAGMENT_DIALOG);
            }

            @Override
            public void onPermissionRequestCanceled(PermissionRequest req) {
                mPermissionRequest = null;
                DialogFragment fg = (DialogFragment) fm.findFragmentByTag(FRAGMENT_DIALOG);
                if (null != fg) {
                    fg.dismiss();
                }
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage msg) {
                switch (msg.messageLevel()) {
                    case TIP:
                        Log.i(TAG, msg.message());
                        break;
                    case WARNING:
                        Log.i(TAG, msg.message());
                        break;
                    case LOG:
                        Log.i(TAG, msg.message());
                        break;
                    case ERROR:
                        Log.i(TAG, msg.message());
                        break;
                    case DEBUG:
                        Log.i(TAG, msg.message());
                        break;
                }
                if (null != mConsoleMonitor) {
                    mConsoleMonitor.onConsoleMessage(msg);
                }
                return true;
            }
        };
    }

    @Override public void onResume() {
        super.onResume();
        mPort = 8080;
        mWebServer = new SimpleWebServer(mPort, getResources().getAssets());
        mWebServer.start();
        //mWebView.setInitialScale(100);
       // mWebView.loadUrl("http://localhost:" + mPort + "/sample.html" );
       // mWebView.loadUrl("http://localhost:" + mPort  );
        //mWebView.loadUrl("http://mit.edu" );  // this work
       // mWebView.loadUrl("file:///android_asset/www/sample.html");
        // this load the file
        mWebView.loadUrl("file:///android_asset/sample.html");
    }

    @Override public void onPause() {
        mWebServer.stop();
        super.onPause();
    }

    @SuppressLint("SetJavaScriptEnalbe")
    private static void configureWebSettings(WebSettings settings) {
        settings.setJavaScriptEnabled(true);
    }

    @Override
    public void onConfirmation(boolean allowed) {
        if (Build.VERSION.SDK_INT >= 23 ) {
            if (allowed) {
                mPermissionRequest.grant(mPermissionRequest.getResources());
                Log.d(TAG, "Permission granted.");
            } else {
                mPermissionRequest.deny();
                Log.d(TAG, "Permission request denied.");
            }
        }
    }

    public void setmConsoleMonitor(ConsoleMonitor monitor) {
        mConsoleMonitor = monitor;
    }
    // for testing
    public interface ConsoleMonitor {
        public void onConsoleMessage(ConsoleMessage message);
    }
}
