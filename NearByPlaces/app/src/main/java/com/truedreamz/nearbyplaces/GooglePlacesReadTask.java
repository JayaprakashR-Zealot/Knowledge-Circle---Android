package com.truedreamz.nearbyplaces;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by augray on 9/6/2016.
 */
public class GooglePlacesReadTask extends AsyncTask<Object, Integer, String> {
    String TAG="GooglePlacesReadTask";
    String googlePlacesData = null;
    Context context;
    String latitude,longitude;
    int SELECTED_TYPE_FLAG;
    ProgressDialog dialog;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog=new ProgressDialog(context);
        dialog.setMessage("Loading...");
        dialog.show();
    }

    public GooglePlacesReadTask(Context cxt,String lat,String longu,int type) {
        this.context = cxt;
        this.latitude=lat;
        this.longitude=longu;
        this.SELECTED_TYPE_FLAG=type;
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
            Intent intent=new Intent(context,MapActivity.class);
            intent.putExtra("Response",result);
            intent.putExtra("latitude",latitude);
            intent.putExtra("longitude",longitude);
            intent.putExtra("SelectedType",String.valueOf(SELECTED_TYPE_FLAG));
            context.startActivity(intent);
        }
    }
}
