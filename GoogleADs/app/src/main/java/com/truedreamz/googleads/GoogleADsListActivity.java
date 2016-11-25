package com.truedreamz.googleads;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class GoogleADsListActivity extends Activity implements View.OnClickListener{
    private InterstitialAd interstitial;
    private String TAG="GoogleADsListActivity";
    private Button btnInterstitialAds;
    private Button btnNativeAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_ads_list);

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        Log.d(TAG,"Device id:" + telephonyManager.getDeviceId());

        btnInterstitialAds=(Button)findViewById(R.id.btnInterstitialADs);
        btnInterstitialAds.setOnClickListener(this);

        btnNativeAds=(Button)findViewById(R.id.btnNativeADs);
        btnNativeAds.setOnClickListener(this);

        // Banner AD
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Prepare the Interstitial Ad
        interstitial = new InterstitialAd(GoogleADsListActivity.this);
        // Insert the Ad Unit ID
        interstitial.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        // Load ads into Interstitial Ads
        interstitial.loadAd(adRequest);
        // Prepare an Interstitial Ad Listener
        interstitial.setAdListener(new AdListener() {
            public void onAdLoaded() {
                // Call displayInterstitial() function
                Log.d(TAG,"AD is loaded.");
            }
        });


    }

    public void displayInterstitial() {
        // If Ads are loaded, show Interstitial else show nothing.
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnInterstitialADs:
                displayInterstitial();
                break;
            case R.id.btnNativeADs:
                startActivity(new Intent(GoogleADsListActivity.this,NativeADsActivity.class));
                break;

        }
    }
}