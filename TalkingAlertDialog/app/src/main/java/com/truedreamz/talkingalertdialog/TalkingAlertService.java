package com.truedreamz.talkingalertdialog;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.WindowManager;

import java.util.Locale;

public class TalkingAlertService extends Service implements  TextToSpeech.OnInitListener{
    public TalkingAlertService() {
    }
    public static String TAG="TalkingAlert";
    private TextToSpeech mTts;

    @Override
    public void onCreate() {
        super.onCreate();

        mTts = new TextToSpeech(this,
                this  // OnInitListener
        );
        mTts.setSpeechRate(1.0f);
        mTts.setLanguage(Locale.US);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String message="This is the talking alert dialog from services.";
                speakAlert(message);
                showNotificationAlert("Talking alert",message);
            }
        },3000);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
    }

    @Override
    public void onInit(int status) {
        Log.v(TAG, "oninit");
        if (status == TextToSpeech.SUCCESS) {
            int result = mTts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.v(TAG, "Language is not available.");
            }
        } else {
            Log.v(TAG, "Could not initialize TextToSpeech.");
        }
    }

    private void showNotificationAlert(String title,final String descr){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        alertDialog.setTitle(title);
        alertDialog.setMessage(descr);

        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                dialog.dismiss();
                stopSelf();
            }
        });


        alertDialog.show();
    }

    private void speakAlert(String str) {
        mTts.speak(str,
                TextToSpeech.QUEUE_FLUSH,
                null);
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
