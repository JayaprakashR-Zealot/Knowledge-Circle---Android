package com.truedreamz.nearbyplaces;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HomeActivity extends Activity implements com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult> {
    public static final String TAG="GooglePlaces";

    public static final int TYPE_FOOD_FLAG=1;
    public static final int TYPE_BANK_FLAG=2;
    public static final int TYPE_ENTERTAIN_FLAG=3;
    public static final int TYPE_SHOP_FLAG=4;
    public static final int TYPE_TRANS_FLAG=5;
    public static final int TYPE_HEALTH_FLAG=6;
    public static final int TYPE_PEACE_FLAG=7;

    public static int SELECTED_TYPE_FLAG=0;

    public static final String TYPE_FOOD="restaurant|food|bar|cafe|bakery|meal_takeaway|lodging";
    public static final String TYPE_BANK="bank|atm|accounting|courthouse|embassy|insurance_agency|local_government_office|police|post_office|school|university";
    public static final String TYPE_ENTERTAIN="amusement_park|aquarium|art_gallery|casino|city_hall|" +
            "movie_rental|movie_theater|museum|night_club|park|stadium|zoo|bowling_alley|library";
    public static final String TYPE_SHOPPING="bicycle_store|book_store|clothing_store|convenience_store|department_store" +
            "|electronics_store|furniture_store|hardware_store|home_goods_store|jewelry_store|liquor_store|pet_store|shoe_store|shopping_mall";
    public static final String TYPE_TRANSPORT="airport|bus_station|car_dealer|car_rental|car_repair|car_wash|fire_station|gas_station|subway_station" +
            "|taxi_stand|train_station|transit_station|travel_agency";
    public static final String TYPE_HEALTH="beauty_salon|campground|dentist|doctor|florist|gym|hair_care|hospital|laundry|pharmacy|physiotherapist|spa" +
            "|veterinary_care";
    public static final String TYPE_PEACE="cemetery|church|hindu_temple|mosque|synagogue";


    private LocationManager manager;
    String latitude=null, longitude=null;
    private int PROXIMITY_RADIUS = 2000;

    protected static GoogleApiClient mGoogleApiClient;
    protected static LocationSettingsRequest mLocationSettingsRequest;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected static final long EXPIRE_INTERVAL_IN_MILLISECONDS = 40000;
    protected static LocationRequest mLocationRequest;

    protected Location mLastLocation;
    protected static final int REQUEST_CHECK_SETTINGS = 1000;
    private Context ctx;
    private static boolean isNoGPS=false;
    private static final int GET_LOCATION_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ctx = this.getApplicationContext();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertMessageIfNoGps();
        }else{
            buildGoogleApiClient();
            createLocationRequest();
            buildLocationSettingsRequest();
            checkLocationSettings();
        }
    }

    private void showAlertMessageIfNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        finish();
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void onFoodStayListener(View v){

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertMessageIfNoGps();
        } else {
            if (isNetworkAvailable()) {
                if(latitude!=null && longitude!=null){
                    SELECTED_TYPE_FLAG=TYPE_FOOD_FLAG;
                    // Fetching from text file (Only for Testing)
                    /*InputStream inputStream = getResources().openRawResource(R.raw.google_places_loc);
                    String result=readTextFileFromRaw(inputStream);
                    Log.d(TAG,"Place response:"+result);
                    Intent intent=new Intent(this,MapActivity.class);
                    intent.putExtra("Response",result);
                    intent.putExtra("latitude",latitude);
                    intent.putExtra("longitude",longitude);
                    intent.putExtra("SelectedType",SELECTED_TYPE_FLAG);
                    startActivity(intent);*/

                    //getMyRestaurentAlert(result, latitude, longitude);

                    // Fetching location from places URL
                    getGooglePlacesData(latitude,longitude,TYPE_FOOD);


                }
            } else {
                Toast.makeText(HomeActivity.this, getResources().getString(R.string.strNoInternet), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onBankListener(View v){
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertMessageIfNoGps();
        } else {
            if (isNetworkAvailable()) {
                if(latitude!=null && longitude!=null){
                    SELECTED_TYPE_FLAG=TYPE_BANK_FLAG;
                    // Fetching location from places URL
                    getGooglePlacesData(latitude,longitude,TYPE_BANK);
                }
            } else {
                Toast.makeText(HomeActivity.this, getResources().getString(R.string.strNoInternet), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onShoppingListener(View v){
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertMessageIfNoGps();
        } else {
            if (isNetworkAvailable()) {
                if(latitude!=null && longitude!=null){
                    SELECTED_TYPE_FLAG=TYPE_SHOP_FLAG;
                    // Fetching location from places URL
                    getGooglePlacesData(latitude,longitude,TYPE_SHOPPING);
                }
            } else {
                Toast.makeText(HomeActivity.this, getResources().getString(R.string.strNoInternet), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onEntertainmentListener(View v){
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertMessageIfNoGps();
        } else {
            if (isNetworkAvailable()) {
                if(latitude!=null && longitude!=null){
                    SELECTED_TYPE_FLAG=TYPE_ENTERTAIN_FLAG;
                    // Fetching location from places URL
                    getGooglePlacesData(latitude,longitude,TYPE_ENTERTAIN);
                }
            } else {
                Toast.makeText(HomeActivity.this, getResources().getString(R.string.strNoInternet), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onTransportListener(View v){
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertMessageIfNoGps();
        } else {
            if (isNetworkAvailable()) {
                if(latitude!=null && longitude!=null){
                    SELECTED_TYPE_FLAG=TYPE_TRANS_FLAG;
                    // Fetching location from places URL
                    getGooglePlacesData(latitude,longitude,TYPE_TRANSPORT);
                }
            } else {
                Toast.makeText(HomeActivity.this, getResources().getString(R.string.strNoInternet), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onHealthListener(View v){
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertMessageIfNoGps();
        } else {
            if (isNetworkAvailable()) {
                if(latitude!=null && longitude!=null){
                    SELECTED_TYPE_FLAG=TYPE_HEALTH_FLAG;
                    // Fetching location from places URL
                    getGooglePlacesData(latitude,longitude,TYPE_HEALTH);
                }
            } else {
                Toast.makeText(HomeActivity.this, getResources().getString(R.string.strNoInternet), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onPeaceListener(View v){
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertMessageIfNoGps();
        } else {
            if (isNetworkAvailable()) {
                if(latitude!=null && longitude!=null){
                    SELECTED_TYPE_FLAG=TYPE_PEACE_FLAG;
                    // Fetching location from places URL
                    getGooglePlacesData(latitude,longitude,TYPE_PEACE);
                }
            } else {
                Toast.makeText(HomeActivity.this, getResources().getString(R.string.strNoInternet), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation=location;

        latitude = String.valueOf(mLastLocation.getLatitude());
        longitude = String.valueOf(mLastLocation.getLongitude());
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(mGoogleApiClient.isConnected()) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            }

            //if (mGoogleApiClient.isConnected() && location==null){
            if (mLastLocation != null) {
                latitude = String.valueOf(mLastLocation.getLatitude());
                longitude = String.valueOf(mLastLocation.getLongitude());
            }
            getLocationUpdate();
        }
    }

    /*private void handleNewLocation(Location location) {
        Log.d(TAG,"handleNewLocation:"+ location.getLatitude()+"/"+location.getLongitude());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        float LocationAccuracy = location.getAccuracy();
        //mPrefs.setLocation(currentLatitude + "", currentLongitude + "", LocationAccuracy + "");
    }*/


    private void getLocationUpdate() {
        String[] PERMISSIONS ={Manifest.permission.ACCESS_FINE_LOCATION};
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.getPermissionInstance().requestPermission(this,PERMISSIONS,ApplicationUtils.PERMISSION_REQUEST_LOCATION_ID,ApplicationUtils.PERMISSION_LOCATION_MESSAGE);
        }
        else {
            try {
                if(!mGoogleApiClient.isConnected())
                    return;
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient,
                        mLocationRequest,
                        this
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended");
        if(mGoogleApiClient!=null) mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed");
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {

        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    // Show the dialog by calling startResolutionForResult(), and
                    // check the result
                    // in onActivityResult().
                    status.startResolutionForResult(this,
                            REQUEST_CHECK_SETTINGS);

                } catch (IntentSender.SendIntentException e) {
                    //	Log.i(GEO_TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i("FETCH", "Location settings are inadequate, and cannot be fixed here");
                break;
        }
    }

    protected void buildGoogleApiClient() {
        //Log.i(GEO_TAG, "Building GoogleApiClient");
        //Toast.makeText(getApplicationContext(), "Building API", Toast.LENGTH_LONG).show();
        mGoogleApiClient = new GoogleApiClient.Builder(ctx)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    protected void createLocationRequest() {
       mLocationRequest = new LocationRequest();
       mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
       mLocationRequest
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
       mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
       mLocationRequest.setExpirationDuration(EXPIRE_INTERVAL_IN_MILLISECONDS);
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();
    }

    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                .checkLocationSettings(mGoogleApiClient,
                        mLocationSettingsRequest);
        result.setResultCallback(this);
    }


    public String readTextFileFromRaw(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            Log.e(TAG,"IOException:"+e.getMessage());
        }
        return outputStream.toString();
    }


    private void getGooglePlacesData(String latitude,String longitude,String TYPES){
        String type = "restaurant";
        // String strDistanceInMeter=new DecimalFormat("####").format(getDistanceFromPreference());
        //PROXIMITY_RADIUS=1000;

        PROXIMITY_RADIUS=2000;


        /*private static final String URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
        * return URL + "location=" + lat + "," + lon + "&radius="
					+ (radius * 1000.0f) + "&types=" + TYPE
					+ "&sensor=true&key=" + key;
        * */

        //String GOOGLE_API_KEY=getResources().getString(R.string.map_browser_api_key);
        String GOOGLE_API_KEY=getResources().getString(R.string.google_places_api_key);

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&types=" + TYPES);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&hasNextPage=true");
        googlePlacesUrl.append("&nextPage()=true");
        googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);

        String strPlacesUrl=googlePlacesUrl.toString();
        Log.d(TAG, "getGooglePlacesData-URL:" + strPlacesUrl);
        GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask(ctx);
        Object[] toPass = new Object[1];
        //toPass[0] = "";
        toPass[0] = strPlacesUrl;
        googlePlacesReadTask.execute(toPass);
    }

    class GooglePlacesReadTask extends AsyncTask<Object, Integer, String> {
        String googlePlacesData = null;
        Context context;
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(HomeActivity.this);
            dialog.setMessage("Loading...");
            dialog.show();
        }

        public GooglePlacesReadTask(Context cxt) {
            this.context = cxt;
        }


        @Override
        protected String doInBackground(Object... inputObj) {
            try {
                //googleMap = (GoogleMap) inputObj[0];
                String googlePlacesUrl = (String) inputObj[0];
                Http http = new Http();
                googlePlacesData = http.read(googlePlacesUrl);
            } catch (Exception e) {
                Log.d("GooglePlaces", e.toString());
            }
            return googlePlacesData;
        }

        @Override
        protected void onPostExecute(String result) {

            Log.d(TAG, "Map result:" + result);
            if(result!=null) {
                //getMyRestaurentAlert(result, latitude, longitude);
                if(dialog!=null){
                    if(dialog.isShowing())dialog.dismiss();
                }
                Intent intent=new Intent(HomeActivity.this,MapActivity.class);
                intent.putExtra("Response",result);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                intent.putExtra("SelectedType",String.valueOf(SELECTED_TYPE_FLAG));
                startActivity(intent);
            }
        }
    }
}