package com.xfsi.swipe_demo1;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import com.xfsi.swipe_demo1.common.logger.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by local-kieu on 3/8/16.
 */
public class FetchAddressIntentService extends IntentService {
    public static final String TAG ="FetchAddressIS";
    protected ResultReceiver mReceiver;

    //This constructor is required, and calls the super IntentService(String)
    // constructor with the name for a worker thread.
    public FetchAddressIntentService() { super(TAG);}

    @Override public void onHandleIntent(Intent intent){
        String errorMsg = "";

        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        if (null == mReceiver){
            Log.wtf(TAG, "No Receiver. There is nowhere to send the results to.");
            return;
        }
        Location loc = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        if (null == loc){
            errorMsg = getString(R.string.no_location_data_provided);
            Log.wtf(TAG, errorMsg);
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMsg);
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
        }catch (IOException e){
            errorMsg = getString(R.string.service_not_available);
            Log.e(TAG, errorMsg);
        }catch (IllegalArgumentException e){
            errorMsg = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMsg + ". Lat: " + loc.getLatitude() + ", Long: " + loc.getLongitude());
        }

        if (addresses == null || addresses.size() == 0){
            if (errorMsg.isEmpty()){
                errorMsg = getString(R.string.no_address_found);
                Log.e(TAG, errorMsg);
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMsg);
        } else {
            Address ad = addresses.get(0);
            ArrayList<String> afrgs = new ArrayList<String>();
            for(int i=0; i< ad.getMaxAddressLineIndex(); i++){
                afrgs.add(ad.getAddressLine(i));
            }
            Log.i(TAG, getString(R.string.address_found));
            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"), afrgs));
        }
    }

    private void deliverResultToReceiver(int resultCode, String msg){
        Bundle b = new Bundle();
        b.putString(Constants.RESULT_DATA_KEY, msg);
        mReceiver.send(resultCode, b);
    }
}
