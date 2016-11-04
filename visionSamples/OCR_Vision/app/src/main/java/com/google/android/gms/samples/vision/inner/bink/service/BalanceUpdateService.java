package com.google.android.gms.samples.vision.inner.bink.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.loyaltyangels.bink.App;
import com.loyaltyangels.bink.module.AppComponent;

/**
 * Created by jmcdonnell on 12/10/2016.
 */

public class BalanceUpdateService extends IntentService {

    private static final String TAG = BalanceUpdateService.class.getSimpleName();

    public BalanceUpdateService() {
        super(BalanceUpdateService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppComponent appComponent = ((App) getApplication()).getAppComponent();

        Log.d(TAG, "Updating Balances");

        Throwable error = appComponent.model()
                .updateStaleSchemeAccounts()
                .get();

        if (error == null) {
            Log.d(TAG, "Updated Balances");
            stopSelf();
        } else {
            Log.d(TAG, "Error updating balances");
            error.printStackTrace();
        }
    }
}
