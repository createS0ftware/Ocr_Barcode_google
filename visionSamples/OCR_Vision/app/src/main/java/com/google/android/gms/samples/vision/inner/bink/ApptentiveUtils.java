package com.google.android.gms.samples.vision.inner.bink;

import android.content.Context;

import com.apptentive.android.sdk.Apptentive;

/**
 * Created by bb on 18/07/16.
 */
public final class ApptentiveUtils {

    public static final String APPOPEN = "appOpen";
    public static final String ADDLOYALTY = "addLoyaltyScheme";
    public static final String ADDPAYMENT = "addPaymentCard";
    
    private ApptentiveUtils(){
    }

    public static boolean appOpen(Context context){
        return Apptentive.engage(context, APPOPEN);
    }

    public static boolean addLoyaltyScheme(Context context){
        return Apptentive.engage(context, ADDLOYALTY);
    }

    public static boolean addPaymentCard(Context context){
        return Apptentive.engage(context, ADDPAYMENT);
    }
}
