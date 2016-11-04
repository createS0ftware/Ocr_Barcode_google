package com.google.android.gms.samples.vision.inner.bink.module;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.loyaltyangels.bink.App;
import com.loyaltyangels.bink.BuildConfig;
import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.analytics.GoogleTracker;
import com.loyaltyangels.bink.analytics.LogTracker;
import com.loyaltyangels.bink.analytics.MultiTracker;
import com.loyaltyangels.bink.analytics.Tracker;
import com.loyaltyangels.bink.config.AppConfig;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jm on 12/07/16.
 */

@Module
public class AppModule {
    @Provides
    @Singleton
    Context provideContext() {
        return App.getInstance().getApplicationContext();
    }

    @Provides
    @Singleton
    AppConfig provideAppConfig(Context context) {
        return new AppConfig(context);
    }

    @Provides
    @Singleton
    Tracker provideTracker(Context context) {
        GoogleAnalytics googleAnalytics = GoogleAnalytics.getInstance(context);
        GoogleTracker googleTracker = new GoogleTracker(googleAnalytics.newTracker(R.xml.global_tracker));

        List<Tracker> trackers = new ArrayList<>();
        trackers.add(googleTracker);

        if (BuildConfig.DEBUG) {
            trackers.add(new LogTracker());
        }

        return new MultiTracker(trackers);
    }
}
