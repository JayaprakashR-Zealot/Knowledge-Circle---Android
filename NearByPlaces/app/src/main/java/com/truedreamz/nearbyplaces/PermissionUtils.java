package com.truedreamz.nearbyplaces;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by ${"Vignesh"} on 7/4/2016.
 */
public class PermissionUtils {
    private static PermissionUtils ourInstance = new PermissionUtils();
    public static PermissionUtils getPermissionInstance(){return ourInstance;}

    public static void requestPermission(Activity activity,String[] requestPermissions,int requestId,String requestMessage)
    {
        Log.d("RUNTIME_AUGRAY","PermissionRationale");

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermissions[0])) {
                // Display a dialog with rationale.
                showPermissionDialog(activity, requestPermissions, requestId, requestMessage);
            } else {
                // Location permission has not been granted yet, request it.
                for(String requestPermission:requestPermissions) {
                    ActivityCompat.requestPermissions(activity, new String[]{requestPermission}, requestId);
                }
            }
    }

    private static void showPermissionDialog(final Activity activity, final String[] requestPermissions, final int requestId,String requestMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(requestMessage);

        builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    if(requestPermissions.length==1)
                        ActivityCompat.requestPermissions(activity, new String[]{requestPermissions[0]}, requestId);
                    if(requestPermissions.length==2)
                        ActivityCompat.requestPermissions(activity, new String[]{requestPermissions[0], requestPermissions[1]}, requestId);
                } catch (android.content.ActivityNotFoundException anfe) {

                }
            }
        });
        builder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {

                } catch (android.content.ActivityNotFoundException anfe) {

                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
