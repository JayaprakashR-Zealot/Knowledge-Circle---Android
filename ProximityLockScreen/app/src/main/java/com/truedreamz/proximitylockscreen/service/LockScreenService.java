package com.truedreamz.proximitylockscreen.service;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.truedreamz.proximitylockscreen.phone_state.PhoneStateReceiver;
import com.truedreamz.proximitylockscreen.receiver.DeviceAdmin;


/**
 * Created by Jayaprakash.
 */
public class LockScreenService extends Service {

    private SensorManager mSensorManager;
    private Sensor mProximitySensor;

    private static final String TAG="LockScreenService";
    public static final String BROADCAST = "com.truedreamz.proximitylockscreen.phone_state.android.action.broadcast";
    PhoneStateReceiver receiver = new PhoneStateReceiver();


    public static Boolean isPhoneIdle=true;
    private Boolean isFalseTrigger=false;
    //private Boolean isDeviceFlat=false;
    //private int triggerCount=0;
    public static final String BROADCAST_ACTION = "com.truedreamz.proximitylockscreen.service.sensorinfo.android.action.broadcast";



    Intent call_intent;
    DevicePolicyManager deviceManger;
    ComponentName compName;

    float sensorRating=0;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        call_intent = new Intent(BROADCAST_ACTION);

        mSensorManager = (SensorManager) getSystemService(
                Context.SENSOR_SERVICE);
        mProximitySensor = mSensorManager.getDefaultSensor(
                Sensor.TYPE_PROXIMITY);

        deviceManger = (DevicePolicyManager)getSystemService(
                Context.DEVICE_POLICY_SERVICE);

        compName = new ComponentName(this, DeviceAdmin.class);

        if(mProximitySensor!=null){
            call_intent.putExtra("SensorName", mProximitySensor.getName());
            call_intent.putExtra("MaxSensorRange", String.valueOf(mProximitySensor.getMaximumRange()));
            sendBroadcast(call_intent);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSensorManager.registerListener(mSensorEventListener,
                mProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);

        /* Registering phone state receiver*/
        IntentFilter filter = new IntentFilter(BROADCAST);
        registerReceiver(receiver, filter);

        call_intent.putExtra("AlertMessage", "Proximity lock mode : ON");
        sendBroadcast(call_intent);
        return super.onStartCommand(intent, flags, startId);
    }

    // To Check if any VOIP call is in progress
    public static boolean isVOIPCallActive(Context context){
        AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        if(manager.getMode()== AudioManager.MODE_IN_CALL || manager.getMode()== AudioManager.MODE_IN_COMMUNICATION){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(mSensorEventListener);
        unregisterReceiver(receiver);
        call_intent.putExtra("AlertMessage", "Proximity lock mode : OFF");
        sendBroadcast(call_intent);
    }

    private void lockScreen(){
        boolean active = deviceManger.isAdminActive(compName);
        if (active) {
            deviceManger.lockNow();
        }
    }

    SensorEventListener mSensorEventListener
            = new SensorEventListener(){
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub
            if(event.sensor.getType()== Sensor.TYPE_PROXIMITY)
            {
                try {
                    sensorRating=event.values[0];
                        Boolean isVOIPCallInprogress=isVOIPCallActive(getApplicationContext());

                        if( sensorRating <5.0 && isPhoneIdle && !isVOIPCallInprogress){
                            Log.d(TAG, "Distance <5.0:" + sensorRating);
                                isFalseTrigger=false;
                            if(!isFalseTrigger) {
                                Log.d(TAG, "Lock screen");
                                isFalseTrigger = false;
                                lockScreen();
                            }
                        }else{
                            isFalseTrigger=true;
                        }

                }catch (Exception ex){
                    Log.e(TAG, "onSensorChanged Exception:" + ex.getMessage());
                }
            }
        }
    };
}