/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.truedreamz.nearbyplaces;

/**
 * This class defines constants used by Augray.
 */
public final class ApplicationUtils {
    public static final String GCM_TOKEN= "registration token";
    /*
     * A log tag for the application
     */
    public static final String KEY_UUID="uuid";
    public static final String PACKAGE_NAME ="com.wis.geofence";
    public static final String INVALID_STRING_VALUE="";

    public static final String CKEY_LAT="clatiude";
    
    public static final String CKEY_LONG="clongitude";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
    //FOR PERMISSION REQUEST
    public static final int PERMISSION_REQUEST_LOCATION_ID = 11001;
    public static final int PERMISSION_REQUEST_STORAGE_ID =  11002;
    public static final int PERMISSION_REQUEST_CONTACT_ID = 11003 ;
    public static final int PERMISSION_SCREEN_RECORDING_ID = 11004 ;

    //permission request message
    public static final String PERMISSION_LOCATION_MESSAGE = "Location Settings are necessary to provide location based contents. Enable Location service";
    public static final String PERMISSION_READ_STORAGE_MESSAGE = "See your videos in VR mode by letting AugRay read your phone's storage";
    public static final String PERMISSION_WRITE_STORAGE_MESSAGE = "See your videos in VR mode by letting AugRay write in to your phone's storage";
    public static final String PERMISSION_SAVE_TO_GALLERY_MESSAGE = "Enjoy taking Augmented images by letting AugRay write in to your phone's storage";
    public static final String PERMISSION_CONTACT_MESSAGE ="Allow AugRay to Save contacts via Augmented Cards";
    public static final String PERMISSION_SCREEN_RECORDING_MESSAGE = "Enjoy taking Augmented videos by letting AugRay write in to your phone's storage";
    //For Address 
    public static final String KEY_ADDRESS=PACKAGE_NAME+".KEY_ADDRESS";
    public static final String KEY_CITY = PACKAGE_NAME+".KEY_CITY";
    public static final String KEY_COUNTRY= PACKAGE_NAME+".KEY_COUNTRY";
    public static final String KEY_STATE = PACKAGE_NAME+".KEY_STATE";
    //For Location 
    public static final String SKEY_LATITUDE =PACKAGE_NAME+".KEY_LATITUDE";
    public static final String SKEY_LONGITUDE =PACKAGE_NAME+".SKEY_LONGITUDE";
    public static final String SKEY_ACCURACY =PACKAGE_NAME+".SKEY_ACCURACY";
    
    public static final String LOCATION__SETTINGS_STATUS=PACKAGE_NAME+".KEY_LOCATION_STATUS";
    public static final String GCM_REGISTRATION_STATUS =PACKAGE_NAME+".GCM_STATUS";
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS=30000;
    public static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS=1000 * 5;

    public static final String KEY_USERNAME ="USERNAME";
    public static final String KEY_EMAIL="EMAIL";

}
