package com.google.android.gms.samples.vision.inner.bink.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.loyaltyangels.bink.App;
import com.loyaltyangels.bink.model.Model;
import com.loyaltyangels.bink.model.user.settings.SettingsUpdate;
import com.loyaltyangels.bink.module.AppComponent;

/**
 * Created by jmcdonnell on 17/08/2016.
 */

public class SettingsUpdateService extends IntentService {

    private static final String TAG = SettingsUpdateService.class.getSimpleName();

    public static final String EXTRA_SETTINGS = "settings";

    public SettingsUpdateService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.hasExtra(EXTRA_SETTINGS)) {
            Log.d(TAG, "Updating User Settings");
            AppComponent appComponent = ((App) getApplication()).getAppComponent();

            SettingsUpdate settingsUpdate = intent.getParcelableExtra(EXTRA_SETTINGS);

            Model model = appComponent.model();
            model.updateSettings(settingsUpdate)
                    .subscribe(update -> {
                        Log.d(TAG, "Successfully updated User Settings");
                    }, error -> {
                        Log.e(TAG, "Error updating User Settings");
                        error.printStackTrace();
                    });
        } else {
            Log.e(TAG, "Settings not provided");
        }
    }

}
