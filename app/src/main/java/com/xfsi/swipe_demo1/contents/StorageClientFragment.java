package com.xfsi.swipe_demo1.contents;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.provider.SyncStateContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.xfsi.swipe_demo1.R;
import com.xfsi.swipe_demo1.common.logger.Log;

import java.io.EOFException;
import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by local-kieu on 3/12/16.
 */
public class StorageClientFragment extends Fragment {
    public static final String TAG = "StorageClientFragment";
    public static final int READ_REQUEST_CODE = 35;

    @Override public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        performFileSearch();
    }

    private void performFileSearch(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    // Careful!! onActivityResult, not onActivityForResult
    @Override public void onActivityResult(int req, int res, Intent data){

        if (req == READ_REQUEST_CODE && res == Activity.RESULT_OK){
            if (data != null){
                Uri uri = data.getData();
                Log.i(TAG, uri.toString());
                showImage(uri);
            }
        }
    }

    public void showImage(Uri uri){
        if (uri != null){
            FragmentManager fm = getActivity().getSupportFragmentManager();
            ImageDialogFragment fg = new ImageDialogFragment();
            Bundle b = new Bundle();
            b.putParcelable("URI", uri);
            fg.setArguments(b);
            fg.show(fm, "image_dialog");
        }

    }

    public static class ImageDialogFragment extends DialogFragment {
        public Uri mUri;
        private Dialog mDialog;

        @Override public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            if ( savedInstanceState != null){
                // Careful!! get Arguments() not savedInstanceState
                mUri = getArguments().getParcelable("URI");
            }
        }

        @Override public Dialog onCreateDialog(Bundle savedInstanceState){
            // Interesting!! you get the Dialog from super
            mDialog = super.onCreateDialog(savedInstanceState);
            mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            final ImageView imageV = new ImageView(getActivity());
            mDialog.setContentView(imageV);

            AsyncTask<Uri, Void, Bitmap> loadImageTask = new AsyncTask<Uri, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Uri... uris) {
                    dumpImageMetaData(uris[0]);
                    return bitmapFromUri(uris[0]);
                }
                @Override protected void onPostExecute(Bitmap bm){
                    imageV.setImageBitmap(bm);
                }
            };
            loadImageTask.execute(mUri);

            return mDialog;
        }

        @Override
        public void onStop() {
            super.onStop();
            // getDialog() from DialogFragment class
            if(getDialog() != null){
                getDialog().dismiss();
            }
        }

        // create AsynTask LoadImangTask
        private Bitmap bitmapFromUri(Uri uri){
            ParcelFileDescriptor pfd = null;
            if ( uri != null) {
                try {
                    pfd = getActivity().getContentResolver().openFileDescriptor(uri, "r");
                    FileDescriptor fd = pfd.getFileDescriptor();
                    Bitmap bm = BitmapFactory.decodeFileDescriptor(fd);
                    pfd.close();    // I missed this
                    return bm;
                } catch (Exception e) {
                    Log.e(TAG, "Failed to load image.");
                    return null;
                } finally {
                    try {
                        if (pfd != null) {
                            pfd.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Error closing ParcelFileDescriptor.");
                    }
                }
            }
            return null;
        }

        // image meta data, title and size and put them into Log.i
        private void dumpImageMetaData(Uri uri){
            if (uri != null) {
                Cursor cr = getActivity().getContentResolver().query(uri, null, null, null, null, null);
                try {
                    if (cr != null && cr.moveToFirst()) {
                        // Careful!! OpenableColumns not just columns
                        String displayName = cr.getString(cr.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        Log.i(TAG, "Display Name: " + displayName);
                        int sizeIdx = cr.getColumnIndex(OpenableColumns.SIZE);
                        String sizeStr = null;
                        if (cr.isNull(sizeIdx)) {
                            sizeStr = cr.getString(sizeIdx);
                        } else {
                            sizeStr = "Unknown";
                        }
                        Log.i(TAG, "Size: " + sizeStr);
                    }
                } finally {
                    if (cr != null) {
                        cr.close();
                    }
                }
            }
        }
    }
}
