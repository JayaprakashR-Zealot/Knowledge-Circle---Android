package com.truedreamz.proximitylockscreen.phone_state;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.truedreamz.proximitylockscreen.service.LockScreenService;

/**
 * Created by Jayaprakash.
 */
public class LockPhoneStateListener extends PhoneStateListener {

    Context context;

    public LockPhoneStateListener(Context cxt){
        this.context=cxt;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                //when Idle i.e no call
                LockScreenService.isPhoneIdle=true;
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //when Off hook i.e in call
                LockScreenService.isPhoneIdle=false;
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                //when Ringing
                LockScreenService.isPhoneIdle=false;
                break;
            default:
                break;
        }
    }
}
