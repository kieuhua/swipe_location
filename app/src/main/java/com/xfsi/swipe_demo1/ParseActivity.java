package com.xfsi.swipe_demo1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.android.gms.location.LocationServices;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.xfsi.swipe_demo1.common.logger.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.parse.Parse;
import com.parse.ParseObject;


/**
 * Created by local-kieu on 3/6/16.
 */
public class
ParseActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    // Address
    protected static final String TAG = "parse-activity";
    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";

    private CoordinatorLayout coordLayout;
    GoogleApiClient mGAC;
    Location mLoc;
    // Address
    protected AddressResultReceiver mReceiver = null;
    protected boolean mAddressReq = false;
    protected String mAddressOutput;
    protected TextView mAddressOutputTV;


    @Override public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse);

        // set toolbar to support action bar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordLayout = (CoordinatorLayout)findViewById(R.id.parse_content);

        if(null == savedInstanceState){
            FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
            com.xfsi.swipe_demo1.ParseFragment fragment = new com.xfsi.swipe_demo1.ParseFragment();
            tr.replace(R.id.parse_fragment, fragment);
            tr.commit();
        }

        updateValuesFromBundle(savedInstanceState);

        // add location stuff
        if (null == mGAC){
            mGAC = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

        }

        // add Address stuff
        // create Receiver
        mReceiver = new AddressResultReceiver(new Handler());
        // find TextView to display address
        mAddressOutputTV = (TextView)findViewById(R.id.section_label);

        // set fab_parse
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab_location);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = "The Device current Location:";
                if (mAddressOutput != null){msg = msg + " near Address: " + mAddressOutput;}
                if (mLoc != null) {msg = msg + ".\n" + "At: " + getLocation();}
                Snackbar.make(coordLayout, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        /* add parser server stuff */

        // Enable local datastore.
        //Parse.enableLocalDatastore(this);
        //ParseObject.registerSubclass(KDevice.class);

        /* not to connect to remote parser server
        Parse.initialize(this, "MyPRE8sesqxSuKiigH44QOf8ctBB1u3cI2reEDyt",
                "01UQnBEm8gtU2CLOe691SIRY4qeYH569fJTjEiLu" );
        */

        // connect to local parse server
        Parse.initialize( new Parse.Configuration.Builder(this)
            .applicationId("MyPRE8sesqxSuKiigH44QOf8ctBB1u3cI2reEDyt")
            .clientKey("01UQnBEm8gtU2CLOe691SIRY4qeYH569fJTjEiLu")
            .server("http://192.168.1.151:1337/parse/")
            .build()
        );

    }

    /* save only mAddressReq and mAddressOutput, I don't need to save mReceiver and mLoc
        because, they only use by startIntentService()
     */
    private void updateValuesFromBundle(Bundle savedInstanceState){
        if (savedInstanceState != null){
            if ( savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)){
                mAddressReq = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }
            if ( savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)){
                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
            }
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_parse, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.newObject:
                createDevObj();
                break;
            case R.id.update:
                updateDevObj();
                break;
            case R.id.search:
                findDevInfo();
                break;
            case R.id.address:
                getAddress();
                break;
            default:
                break;
        }
        return true;
    }
    @Override protected void onStart() {
        mGAC.connect();
        super.onStart();
    }
    @Override protected void onStop() {
        mGAC.disconnect();
        super.onStop();
    }

    @Override public void onConnected(Bundle connectHint){

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED){
            mLoc = LocationServices.FusedLocationApi.getLastLocation(mGAC);
            // add Address lookup stuff
            if (mLoc != null) {
                if (Geocoder.isPresent()) {
                    if (mAddressReq) {
                        startIntentService();
                    }
                } else {
                    Snackbar.make(coordLayout, "Geocoder is not present, so no Address look up", Snackbar.LENGTH_LONG).show();
                }
            }
        } else {
            // I don't see Build.VERSION.VERSON_CODES.Marshmallow
            if (Build.VERSION.SDK_INT >= 23) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Snackbar.make(coordLayout, "This app needs location access permission.", Snackbar.LENGTH_LONG).show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
                            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }
            }
        }

    }
    /* k Location and Address have the same onConnectionFailed and onConnectionSuspended
     */
    @Override public void onConnectionFailed(ConnectionResult result){
        Log.i(TAG, "Connection Failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }
    @Override public void onConnectionSuspended(int cause){
        Log.i(TAG, "Connection Suspended.");
        mGAC.connect();
    }

    @Override public void onRequestPermissionsResult(int reqCode, String[] permissions, int[] grantedResult){

        switch (reqCode){
           // case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION:
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if ( grantedResult.length > 0 && grantedResult[0] == PackageManager.PERMISSION_GRANTED){
                    /* good, even though is red underline, but it compiled and downloaded
                    to my new phone, and it works, see snapshot
                    */
                    mLoc = LocationServices.FusedLocationApi.getLastLocation(mGAC);
                } else {
                    Snackbar.make(coordLayout, "ACCESS_COARSE_LOCATION permission is denied", Snackbar.LENGTH_LONG).show();
                }
                break;
            //other case
        }
    }

    @Override public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressReq);
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);
        super.onSaveInstanceState(savedInstanceState);
    }

    /* create KAndroid object, then save it on parse server
    *   - create KAndroid.java with 3 columns
    * */
    private void createDevObj() {
        /*
        ParseObject kDev = new ParseObject("KDevice");
        kDev.put("email", "kieu.hua@gmail.com");
        kDev.put("address", "77 Strawberry Hill Rd Acton ma");
        kDev.put("battery", 35);
        kDev.saveInBackground();
        Log.i(TAG, "created one kDev object!");
        */

        // create an other GameScore object
        ParseObject game1 = new ParseObject("GameScore");;
        game1.put("score", 55);
        game1.put("playerName", "Morgan");
        game1.put("cheatMode",false);
        game1.saveInBackground();
       // game1.save();
        Log.i(TAG, "created one GameScore object!");
    }



    private void updateDevObj() {}

    private void findDevInfo() {
        /* find email= "kieu1" parse obj */
        /* use ObjectId= zeM734Zslq for now
        ParseQuery<ParseObject> q = ParseQuery.getQuery("KDevice");;
        q.getInBackground("zeM734ZsLq", new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null){
                    Log.i(TAG, "Success retrieved record!");
                } else {
                    Log.i(TAG, "Fail retrieved record!" + e);
                }
            }
        });
        */

        // get GameScore class, id = "objectId":"1pJcYlIfh4"
        ParseQuery<ParseObject> q = ParseQuery.getQuery("GameScore");;
       // q.getInBackground("1pJcYlIfh4", new GetCallback<ParseObject>() {
        q.getInBackground("*", new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null){
                    Log.i(TAG, "Success retrieved record!");
                } else {
                    Log.i(TAG, "Fail retrieved record!" + e);
                }
            }
        });


    }


    private String getLocation(){
        if (mLoc != null) {
            return String.format("Fine The latitude: %f and longitude: %f",
                    mLoc.getLatitude(), mLoc.getLongitude());
        }
        return "Location is null";

    }

    /* check for mGAC and mLoc not null, then call startIntentService(..)
    if it null, change mReqAddress = true; so when mGAC is connected it will
    do the address look up in onConnected()
    */

    private void getAddress() {
        if (mGAC != null && mLoc != null){
            startIntentService();
        }
        mAddressReq = true;
        //updateUI();
    }

    // called by getAddress
    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLoc);
        startService(intent);
    }

    protected void displayAddressOutput() {
        mAddressOutputTV.setText(mAddressOutput);
        Snackbar.make(coordLayout, "Display address: "+mAddressOutput, Snackbar.LENGTH_LONG).show();
    }

    protected void updateUI() {
        mAddressOutputTV.setText(mAddressOutput);
        Snackbar.make(coordLayout,"Address is: "+ mAddressOutput, Snackbar.LENGTH_LONG).show();
    }



    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler){
            super(handler);
        }

        @Override protected void onReceiveResult(int code, Bundle result){
            mAddressOutput = result.getString(Constants.RESULT_DATA_KEY);
            displayAddressOutput();
            if (code == Constants.SUCCESS_RESULT) {
                Snackbar.make(coordLayout, getString(R.string.address_found), Snackbar.LENGTH_LONG).show();
            }
            mAddressReq = false;
            //updateUI();
        }
    }
}
