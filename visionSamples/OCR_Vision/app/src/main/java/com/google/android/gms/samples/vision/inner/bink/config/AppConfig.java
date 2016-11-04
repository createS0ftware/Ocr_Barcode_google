package com.google.android.gms.samples.vision.inner.bink.config;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jmcdonnell on 28/07/2016.
 * <p>
 * Stores user / app preferences in a private SharedPreferences instance.
 */

public class AppConfig {

    private static final String PREF_SHOW_MONETARY_VALUE = "show_monetary_value";
    private static final String PREF_FIRST_AIRSHIP_RUN = "first_airship_run";

    private SharedPreferences preferences;

    private boolean firstAirshipRun;
    private boolean showMonetaryValue;

    public AppConfig(Context context) {
        preferences = context.getSharedPreferences("app_config", Context.MODE_PRIVATE);
        loadPreferences();
    }

    private void loadPreferences() {
        showMonetaryValue = preferences.getBoolean(PREF_SHOW_MONETARY_VALUE, false);
        firstAirshipRun = preferences.getBoolean(PREF_FIRST_AIRSHIP_RUN, false);
    }

    public boolean shouldShowMonetaryValue() {
        return showMonetaryValue;
    }

    public AppConfig setShowMonetaryValue(boolean showMonetaryValue) {
        this.showMonetaryValue = showMonetaryValue;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_SHOW_MONETARY_VALUE, showMonetaryValue);
        editor.commit();

        return this;
    }

    public boolean isFirstAirshipRun() {
        return firstAirshipRun;
    }

    public AppConfig setFirstAirshipRun(boolean firstRun) {
        firstAirshipRun = firstRun;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_FIRST_AIRSHIP_RUN, firstAirshipRun);
        editor.apply();

        return this;
    }


    public void clear() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        loadPreferences();
    }

}
