package com.truedreamz.nearbyplaces;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public class MapActivity extends FragmentActivity implements LocationListener{

    private static final String TAG = "MapActivity";
    public static final int TYPE_FOOD_FLAG=1;
    public static final int TYPE_BANK_FLAG=2;
    public static final int TYPE_ENTERTAIN_FLAG=3;
    public static final int TYPE_SHOP_FLAG=4;
    public static final int TYPE_TRANS_FLAG=5;
    public static final int TYPE_HEALTH_FLAG=6;
    public static final int TYPE_PEACE_FLAG=7;

    GoogleMap googleMap;
    double latitude=0;
    double longitude =0;
    private static final int GET_LOCATION_CODE=1;
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyAOLfVEAzUpcaQ0zTDuhCsrMk9ZDuyr5lw";

    boolean isMarkerAdded=false;
    MarkerOptions marker = new MarkerOptions();
    private TextView locationTv;
    String strPlaceResponse=null;
    int selectedType=0;
    public static boolean hasNextToken=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        setContentView(R.layout.activity_map);

        locationTv= (TextView) findViewById(R.id.latlongLocation);

        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        googleMap = supportMapFragment.getMap();
        googleMap.setMyLocationEnabled(true);

        //Context cxt=;

        if(getIntent().getExtras()!=null){
            double latitude=Double.valueOf(getIntent().getStringExtra("latitude"));
            double longitude= Double.valueOf(getIntent().getStringExtra("longitude"));
            strPlaceResponse=getIntent().getStringExtra("Response");
            selectedType=Integer.valueOf(getIntent().getStringExtra("SelectedType"));
            //addingMarkerOnMap(new LatLng(latitude, longitude), "My location!");
            hasNextToken=true;
            showGooglePlaces(strPlaceResponse,latitude,longitude,selectedType);
        }
    }

    /*public class getLatLongTask extends AsyncTask<Void,Void,String> {

        ProgressDialog dialog=null;
        String address=null;

        getLatLongTask(String address){
            this.address=address;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            locationTv.setText("");
            dialog=new ProgressDialog(MapActivity.this);
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            HashMap<String, String> input=new HashMap<String,String>();
            return  getLatLongFromPlaceURL(address, input);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            LatLng loc=parseLatLong(result);

            if(loc!=null){
                addingMarkerOnMap(loc,address);
            }
            if(dialog!=null){
                if(dialog.isShowing()) dialog.dismiss();
            }
        }
    }*/

    private void showGooglePlaces(String result,double currentLat,double currentLong,int selectedType) {
        try {
            if(hasNextToken){
                addingMarkerOnMap(new LatLng(currentLat, currentLong), "My location!");
            }

            JSONObject googlePlacesJson;
            googlePlacesJson = new JSONObject(result);

            com.truedreamz.nearbyplaces.Places placeJsonParser = new com.truedreamz.nearbyplaces.Places();
            List<HashMap<String, String>> googlePlacesList = null;
            googlePlacesList = placeJsonParser.parse(googlePlacesJson);
            BitmapDescriptor icon=getPlaceMarkerIcon(selectedType);
            for (int i = 0; i < googlePlacesList.size(); i++) {
                HashMap<String, String> googlePlace = googlePlacesList.get(i);
                PlacesList place = getPlaceInfo(googlePlace, currentLat, currentLong, true);
                if (place != null) {
                    if(!place.restauName.equals("-NA-")){
                        addingPlacesMarkerOnMap(new LatLng(Double.valueOf(place.restauLatitude),Double.valueOf(place.restauLongitude))
                                ,place.restauName,icon);
                    }
                  }
            }

            // If we need to get more than 20 places from API response uncomment & try it out.
            /*if(hasNextToken){
                getNextPageResponse(result);
            }*/

        }catch (Exception ex){

        }

    }

    private void getNextPageResponse(String result){
        // Getting next page token
        try {
            final JSONObject googlePlacesJson;
            googlePlacesJson = new JSONObject(result);
            if(googlePlacesJson.has("next_page_token")){

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String NEXT_PAGE_TOKEN = googlePlacesJson.getString("next_page_token");
                            getNextPageGooglePlaces(NEXT_PAGE_TOKEN);
                        }catch (Exception ex){
                            Log.e(TAG,"Exception:"+ex.getMessage());
                        }
                    }
                }, 1000);
            }else{
                hasNextToken=false;
                showGooglePlaces(result,latitude,longitude,selectedType);
            }
        }catch (JSONException ex){
            Log.e(TAG,"NextpageToken JSONException:"+ex.getMessage());
        }
    }

    private void getNextPageGooglePlaces(String NEXT_PAGE_TOKEN){
        /*String strDistanceInMeter=new DecimalFormat("####").format(getDistanceFromPreference());
        PROXIMITY_RADIUS=Integer.valueOf(strDistanceInMeter);*/

        int PROXIMITY_RADIUS=2000;

        String GOOGLE_API_KEY=getResources().getString(R.string.google_places_api_key);

        //StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?"); // working
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/radarsearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&types=" + String.valueOf(selectedType));
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&hasNextPage=true");
        googlePlacesUrl.append("&nextPage()=true");
        googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);
        googlePlacesUrl.append("&pagetoken=" + NEXT_PAGE_TOKEN);

        String strPlacesUrl=googlePlacesUrl.toString();
        Log.d("GooglePlaces","getGooglePlacesData-URL:"+strPlacesUrl);

        GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask(this.getApplicationContext());
        Object[] toPass = new Object[1];
        //toPass[0] = "";
        toPass[0] = strPlacesUrl;
        googlePlacesReadTask.execute(toPass);
    }

    class GooglePlacesReadTask extends AsyncTask<Object, Integer, String> {
        String googlePlacesData = null;
        Context context;
        //ProgressDialog dialog;
        /*@Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(getApplicationContext());
            dialog.setMessage("Loading...");
            dialog.show();
        }*/

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
                /*if(dialog!=null){
                    if(dialog.isShowing())dialog.dismiss();
                }*/

                showGooglePlaces(result,latitude,longitude,selectedType);

                /*Intent intent=new Intent(HomeActivity.this,MapActivity.class);
                intent.putExtra("Response",result);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                intent.putExtra("SelectedType",String.valueOf(SELECTED_TYPE_FLAG));
                startActivity(intent);*/
            }
        }
    }

    private BitmapDescriptor getPlaceMarkerIcon(int selectedType){
        BitmapDescriptor icon=null;

        switch (selectedType){
            case TYPE_FOOD_FLAG:
                icon=BitmapDescriptorFactory.fromResource(R.drawable.restaurants);
                break;
            case TYPE_BANK_FLAG:
                icon=BitmapDescriptorFactory.fromResource(R.drawable.bank);
                break;
            case TYPE_SHOP_FLAG:
                icon=BitmapDescriptorFactory.fromResource(R.drawable.shopping);
                break;
            case TYPE_ENTERTAIN_FLAG:
                icon=BitmapDescriptorFactory.fromResource(R.drawable.entertainment);
                break;
            case TYPE_TRANS_FLAG:
                icon=BitmapDescriptorFactory.fromResource(R.drawable.transport);
                break;
            case TYPE_HEALTH_FLAG:
                icon=BitmapDescriptorFactory.fromResource(R.drawable.health);
                break;
            case TYPE_PEACE_FLAG:
                icon=BitmapDescriptorFactory.fromResource(R.drawable.peace);
                break;
        }

        return icon;
    }

    private PlacesList getPlaceInfo(HashMap<String, String> googlePlace,double currentLat,double currentLong,boolean isMyRest){
        PlacesList place = new PlacesList();
        place.restauName = googlePlace.get("place_name");
        place.restauAddress = googlePlace.get("vicinity");
        place.restauLatitude= googlePlace.get("lat");
        place.restauLongitude = googlePlace.get("lng");
        place.restauRating = googlePlace.get("rating");
        place.restauWorkingStatus = googlePlace.get("is_open");
        place.isMyRestau = isMyRest;
        double lat = Double.parseDouble(googlePlace.get("lat"));
        double lng = Double.parseDouble(googlePlace.get("lng"));
        Log.d(TAG, "Place name :" + place.restauName + "--" + place.restauRating +
                "--" + place.restauWorkingStatus);
        double dist = 0;
        String distKM = null;
        if (lat != 0 && lng != 0) {
            dist = distanceBtnLocations(currentLat, currentLong, lat, lng);
            dist = dist / 1000;
            distKM = new DecimalFormat("#.0").format(dist);
        }
        place.restauDistance = distKM ;
        return place;
    }

    private double distanceBtnLocations(double lat1, double lon1, double lat2, double lon2) {
        Location selected_location=new Location("locationA");
        selected_location.setLatitude(lat1);
        selected_location.setLongitude(lon1);
        Location near_locations=new Location("locationB");
        near_locations.setLatitude(lat2);
        near_locations.setLongitude(lon2);

        double distance=selected_location.distanceTo(near_locations);
        return distance;
    }

    private void addingMarkerOnMap(LatLng loc,String address){

        googleMap.clear();

        MarkerOptions new_marker = new MarkerOptions();
        new_marker.position(loc).title(address);
        //new_marker.snippet(address);
        // Changing marker icon
        new_marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location));
        new_marker.draggable(true);
        // adding marker
        googleMap.addMarker(new_marker).showInfoWindow();

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc));

        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(loc)
                        .bearing(45)
                        .tilt(90)
                        .zoom(15)
                        .build();
        googleMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(cameraPosition),
                3000,
                null);

        googleMap.getUiSettings().setRotateGesturesEnabled(true);

        latitude=loc.latitude;
        longitude=loc.longitude;

        locationTv.setText("Latitude:" + latitude + ", Longitude:" + longitude);
    }



    private void addingPlacesMarkerOnMap(LatLng loc,String placeName,BitmapDescriptor icon){

        //googleMap.clear();

        MarkerOptions new_marker = new MarkerOptions();
        new_marker.position(loc).title(placeName);
        // Changing marker icon
        //new_marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.restau_marker));
        new_marker.icon(icon);
        //new_marker.draggable(true);
        // adding marker
        googleMap.addMarker(new_marker);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }
}