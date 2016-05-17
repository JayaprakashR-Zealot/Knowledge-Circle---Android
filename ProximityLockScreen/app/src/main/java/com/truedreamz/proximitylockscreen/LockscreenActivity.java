package com.truedreamz.proximitylockscreen;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.truedreamz.proximitylockscreen.receiver.DeviceAdmin;
import com.truedreamz.proximitylockscreen.service.LockScreenService;

public class LockscreenActivity extends AppCompatActivity {

    public static final String TAG="LockscreenActivity";
    public static Boolean isBroadcastRegistered=false;
    private TextView txtSensorName,txtSensorRange;
    DevicePolicyManager deviceManger;
    ActivityManager activityManager;
    ComponentName compName;
    private static final int RESULT_ENABLE = 1;
    boolean isAdminEnabled=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockscreen);
        txtSensorName=(TextView)findViewById(R.id.txtSensorName);
        txtSensorRange=(TextView)findViewById(R.id.txtSensorRange);

        deviceManger = (DevicePolicyManager)getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        activityManager = (ActivityManager)getSystemService(
                Context.ACTIVITY_SERVICE);
        compName = new ComponentName(this, DeviceAdmin.class);

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    private void updateUI(Intent intent) {
        String strSensorName = intent.getStringExtra("SensorName");
        String strMaxSensorRange = intent.getStringExtra("MaxSensorRange");
        String strAlertMessage = intent.getStringExtra("AlertMessage");


        if(strAlertMessage !=null){
            Toast.makeText(this, strAlertMessage, Toast.LENGTH_SHORT).show();
        }

        if(strSensorName!=null && strMaxSensorRange!=null){
            txtSensorName.setText("Sensor present with name: " + " " +strSensorName);
            txtSensorRange.setText("Maximum Range: " + strMaxSensorRange + " cm");
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(LockScreenService.BROADCAST_ACTION));
        isBroadcastRegistered=true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isBroadcastRegistered){
            unregisterReceiver(broadcastReceiver);
        }
    }

    public void onActivateProximityLock(View v){

        boolean active = deviceManger.isAdminActive(compName);
        if(active){
            startService(new Intent(this,LockScreenService.class));
        }else{
            Intent intent = new Intent(DevicePolicyManager
                    .ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    compName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "Additional text explaining why this needs to be added.");
            startActivityForResult(intent, RESULT_ENABLE);
        }
    }

    public void onDeactivateProximityLock(View v) {
        if(isAdminEnabled){
            deviceManger.removeActiveAdmin(compName);
        }

        stopService(new Intent(this, LockScreenService.class));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_ENABLE:
                if (resultCode == Activity.RESULT_OK) {
                    Log.i(TAG, "Admin enabled!");
                    isAdminEnabled=true;
                    startService(new Intent(this,LockScreenService.class));
                } else {
                    isAdminEnabled=false;
                    Log.i(TAG, "Admin enable FAILED!");
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}